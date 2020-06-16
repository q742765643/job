package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.DateUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;

import org.codehaus.plexus.util.StringUtils;
import org.htht.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 扫描到所有待处理文件
 */
@Service("h8processShard")
public class H8ProjectionShard implements SharingHandler {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	/**
	 * 查询待预处理的文件 将待预处理的文件提交至调度中心
	 */
	@Override
	public ResultUtil<List<String>> execute(String params,
			LinkedHashMap fixedMap, LinkedHashMap dymap) throws Exception {
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();

		String beginTime = (String) fixedMap.get("beginTime");
		String endTime = (String) fixedMap.get("endTime");

		// String fileNameForm = (String) fixedMap.get("fileNameForm");
		String inputDataPath = (String) fixedMap.get("inputDataPath");
		String filePattern = (String) fixedMap.get("filePattern");
		String satelliteSenior = (String) fixedMap.get("satelliteSenior");

		File inputFile = new File(inputDataPath);
		if (!inputFile.exists()) {
			return result;
		}

		Date beginDate = DateUtil.strToDate(beginTime, "yyyy-MM-dd");
		Date endDate = DateUtil.strToDate(endTime, "yyyy-MM-dd");

		List<File> allFilesList = FileUtil.iteratorFile(inputFile, filePattern);
		if (allFilesList.isEmpty()) {
			return result;
		}

		// 检索所有文件，找出未处理的文件名进行分片
		Map<String, String> fileNamePathMap = new HashMap<String, String>();
		for (File h8FileList : allFilesList) {
			String[] strs = h8FileList.getName().split("_");
			if (strs.length != 8) {
				continue;
			}
			String dateStr = strs[2] + strs[3];
			Date fileDate = DateUtil.strToDate(dateStr, "yyyyMMddHHmm");
			
			if (isEffectiveDate(fileDate, beginDate, endDate)) {
				String valueStr = fileNamePathMap.get(dateStr);
				if (StringUtils.isEmpty(valueStr)) {
					fileNamePathMap.put(dateStr, h8FileList.getParent());
				}
			}
		}
		List<String> machedFileList = new ArrayList<String>();

		String ss[] = satelliteSenior.split("_");

		List<Date> dbList = findDBData(ss[0], ss[1], beginTime, endTime);
		List<String> dateList = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		for (Date d : dbList) {
			String dataStr = sdf.format(d);
			dateList.add(dataStr);
		}

		Set<Entry<String, String>> entrySet = fileNamePathMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String entryKey = entry.getKey();
			if (!dateList.contains(entryKey)) {
				machedFileList.add(entry.getKey() + "#" + entry.getValue());
			}
		}

		result.setResult(machedFileList);

		return result;
	}

	public List<Date> findDBData(String satellite, String sensor,
			String startDate, String endDate) {
		String dataLevel = "L2";
		StringBuffer sql = new StringBuffer(
				"SELECT i.f_producetime FROM	htht_dms_meta_img i"
						+ " WHERE	1=1 " + " AND i.f_satelliteid = '"
						+ satellite + "'" + " AND i.f_sensorid = '" + sensor
						+ "' "
						 + " AND i.f_producetime >= '" + startDate + "'"
						 + " AND i.f_producetime <= '" + endDate + "'"
						+ " AND i.F_LEVEL = '" + dataLevel + "'");
		// sql.append(" ORDER BY i.f_centertime");
		return baseDaoUtil.getByJpql(sql.toString(), null);
	}

	public boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
		if (nowTime.getTime() == startTime.getTime()
				|| nowTime.getTime() == endTime.getTime()) {
			return true;
		}

		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);

		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}

}
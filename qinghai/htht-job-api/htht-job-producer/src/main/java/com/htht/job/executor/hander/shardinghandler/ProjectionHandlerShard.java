package com.htht.job.executor.hander.shardinghandler;

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

import org.htht.util.Consts;
import org.htht.util.DateUtil;
import org.htht.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.ProjectionService;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.paramtemplate.PreDataParam;
import com.htht.job.executor.redis.RedisService;

/**
 * 扫描到所有待处理文件
 */
@Service("projectionHandlerShard")
public class ProjectionHandlerShard implements SharingHandler {

	@Autowired
	private DataMataInfoService dataMataInfoService;
	@Autowired
	private RedisService redisService;

	/**
	 * 查询待预处理的文件 将待预处理的文件提交至调度中心
	 */
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();

		PreDataParam preDataParam = null;
		try {
			preDataParam = JSON.parseObject(params, PreDataParam.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String[] ssr = preDataParam.getPreDataTaskName().split("_");
		String satellite = null;
        if (ssr.length == 3) {
            satellite = ssr[0];
            String sensor = ssr[1];
            String resolutionX = ssr[2];
            if ("GLL".equals(preDataParam.getProjectionIdentify())) {
                resolutionX = Integer.toString((int) (Double.parseDouble(preDataParam.getResolutionX()) * 100000));
            } else {
                resolutionX = preDataParam.getResolutionX();
            }

            paramMap.put("satellite", satellite);
            paramMap.put("sensor", sensor);
            paramMap.put("resolution", resolutionX);
            paramMap.put("datatype", Consts.PreDateOutType.PROJECTION);
            dealFileDate(preDataParam);
            paramMap.put("startDate", preDataParam.getStartDate());
            paramMap.put("endDate", preDataParam.getEndDate());
        }
		
        List<String> machedFileList = new ArrayList<String>();
		
		
		// fileNamePattern
		String fileNamePattern = preDataParam.getFileNamePattern();
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse(preDataParam.getStartDate());
		Date endDate = sdf.parse(preDataParam.getEndDate());
		
		//查询预处理结果
        List<String> existIssues = dataMataInfoService.findProjectedData(paramMap);
		
		String filePath = preDataParam.getInputDataFilePath();
		Calendar c = Calendar.getInstance();
		while (startDate.before(endDate)) {
			c.setTime(startDate);
			
			String tmpPath = dealFilePath(filePath,startDate);
			fileNamePattern = dealFilePath(fileNamePattern,startDate);
			
			List<File> files = FileUtil.iteratorFileAndDirectory(new File(tmpPath), fileNamePattern);
			for(File f:files){
				String issue = ProjectionService.getFileDateBySatellite(satellite,f.getName());
				if (!existIssues.contains(issue) && !redisService.exists("projection_" + f.getName())) {
                    machedFileList.add(f.getPath()+"&&"+issue);
                    redisService.add("projection_" + f.getName(), f.getName());
                }
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
			startDate = c.getTime();
		}

		
		if (!result.isSuccess()) {
			return result;
		}
		if (machedFileList.isEmpty()) {
			result.setResult(new ArrayList<>());
			return result;
		}
		result.setResult(machedFileList);
		return result;
	}

	private static String dealFilePath(String filePath,Date startDate) {
		
		String yyyy = String.format("%tY", startDate);
		String MM = String .format("%tm", startDate);
		String dd = String .format("%td", startDate);
		
		filePath = filePath.replace("{", "").replace("}", "");
		filePath = filePath.replace("yyyyMMdd", yyyy+MM+dd);
		filePath = filePath.replace("yyyyMM", yyyy+MM);
		filePath = filePath.replace("yyyy", yyyy);
		filePath = filePath.replace("MM", MM);
		filePath = filePath.replace("dd", dd);
		return filePath;
	}

	private void dealFileDate(PreDataParam preDataParam) {
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 实时
		if("1".equals(preDataParam.getDateType())){
			int days = null==preDataParam.getRangeDay()?0:preDataParam.getRangeDay();
			startDate = new Date();
			Calendar c = Calendar.getInstance();
	        c.setTime(startDate);
	        c.add(Calendar.DAY_OF_MONTH, days);// 今天+3天
	        endDate = c.getTime();
			
		}else{
			String historyDate = preDataParam.getProjectioDate();
			String[] dateStr = historyDate.split(" - ");
			// 2019-12-25 - 2019-12-26
			try {
				startDate = sdf.parse(dateStr[0]);
				endDate = sdf.parse(dateStr[1]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		preDataParam.setStartDate(sdf.format(startDate));
		preDataParam.setEndDate(sdf.format(endDate));
	}
	
}

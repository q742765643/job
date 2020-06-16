package com.htht.job.executor.hander.shardinghandler;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.DateUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.paramtemplate.PreDataParam;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.downupload.DownResultService;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * 扫描到所有待处理文件
 */
@Service("projectionHandlerShard")
public class ProjectionHandlerShard implements SharingHandler {

	@Autowired
	private DataMataInfoService dataMataInfoService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private DownResultService downResultService;

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
		List<String> machedFileList = new ArrayList<String>();
		String satellite = null;
		if (ssr.length == 3) {
			satellite = ssr[0];
			String sensor = ssr[1];
			String resolutionX = ssr[2];
			
			if ((preDataParam.getPreDataTaskName().contains("FY4A")
					|| (preDataParam.getPreDataTaskName().contains("FY3D")
					&& preDataParam.getPreDataTaskName().contains("MERSI")))
					&& !StringUtils.isEmpty(preDataParam.getResolutionX())) {
				if ("GLL".equals(preDataParam.getProjectionIdentify())) {
					resolutionX = Integer.toString((int) (Double.parseDouble(preDataParam.getResolutionX()) * 100000));
				} else {
					resolutionX = preDataParam.getResolutionX();
				}
			}

			paramMap.put("satellite", satellite);
			paramMap.put("sensor", sensor);
			paramMap.put("resolution", resolutionX);
			paramMap.put("isProjection", "isProjection");
			if (Consts.PreDateType.DAY_RANGE_INT.equals(preDataParam.getDateType())) {
				String startDate = DateUtil.getCurrentDateStringWithOffset(Consts.DateForMat.yyMMddFormat,
						-1 * preDataParam.getRangeDay());
				String endDate = DateUtil.getCurrentDateString(Consts.DateForMat.yyMMddFormat);

				paramMap.put("startDate", startDate);
				paramMap.put("endDate", endDate);
			} else if (Consts.PreDateType.DAY_RANGE_STR.equals(preDataParam.getDateType())) {
				String[] temp = preDataParam.getProjectioDate().split(" - ");
				String beginTime = temp[0];
				String endTime = temp[1];

				preDataParam.setStartDate(beginTime.replaceAll("-", ""));
				preDataParam.setEndDate(endTime.replaceAll("-", ""));

				paramMap.put("startDate", preDataParam.getStartDate());
				paramMap.put("endDate", preDataParam.getEndDate());
			}
			// 查询气象数据
			List<String> FileList = new ArrayList<String>();
			if((preDataParam.getPreDataTaskName().equals("H08_AHI_1000"))){
//				FileList = downResultService.findH8DataToProject("2019-05-29 12:50:00", "2019-05-30 12:50:00");
				FileList = downResultService.findH8DataToProject(preDataParam.getStartDate(), preDataParam.getEndDate());
			}else{
				
				FileList = dataMataInfoService.findDataToProject(paramMap);
			}
			for (String file : FileList) {
				File handleFile = new File(file);
				String fileName = handleFile.getName();
				if (handleFile.exists()) {
					if (null == redisService.get("projection_" + fileName)) {
						redisService.set("projection_" + fileName, fileName);
						machedFileList.add(file);
					}
				}
			}

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
}
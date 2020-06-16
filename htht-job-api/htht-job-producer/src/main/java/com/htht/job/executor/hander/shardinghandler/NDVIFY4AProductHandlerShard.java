package com.htht.job.executor.hander.shardinghandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.htht.util.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.DateUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;

/**
 * @author HUHUI
 *
 *         2018年11月26日
 */
@Service("nDVIFY4AProductHandlerShard")
public class NDVIFY4AProductHandlerShard implements SharingHandler {

	@Autowired
	private DataMataInfoService dataMataInfoService;
	private static Logger logger = LoggerFactory.getLogger(NDVIFY4AProductHandlerShard.class);
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {

		List<String> FileList = new ArrayList<>();
		ResultUtil<List<String>> result = new ResultUtil<>();
		String startTime = (String) fixmap.get("startTime");
		String endTime = (String) fixmap.get("endTime");
		// 输入文件路径
		String inputData = (String) dymap.get("inputData");
		List<String> lists = Arrays.asList(inputData.split("\\\\"));
		String fileName = Arrays.asList(lists.get(lists.size() - 1).split("\\.")).get(0);
		List<String> lista = Arrays.asList(fileName.split("_"));
		Map<String, Object> paramMap = new HashMap<>();
		String resolution = lista.get(lista.size() - 1);
		paramMap.put("satellite", lista.get(0));
		paramMap.put("sensor", lista.get(1));
		paramMap.put("resolution", resolution.substring(0, resolution.length() - 1));
		paramMap.put("startDate", startTime);
		paramMap.put("endDate", endTime);
		paramMap.put("dataLevel", "L2");
		if (startTime.contains("{") && startTime.contains("yyyy") || startTime.length() <= 0 || endTime.isEmpty()) {
			String startDate = DateUtil.getCurrentDateStringWithOffset(Consts.DateForMat.yyMMddFormat, -1);
			String endDate = DateUtil.getCurrentDateString(Consts.DateForMat.yyMMddFormat);
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
		}

		FileList = dataMataInfoService.findDataToProject(paramMap);
		List<String> needFileList = new ArrayList<>();
		// 判断算法所需文件是否至少存在一个
		for (int i = 0; i < FileList.size(); i++) {
			String fileString = FileList.get(i);
			String parentFileString = new File(fileString).getParent();
			File parentFile = new File(parentFileString);
			if (!parentFile.exists()) {
				continue;
			}
			//判断是否存在0-9点整时刻或半时刻文件
			if (fileString.matches(".*" + parentFile.getName() + "_0[0123456789][03]0_" + paramMap.get("resolution") + ".*")
					&& new File(fileString).exists()
					&& !needFileList.contains(parentFileString)) {
				needFileList.add(parentFileString);
			}
		}

		if (needFileList.isEmpty()) {
			return result;
		}
		if (!result.isSuccess()) {
			return result;
		}
		result.setResult(needFileList);
		if (result.getResult().isEmpty()) {
			logger.info("ShardEnd");
		}
		return result;
	}
}

package com.htht.job.executor.service.impl;


import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.htht.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.BaseShardService;

/**
 *  旬月报插件
 * @author zzp
 *
 */

@Transactional
@Service("monthlyReportService")
public class MonthlyReportService extends BaseShardService{
	
	@Autowired
	 private RedisService redisService;
	 

	/**
	 * 根据redis中的nc预处理的tif数据来制作
	 */
	@Override
	protected Map<String, Object> initXmlParam(String issue, LinkedHashMap dymap, Map<String, Object> inputxmlParam) {

		String inputFile  = (String) dymap.get("inputPath");
		Date date = DateUtil.strToDate(issue, "yyyyMMddHHmm");
		if (inputFile.indexOf("{") > -1 && inputFile.indexOf("}") > -1 ) {
			inputFile = DateUtil.getPathByDate(inputFile, date);
		}
		
		if(!new File(inputFile).exists()){
			return inputxmlParam;
		}
		
		inputxmlParam.put("inputFile", inputFile); 
		return inputxmlParam;
	}

	@Override
	protected boolean saveProduct(TriggerParam triggerParam,
			Map<String, String> inputXmlParam) {

		return this.saveProductOne(triggerParam, inputXmlParam);
	}

	@Override
	protected void cleanRedis(Map<String, String> inputXmlParam) {
		String  projectKey =inputXmlParam.get("projectKey");
		String  issue =inputXmlParam.get("issue");
		this.cleanRedisComm(projectKey + issue);
	}


}

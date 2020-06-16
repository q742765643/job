package com.htht.job.executor.service.impl;


import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.htht.util.DateUtil;
import org.htht.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.BaseShardService;

/**
 *  土壤重量含水率
 * @author zzp
 *
 */

@Transactional
@Service("soilWeightWaterService")
public class SoilWeightWaterService extends BaseShardService{
	
	@Autowired
	 private RedisService redisService;
	 

	/**
	 * 根据redis中的nc预处理的tif数据来制作
	 */
	@Override
	protected Map<String, Object> initXmlParam(String issue, LinkedHashMap dymap, Map<String, Object> inputxmlParam) {

		String inputFile  = (String) dymap.get("inputPath");
		String inputFileMOD09A1  = (String) dymap.get("inputFileMOD09A1");
		String inputFileMOD11A2  = (String) dymap.get("inputFileMOD11A2");
		Date date = DateUtil.strToDate(issue, "yyyyMMddHHmm");
		String fileTime = DateUtil.formatDateTime(date, "yyyyDD");
		if (inputFile.indexOf("{") > -1 && inputFile.indexOf("}") > -1 ) {
			inputFile = DateUtil.getPathByDate(inputFile, date);
		}
		if (inputFileMOD09A1.indexOf("{") > -1 && inputFileMOD09A1.indexOf("}") > -1 ) {
			inputFileMOD09A1 = DateUtil.getPathByDate(inputFileMOD09A1, date);
			List<File> fileMOD09A1 = FileUtil.iteratorFile(new File(inputFileMOD09A1), ".*("+fileTime+").*.hdf");
			if(fileMOD09A1.size()==0){
				return inputxmlParam;
			}
			inputxmlParam.put("inputFileMOD09A1",  StringUtils.join(fileMOD09A1.toArray(), ","));
		}
		if (inputFileMOD11A2.indexOf("{") > -1 && inputFileMOD11A2.indexOf("}") > -1 ) {
			inputFileMOD11A2 = DateUtil.getPathByDate(inputFileMOD11A2, date);
			List<File> fileMOD11A2 = FileUtil.iteratorFile(new File(inputFileMOD11A2), ".*("+fileTime+").*.hdf");
			if(fileMOD11A2.size()==0){
				return inputxmlParam;
			}
			inputxmlParam.put("inputFileMOD11A2", StringUtils.join(fileMOD11A2.toArray(), ",")); 
		}
		
		if(!new File(inputFile).exists() || !new File(inputFileMOD09A1).exists() || !new File(inputFileMOD11A2).exists()){
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

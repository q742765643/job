package com.htht.job.executor.service.impl;


import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.DateUtil;
import org.htht.util.Consts.NcProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.BaseShardService;
import com.htht.job.executor.service.downupload.DownResultService;


@Transactional
@Service("commService")
public class CommService extends BaseShardService{
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private DownResultService downResultService;
	 

	@Override
	protected Map<String, Object> initXmlParam(String issue, LinkedHashMap dymap, Map<String, Object> inputxmlParam) {
		String inputPath = (String) dymap.get("inputPath");
		String projectKey = (String) inputxmlParam.get("projectKey");
		//秋收秋种
		if("QSQZ".equals(projectKey)){
			String fileFormat  = (String) dymap.get("fileFormat");
			String startTime = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8) +" 00:00:00";
			String endTime = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8) +" 23:59:59";
			if(StringUtils.isNotEmpty(fileFormat)){
				List<String> list = downResultService.findFileByRegs(".GRB2",fileFormat, startTime, endTime);
				if(list.size() > 0){
					inputxmlParam.put("inputFile", list.get(0));
				}else{
					Calendar c = Calendar.getInstance();
					c.setTime( DateUtil.strToDate(issue, "yyyyMMddHHmm"));
					c.add(Calendar.DAY_OF_YEAR, -1);
					String ymd = DateUtil.formatDateTime(c.getTime(), "yyyy-MM-dd");
					startTime = ymd + " 00:00:00";
					endTime = ymd + " 23:59:59";
					list = downResultService.findFileByRegs(".GRB2",fileFormat, startTime, endTime);
					if(list.size() > 0){
						inputxmlParam.put("inputFile", list.get(0));
					}
				}
			}
			return inputxmlParam;
		}
		if(projectKey.indexOf("FTPNCTMP") > -1){
			String fileFormat  = (String) dymap.get("fileFormat");
			String startTime = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8) +" 00:00:00";
			String endTime = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8) +" 23:59:59";
			if(StringUtils.isNotEmpty(fileFormat)){
				List<String> list = downResultService.findFileByRegs(getSuffix(projectKey),fileFormat, startTime, endTime);
				if(list.size() == 24){
					inputxmlParam.put("inputFile", String.join(",", list)); 
					inputxmlParam.put("projectKey", projectKey.split("_")[0]);
				}
			}
			return inputxmlParam;
		}
		//
		if(projectKey.indexOf("FTPGRIB") > -1 ){
			String fileFormat  = (String) dymap.get("fileFormat");
			String startTime = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8) +" " + issue.substring(8,10)+":00:00";
			String endTime = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8) +" " + issue.substring(8,10)+":59:59";
			if(StringUtils.isNotEmpty(fileFormat)){
				List<String> list = downResultService.findFileByRegs(getSuffix(projectKey),fileFormat, startTime, endTime);
				if(list.size() > 0){
					for(String str : list){
						if(str.indexOf("SPCC") > -1 ){
							inputxmlParam.put("inputFile", str); 
							inputxmlParam.put("projectKey", projectKey.split("_")[0]);
							break;
						}
					}
				}
			}
			return inputxmlParam;
		
		}
		if(projectKey.indexOf("FTPNC") > -1){
			String fileFormat  = (String) dymap.get("fileFormat");
			String startTime = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8) +" " + issue.substring(8,10)+":00:00";
			String endTime = issue.substring(0,4) + "-" + issue.substring(4,6) + "-" + issue.substring(6,8) +" " + issue.substring(8,10)+":59:59";
			if(StringUtils.isNotEmpty(fileFormat)){
				List<String> list = downResultService.findFileByRegs(getSuffix(projectKey),fileFormat, startTime, endTime);
				if(list.size() > 0){
					inputxmlParam.put("inputFile", list.get(0)); 
					inputxmlParam.put("projectKey", projectKey.split("_")[0]);
				}
			}
			return inputxmlParam;
			
		}
		
		Date date = DateUtil.strToDate(issue, "yyyyMMddHHmm");
		if (inputPath.indexOf("{") > -1 && inputPath.indexOf("}") > -1 ) {
			inputPath = DateUtil.getPathByDate(inputPath, date);
		}
		
		if(!new File(inputPath).exists()){
			return inputxmlParam;
		}
		inputxmlParam.put("inputFile", inputPath); 
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

	private String getSuffix(String str){
		if(str.toLowerCase().indexOf("nc") > -1){
			return ".nc";
		}else if(str.toLowerCase().indexOf("grib") > -1){
			return ".GRB2";
		}
		return str;
	}

}

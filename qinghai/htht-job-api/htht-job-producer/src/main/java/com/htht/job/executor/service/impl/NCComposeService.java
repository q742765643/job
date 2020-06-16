package com.htht.job.executor.service.impl;


import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.Consts.NcProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.BaseShardService;


@Transactional
@Service("ncComposeService")
public class NCComposeService extends BaseShardService{
	
	@Autowired
	 private RedisService redisService;
	 

	/**
	 * 根据redis中的nc预处理的tif数据来制作
	 */
	@Override
	protected Map<String, Object> initXmlParam(String issue, LinkedHashMap dymap, Map<String, Object> inputxmlParam) {

		StringBuffer sb = new StringBuffer();
		String valueADFP = (String) redisService.get(NcProduct.ADFP + issue.substring(0,10));
		
		if(StringUtils.isNotEmpty(valueADFP) && valueADFP.split(",").length >= NcProduct.ADFPKEY.length){
			if(StringUtils.isNoneEmpty(sb.toString())){
				sb.append(","+valueADFP);
			}else{
				sb.append(valueADFP);
			}
		}
		String valueSH = (String) redisService.get(NcProduct.SH + issue.substring(0,10));
		if(StringUtils.isNotEmpty(valueSH) && valueSH.split(",").length >= NcProduct.SHKEY.length){
			if(StringUtils.isNoneEmpty(sb.toString())){
				sb.append(","+valueSH);
			}else{
				sb.append(valueSH);
			}
		}
		String valueSRH = (String) redisService.get(NcProduct.SRH + issue.substring(0,10));
		if(StringUtils.isNotEmpty(valueSRH) && valueSRH.split(",").length >= NcProduct.SRHKEY.length){
			if(StringUtils.isNoneEmpty(sb.toString())){
				sb.append(","+valueSRH);
			}else{
				sb.append(valueSRH);
			}
		}
		String valueST = (String) redisService.get(NcProduct.ST + issue.substring(0,10));
		if(StringUtils.isNotEmpty(valueST) && valueST.split(",").length >= NcProduct.STKEY.length){
			if(StringUtils.isNoneEmpty(sb.toString())){
				sb.append(","+valueST);
			}else{
				sb.append(valueST);
			}
		}
		if(StringUtils.isNotEmpty(sb.toString())){
			String inputFile = sb.toString();
			if(inputFile.startsWith(",")){
				inputFile = inputFile.substring(1);
			}
			inputxmlParam.put("inputFile", sb.toString()); 
		}
		return inputxmlParam;
	}

	@Override
	protected boolean saveProduct(TriggerParam triggerParam,
			Map<String, String> inputXmlParam) {

		return true;
	}

	@Override
	protected void cleanRedis(Map<String, String> inputXmlParam) {
		String  projectKey =inputXmlParam.get("projectKey");
		String  issue =inputXmlParam.get("issue");
		this.cleanRedisComm(projectKey + issue);
	}


}

package com.htht.job.executor.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.DateUtil;
import org.htht.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.service.BaseShardService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductService;

/**
 * 
 * @author zzp
 *
 */
@Service("droughtMonitorService")
public class DroughtMonitorService extends BaseShardService {

	
	 
	 @Autowired
	 private DictCodeService dictCodeService;
	 
	 @Autowired
	 private DataMataInfoService dataMataInfoService;
	 
	 @Autowired
	 private ProductService productService;
	

	 /**
	  * 1.必须有3个文件，要么同时是06.tif，要么同时是05.tif,要么同时是04.tif;
	  * 2.超过3天后，NRT文件不全，就尝试用RT数据的06.tif。
	  */
	@Override
	protected Map<String, Object> initXmlParam(String issue,
			LinkedHashMap dymap, Map<String, Object> inputxmlParam) {
		
		String inputPath  = (String) dymap.get("inputPath");
		String fileFormat = (String) dymap.get("fileFormat");

		Date date = DateUtil.strToDate(issue, "yyyyMMddHHmm");
		if (inputPath.indexOf("{") > -1 && inputPath.indexOf("}") > -1 ) {
			inputPath = DateUtil.getPathByDate(inputPath, date);
		}else if(inputPath.indexOf(issue.substring(6)) < 0 || inputPath.indexOf(issue.substring(8)) < 0){
			inputPath = inputPath +File.separator+ issue.substring(0, 6) + File.separator + issue.substring(0, 8) ;
		}
		inputPath = inputPath + File.separator + "NRT/HOR" ;
		boolean over3days = false;

		//如果超过3天NRT的数据还没有，就用RT数据制作
		if(!new File(inputPath).exists()){
			Date now = new Date();
			long between = (now.getTime() - date.getTime()) / 1000;
			if(between/(24 * 3600) >= 3){
				inputPath = inputPath.replace("NRT", "RT");
				over3days = true;
			}else{
				return inputxmlParam;
			}
		}
		
		File[] dataFiles;
		try {
			dataFiles = FileUtil.getDataFileList(inputPath, fileFormat.replace("issue", issue.substring(0, 8)));
		} catch (IOException e) {
			e.printStackTrace();
			return inputxmlParam;
		}
		List<String> fileArrays06 = new ArrayList<String>();
		List<String> fileArrays05 = new ArrayList<String>();
		List<String> fileArrays04 = new ArrayList<String>();
		if(dataFiles == null){
			Date now = new Date();
			long between = (now.getTime() - date.getTime()) / 1000;
			if(between/(24 * 3600) >= 3){
				inputPath = inputPath.replace("NRT", "RT");
				try {
					dataFiles=FileUtil.getDataFileList(inputPath, fileFormat.replace("issue", issue.substring(0, 8)));
				} catch (IOException e) {
					e.printStackTrace();
					return inputxmlParam;
				}
				over3days = true;
				if(dataFiles == null){
					return inputxmlParam;
				}
			}else{
				return inputxmlParam;
			}
		}
		
		for (File file:dataFiles) {
			if(file.getName().indexOf("06.tif") > -1){
				fileArrays06.add(file.getAbsolutePath());
			}else if(file.getName().indexOf("05.tif") > -1){
				fileArrays05.add(file.getAbsolutePath());
			}else if(file.getName().indexOf("04.tif") > -1){
				fileArrays04.add(file.getAbsolutePath());
			}
		}
		String inputFile = null;
		if(over3days){
			if(fileArrays06.size() > 2){
				inputFile = StringUtils.join(fileArrays06, ",");
			}
		}else{
			if(fileArrays06.size() > 2){
				inputFile = StringUtils.join(fileArrays06, ",");
			}else if(fileArrays05.size() > 2){
				inputFile = StringUtils.join(fileArrays05, ",");
			}else if(fileArrays04.size() > 2){
				inputFile = StringUtils.join(fileArrays04, ",");
			}
		}
		
		if(StringUtils.isEmpty(inputFile)){
			return inputxmlParam;
		}
//		String mubanPath  = (String) dymap.get("mubanPath");
//		String sdConfigXml  = (String) dymap.get("sdConfigXml");
		inputxmlParam.put("inputFile", inputFile); 
//		inputxmlParam.put("mubanPath", mubanPath); 
//		inputxmlParam.put("sd_config_xml", sdConfigXml); 
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

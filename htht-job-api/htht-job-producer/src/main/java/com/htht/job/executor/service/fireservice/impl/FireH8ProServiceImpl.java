package com.htht.job.executor.service.fireservice.impl;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.htht.util.FileOperate;
import org.htht.util.FileUtil;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.DateUtil;
import com.htht.job.core.util.MatchTime;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.util.UUIDTool;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.fireservice.FireH8ProService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

/**
 * 判断需要处理的H8数据，把相应的数据制作成xml，把xml路径返回
 * 
 * @author zzp
 *
 */
@Service("fireH8ProService")
public class FireH8ProServiceImpl implements FireH8ProService {
	
	
	@Autowired
    protected ProductUtil productUtil;
	
	 @Autowired
	 protected AtomicAlgorithmService atomicAlgorithmService;
	 
	 @Autowired
	 protected ProductInfoService productInfoService;

	 @Autowired
	 private RedisService redisService;
	 
	 @Autowired
	 private DictCodeService dictCodeService;
	
	@Override
	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
        /** 1.获取参数列表 **/
        LinkedHashMap<?, ?> fixmap = triggerParam.getFixedParameter();
		/** 2.解析产品参数 **/
		String exePath = (String) fixmap.get("exePath");
		String outputlogpath = triggerParam.getLogFileName();
		String projectKey = (String) fixmap.get("projectKey");
		XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
		String issue = "";
		try {
			// 制作输入xml
			String paramStr = triggerParam.getExecutorParams();
			Gson gson = new Gson();
			java.lang.reflect.Type type = new TypeToken<Map<String, String>>() {}.getType();
			Map<String, String> inputxmlParam = gson.fromJson(paramStr, type);
			issue = inputxmlParam.get("file_time");
			String outputXml = inputxmlParam.get("output_xml");
			String inputXml = outputXml.replace("outputXml" , "inputXml");
			List<XmlDTO> inputList = FormatXmlParam(inputxmlParam);
			XmlUtils.createAlgorithmXml("", inputList, new ArrayList<XmlDTO>(), inputXml);
			 /** =======4.执行脚本=========== **/
            XxlJobLogger.logByfile(outputlogpath, issue + "火情正在执行算法");
            ServerImpUtil.executeCmd(exePath, inputXml);
            XxlJobLogger.logByfile(outputlogpath, issue + "火情算法运行完毕");
            /** ========5.脚本 结束======= **/
            File outputXmlFile = new File(outputXml);
            if(!outputXmlFile.exists()){
            	
            	result.setErrorMessage("outputXmlFile文件不存在，入库失败");
            	XxlJobLogger.logByfile(outputlogpath, "outputXmlFile文件不存在，路径为：" + outputXml);
            	
            	// 释放redis
        		if(redisService.exists(projectKey + issue)){
        			redisService.remove(projectKey + issue);
        		}
        		
            	return result;
            }
            XxlJobLogger.logByfile(outputlogpath, "开始读取输出xml文件" + outputXml);
            int b = XmlUtils.isFireSuccessByXml(outputXml);
            if (b == 0) {
            	result.setErrorMessage("outputxml显示算法失败" + outputXml);
            	XxlJobLogger.logByfile(outputlogpath, "算法执行失败，执行的算法为："+ exePath +"参数为：" + inputXml);
            	
            	// 释放redis
        		if(redisService.exists(projectKey + issue)){
        			redisService.remove(projectKey + issue);
        		}
            	return result;
            }
            if(b == 1){
            	result.setMessage("outputxml显示算法执行成功，无火");
            	XxlJobLogger.logByfile(outputlogpath, "算法执行成功，无火");
            	
            	// 释放redis
        		if(redisService.exists(projectKey + issue)){
        			redisService.remove(projectKey + issue);
        		}
            	return result;
            }
            XxlJobLogger.logByfile(outputlogpath, "算法执行成功，准备入库");
            //新版的入库
            String productInfoId = statistics(triggerParam, outputXml, outputlogpath,  projectKey, issue,inputXml);
            if (!result.isSuccess()) {
                result.setErrorMessage("入库出错");
                XxlJobLogger.logByfile(outputlogpath, "入库失败");
             // 释放redis
        		if(redisService.exists(projectKey + issue)){
        			redisService.remove(projectKey + issue);
        		}
                return result;
            }

            XxlJobLogger.logByfile(outputlogpath, "算法执行成功，开始入库");
            XxlJobLogger.logByfile(outputlogpath, "执行成功");
            result.setResult("成功");
		} catch (Exception e) {
			System.out.println(e.getMessage());
            result.setErrorMessage("火情监测出现异常");
            XxlJobLogger.logByfile(outputlogpath, issue+"期次的火情监测出现异常");
            // 释放redis
    		if(redisService.exists(projectKey + issue)){
    			redisService.remove(projectKey + issue);
    		}
            throw new RuntimeException();
		}
		result.setMessage("火情监测执行完毕");
		// 释放redis
		if(redisService.exists(projectKey + issue)){
			redisService.remove(projectKey + issue);
		}
		return result;

	}

	
	@Override
	public ResultUtil<List<String>> execute(String params,LinkedHashMap fixmap, LinkedHashMap dymap) {
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		String projectKey = (String) fixmap.get("projectKey");
		//默认:H8Fire
		if (StringUtils.isBlank(projectKey)) { 
			projectKey = "H8Fire"; 
		}
		//刷新时间戳的值，判断插件是否在运行
		redisService.set(projectKey, System.currentTimeMillis());
		
		List<String> list = new ArrayList<String>();
		String issue = "";
		StringBuffer sbIssue = new StringBuffer();
		try {

			/** 解析产品参数 **/
			String xmlPath = (String) fixmap.get("xmlPath");
			String startTime = (String) fixmap.get("startTime");
			String endTime = (String) fixmap.get("endTime");
			String logPath = (String) dymap.get("outputlog");
			String inputPath = (String) dymap.get("inputPath");
			String outputPath = (String) dymap.get("outputPath");
			String cycle = (String) dymap.get("cycle");
			// 默认设置要处理的数据时间个数
			String dataTime = "{yyyy}{MM}{dd}{HH}{mm}";
			if (dymap.containsKey("dataTime")) {
				dataTime = (String) dymap.get("dataTime");
			}
			// 判断是否有文件正则参数
			String fileNamePattern = null;
			if (dymap.containsKey("fileNamePattern")) {
				fileNamePattern = (String) dymap.get("fileNamePattern");
			}
			// 要处理的数据时间段
			Date doEndTime = DateUtil.getBeiJingTime();
			if (StringUtils.isNotEmpty(endTime)) {
				doEndTime = DateUtil.strToDate(endTime, "yyyy-MM-dd HH:mm:ss");
			}
			Date doStartTime = new Date(doEndTime.getTime() - (1000 * 60 * 29));
			if (StringUtils.isNotEmpty(startTime)) {
				doStartTime = DateUtil.strToDate(startTime, "yyyy-MM-dd HH:mm:ss");
			}

			// 获取需要处理的数据
			Calendar calendar = Calendar.getInstance();

			// 转成utc时间
			calendar.setTime(doStartTime);
			calendar.add(Calendar.HOUR, -8);
			doStartTime = calendar.getTime();
			
			// 转成utc时间
			calendar.setTime(doEndTime);
			calendar.add(Calendar.HOUR, -8);
			doEndTime = calendar.getTime();
			
			while (calendar.getTime().getTime() >= doStartTime.getTime()) {
				issue = MatchTime.matchIssue(calendar.getTime(), dataTime, cycle);
				sbIssue.append(issue + ";");
				if(redisService.exists(projectKey + issue)){
					// 时间增加10分钟
					calendar.add(Calendar.MINUTE, -10);
					continue;
				}
				//只有在执行实时模式时，判断该期次是否执行过了
				if(StringUtils.isEmpty(endTime)){

					List<ProductInfoDTO> lsp = productInfoService.findProductExits(issue, cycle, projectKey);
					if(lsp != null && lsp.size() > 0){
						// 时间增加10分钟
						calendar.add(Calendar.MINUTE, -10);
						continue;
					}
				}
				String folderTime = issue.substring(0, 8);
				File fileDir = new File(inputPath + File.separator + issue.substring(0,4) + File.separator + folderTime);
				if(!analysisIssueFile(issue, fileDir, fileNamePattern)){
					// 时间增加10分钟
					calendar.add(Calendar.MINUTE, -10);
					continue;
				}
				
				String outputLogOrXmlName = "_" + projectKey; //sample:_H8Fire_SC			
				String outputLog = logPath + File.separator + folderTime + File.separator + issue + outputLogOrXmlName + ".log";
				FileOperate.newParentFolder(outputLog);
				String outputXml = xmlPath + File.separator	+ "outputXml" + File.separator + folderTime  + File.separator + issue + outputLogOrXmlName + ".xml";
				FileOperate.newParentFolder(outputXml);
				Date dateBJ = DateUtil.strToDate(issue, "yyyyMMddHHmm");
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateBJ);
				cal.add(Calendar.HOUR, 8);
				String timeBJ = DateUtil.formatDateTime(cal.getTime(), "yyyyMMddHHmm");
				String out_fire_dir = outputPath + File.separator + timeBJ.substring(0, 8) + File.separator + timeBJ;
				// 制作xml参数
				Map<String, Object> inputxmlParam = new HashMap<>();
				
				inputxmlParam.put("file_time", issue);
				inputxmlParam.put("input_data_dir", fileDir.getPath());
				inputxmlParam.put("output_xml", outputXml);
				inputxmlParam.put("out_fire_dir", out_fire_dir);
				inputxmlParam.put("out_pre_pro_dir", (String) dymap.get("outPreProDir"));
				inputxmlParam.put("temp_dir", (String) dymap.get("tempDir"));
				
				Gson gson = new Gson();
				String str = gson.toJson(inputxmlParam);
				if(!list.contains(str)){
					list.add(str);
				}
				//放入缓存，半天之内不再重做
				redisService.set(projectKey + issue, projectKey + issue,12*3600L);
				// 时间增加10分钟
				calendar.add(Calendar.MINUTE, -10);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 释放redis
			if(redisService.exists(projectKey + issue)){
				redisService.remove(projectKey + issue);
			}
		}
		if (!result.isSuccess()) {
			return result;
		}
		result.setResult(list);
		if(list.size() < 1){
			result.setMessage("本次调度没有需要处理的数据，期次是" + sbIssue.toString());
		}
		/** 返回结果 **/
		return result;
	}
	
	
	/**
	 * 判断需要处理的数据是否完整
	 * @param issue
	 * @param fileDir
	 * @param fileNamePattern
	 * @return
	 * @throws ParseException
	 */
	private boolean analysisIssueFile(String issue, File fileDir, String fileNamePattern) throws ParseException{
		String time = issue.substring(0,8) + "_" + issue.substring(8,12);
		String[] bands = {"01","02","03","04","07","13"}; 
		String[] bs = {"02","03"};
		int flag = 0 ;
		// 获取文件
		List<File> filePathList = FileUtil.iteratorFile(fileDir, fileNamePattern);
		if (filePathList != null && filePathList.size() > 0) {
			//H08_20181229_0720_B16_FLDK_R20_S0610.DAT.BZ2
			file:for (File file : filePathList) {
				String fileName = file.getName();
				for(String band:bands){
					for(String b : bs){
						if(fileName.indexOf(time) < 0){
							continue file;
						}
						if(fileName.indexOf("_B" + band) < 0){
							continue ;
						}
						if(fileName.indexOf("S" + b) < 0){
							continue ;
						}
						flag++;
						continue file;
					}
				}
			}
		}
		if(flag>11){
			return true;
		}
		return false ; 
	}

	private String statistics(TriggerParam triggerParam, String outputxmlpath,
			String outputlogpath, String projectKey, String issue,
			String inputXml) {
		LinkedHashMap dymap = triggerParam.getDynamicParameter();
		List<String> regionIdList = XmlUtils.getXmlAttrVal(outputxmlpath,
				"region", "identify");
		String productPath = dictCodeService.findOneself("productPath").getDictCode();
		// 产品信息及文件信息入库
		ProductInfoDTO productInfoDTO = new ProductInfoDTO();
		if (regionIdList != null && regionIdList.size() > 0) {
			for (String regionId : regionIdList) {
				productInfoDTO = productUtil.saveProductInfo(
						triggerParam.getProductId(), regionId, issue,
						(String) dymap.get("cycle"), "", projectKey, inputXml);

				List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(
						outputxmlpath, "region", "identify", regionId);
				for (String file : lFiles) {
					productUtil.saveProductInfoFile(productInfoDTO.getId(),
							file, productPath, regionId, issue,
							(String) dymap.get("cycle"));
				}
			}

		}
		int b = XmlUtils.isFireSuccessByXml(outputxmlpath);
		if (b == 2) {
			// 产品结果信息入库
			XxlJobLogger.logByfile(outputlogpath, "正在执行_统计入库");

			List<Element> xmllists = XmlUtils.getTablenameElements(
					outputxmlpath, "table");
			for (Element e : xmllists) {
				// 获取数据集
				List<Element> list2 = e.elements();
				List<String> cc = new ArrayList<>();
				for (Element e2 : list2) {
					if ("values".equals(e2.getName())) {
						cc.add(e2.getText());
					}
				}
				// 封数据执行入库
				ProductAnalysisTableInfo productAnalysisTableInfo = new ProductAnalysisTableInfo(
						e.attribute("identify").getValue());
				String[] fields = (e.element("field").getText() + ",product_info_id")
						.replace("'", "").trim().split(",");
				String otherValue = "," + productInfoDTO.getId();

				for (int i = 0; i < fields.length; i++) {
					for (String s : cc) {
						String[] values = (s + otherValue).replace("'", "")
								.replace(",", " ").trim().split("\\s+");
						String pathValue = values[i].trim();
						if (pathValue.indexOf("\\") > -1
								|| pathValue.indexOf("/") > -1) {
							pathValue = pathValue.replace(productPath, "");
							pathValue = pathValue.replace("\\", "\\\\");
						}
						productAnalysisTableInfo.addFieldAndValue(
								fields[i].trim(), pathValue);
					}
				}
				productUtil.saveProductDetail(productAnalysisTableInfo);
			}
		}
		
		return productInfoDTO.getId();
	}

	private List<XmlDTO> FormatXmlParam(Map<String, String> map){
		List<XmlDTO>  inputList = new ArrayList<>();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			XmlDTO inputFileXmlDTO = new XmlDTO();
			inputFileXmlDTO.setIdentify(key);
			inputFileXmlDTO.setValue(map.get(key));
			inputFileXmlDTO.setDescription(" ");
			inputFileXmlDTO.setType("string");
			inputList.add(inputFileXmlDTO);
		}
		return inputList;
	}
}

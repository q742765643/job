package com.htht.job.executor.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.DateUtil;
import org.htht.util.FileOperate;
import org.htht.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.ModisComposeService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

/**
 * 
 * @author zzp
 *
 */
@Service("modisComposeService")
public class ModisComposeServiceImpl implements ModisComposeService {
	
	
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
	 
	 @Autowired
	 private DataMataInfoService dataMataInfoService;
	 
	 @Autowired
	 private ProductService productService;
	
	@Override
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
        /** 1.获取参数列表 **/
        LinkedHashMap<?, ?> fixmap = triggerParam.getFixedParameter();
		/** 2.解析产品参数 **/
		String exePath = (String) fixmap.get("exePath");
		String outputlogpath = triggerParam.getLogFileName();
		String projectKey = (String) fixmap.get("projectKey");
		XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
		// 算法标识
		String algorId = triggerParam.getAlgorId();
		AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(algorId);
		String issue = "";
		
		try {
			// 制作输入xml
			String paramStr = triggerParam.getExecutorParams();
			Gson gson = new Gson();
			java.lang.reflect.Type type = new TypeToken<Map<String, String>>() {}.getType();
			Map<String, String> inputxmlParam = gson.fromJson(paramStr, type);
			String outputXml = inputxmlParam.get("outXMLPath");
			String cycle = inputxmlParam.get("cycle");
			String inputXml = outputXml.replace("outputXml" , "inputXml");
			
			
			issue = inputxmlParam.get("inTime");

			 //存在就不再执行
			List<ProductInfo> pisExit = productInfoService.findProductExits(triggerParam.getProductId(), issue, cycle, at.getModelIdentify(), null, null);
			if(pisExit!=null && pisExit.size() > 0){
				result.setErrorMessage("该期次的产品已经存在，不再制作。期号为：" + issue);
				XxlJobLogger.logByfile(outputlogpath, "该期次的产品已经存在，不再制作。期号为：" + issue);

				// 释放redis
				if (redisService.exists(projectKey + issue)) {
					redisService.remove(projectKey + issue);
				}
				return result;
			}
			List<XmlDTO> inputList = FormatXmlParam(inputxmlParam);
			XmlUtils XmlUtils = new XmlUtils();
			if(XmlUtils.createAlgorithmXml(projectKey, inputList, new ArrayList<XmlDTO>(), inputXml)){
				/** =======4.执行脚本=========== **/
				XxlJobLogger.logByfile(outputlogpath, inputXml + "正在执行算法");
				ServerImpUtil.executeCmd(exePath, inputXml);
				XxlJobLogger.logByfile(outputlogpath, inputXml + "算法运行完毕");
			}
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
            
            if (!XmlUtils.isSuccessByXml(outputXml)) {
            	result.setErrorMessage("outputxml显示算法失败" + outputXml);
            	XxlJobLogger.logByfile(outputlogpath, "算法执行失败，执行的算法为："+ exePath +"参数为：" + inputXml);
            	
            	// 释放redis
        		if(redisService.exists(projectKey + issue)){
        			redisService.remove(projectKey + issue);
        		}
            	return result;
            }
            XxlJobLogger.logByfile(outputlogpath, "算法执行成功，准备入库");
            
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
            result.setErrorMessage("出现异常");
            XxlJobLogger.logByfile(outputlogpath, issue+"期次出现异常");
            // 释放redis
    		if(redisService.exists(projectKey + issue)){
    			redisService.remove(projectKey + issue);
    		}
            throw new RuntimeException();
		}
		result.setMessage("执行完毕");
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
		//刷新时间戳的值，判断插件是否在运行
		
		List<String> list = new ArrayList<String>();
		String issue ="";
		try {
			/** 解析产品参数 **/
			String startTime = (String) fixmap.get("startTime");
			String endTime = (String) fixmap.get("endTime");
			String xmlPath = (String) fixmap.get("xmlPath");
			String logPath = (String) fixmap.get("logPath");
			
			String satellite = (String) dymap.get("satellite");
			String outputPath = (String) dymap.get("outputPath");
			String inputPath = (String) dymap.get("inputPath");
			String tempPath = (String) dymap.get("tempPath");
			String processType = (String) dymap.get("processType");
			String cycle = (String) dymap.get("cycle");

			// 默认设置要处理的数据时间个数
			String dataTime = "{yyyy}{MM}{dd}{HH}{mm-1}";
			if (dymap.containsKey("dataTime")) {
				dataTime = (String) dymap.get("dataTime");
			}
			// 要处理的数据时间段
			Calendar calendar = Calendar.getInstance();
			Date doEndTime = calendar.getTime();
			if (StringUtils.isNotEmpty(endTime)) {
				doEndTime = DateUtil.strToDate(endTime, "yyyy-MM-dd");
			}
			Date doStartTime = null;
			if (StringUtils.isNotEmpty(startTime)) {
				doStartTime = DateUtil.strToDate(startTime, "yyyy-MM-dd");
				// 获取需要处理的数据
				calendar.setTime(doStartTime);
			}
			calendar = MatchTime.getCalendar(calendar.getTime(), dataTime);
			//确保同一数据只执行一次
			List<String> outputFileList = new ArrayList<String>();
			while(calendar.getTimeInMillis() <= doEndTime.getTime()){
				
				issue = MatchTime.matchIssue(calendar.getTime(), cycle);
				int folderTime = calendar.get(Calendar.YEAR);
			
				if(redisService.exists(projectKey + issue)){
					calendar = MatchTime.getCalendarByCycle(calendar, cycle);
					continue;
				}
				
				if(outputFileList.contains(issue)){
					calendar = MatchTime.getCalendarByCycle(calendar, cycle);
					continue;
				}
				String filePath = inputPath + File.separator + folderTime ;
				outputFileList.add(issue);
				//放入缓存
				redisService.add(projectKey + issue,issue);
				String dirlog = logPath + File.separator + folderTime + File.separator + issue + ".log";
				FileOperate.newParentFolder(dirlog);
				String outputXml = xmlPath + File.separator	+ "outputXml" + File.separator + folderTime  + File.separator + issue + ".xml";
				FileOperate.newParentFolder(outputXml);
				
				// 制作xml参数
				Map<String, Object> inputxmlParam = new HashMap<>();
				
				inputxmlParam.put("satellite", satellite);
				inputxmlParam.put("inTime", issue);
				inputxmlParam.put("period", cycle);
				inputxmlParam.put("processType", processType);
				inputxmlParam.put("inputpath", filePath);
				inputxmlParam.put("outpath", outputPath);
				inputxmlParam.put("temp_path", tempPath);
				inputxmlParam.put("outXMLPath", outputXml);
				inputxmlParam.put("outLogPath", dirlog);
				
				Gson gson = new Gson();
				String inputParam = gson.toJson(inputxmlParam);
				if(!list.contains(inputParam)){
					list.add(inputParam);
				}
				calendar = MatchTime.getCalendarByCycle(calendar, cycle);
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
			result.setMessage("本次调度没有需要处理的数据，期次是" + issue);
		}
		/** 返回结果 **/
		return result;
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

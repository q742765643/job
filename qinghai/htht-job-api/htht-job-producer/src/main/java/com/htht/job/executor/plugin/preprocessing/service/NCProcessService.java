package com.htht.job.executor.plugin.preprocessing.service;

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
import org.htht.util.Consts;
import org.htht.util.DateUtil;
import org.htht.util.FileOperate;
import org.htht.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

@Transactional
@Service("ncProcessService")
public class NCProcessService {

	@Autowired
	protected ProductUtil productUtil;

	@Autowired
	protected AtomicAlgorithmService atomicAlgorithmService;

	@Autowired
	protected ProductInfoService productInfoService;

	@Autowired
	protected ProductFileInfoService productFileInfoService;

	@Autowired
	private RedisService redisService;

	@Autowired
	private DictCodeService dictCodeService;

	@Autowired
	private DataMataInfoService dataMataInfoService;

	@Autowired
	private ProductService productService;

	public ResultUtil<String> execute(TriggerParam triggerParam,
			ResultUtil<String> result) {

		/** 1.获取参数列表 **/
		LinkedHashMap<?, ?> fixmap = triggerParam.getFixedParameter();
		/** 2.解析产品参数 **/
		String exePath = (String) fixmap.get("exePath");
		String outputlogpath = triggerParam.getLogFileName();
		String projectKey = (String) fixmap.get("projectKey");
		XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);

		String issue = "";

		// 制作输入xml
		String paramStr = triggerParam.getExecutorParams();
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> inputXmlParam = gson.fromJson(paramStr, type);
		String outputXml = inputXmlParam.get("outXMLPath");
		String inputXml = outputXml.replace("outputXml", "inputXml");

		issue = inputXmlParam.get("issue");
		//用于清除redis中存在的数据
		String inputFile = inputXmlParam.get("inputFile");
		
		List<XmlDTO> inputList = FormatXmlParam(inputXmlParam);
		XmlUtils XmlUtils = new XmlUtils();
		XmlUtils.createAlgorithmXml(projectKey, inputList,	new ArrayList<XmlDTO>(), inputXml);
		try {
			/** =======4.执行脚本=========== **/
			XxlJobLogger.logByfile(outputlogpath, inputXml + "正在执行算法");
			ServerImpUtil.executeCmd(exePath, inputXml);
			XxlJobLogger.logByfile(outputlogpath, inputXml + "算法运行完毕");
			/** ========5.脚本 结束======= **/
			File outputXmlFile = new File(outputXml);
			if (!outputXmlFile.exists()) {

				result.setErrorMessage("outputXmlFile文件不存在，入库失败");
				XxlJobLogger.logByfile(outputlogpath, "outputXmlFile文件不存在，路径为："
						+ outputXml);

				// 释放redis
				if (redisService.exists(projectKey + issue)) {
					redisService.remove(projectKey + issue);
					removeRedisFiles(inputFile);
				}

				return result;
			}
			XxlJobLogger.logByfile(outputlogpath, "开始读取输出xml文件" + outputXml);

			if (!XmlUtils.isSuccessByXml(outputXml)) {
				result.setErrorMessage("outputxml显示算法失败" + outputXml);
				XxlJobLogger.logByfile(outputlogpath, "算法执行失败，执行的算法为："
						+ exePath + "参数为：" + inputXml);

				// 释放redis
				if (redisService.exists(projectKey + issue)) {
					redisService.remove(projectKey + issue);
					removeRedisFiles(inputFile);
				}
				return result;
			}

			XxlJobLogger.logByfile(outputlogpath, "算法执行成功，准备入库");
			
			/*
			// 读取xml，把xml转换成map对象
			Map<String, List<Element>> map = XmlUtils.outputFilesXmlToMap(outputXml);

			List<String> regionIdList = XmlUtils.getXmlAttrVal(map, "region",
					"identify");

			if (regionIdList != null && regionIdList.size() > 0) {
				for (String regionId : regionIdList) {
					List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(
							map, "region", "identify", regionId);
					//把所有的文件放到redis中，以备后用
					ncDataUtil.addRedisByNcNames(lFiles);
				}
			}
			*/
			XxlJobLogger.logByfile(outputlogpath, "算法执行成功，开始入库");
			XxlJobLogger.logByfile(outputlogpath, "执行成功");
			result.setResult("成功");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			result.setErrorMessage("出现异常");
			XxlJobLogger.logByfile(outputlogpath, issue + "期次出现异常");
			// 释放redis
			if (redisService.exists(projectKey + issue)) {
				redisService.remove(projectKey + issue);
				removeRedisFiles(inputFile);
			}
			throw new RuntimeException();
		}
		result.setMessage("执行完毕");
		// 释放redis
		if (redisService.exists(projectKey + issue)) {
			redisService.remove(projectKey + issue);
			removeRedisFiles(inputFile);
		}
		return result;

	}

	public ResultUtil<List<String>> execute(String params,
			LinkedHashMap fixmap, LinkedHashMap dymap) {

		ResultUtil<List<String>> result = new ResultUtil<List<String>>();

		List<String> list = new ArrayList<String>();
		String projectKey = (String) fixmap.get("projectKey");
		String issue = "";
		try {
			/** 解析产品参数 **/
			String startTime = (String) fixmap.get("startTime");
			String endTime = (String) fixmap.get("endTime");
			String xmlPath = (String) fixmap.get("xmlPath");
			String logPath = (String) fixmap.get("logPath");

			String gdalTranslate = (String) dymap.get("gdalTranslate");
			String areaID = (String) dymap.get("areaID");
			String outFolder = (String) dymap.get("outputPath");
			String inputPath = (String) dymap.get("inputPath");
			String cycle = "COOH";
			if (dymap.containsKey("cycle")) {
				cycle = (String) dymap.get("cycle");
			}

			// 默认设置要处理的数据时间个数
			String dataTime = "{yyyy}{MM}{dd}{HH}{mm}";
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
			// 确保同一数据只执行一次
			while (calendar.getTimeInMillis() <= doEndTime.getTime()) {

				issue = MatchTime.matchIssue(calendar.getTime(), cycle);

				// 缓存中已经存在，就进行下一个期次
				if (redisService.exists(projectKey + issue)) {
					calendar = MatchTime.getCalendarByCycle(calendar, "COOD");
					continue;
				}
				// Y:/download/nc/20190708
				String filePath = "";
				if (inputPath.indexOf("{") > -1) {
					filePath = DateUtil.getPathByDate(inputPath,
							calendar.getTime());
				} else {
					filePath = inputPath + File.separator
							+ issue.substring(0, 8);
				}
				// 这个目录不存在，则进行下一个期次
				if (!new File(filePath).exists()) {
					calendar = MatchTime.getCalendarByCycle(calendar, "COOD");
					continue;
				}
				// 用于存储需要处理的文件
				List<String> inputFiles = new ArrayList<String>();
				File[] allFilesList = new File(filePath).listFiles();
				
				if(allFilesList == null || allFilesList.length == 0){
					calendar = MatchTime.getCalendarByCycle(calendar, "COOD");
					continue;
				}
				for (File file : allFilesList) {
					if (!redisService.exists(file.getName())) {
						redisService.set(file.getName(), file.getAbsoluteFile(),24*3600*5L);
						inputFiles.add(file.getAbsolutePath());
					}

				}
				
				if(inputFiles.size() ==0){
					calendar = MatchTime.getCalendarByCycle(calendar, "COOD");
					continue;
				}
				// 放入缓存 防止同一时间执行同一期次,不然输入xml会覆盖
				redisService.add(projectKey	+ issue, issue);

				String outLogPath = logPath + File.separator + projectKey
						+ issue + ".log";
				FileOperate.newParentFolder(outLogPath);
				String outputXml = xmlPath + File.separator + "outputXml"
						+ File.separator + issue + File.separator + issue
						+ ".xml";
				FileOperate.newParentFolder(outputXml);

				// 制作xml参数
				Map<String, Object> inputxmlParam = new HashMap<>();

				inputxmlParam.put("gdal_translate", gdalTranslate);
				inputxmlParam.put("areaID", areaID);
				inputxmlParam.put("issue", issue);
				inputxmlParam.put("cycle", cycle);
				inputxmlParam.put("inputFile", String.join(",", inputFiles));
				inputxmlParam.put("outFolder", outFolder);
				inputxmlParam.put("outXMLPath", outputXml);
				inputxmlParam.put("outLogPath", outLogPath);

				Gson gson = new Gson();
				String inputParam = gson.toJson(inputxmlParam);
				if (!list.contains(inputParam)) {
					list.add(inputParam);
				}
				calendar = MatchTime.getCalendarByCycle(calendar, "COOD");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 释放redis
			if (redisService.exists(projectKey + issue)) {
				redisService.remove(projectKey + issue);
			}
		}
		if (!result.isSuccess()) {
			return result;
		}
		result.setResult(list);
		if (list.size() < 1) {
			result.setMessage("本次调度没有需要处理的数据，期次是" + issue);
		}
		/** 返回结果 **/
		return result;
	}

	private List<XmlDTO> FormatXmlParam(Map<String, String> map) {
		List<XmlDTO> inputList = new ArrayList<>();
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
	
	/*
	 * 把缓存的文件去除
	 */
	public void removeRedisFiles(String inputFile) {
		if (StringUtils.isNoneEmpty(inputFile)) {
			String[] inputFiles = inputFile.split(",");
			for (String file : inputFiles) {
				redisService.remove(new File(file).getName());
			}

		}
	}
}

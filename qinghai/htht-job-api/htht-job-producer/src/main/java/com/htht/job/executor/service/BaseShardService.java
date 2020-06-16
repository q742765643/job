package com.htht.job.executor.service;

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
import org.dom4j.Element;
import org.htht.util.Consts;
import org.htht.util.DateUtil;
import org.htht.util.FileOperate;
import org.htht.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

/**
 * 分片方式实现的调度类 任务必须参数有，
 * 
 * 输出xml必须含有的参数， areaID：行政区划编号； issue：产品期次 cycle：产品周期
 * inputFile：输入目录或者文件集合或者文件绝度路径 outFolder：输出目录 outXMLPath：输出xml路径
 * outLogPath：输出log路径
 * 
 * @author zzp
 *
 */
public abstract class BaseShardService {
	
	private static Logger logger = LoggerFactory.getLogger(BaseShardService.class.getName());
	
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
	private ProductFileInfoService productFileInfoService;
	
	@Autowired
	private ProductService productService;

	/**
	 * 1. 根据各自逻辑把inputFile赋值 2. 根据各自算法把不同的xml信息赋值
	 * 
	 * @param issue
	 * @param dymap
	 * @param metaDateMap
	 * @return
	 */
	protected abstract Map<String, Object> initXmlParam(String issue,
			LinkedHashMap dymap, Map<String, Object> inputxmlParam);

	/**
	 * 根据xml信息把结果入库、存放redis、不存库
	 * 
	 * @param triggerParam
	 * @param inputXmlParam
	 * @return
	 */
	protected abstract boolean saveProduct(TriggerParam triggerParam,
			Map<String, String> inputXmlParam);

	/**
	 * 统一清理缓存
	 * 
	 * @param inputXmlParam
	 * @return
	 */
	protected abstract void cleanRedis(Map<String, String> inputXmlParam);

	/**
	 * 分片后执行调度算法
	 * 
	 * @param triggerParam
	 * @param result
	 * @return
	 */
	public ResultUtil<String> execute(TriggerParam triggerParam,
			ResultUtil<String> result) {

		/** 1.获取参数列表 **/
		LinkedHashMap<?, ?> fixmap = triggerParam.getFixedParameter();
		/** 2.解析产品参数 **/
		String exePath = (String) fixmap.get("exePath");
		String outputlogpath = triggerParam.getLogFileName();
		String projectKey = (String) fixmap.get("projectKey");
		XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);

		// 制作输入xml
		String paramStr = triggerParam.getExecutorParams();
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> inputXmlParam = gson.fromJson(paramStr, type);
		String outputXml = inputXmlParam.get("outXMLPath");
		String inputXml = outputXml.replace("outputXml", "inputXml");
		String issue = inputXmlParam.get("issue");
		String cycle = inputXmlParam.get("cycle");
		String areaID = inputXmlParam.get("areaID");

		//判断产品是否执行过
		String algorId = triggerParam.getAlgorId();
		AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(algorId);
		List<ProductInfo> pis = new ArrayList<ProductInfo>();
		//用于一次调度制作多级产品
		Product productNow = productService.findById(triggerParam.getProductId());
		List<Product> productList = productService.getProductsByParentId(productNow.getTreeId());
		if(productList.size() > 0){
			for(Product pd : productList){
				pis = productInfoService.findProductExits(pd.getId(), issue, cycle,	at.getModelIdentify(), null, null);
				if(pis.size() > 0){
					break;
				}
			}
		}else{
			pis = productInfoService.findProductExits(triggerParam.getProductId(), issue, cycle,	at.getModelIdentify(), null, null);
		}
		if(pis.size() > 0){
			result.setErrorMessage("产品已经制作过" + cycle + "_" + issue);
			XxlJobLogger.logByfile(outputlogpath, "产品已生产" + cycle + "_" + issue);
			this.cleanRedis(inputXmlParam);
			return result; 
		}
		
		String logId = "XxlJobLog" + triggerParam.getLogId();
		StringBuffer sb = new StringBuffer();
		sb.append(productNow.getName());
		sb.append(",");
		
		sb.append("|");
		sb.append(issue);
		sb.append("|");
		redisService.set(logId, sb.toString(),24*3600*1L);

		
		List<XmlDTO> inputList = formatXmlParam(inputXmlParam);
		XmlUtils XmlUtils = new XmlUtils();
		XmlUtils.createAlgorithmXml(inputXmlParam.get("projectKey"), inputList,
				new ArrayList<XmlDTO>(), inputXml);
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

				this.cleanRedis(inputXmlParam);

				return result;
			}
			XxlJobLogger.logByfile(outputlogpath, "开始读取输出xml文件" + outputXml);

			if (!XmlUtils.isSuccessByXml(outputXml)) {
				result.setErrorMessage("outputxml显示算法失败" + outputXml);
				XxlJobLogger.logByfile(outputlogpath, "算法执行失败，执行的算法为："
						+ exePath + "参数为：" + inputXml);

				this.cleanRedis(inputXmlParam);

				return result;
			}

			XxlJobLogger.logByfile(outputlogpath, "算法执行成功，准备入库");
			// 调用子类入库
			this.saveProduct(triggerParam, inputXmlParam);

			XxlJobLogger.logByfile(outputlogpath, "算法执行成功，开始入库");
			XxlJobLogger.logByfile(outputlogpath, "执行成功");
			result.setResult("成功");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			result.setErrorMessage("出现异常");
			XxlJobLogger.logByfile(outputlogpath, issue + "期次出现异常");

			this.cleanRedis(inputXmlParam);

			throw new RuntimeException();
		}
		result.setMessage("执行完毕");
		this.cleanRedis(inputXmlParam);
		return result;

	}

	/**
	 * 执行分片前的数据判断
	 * 
	 * @param params
	 * @param fixmap
	 * @param dymap
	 * @return
	 */
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

			String areaID = (String) dymap.get("areaID");
			String outputPath = (String) dymap.get("outputPath");
			String inputPath = (String) dymap.get("inputPath");
			String cycle = "COOD";
			if (dymap.containsKey("cycle")) {
				cycle = (String) dymap.get("cycle");
			}

			// 默认设置要处理的数据时间个数
			String dataTime = "{yyyy}{MM}{dd}{HH}{mm}";
			if (dymap.containsKey("dataTime") && StringUtils.isNotEmpty((String) dymap.get("dataTime"))) {
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
			//dataTime如果是固定值，重新给结束时间赋值
			if(StringUtils.isNumeric(dataTime)){
				doEndTime = calendar.getTime();
			}
			while (calendar.getTimeInMillis() <= doEndTime.getTime()) {

				issue = MatchTime.matchIssue(calendar.getTime(), cycle);

				// 缓存中已经存在，就进行下一个期次
				if (redisService.exists(projectKey + issue)) {
					calendar = MatchTime.getCalendarByCycle(calendar, cycle);
					continue;
				}
				String filePath = inputPath;
				if (outputPath.indexOf("{") > -1
						&& outputPath.indexOf("}") > -1) {
					filePath = DateUtil.getPathByDate(inputPath,
							calendar.getTime());
				}

				// 这个目录不存在，则进行下一个期次
				if (!new File(filePath).exists()) {
					calendar = MatchTime.getCalendarByCycle(calendar, cycle);
					continue;
				}
				
				String outLogPath = logPath + File.separator + projectKey
						+ issue + ".log";
				FileOperate.newParentFolder(outLogPath);
				String outXMLPath = xmlPath + File.separator + "outputXml"
						+ File.separator + issue + File.separator + projectKey
						+ "_" + issue + ".xml";
				FileOperate.newParentFolder(outXMLPath);

				// 制作xml参数
				Map<String, Object> inputxmlParam = new HashMap<String, Object>();
				inputxmlParam.put("areaID", areaID);
				inputxmlParam.put("issue", issue);
				inputxmlParam.put("cycle", cycle);
				inputxmlParam.put("outXMLPath", outXMLPath);
				inputxmlParam.put("outLogPath", outLogPath);
				inputxmlParam.put("outFolder", outputPath);
				inputxmlParam.put("projectKey", projectKey);

				// 调用子类，把动态参数中的不统一数据添加到inputxmlParam中，和需要处理的数据添加到inputxmlParam中
				inputxmlParam = this.initXmlParam(issue, dymap, inputxmlParam);

				if (!inputxmlParam.containsKey("inputFile")
						|| StringUtils.isEmpty((String) inputxmlParam
								.get("inputFile"))) {
					calendar = MatchTime.getCalendarByCycle(calendar, cycle);
					continue;
				}
				// inputxmlParam.put("inputFile", String.join(",", inputFiles));

				Gson gson = new Gson();
				String inputParam = gson.toJson(inputxmlParam);
				if (!list.contains(inputParam)) {
					list.add(inputParam);
				}
				calendar = MatchTime.getCalendarByCycle(calendar, cycle);
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

	/**
	 * 格式化xml
	 * 
	 * @param map
	 * @return
	 */
	private List<XmlDTO> formatXmlParam(Map<String, String> map) {
		List<XmlDTO> inputList = new ArrayList<>();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			if("projectKey".equals(key)){
				continue;
			}
			XmlDTO inputFileXmlDTO = new XmlDTO();
			inputFileXmlDTO.setIdentify(key);
			inputFileXmlDTO.setValue(map.get(key));
			inputFileXmlDTO.setDescription(" ");
			inputFileXmlDTO.setType("string");
			inputList.add(inputFileXmlDTO);
		}
		return inputList;
	}

	/**
	 * 数据入库，包括productInfo productFileInfo 无统计表
	 * 
	 * @param triggerParam
	 * @param inputXmlParam
	 * @return
	 */
	public boolean saveProductOne(TriggerParam triggerParam,
			Map<String, String> inputXmlParam) {
		String issue = inputXmlParam.get("issue");
		String cycle = inputXmlParam.get("cycle");
		String outputXml = inputXmlParam.get("outXMLPath");
		String inputXml = outputXml.replace("outputXml", "inputXml");
		String algorId = triggerParam.getAlgorId();
		AtomicAlgorithm at = atomicAlgorithmService
				.findModelIdentifyById(algorId);
		DictCode productPath = dictCodeService.findOneself("productPath");

		XmlUtils XmlUtils = new XmlUtils();
		
		//读取xml，把xml转换成map对象
		Map<String,List<Element>> map = XmlUtils.outputFilesXmlToMap(outputXml);
		logger.info("outputxml: " + outputXml );
		List<String> regionIdList = XmlUtils.getXmlAttrVal( map, "region", "identify");
		String mosaicFile = "";
		
		//用于一次调度制作多级产品
		Product productNow = productService.findById(triggerParam.getProductId());
		List<Product> productList = productService.getProductsByParentId(productNow.getTreeId());
		//只有一个产品
		if (productList.size() < 2) {
			if(map.containsKey("mosaicFile")){
				List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(map,
						"mosaicFile");
				if (mosaicFiles.size() > 0) {
					mosaicFile = mosaicFiles.get(0);
				}
			}
			ProductInfo productInfo = null;
			String modelIdentify = at.getModelIdentify();

			if (regionIdList != null && regionIdList.size() > 0) {
				for (String regionId : regionIdList) {
					// 查重 并删除
					List<ProductInfo> pis = productInfoService.findProductExits(
							triggerParam.getProductId(), issue, cycle,
							at.getModelIdentify(), null, regionId);
					for (ProductInfo pi : pis) {
						productInfoService.deleteProductInfo(pi.getId());
						// 查重
						productFileInfoService.deleteByproductInfoId(pi.getId());
					}
					// 产品信息及文件信息入库
					productInfo = productUtil.saveProductInfo(
							triggerParam.getProductId(), regionId, issue, cycle,
							mosaicFile, at.getModelIdentify(), inputXml);


					List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(map,
							"region", "identify", regionId);
					for (String file : lFiles) {
						productUtil.saveProductInfoFile(productInfo.getId(),
								file.replace("\\", "/"), productPath.getDictCode(),
								regionId, issue, cycle);
					}

				}
				// 产品结果信息入库
				List<Element> xmllists = XmlUtils.getTablenameElements(outputXml, "table");

				
				if (null == xmllists || xmllists.size() == 0) {
					 logger.info("统计表信息为空，不进行统计入库 ;");
				} else {
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
						String[] fields = (e.element("field").getText() + ",product_info_id,Cycle,model_identify,file_name")
								.replace("'", "").trim().split(",");
						String otherValue = "," + productInfo.getId() + "," + cycle
								+ "," + modelIdentify + "," + inputXml;

						for (int i = 0; i < fields.length; i++) {

							for (String s : cc) {

								String[] values = (s + otherValue).replace("'", "")
										.replace(",", " ").trim().split("\\s+");
								productAnalysisTableInfo.addFieldAndValue(
										fields[i].trim(), values[i]);
							}
						}
						productUtil.saveProductDetail(productAnalysisTableInfo);
					}
				}
			}
			
		}else {
			for(Product product :productList){
				
				String bz = product.getBz();
				if (null==bz || "".equals(bz)) {
					continue;
				}
				ProductInfo productInfo = new ProductInfo();
				for (String regionId : regionIdList) {
					// 查重 并删除
					List<ProductInfo> pis = productInfoService.findProductExits(
							product.getId(), issue, cycle,
							at.getModelIdentify(), null, regionId);
					for (ProductInfo pi : pis) {
						productInfoService.deleteProductInfo(pi.getId());
						// 查重
						productFileInfoService.deleteByproductInfoId(pi.getId());
					}
					List<String> mosaics = XmlUtils.getXmlAttrFileElementVal(map, "mosaicFile");
					for (String mosaic : mosaics) {
						if (mosaic.contains(bz)) {
							productInfo = productUtil.saveProductInfo( product.getId(), regionId, issue, cycle, mosaic, at.getModelIdentify(), inputXml);
							break;
						} 
					}
					
					List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(map, "region","identify",regionId);
					for (String file : lFiles) {
						if (file.contains(bz)) {
							productUtil.saveProductInfoFile(productInfo.getId(), file.replace("\\", "/"), productPath.getDictCode(), regionId, issue, cycle);
						}
					}
				}
			}
		}
		return true;
	}


	
	/**
	 * 
	 * @param key
	 *            = projectKey + issue
	 */
	public void cleanRedisComm(String key) {
		// 释放redis
		if (redisService.exists(key)) {
			redisService.remove(key);
		}
	}

	public void cleanRedisByInputFile(String key, String inputFile) {
		// 释放redis
		if (redisService.exists(key)) {
			redisService.remove(key);
			removeRedisFiles(inputFile);
		}
	}
	/*
	 * 把缓存的文件去除
	 */
	public void removeRedisFiles(String inputFile ) {
		if (StringUtils.isNoneEmpty(inputFile)) {
			String[] inputFiles = inputFile.split(",");
			for (String file : inputFiles) {
				redisService.remove(new File(file).getName());
			}

		}
	}
}

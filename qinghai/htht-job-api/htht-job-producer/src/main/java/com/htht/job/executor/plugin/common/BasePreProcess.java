package com.htht.job.executor.plugin.common;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.axis2.databinding.types.Day;
import org.htht.util.DateUtil;
import org.htht.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.PreProcessConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import com.htht.job.executor.model.preprocess.PreProcess;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.preprocess.PreProcessService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.util.XmlUtils;

public abstract class BasePreProcess {
	
	private static Logger logger = LoggerFactory.getLogger(BasePreProcess.class.getName());
	
	@Autowired
	private ProductService productService;

	@Autowired
	private PreProcessService preProcessService;
	
	@SuppressWarnings("rawtypes")
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) throws Exception
	{
		logger.info("enter method with executorParams = "+triggerParam.getExecutorParams()+" ;");
		LinkedHashMap dymap = triggerParam.getDynamicParameter();
		
		ProductParam modelParam = null;
		try {
			modelParam = JSON.parseObject(triggerParam.getModelParameters(), ProductParam.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//产品
		String productId = triggerParam.getProductId();
		Product product = productService.findById(productId);
		
		//获取输入参数
		String inputPath = (String) dymap.get("inputPath");
		String outputPath = (String) dymap.get("outputPath");
		String issueFormat = (String) dymap.get("issue");
		String outputLogPath = (String) dymap.get("outputlog");
		
		//获取模型参数
		String exePath =  modelParam.getExePath();
		String inputxml = modelParam.getInputxml();
		
		//根据上述参数获取额外一些所需参数
		String type = product.getMark();
		String cycle = product.getCycle();

		//获取时间期号作为产品编号
		String issueExe = triggerParam.getExecutorParams();
		List<String> issuees = new ArrayList<String>();
		if (null == issueExe || "".equals(issueExe)) {
			// 非分片广播方式
			HashMap<String, Date> rangeDate = this.getRangeDate(modelParam, dymap, cycle);
			Date doStartTime = rangeDate.get("doStartTime");
			Date doEndTime = rangeDate.get("doEndTime");
			String fileFormat = "";
			fileFormat = (String) dymap.get("fileFormat");
			if (null == fileFormat || "".equals(fileFormat)) {
				fileFormat = null;
			}
			issuees = this.getIssuees(productId, inputPath, doStartTime, doEndTime, issueFormat, fileFormat);
		} else {
			// 分片广播方式
			issuees.add(issueExe);
		}
		
		//组装outputXML路径
		File outputDir = new File(outputPath);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		String outputXMLPath = outputDir.getPath()+"\\outputXML\\";
		File outputXMLDir = new File(outputXMLPath);
		if (!outputXMLDir.exists()) {
			outputXMLDir.mkdirs();
		}
		
		//组装outputLog路径
		File outputLogDir = new File(outputLogPath);
		if (!outputLogDir.exists()) {
			outputLogDir.mkdirs();
		}
		
		for (String issue : issuees) {
			String issue12 = changeIssueToIssue12(issue);
			
			/** =======1.拼装dymap=========== **/
			String outputBaseXml = outputXMLDir.getPath() + File.separator ;
			String outputLog = outputLogDir.getPath() + File.separator+ type + "_" + issue12 + ".log";
			String inputxmlpath = inputxml  + type+ "_" + issue12 + ".xml";
			
			/** =======2.创建日志文件=========== **/
			XxlJobFileAppender.makeLogFileNameByPath(outputLog);
			// 回调需要用到
			triggerParam.setLogFileName(outputLog);
			PreProcess preProcess = preProcessService.findProcessByProductIdAndIssue(productId, issue12);
			
			if (null != preProcess) {
				/** =======3.判断是否需要执行当前任务=========== **/
				boolean status = checkPreProcessStatus(preProcess);
				if (!status) {
					XxlJobLogger.logByfile(outputLog, "There was no need to execute algorithm with productId = " + productId + "and issue = " +  issue12);
					result.setMessage("already done");
					return result;
				}
			}else {
				preProcess = new PreProcess();
				preProcess.setCreateTime(new Date());
				preProcess.setIssue(issue12);
				preProcess.setMark(product.getName());
				preProcess.setProductId(productId);
			}
			
			/** =======4.生成log文件=========== **/
			XxlJobLogger.logByfile(outputLog, "make input_xml file");


			Map<String, Object> inputMap = this.getInputParam(triggerParam,issue);
			//创建临时文件夹目录，统一路径
			String tempPath = (String) inputMap.get("tempPath");
			if (null == tempPath || "".equals(tempPath)) {
				tempPath = "E:\\data\\tmp\\";
			}
			if(!tempPath.endsWith(File.separator)){
				tempPath +=File.separator;
			}
			File tmpPath = new File(tempPath + issue);
			if (!tmpPath.exists()) {
				tmpPath.mkdirs();
			}
			inputMap.replace("tempPath", tmpPath.getAbsolutePath());
			
			Map<String, Object> outputMap = new HashMap<>();
			Map<String, String> outputXmlMap =new HashMap<>();
			outputMap.put("outLogPath", outputLog);
			File outputxmlBaseDir = new File(outputBaseXml + issue12);
			if (!outputxmlBaseDir.exists()) {
				outputxmlBaseDir.mkdirs();
			}
			String outputxml = outputxmlBaseDir.getAbsolutePath() + File.separator + product.getMark() + "_" + issue12 + ".xml" ;
			outputXmlMap.put(product.getId(), outputxml);
			outputMap.put("outXMLPath",outputxml );
			outputMap.put("outFolder", outputPath);
			
			List<XmlDTO> inputList = this.changeMapToList(inputMap);
			List<XmlDTO> outputList = this.changeMapToList(outputMap);

			XmlUtils XmlUtils = new XmlUtils();
			XmlUtils.createAlgorithmXml(type,inputList,outputList,inputxmlpath);
			
			/** =======5.执行脚本=========== **/
			XxlJobLogger.logByfile(outputLog, "start executing algorithm");
			preProcess.setStatus(PreProcessConstant.STATUS_PROCESSING);
			preProcess.setExecuteTime(new Date());
			preProcess = preProcessService.savePreProcess(preProcess);
			ServerImpUtil.executeCmd(exePath, inputxmlpath);
			
			/** ========6.脚本 结束入库======= **/
			
			Set<Entry<String, String>> outputXmlEntrySet = outputXmlMap.entrySet();
			
			for (Entry<String, String> entry : outputXmlEntrySet) {
				String outputxmlTemp = entry.getValue();
				File outputxmlFile = new File(outputxmlTemp);
				if (!outputxmlFile.exists()||!outputxmlFile.isFile()) {
					result.setErrorMessage("no file with outputxml，maybe something was wrong in execution");
					XxlJobLogger.logByfile(outputLog, "no file with outputxml，maybe something was wrong in execution");
					preProcess.setStatus(PreProcessConstant.STATUS_FAIL);
					preProcess.setUpdateTime(new Date());
					preProcess = preProcessService.savePreProcess(preProcess);
					return result;
				}
			
				boolean b = XmlUtils.isSuccessByXml(outputxmlTemp);
				if (!b) {
					String failedInfo = XmlUtils.getFailedInfo(outputxmlTemp);
					result.setErrorMessage("Algorithm execution return：" + failedInfo);
					XxlJobLogger.logByfile(outputLog, "Algorithm execution return：" + failedInfo);
					preProcess.setStatus(PreProcessConstant.STATUS_FAIL);
					preProcess.setUpdateTime(new Date());
					preProcess = preProcessService.savePreProcess(preProcess);
					return result;
				}
				
			}
		preProcess.setStatus(PreProcessConstant.STATUS_SUCCESS);
		preProcess.setUpdateTime(new Date());
		preProcess = preProcessService.savePreProcess(preProcess);
		result.setResult("成功");
		if (!result.isSuccess()) {
			preProcess.setStatus(PreProcessConstant.STATUS_FAIL);
			preProcess.setUpdateTime(new Date());
			preProcess = preProcessService.savePreProcess(preProcess);
			result.setErrorMessage("Inbound error");
			return result;
		   }
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private List<XmlDTO> changeMapToList(Map<String, Object> inputMap) {
		List<XmlDTO>  inputList = new ArrayList<>();
		Iterator iter = inputMap.entrySet().iterator();
		while (iter.hasNext()) {
			XmlDTO to = new XmlDTO();
			Map.Entry entry = (Map.Entry) iter.next();
			to.setIdentify(entry.getKey().toString());
			to.setValue(entry.getValue().toString());
			to.setDescription(entry.getKey().toString());
			to.setType("string");
			inputList.add(to);
		}
		return inputList;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap<String, Date> getRangeDate(ProductParam productParam, Map dymap,String cycle) throws Exception {

		HashMap<String, Date> rangeDate = new HashMap<String, Date>();
		Date doStartTime = null;
		Date doEndTime = null;
		if (null == cycle || "".equals(cycle) ) {
			cycle = "COOD";
		}
		
		Calendar c = Calendar.getInstance();
		if (String.valueOf(BusinessConst.PROCESSTASKPRODUCT_DATETYPE_REAL_TIME).equals(productParam.getDateType())) {
			// 实时
			String dateFormatStr = "yyyyMMddHHmm";
			String issueStr = (String) dymap.get("prd_date");
			if ("".equals(issueStr) || null == issueStr) {
				issueStr = (String) dymap.get("issue");
			}

			String issue = MatchTime.matchIssue(issueStr,cycle);
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
			Date issueDate = sdf.parse(issue);
			c.setTime(issueDate);
			doEndTime = c.getTime();
			if (null != productParam.getProductRangeDay() && !"".equals(productParam.getProductRangeDay())) {
				c.add(Calendar.DATE, -(Integer.parseInt(productParam.getProductRangeDay())));
			}
			doStartTime = c.getTime();
		} else if (String.valueOf(BusinessConst.PROCESSTASKPRODUCT_DATETYPE_HISTORICAL_TIME)
				.equals(productParam.getDateType())) {
			// 历史
			String[] temp = productParam.getProductRangeDate().split(" - ");
			String stratTime = temp[0];
			String endTime = temp[1]+"235959";
			
			String stratTimePattern = this.timeToPattern(stratTime);
			String endTimePattern = this.timeToPattern(endTime);
			doStartTime = DateUtil.getDate(stratTime.replace("-", "").replace(":", "").replace(" ", ""),
					stratTimePattern);
			doEndTime = DateUtil.getDate(endTime.replace("-", "").replace(":", "").replace(" ", ""), endTimePattern);
		}
		rangeDate.put("doStartTime", doStartTime);
		rangeDate.put("doEndTime", doEndTime);

		return rangeDate;
	}
	
	public String timeToPattern(String time) {
		String timeStr = time.replace("-", "").replace(" ", "").replace(":", "");
		String timePattern = null;
		switch (timeStr.length()) {
		case 8:
			timePattern = "yyyyMMdd";
			break;
		case 10:
			timePattern = "yyyyMMddHH";
			break;
		case 12:
			timePattern = "yyyyMMddHHmm";
			break;
		case 14:
			timePattern = "yyyyMMddHHmmss";
			break;
		default:
			break;
		}
		return timePattern;
	}
	
	public String changeIssueToIssue12(String issue){
		
		String issue12 = issue;
		if (issue12.length() < 12) {
			for (int i = issue12.length(); i < 12; i++) {
				issue12 = issue12 + "0";
			}
		}
		if(issue12.contains("_")) {
			String[] issue1=issue12.split("_");
			issue12=issue1[0]+issue1[1];
		}
		return issue12;
	
	}
	
	/**
	 *  根据算法需求获取issue集合
	 * @param ptoductId 产品Id
	 * @param inputPath 输入路径（可能需要输入路径去获取相关issue）
	 * @param doStartTime 开始时间
	 * @param doEndTime	结束时间
	 * @param issueFormat 期号格式
	 * @return List<String> 期号集合
	 */
	public abstract List<String> getIssuees(String ptoductId, String inputPath, Date doStartTime, Date doEndTime, String issueFormat , String fileFormat) throws IOException;

	/**
	 * 获取算法所需xml中的input值
	 * @param triggerParam 
	 * @param issue12 期号
	 * @return 
	 */
	public abstract Map<String, Object> getInputParam(TriggerParam triggerParam, String issue)throws IOException;
	
	/**
	 * 判断是否需要执行该期预处理任务
	 * 		true: 需要执行
	 * 		false:不需要执行
	 * @param PreProcess
	 * @return boolean 
	 * 		true: 需要执行
	 * 		false:不需要执行
	 * 	
	 */
	public boolean checkPreProcessStatus(PreProcess preProcess){

		if (null == preProcess) {
			return true;
		}
		logger.info("current preProcess name = " + preProcess.getMark() + "and ProductId = " + preProcess.getProductId() + "and issue = " +  preProcess.getIssue());
		if (PreProcessConstant.STATUS_SUCCESS.equals(preProcess.getStatus())) {
			logger.info("There was no need to execute algorithm with status = " + preProcess.getStatus());
			return false;
		}
		
		if (PreProcessConstant.STATUS_PROCESSING.equals(preProcess.getStatus())) {
			logger.info("current status was " + preProcess.getStatus());
			Calendar calendar = Calendar.getInstance();
			Date currentTime = calendar.getTime();
			calendar.setTime(preProcess.getExecuteTime());
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			Date tempTime = calendar.getTime();
			if (tempTime.before(currentTime)) {
				preProcess.setStatus(PreProcessConstant.STATUS_STOP);
				preProcessService.savePreProcess(preProcess);
				logger.info("executed time out, forced stopped this task ,please restart after an hour" );
				return true;
			}
			return false;
		}
		
		return true;
		
	} 
	

}

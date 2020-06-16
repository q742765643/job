package com.htht.job.executor.hander.h8.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.htht.util.ServerImpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.productfileinfo.ProductFileInfo;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.CImissMatchTime;
import com.htht.job.executor.util.XmlUtils;

@Transactional
@Service("SnowH8KeepDaysHandlerService")
public class SnowH8KeepDaysHandlerService {
	
	private static final String ISSUE_SUFFIX = "1400";
	private static final String FR_Start_date = "20191001";
	// 积雪季
	private static final String SnowDepthDay_H8_SQ = "SnowDepthDay_H8_SQ";
	// 月周期
	private static final String SnowDepthDay_H8_SM = "SnowDepthDay_H8_SM";
	// 自定义
	private static final String SnowDepthDay_H8_SC = "SnowDepthDay_H8_SC";
	// 邻近积雪维持日数
	private static final String SnowDepthRecentDay_H8 = "SnowDepthRecentDay_H8";

	private static Logger logger = LoggerFactory.getLogger(SnowH8KeepDaysHandlerService.class.getName());
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private ProductFileInfoService productFileInfoService;
	
	@Autowired
	private AtomicAlgorithmService atomicAlgorithmService;
	
	@Autowired
	private DictCodeService dictCodeService;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private ProductUtil productUtil;
	
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		String redisKey = null;
		try{
			//产品
			String productId = triggerParam.getProductId();
			Product product = productService.findById(productId);
			//根据上述参数获取额外一些所需参数
			String type = product.getMark();
			
			//算法标识
			String algorId = triggerParam.getAlgorId();
			AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(algorId);
			String modelIdentify = at.getModelIdentify();
			
			DictCode productPath = dictCodeService.findOneself("productPath");
					
			/** 1.获取参数列表 **/
			@SuppressWarnings("unchecked")
			LinkedHashMap<String,String> fixmap = triggerParam.getFixedParameter();
			
			String areaID = fixmap.get("areaID");
			String input = fixmap.get("inputXML");
			String inputFile = fixmap.get("inputFile");
			String output = fixmap.get("output");
			String cycle = fixmap.get("cycle");
			String exePath = fixmap.get("exePath");
			String assign_name = fixmap.get("assign_name");
			
			String startIssue = fixmap.get("startIssue");
			String endIssue = fixmap.get("endIssue");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			if(SnowDepthDay_H8_SQ.equalsIgnoreCase(type)){
				startIssue = FR_Start_date + ISSUE_SUFFIX;
				if(StringUtils.isNotBlank(endIssue)){
					date = sdf.parse(endIssue);
				}
				endIssue = getIssueByDate(date);
			}else if(SnowDepthDay_H8_SM.equalsIgnoreCase(type)){
				if(StringUtils.isNotBlank(endIssue)){
					date = sdf.parse(endIssue);
				}
				endIssue = getIssueByDate(date);
				startIssue = String.format("%tY", date) + String.format("%tm", date)
						+ "01" + ISSUE_SUFFIX;
			}else if(SnowDepthRecentDay_H8.equalsIgnoreCase(type)){
				if(StringUtils.isNotBlank(endIssue)){
					date = sdf.parse(endIssue);
				}
				endIssue = getIssueByDate(date);
				startIssue = String.format("%tY", date) + "0101" + ISSUE_SUFFIX;
				
			}else{
				if(StringUtils.isNotBlank(startIssue)){
					date = sdf.parse(startIssue);
				}
				startIssue = getIssueByDate(date);
				if(StringUtils.isNotBlank(endIssue)){
					date = sdf.parse(endIssue);
				}
				endIssue = getIssueByDate(date);
				endIssue = startIssue.replace(ISSUE_SUFFIX, "") + " - " + endIssue.replace(ISSUE_SUFFIX, "");
			}
			
			String issue = endIssue;
			
			List<ProductInfo> productFile = productInfoService.findProductExits(product.getId(), issue, cycle,
					at.getModelIdentify(), "", areaID);
			if (null!=productFile && !productFile.isEmpty()) {
				result.setMessage("产品已生产");
				logger.info(this.getClass().getSimpleName() + "@@" + issue);
				return result;
			}
			
			
			//组装outputXML路径
			output = output + File.separator + type;
			File outputDir = new File(output);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			String outputXMLPath = output + "\\outputXML";
			File outputXMLDir = new File(outputXMLPath);
			if (!outputXMLDir.exists()) {
				outputXMLDir.mkdirs();
			}
			
			String outputLogPath = output + "\\log";
			//组装outputLog路径
			File outputLogDir = new File(outputLogPath);
			if (!outputLogDir.exists()) {
				outputLogDir.mkdirs();
			}
			redisKey = type + "@@" + issue;
			if(redisService.exists(redisKey)){
				logger.info("redis中存在：" + redisKey);
				return result;
			}else{
				redisService.add(redisKey,redisKey);
			}
			
			/** =======1.拼装dymap=========== **/
			String outXMLPath = outputXMLPath + File.separator + issue + ".xml";
			String outputLog = outputLogPath + File.separator + issue + ".log";
			String inputxmlpath = input  + File.separator + type + File.separator  + issue + ".xml";
			
			
			/** =======2.创建日志文件=========== **/
			XxlJobFileAppender.makeLogFileNameByPath(outputLog);
			// 回调需要用到
			triggerParam.setLogFileName(outputLog);
			
			String logId = "XxlJobLog" + triggerParam.getLogId();
			StringBuffer sb = new StringBuffer();
			sb.append(product.getName());
			sb.append(",");
			
			sb.append("|");
			sb.append(issue);
			sb.append("|");
			redisService.set(logId, sb.toString(),24*3600*1L);
			
			XxlJobLogger.logByfile(outputLog, "开始执行getInputParam，期号为：" + issue);
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("areaID", areaID);
			inputMap.put("issue", endIssue);
			inputMap.put("issue1", startIssue);
			inputMap.put("cycle", cycle);
			inputMap.put("inputFile", inputFile);
			if(SnowDepthDay_H8_SC.equalsIgnoreCase(type)){
				inputMap.put("CustomLevel", fixmap.get("CustomLevel"));
			}
			
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put("outFolder", output);
			outputMap.put("outXMLPath", outXMLPath);
			outputMap.put("outLogPath", outputLog);
			outputMap.put("assign_name", assign_name);
			
			
			
			List<XmlDTO> inputList = this.changeMapToList(inputMap);
			List<XmlDTO> outputList = this.changeMapToList(outputMap);

			XmlUtils XmlUtils = new XmlUtils();
			XmlUtils.createAlgorithmXml(type,inputList,outputList,inputxmlpath);

			/** =======5.执行脚本=========== **/
			XxlJobLogger.logByfile(outputLog, "正在执行_运行");
			ServerImpUtil.executeCmd(exePath, inputxmlpath);
			
			boolean b = XmlUtils.isSuccessByXml(outXMLPath);
			if (!b) {
				result.setErrorMessage("产品生产失败");
				XxlJobLogger.logByfile(outputLog, "输出xml为空，生产失败");
				redisService.remove(redisKey);
				return result;
			}
			
			XxlJobLogger.logByfile(outputLog, "正在执行_入库");
			
			Map<String,List<Element>> map = XmlUtils.outputFilesXmlToMap(outXMLPath);
			logger.info("outputxml: " + outXMLPath );
			List<String> regionIdList = XmlUtils.getXmlAttrVal( map, "region", "identify");
			List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal( map, "mosaicFile");
			String mosaicFile = "";

			if (null == mosaicFiles|| mosaicFiles.size() == 0) {
				logger.info("没有mosaicFiles,算法处理异常，不予入库");
				result.setErrorMessage("没有mosaicFiles,算法处理异常，不予入库");
				redisService.remove(redisKey);
				return result;
			}else if(mosaicFiles.size() > 1){//选择镶嵌数据集最小的日期
				String mindata=getMinDate(mosaicFiles)+"";
				for(String mosaicfile:mosaicFiles) {
					if(mosaicfile.contains(mindata)) {
						mosaicFile=mosaicfile;
					}
				}
			}else if(mosaicFiles.size() == 1){
				mosaicFile = mosaicFiles.get(0);
			}
			// 产品信息及文件信息入库
			ProductInfo productInfo = new ProductInfo();
			if(regionIdList!=null&&regionIdList.size()>0) {
				for(String region:regionIdList) {
					productInfo = productUtil.saveProductInfo( productId, region, issue, cycle, mosaicFile, at.getModelIdentify(), inputxmlpath);
					List<String> lFiles = XmlUtils.getXmlAttrFileElementVal( map, "region","identify",region);
					for (String file : lFiles) {
						productUtil.saveProductInfoFile(productInfo.getId(), file.replace("\\", "/"), productPath.getDictCode(), region, issue, cycle);
					}
				}
			}
			
			//产品结果信息入库
			XxlJobLogger.logByfile(outputLog, "正在执行_统计入库");
			List<Element> xmllists = XmlUtils.getTablenameElements(outXMLPath,"table");
			
	        if(null == xmllists|| xmllists.size() == 0) {
	        	logger.info("统计表信息为空，不进行统计入库 ;");
		     }else {
				for(Element e : xmllists){
					//获取数据集
					List<Element> list2 = e.elements();
					List<String> cc = new ArrayList<>();
					for(Element e2 : list2){
						if("values".equals(e2.getName())){
							cc.add(e2.getText());
						}
					}
					//封数据执行入库
					ProductAnalysisTableInfo productAnalysisTableInfo = new ProductAnalysisTableInfo(e.attribute("identify").getValue());
					String[] fields = (e.element("field").getText()+",product_info_id,Cycle,model_identify,file_name").replace("'","").trim().split(",");
					String otherValue = ","+productInfo.getId()+","+cycle+","+modelIdentify+","+inputxmlpath;

					for(int i=0;i<fields.length;i++){
						
						if ("WATER_MODIS".equals(type)) {
							for(String s : cc){
								String[] values = (s + otherValue + "Water").replace("'","").trim().split(",");
								try {
									productAnalysisTableInfo.addFieldAndValue(fields[i].trim(), values[i]);
								} catch (Exception e1) {
									System.out.println(values.toString());
									e1.printStackTrace();
								}
							}
							
						}else {
							for(String s : cc){
								
								String[] values = (s+otherValue).replace("'","").replace(","," ").trim().split("\\s+");
								productAnalysisTableInfo.addFieldAndValue(fields[i].trim(), values[i]);
							}
						}
					}
					productUtil.saveProductDetail(productAnalysisTableInfo);
	         }
		   }
	        XxlJobLogger.logByfile(outputLog, "数据入库成功");
		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(null!=redisKey){
				redisService.remove(redisKey);
			}
		}
		result.setMessage("执行调度完成！");
		return result;
	}


	private static String getIssueByDate(Date date) {
		String issueStr = String.format("%tY", date) + String.format("%tm", date)
					+ String.format("%td", date) + ISSUE_SUFFIX;
		return issueStr;
	}
	
	
	@SuppressWarnings("rawtypes")
	private List<XmlDTO> changeMapToList(Map<String, Object> inputMap) {
		List<XmlDTO>  inputList = new ArrayList<>();
		Iterator iter = inputMap.entrySet().iterator();
		while (iter.hasNext()) {
			XmlDTO to = new XmlDTO();
			Map.Entry entry = (Map.Entry) iter.next();
			if(null==entry.getKey() || null==entry.getValue()){
				continue;
			}
			to.setIdentify(entry.getKey().toString());
			to.setValue(entry.getValue().toString());
			to.setDescription(entry.getKey().toString());
			to.setType("string");
			inputList.add(to);
		}
		return inputList;
	}
	
	private int getMinDate(List<String> mosaicFiles) {
		int length=mosaicFiles.size();
		int[] issuedate=new int[length];
		int num=0;
		for(String mosaicFile:mosaicFiles) {
			List<String>  list = Arrays.asList(mosaicFile.split("_"));
			
			issuedate[num++]=Integer.parseInt(list.get(3).substring(4, 8));
			
		}
		BubbleSort(issuedate);
		return issuedate[0];
	}
	
	public static void BubbleSort(int[] arr) {
        int temp;//定义一个临时变量
        for(int i=0;i<arr.length-1;i++){//冒泡趟数
            for(int j=0;j<arr.length-i-1;j++){
                if(arr[j+1]<arr[j]){
                    temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }
}

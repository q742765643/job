package com.htht.job.executor.hander.producthandler.service;

import java.io.File;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;


@Transactional
@Service("grassTurningGreenHandlerService")
public class GrassTurningGreenHandlerService {
	
	private static Logger logger = LoggerFactory.getLogger(GrassTurningGreenHandlerService.class.getName());
	
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
	
	private static final String IssueDateFormat = "yyyy-MM-dd";

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
			
			DictCode productPath = dictCodeService.findOneself("productPath");
					
			/** 1.获取参数列表 **/
			@SuppressWarnings("unchecked")
			// 固定参数
			LinkedHashMap<String,String> fixmap = triggerParam.getFixedParameter();
			
			String input = fixmap.get("input");
			String output = fixmap.get("output");
			String exePath = fixmap.get("exePath");
			
			String areaID = fixmap.get("areaID");
			String cycle = fixmap.get("cycle");
			String inputFile = fixmap.get("inputFile");
			String fqdaysFolder = fixmap.get("fqdaysFolder");
			
			String startDateStr = fixmap.get("startDate");
			String endDateStr = fixmap.get("endDate");
			
			String necessary = fixmap.get("necessary");
			boolean isNecessary = "Y".equals(necessary);
			
			Date current = new Date();
			
			Date startDate = current;
			Date endDate = current;
			if(StringUtils.isNotBlank(startDateStr)){
				startDate = DateUtils.parseDate(startDateStr, IssueDateFormat);
			}
			if(StringUtils.isNotBlank(endDateStr)){
				endDate = DateUtils.parseDate(endDateStr, IssueDateFormat);
			}
			
			List<String> issueLst = getDaysBetween(startDate,endDate);
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
			
			String outputLog = outputLogPath + File.separator + System.currentTimeMillis() + ".log";
			/** =======2.创建日志文件=========== **/
			XxlJobFileAppender.makeLogFileNameByPath(outputLog);
			// 回调需要用到
			triggerParam.setLogFileName(outputLog);
			
			for(String issue:issueLst){
				
				issue = changeIssueToIssue12(issue);
				redisKey = type + "@@" + issue;
				if(redisService.exists(redisKey)){
					result.setMessage("redis中存在：" + redisKey);
					logger.info("redis中存在：" + redisKey);
					XxlJobLogger.logByfile(outputLog, "redis中存在：" + redisKey);
					return result;
				}
				
				XxlJobLogger.logByfile(outputLog, "开始执行getInputParam，期号为：" + issue);
				
				/** =======1.拼装dymap=========== **/
				String outXMLPath = outputXMLPath + File.separator + issue + ".xml";
				
				String inputxmlpath = input  + File.separator + type + File.separator  + issue + ".xml";
				
				// 是否前一天的产品已生产为必要条件判断
				if(isNecessary){
					String yesterdayissue = getYesterdayissue(issue);
					List<ProductInfo> productFileYesterday = productInfoService.findProductExits(product.getId(), yesterdayissue, cycle,
							at.getModelIdentify(), "", areaID);
					if (null==productFileYesterday || productFileYesterday.isEmpty()) {
						result.setErrorMessage("缺少"+yesterdayissue+"产品");
						XxlJobLogger.logByfile(outputLog, "缺少"+yesterdayissue+"产品");
						return result;
					}
				}
				
				List<ProductInfo> productFile = productInfoService.findProductExits(product.getId(), issue, cycle,
						at.getModelIdentify(), "", areaID);
				if (null!=productFile && !productFile.isEmpty()) {
					result.setMessage(issue+"产品已生产");
					XxlJobLogger.logByfile(outputLog, issue+"产品已生产");
					continue;
				}
				
				
				Map<String, Object> inputMap = new HashMap<String, Object>();
				inputMap.put("areaID", areaID);
				inputMap.put("issue", issue);
				inputMap.put("cycle", cycle);
				inputMap.put("inputFile", inputFile);
				
				Map<String, Object> outputMap = new HashMap<String, Object>();
				outputMap.put("outLogPath", outputLog);
				outputMap.put("outFolder", output);
				outputMap.put("outXMLPath", outXMLPath);
				outputMap.put("fqdaysFolder", fqdaysFolder);
				
				redisService.add(redisKey,redisKey);
				List<XmlDTO> inputList = this.changeMapToList(inputMap);
				List<XmlDTO> outputList = this.changeMapToList(outputMap);

				XmlUtils XmlUtils = new XmlUtils();
				XmlUtils.createAlgorithmXml(type,inputList,outputList,inputxmlpath);

				/** =======5.执行脚本=========== **/
				XxlJobLogger.logByfile(outputLog, "正在执行_运行");
				ServerImpUtil.executeCmd(exePath, inputxmlpath);
				
				boolean b = XmlUtils.isSuccessByXml(outXMLPath);
				if (!b) {
					result.setErrorMessage(issue+"产品生产失败");
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
				redisService.remove(redisKey);
		        XxlJobLogger.logByfile(outputLog, "数据入库成功");
				
			}
			
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

	
	
	private static String getYesterdayissue(String issue) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(issue.substring(0,8));
		Date yesterDay = DateUtils.addDays(date, -1);
		return changeIssueToIssue12(sdf.format(yesterDay));
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
	
	/**
     * @param cntDateBeg 开始时间
     * @param cntDateEnd 结束时间
     * @return
     */
	public static List<String> getDaysBetween(Date cntDateBeg, Date cntDateEnd) {
        List<String> list = new ArrayList<>();
        //拆分成数组
        //开始时间转换成时间戳
        Calendar start = Calendar.getInstance();
        start.set(Integer.valueOf(String.format("%tY", cntDateBeg)), Integer.valueOf(String.format("%tm", cntDateBeg)) - 1, Integer.valueOf(String.format("%td", cntDateBeg)));
        Long startTIme = start.getTimeInMillis();
        //结束时间转换成时间戳
        Calendar end = Calendar.getInstance();
        end.set(Integer.valueOf(String.format("%tY", cntDateEnd)), Integer.valueOf(String.format("%tm", cntDateEnd)) - 1, Integer.valueOf(String.format("%td", cntDateEnd)));
        Long endTime = end.getTimeInMillis();
        //定义一个一天的时间戳时长
        Long oneDay = 1000 * 60 * 60 * 24l;
        Long time = startTIme;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //循环得出
        while (time <= endTime) {
            list.add(sdf.format(new Date(time)));
            time += oneDay;
        }
        return list;
    }
	
	/**
	 * 若issue长度不足12位，则在其后补0，一直到12位
	 * @param issue
	 * @return issue
	 */
	public static String changeIssueToIssue12(String issue){
		
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
}

package com.htht.job.executor.plugin.common;

import java.io.File;
import java.io.IOException;
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
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.htht.util.DateUtil;
import org.htht.util.FileOperate;
import org.htht.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.hsfire.HSFire;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.hsfire.HSFireService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

public abstract class BasePlugin {
	
	private static Logger logger = LoggerFactory.getLogger(BasePlugin.class.getName());
	
	@Autowired
	private AtomicAlgorithmService atomicAlgorithmService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductUtil productUtil;
	
	@Autowired
	private DictCodeService dictCodeService;
	
	@Autowired
	private HSFireService hsFireService;
	
	@Autowired
	private ProductInfoService productInfoService;
	
	@Autowired
	private RedisService redisService;
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) throws Exception
	{
		logger.info(this.getClass().getSimpleName() + " 当前执行参数：" + triggerParam.getExecutorParams()+" ;");
		LinkedHashMap dymap = triggerParam.getDynamicParameter();
		String redisKey = null;
		ProductParam modelParam = null;
		try {
			modelParam = JSON.parseObject(triggerParam.getModelParameters(), ProductParam.class);
		
			//算法标识
			String algorId = triggerParam.getAlgorId();
			AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(algorId);
			String modelIdentify = at.getModelIdentify();
			
			DictCode productPath = dictCodeService.findOneself("productPath");
			
			//产品
			String productId = triggerParam.getProductId();
			Product product = productService.findById(productId);
			List<Product> productList = this.getProducts(product); // productService.getProductsByParentId(product.getTreeId());
			
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
			String regionId = (String) dymap.get("areaID");
		
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
				issuees = this.getIssuees(inputPath, doStartTime, doEndTime, issueFormat, fileFormat);
				logger.info(this.getClass().getSimpleName() + " 共有：" + issuees.size()+" 期;");
		
			} else {
				// 分片广播方式
				issuees.add(issueExe);
			}
			// H8雪盖 只处理 "12,14,15"三期，H8雪深处理"12,13,14"三期
			List<String> dealIssuees = new ArrayList<String>();
			if("H8SNOW".equals(type)){
				for(String s:issuees){
					String timeFormat = s.substring(8, 10);
					if("13".equals(timeFormat)){
						continue;
					}
					dealIssuees.add(s);
				}
				issuees = dealIssuees;
			}else if("SnowDepth_H8".equals(type)){
				for(String s:issuees){
					String timeFormat = s.substring(8, 10);
					if("15".equals(timeFormat)){
						continue;
					}
					dealIssuees.add(s);
				}
				issuees = dealIssuees;
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
			
			String logId = "XxlJobLog" + triggerParam.getLogId();
			System.out.println("BasePlugin===logID====="+logId);
			StringBuffer sb = new StringBuffer();
			sb.append(product.getName());
			sb.append(",");
			
			for (String issue : issuees) {
				
				redisKey = this.getClass().getSimpleName() + "@@" + type + issue ;
				if(redisService.exists(redisKey)){
					logger.info("redis中存在：" + redisKey);
					continue;
				}else{
					redisService.add(redisKey,redisKey);
				}
				logger.info(redisKey + " 当前执行参数： " + issuees.get(0) );
				
				String issue12 = changeIssueToIssue12(issue);
				
				sb.append("|");
				sb.append(issue12);
				sb.append("|");
				
				//取出每次分片文件名
				String fileName = new File(triggerParam.getExecutorParams()).getName();
				if(StringUtils.isEmpty(fileName)){
					fileName = type+ "_" + issue12 + ".xml";
				}
				
				/** =======1.拼装dymap=========== **/
				String outputBaseXml = outputXMLDir.getPath() + File.separator ;
				String outputLog = outputLogDir.getPath() + File.separator+ type + "_" + issue12 + ".log";
				String inputxmlpath = inputxml  + type+ "_" + issue12 + ".xml";
				
				/** =======2.创建日志文件=========== **/
				XxlJobFileAppender.makeLogFileNameByPath(outputLog);
				// 回调需要用到
				triggerParam.setLogFileName(outputLog);
				
				/** =======3.判断是否存在文件=========== **/
				boolean checkProductExists = this.checkProductExists( productList, issue12, cycle, modelIdentify, fileName, regionId);
				if (checkProductExists) {
					XxlJobLogger.logByfile(outputLog, " 产品已生产");
					result.setMessage("already done");
					redisService.remove(redisKey);
					logger.info(redisKey);
					continue;
				}
				
				XxlJobLogger.logByfile(outputLog, "开始执行getInputParam，期号为：" + issue);
				redisService.set(logId, sb.toString(),24*3600*1L);
		
				Map<String, Object> inputMap = this.getInputParam(triggerParam,issue);
				if(inputMap == null || "".equals((String)inputMap.get("inputFile"))){
					XxlJobLogger.logByfile(outputLog, "没有需要的数据！");
					result.setErrorMessage("没有需要的数据！inputFile为空。");
					redisService.remove(redisKey);
					logger.info(redisKey + "inputFile为空。");
					XxlJobLogger.logByfile(outputLog, "inputFile为空。");
					continue;
				}
				
				if("SpringSowingNew".equals(type)){
					if(null == inputMap.get("tem_mean") || "".equals(inputMap.get("tem_mean"))){
						XxlJobLogger.logByfile(outputLog, redisKey+" tem_mean没有nc数据文件！");
						result.setErrorMessage(redisKey+" tem_mean没有nc数据文件！");
						redisService.remove(redisKey);
						logger.info(redisKey + "tem_mean没有nc数据文件！");
						return result;
					}
				}
				
				//创建临时文件夹目录，统一路径
				String tempPath = (String) inputMap.get("tempPath");
		//			tempPath = tempPath.replaceAll("\\\\\\\\", "/").replaceAll("\\\\", "/");
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
				if (null != productList && 1 == productList.size()) {
					Product product2 = productList.get(0);
					String outputxml = outputxmlBaseDir.getAbsolutePath() + File.separator + product2.getMark() + "_" + issue12 + ".xml" ;
					outputXmlMap.put(product2.getId(), outputxml);
					outputMap.put("outXMLPath",outputxml );
				} else {
					outputMap.put("outXMLPath",outputxmlBaseDir.getAbsolutePath() + File.separator );
					for (Product product2 : productList) {
						String outputxml = outputxmlBaseDir.getAbsolutePath() + File.separator + product.getMark() + "_" + issue12 + ".xml" ;
						outputXmlMap.put(product2.getId(), outputxml);
						outputMap.put("outXMLPath",outputxml );
					}
				}
				outputMap.put("outFolder", outputPath);
				
				List<XmlDTO> inputList = this.changeMapToList(inputMap);
				
				outputMap = this.getOutParam(triggerParam,outputMap);
				List<XmlDTO> outputList = this.changeMapToList(outputMap);
		
				XmlUtils XmlUtils = new XmlUtils();
				XmlUtils.createAlgorithmXml(type,inputList,outputList,inputxmlpath);
		
				/** =======5.执行脚本=========== **/
				XxlJobLogger.logByfile(outputLog, "正在执行_运行");
				ServerImpUtil.executeCmd(exePath, inputxmlpath);
				
				/** ========6.脚本 结束入库======= **/
				if (null != type && type.contains("process")) {
					logger.info("执行成功");
					result.setResult("成功");
					redisService.remove(redisKey);
					continue;
				}
				
				Set<Entry<String, String>> outputXmlEntrySet = outputXmlMap.entrySet();
				
				for (Entry<String, String> entry : outputXmlEntrySet) {
					String productid = entry.getKey();
					String outputxml = entry.getValue();
					File outputxmlFile = new File(outputxml);
					if (!outputxmlFile.exists()||!outputxmlFile.isFile()) {
						result.setErrorMessage("无xml输出，未生产产品");
						XxlJobLogger.logByfile(outputLog, "无xml输出，生产失败");
						redisService.remove(redisKey);
						continue;
					}
				
					boolean b = XmlUtils.isSuccessByXml(outputxml);
					if (!b) {
						result.setErrorMessage("产品生产失败");
						XxlJobLogger.logByfile(outputLog, "输出xml为空，生产失败");
						redisService.remove(redisKey);
						continue;
		//					return result;
					}
					
					XxlJobLogger.logByfile(outputLog, "正在执行_入库");
		
					//读取xml，把xml转换成map对象
					Map<String,List<Element>> map = XmlUtils.outputFilesXmlToMap(outputxml);
					logger.info("outputxml: " + outputxml );
					List<String> regionIdList = XmlUtils.getXmlAttrVal( map, "region", "identify");
					List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal( map, "mosaicFile");
					String mosaicFile = "";
					Product produc = productService.findById(productid);
					if ("FIRE_H8".equals(produc.getBz()) || "FIRE_FY4A".equals(produc.getBz())) {
						logger.info("当前火点没有镶嵌数据集");
						mosaicFiles.add("oh-no");
						logger.info("给火点加上镶嵌数据集" + mosaicFiles.size() + "，便于入库，无其他作用；");
					}
					if (1 == productList.size()) {
						if (null == mosaicFiles|| mosaicFiles.size() == 0) {
							logger.info("没有mosaicFiles,算法处理异常，不予入库");
							result.setErrorMessage("没有mosaicFiles,算法处理异常，不予入库");
							redisService.remove(redisKey);
							continue;
		//						return result;
			
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
								productInfo = productUtil.saveProductInfo( productid, region, issue12, cycle, mosaicFile, at.getModelIdentify(), fileName);
								List<String> lFiles = XmlUtils.getXmlAttrFileElementVal( map, "region","identify",region);
								boolean fire = true; 
								if ("FIRE_H8".equals(produc.getBz()) || "FIRE_FY4A".equals(produc.getBz())) {
									fire = checkFireExist(lFiles);
								}
								if (false == fire) {
									productInfoService.deleteProductInfo(productInfo.getId());
									result.setMessage("没有火点信息");
									result.setResult("成功");
									redisService.remove(redisKey);
									continue;
		//								return result;
								}
								for (String file : lFiles) {
									if (file.endsWith("txt")) {
										boolean flag = saveH8Fire(file,issue12,region,productInfo.getId());
										if (false == flag) {
											productInfoService.deleteProductInfo(productInfo.getId());
											result.setErrorMessage("火点入库出错");
											redisService.remove(redisKey);
											return result;
										}
										continue;
									}
									productUtil.saveProductInfoFile(productInfo.getId(), file.replace("\\", "/"), productPath.getDictCode(), region, issue12, cycle);
								}
							}
						}
						
						//产品结果信息入库
						XxlJobLogger.logByfile(outputLog, "正在执行_统计入库");
						List<Element> xmllists = XmlUtils.getTablenameElements(outputxml,"table");
						
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
		    					String otherValue = ","+productInfo.getId()+","+cycle+","+modelIdentify+","+fileName;
		
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
		                
					}else {
						
						
						String bz = produc.getBz();
						if (null==bz || "".equals(bz)) {
							continue;
						}
						ProductInfo productInfo = new ProductInfo();
							
						for (String region : regionIdList) {
							List<String> mosaics = XmlUtils.getXmlAttrFileElementVal(map, "mosaicFile");
							for (String mosaic : mosaics) {
								if (mosaic.contains(bz)) {
									productInfo = productUtil.saveProductInfo( productid, region, issue12, cycle, mosaic, at.getModelIdentify(), fileName);
									break;
								} 
							}
							
							List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(map, "region","identify",region);
							for (String file : lFiles) {
								if (file.contains(bz)) {
									productUtil.saveProductInfoFile(productInfo.getId(), file.replace("\\", "/"), productPath.getDictCode(), region, issue12, cycle);
								}
							}
						}
					}
				}
				
			result.setResult("成功");
			if (!result.isSuccess()) {
				result.setErrorMessage("入库出错");
				redisService.remove(redisKey);
				continue;
			   }
			redisService.remove(redisKey);
			}
			
		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if(null!=redisKey){
				redisService.remove(redisKey);
			}
		}
		
		return result;
	}
	
	public Map<String, Object> getOutParam(TriggerParam triggerParam,
			Map<String, Object> outputMap) {
		return outputMap;
	}

	/**
	 * 校验是否有火点信息 
	 *  true：有火点信息
	 *  false：无火点信息
	 * @param lFiles
	 * @return boolean
	 */
	private boolean checkFireExist(List<String> lFiles) {
		
		boolean flag = false ;
		for (String file : lFiles) {
			if (file.endsWith("txt")) {
				flag =  true;
				break;
			}
		}
		
		return flag;
		
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
	
	/**
	 * 若issue长度不足12位，则在其后补0，一直到12位
	 * @param issue
	 * @return issue
	 */
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
	
	private boolean saveH8Fire(String path,String issue,String region,String productInfoId) throws IOException{
		FileOperate fileOperate = new FileOperate();
		String[] lines = fileOperate.getFileContentByLine(path);
		if (null != lines) {
			for (int i = 1; i < lines.length; i++) {
				String line = lines[i];
				String[] split = line.trim().replaceAll("\\s+", " ").split(" ");
				if (9 != split.length) {
					logger.info("文件不符合要求，解析失败：" + path);
					return false;
				}
				HSFire hsFire = new HSFire();
				hsFire.setIssue(issue);
				hsFire.setRegionId(region);
				hsFire.setLon(Double.parseDouble(split[0]));
				hsFire.setLat(Double.parseDouble(split[1]));
				hsFire.setTfire(Double.parseDouble(split[2]));
				hsFire.setTbg(Double.parseDouble(split[3]));
				hsFire.setArea(Double.parseDouble(split[4]));
				hsFire.setFrp(Integer.parseInt(split[5]));
				hsFire.setFrpN(Integer.parseInt(split[6]));
				hsFire.setLc(Integer.parseInt(split[7]));
				hsFire.setCred(Integer.parseInt(split[8]));
				hsFire.setProductInfo(productInfoId);
				hsFireService.saveHSFire(hsFire );
			}
		}
		return true;
		
	}
	
	/**
	 *  根据算法需求获取issue集合
	 * @param inputPath 输入路径（可能需要输入路径去获取相关issue）
	 * @param doStartTime 开始时间
	 * @param doEndTime	结束时间
	 * @param issueFormat 期号格式
	 * @return List<String> 期号集合
	 */
	public abstract List<String> getIssuees(String inputPath, Date doStartTime, Date doEndTime, String issueFormat , String fileFormat) throws IOException;

	/**
	 * 获取算法所需xml中的input值
	 * @param triggerParam 
	 * @param issue12 期号
	 * @return 
	 */
	public abstract Map<String, Object> getInputParam(TriggerParam triggerParam, String issue)throws IOException;
	
	/**
	 * 如果有子产品，即获取子产品集合
	 * @param product
	 * @return List<Product>
	 */
	public abstract List<Product> getProducts(Product product);
	

	/**
	 * 判断产品是否已经入库
	 * 		true: 已入库
	 * 		false:没有入库
	 * @param productId
	 * @param issue12
	 * @param cycle
	 * @param modelIdentify
	 * @param fileName
	 * @param regionId
	 * @return boolean
	 */
	public abstract boolean  checkProductExists(List<Product> productList, String issue12, String cycle, String modelIdentify,
			String fileName, String regionId) ;
	

}

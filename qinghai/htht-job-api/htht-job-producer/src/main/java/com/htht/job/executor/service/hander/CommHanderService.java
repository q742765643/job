package com.htht.job.executor.service.hander;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.core.util.ScriptUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.util.XmlUtils;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.htht.util.DateUtil;
import org.htht.util.MatchTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

/**
 * @program: htht-job-producer
 * @description:产品生产通用类，
 * 				动态参数dymap（可选参数）:cycle 默认为COOD;inputFile输入路径;xmlPath xml的路径;
 * 				固定参数fixmap（必须参数）:exePath执行路径,logPath日志路径;executMode 执行方式 cmd或者xml;
 * 				默认是处理实时数据，如果有dataTime这个参数，就以参数值为准。dataTime数据的时间,格式为yyyy-MM-dd
 * @author: zhangzhipeng
 * @create: 2018-09-28 16:10
 */
@Transactional
@Service("commHanderService")
public class CommHanderService {
	@Autowired
	private ProductUtil productUtil;
	@Autowired
	private AtomicAlgorithmService atomicAlgorithmService;

	public ResultUtil<String> execute(TriggerParam triggerParam,
			ResultUtil<String> result) {
		boolean excByCMD = true;
		try {
			// 动态参数（可选参数）
			LinkedHashMap dymap = triggerParam.getDynamicParameter();
			// 固定参数（必须参数）
			LinkedHashMap fixmap = triggerParam.getFixedParameter();
			// 必须参数
			String exePath = (String) fixmap.get("exePath");

			if (StringUtils.isNotEmpty((String) fixmap.get("executMode"))
					&& "xml".equals((String) fixmap.get("executMode"))) {
				excByCMD = false;
			}

			// 日志目录下创建一个默认的已当前时间为名的log日志文件
			String outputLog=triggerParam.getLogFileName();

			/** =======2.创建日志文件=========== **/
			XxlJobFileAppender.makeLogFileNameByPath(outputLog);
			XxlJobLogger.logByfile(outputLog, "进入通用插件开行");
			String out = triggerParam.getParallelLogId();
			// 遍历选题参数
			Set<String> set = dymap.keySet();
			Iterator it = set.iterator();
			// xml需要的参数
			String outputxml = "";
			String outputPath = "";
			String cycle = "COOH";
			if (StringUtils.isNotEmpty((String) dymap.get("cycle"))) {
				cycle = (String) dymap.get("cycle");
			}
			//处理时间
			Date date = new Date();
			if(StringUtils.isNotEmpty((String) fixmap.get("dataTime"))){
				date = DateUtil.getDate((String) fixmap.get("dataTime"), "yyyy-MM-dd");
			}
			String issue = MatchTime.matchIssue(date, cycle);
			AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(triggerParam.getAlgorId());
			
			// 输入文件名的初始化，保证名称不为空
			String inputFileName = System.currentTimeMillis() + "";
			if (StringUtils.isNotEmpty((String) dymap.get("inputFile"))) {
				inputFileName = new File((String) dymap.get("inputFile")).getName();
			}
			
			StringBuffer sb = new StringBuffer(exePath);
			XmlUtils XmlUtils = new XmlUtils();
			if (excByCMD) {
				XxlJobLogger.logByfile(outputLog, "通用插件－CMD 方式开始执行");
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = (String) dymap.get(key);
					// 目录通配符匹配
					if (value.contains("{") && value.contains("}")) {
						value = DateUtil.getPathByDate(value, new Date());
						// dymap.put(key,value);
					}
					if ("xmlPath".equals(key)) {
						outputxml = value + File.separator + "outputxml"
								+ File.separator + System.currentTimeMillis()
								+ ".xml";
						continue;
					}
					if ("cycle".equals(key)) {
						cycle = value;
					}
					if ("outputPath".equals(key)) {
						outputPath = value;
					}
					sb.append(" " + value);
				}
			} else {
				XxlJobLogger.logByfile(outputLog, "通用插件－xml 方式开始执行");
				List<XmlDTO> inputList = new ArrayList<XmlDTO>();
				List<XmlDTO> outputList = new ArrayList<XmlDTO>();
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = (String) dymap.get(key);
					// 目录通配符匹配
					if (value.contains("{") && value.contains("}")) {
						value = DateUtil.getPathByDate(value, new Date());
						// dymap.put(key,value);
					}
					if (key != "xmlPath") {
						XmlDTO x = new XmlDTO();
						x.setIdentify(key);
						x.setValue(value);
						inputList.add(x);
					}
					if ("cycle".equals(key)) {
						cycle = value;
					}
					if ("outputPath".equals(key)) {
						outputPath = value;
					}
				}

				String xmlPath = (String) dymap.get("xmlPath");
				if (StringUtils.isEmpty(xmlPath)) {
					result.setErrorMessage("参数错误，xml方式，缺少xml路径");
					XxlJobLogger.logByfile(outputLog, "通用插件－产品生产失败，缺少xml路径参数");
					return result;
				}
				String inputxml = xmlPath + File.separator + "inputxml"
						+ File.separator + inputFileName + ".xml";
				outputxml = xmlPath + File.separator + "outputxml"
						+ File.separator + inputFileName + ".xml";
				// logPath
				XmlDTO x = new XmlDTO();
				x.setIdentify("logPath");
				x.setValue(outputLog);
				outputList.add(x);
				// outputxml
				x.setIdentify("outputxml");
				x.setValue(outputxml);
				outputList.add(x);

				XxlJobLogger.logByfile(outputLog, "通用插件－开始生成xml文件");
				boolean b = XmlUtils.createAlgorithmXml("", inputList,
						outputList, inputxml);
				if(!b){
					result.setErrorMessage("xml方式，xml生产失败");
					XxlJobLogger.logByfile(outputLog, "通用插件－产品生产失败，inputXml生产失败");
					return result;
				}
				/** ======2.创建输出xml========== **/
				XxlJobFileAppender.makeLogFileNameByPath(outputxml);
			}
			// 回调需要用到
			triggerParam.setLogFileName(outputLog);
			XxlJobLogger.logByfile(outputLog, "通用插件－开始执行调度");

			/** =======5.执行脚本=========== **/

			int exitValue = ScriptUtil.execCmd(sb.toString(), outputLog);
			if (exitValue != 0) {
				result.setErrorMessage("脚本执行错误");
				XxlJobLogger.logByfile(outputLog, "通用插件－脚本执行错误");
				return result;
			}
			XxlJobLogger.logByfile(outputLog, "通用插件－脚本执行成功");
			
			//0 不需要入库 1 需要xml入库
			if(StringUtils.isNotEmpty((String) fixmap.get("executMode")) && "1".equals((String) fixmap.get("executMode")) ){
				/** ========6.脚本结束 开始入库======= **/
				boolean b = XmlUtils.isSuccessByXml(outputxml);
				if (!result.isSuccess()) {
					result.setErrorMessage("产品生产失败");
					XxlJobLogger.logByfile(outputLog, "通用插件－产品生产失败");
					return result;
				}
				XxlJobLogger.logByfile(outputLog, "通用插件－xml方式，算法调度成功");
				
				Map<String, List<Element>> map = XmlUtils.outputFilesXmlToMap(outputxml);
				List<String> regionIdList = XmlUtils.getXmlAttrVal(map, "region", "identify");

				List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(map, "mosaicFile");
				String mosaicFile = "";
				if (mosaicFiles.size() > 0) {
					mosaicFile = mosaicFiles.get(0);
				}
				
				if(regionIdList!=null&&regionIdList.size()>0) {
					for(String regionId:regionIdList) {
						// 产品信息及文件信息入库
						ProductInfo productInfo = productUtil.saveProductInfo(
								triggerParam.getProductId(), regionId, issue,
								(String) dymap.get("cycle"), mosaicFile,
								at.getModelIdentify(), inputFileName);

						List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(map, "region");
						for (String file : lFiles) {
							productUtil.saveProductInfoFile(productInfo.getId(), file,
									(String) dymap.get("outDir"), regionId, issue,
									(String) dymap.get("cycle"));
						}
						
					}
				}
				
				// 产品结果信息入库
				XxlJobLogger.logByfile(outputLog, "正在执行_入库");
				List<Map<String, Object>> ls = new ArrayList<>();

				List<Element> xmllists = XmlUtils
						.getTablenameElements(outputxml,"table");
				if(null!=xmllists && xmllists.size()>0){
					for (Element e : xmllists) {
						// 获取values
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
						String otherValue = "," + triggerParam.getProductId() + ","
								+ (String) dymap.get("cycle") + ","
								+ at.getModelIdentify() + "," + inputFileName;
						
						for (int i = 0; i < fields.length; i++) {
							for (String s : cc) {
								String[] values = (s + otherValue).replace("'", "")
										.replace(",", " ").trim().split("\\s+");
								productAnalysisTableInfo.addFieldAndValue(
										fields[i].trim(), values[i].trim());
							}
						}
						productUtil.saveProductDetail(productAnalysisTableInfo);
					}
				}
				XxlJobLogger.logByfile(outputLog, "数据入库成功");
			}
			
			
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR
					.getValue());
			throw new RuntimeException();
		}
		
		result.setMessage("通用插件调度完成！");
		return result;

	}


}

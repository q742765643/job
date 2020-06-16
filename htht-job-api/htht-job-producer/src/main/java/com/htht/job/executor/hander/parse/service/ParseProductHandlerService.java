package com.htht.job.executor.hander.parse.service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Element;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.MatchTime;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlTools;
import com.htht.job.executor.util.XmlUtils;

@Transactional
@Service("parseProductHandlerService")
public class ParseProductHandlerService {
	@Autowired
	private ProductInfoService productInfoService;
	@Autowired
	private ProductUtil productUtil;
	@Autowired
	private AtomicAlgorithmService atomicAlgorithmService;
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
	private SimpleDateFormat issueFormat = new SimpleDateFormat("yyyyMMddHHmm");
	private static final String CYCLESTR = "cycle";
	private static final String AREAIDSTR = "areaID";

	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findModelIdentifyById(triggerParam.getAlgorId());
		/** 1.获取参数列表 **/
		LinkedHashMap<?, ?> fixmap = triggerParam.getFixedParameter();
		LinkedHashMap<?, ?> dymap = triggerParam.getDynamicParameter();
		ArrayList<String> issueList = new ArrayList<>();
		/** 2.解析产品参数 **/
		String exePath = (String) fixmap.get("exePath");

		String inputIssue = (String) dymap.get("issue");
		String cycle = dymap.get(CYCLESTR) == null ? "" : (String) dymap.get(CYCLESTR);
		String areaID = dymap.get(AREAIDSTR) == null ? "" : (String) dymap.get(AREAIDSTR);
		String inputPath = (String) dymap.get("inputPath");
		String outputlog = FileUtil.formatePath((String) dymap.get("outputlog"));
		String outputxml = FileUtil.formatePath((String) dymap.get("outputxml"));
		String outputPath = FileUtil.formatePath((String) dymap.get("outputPath"));
		String rootTag = (String) dymap.get("rootTag");

		if (null == inputIssue) {
			String startIssue = (String) dymap.get("startIssue");
			String endIssue = (String) dymap.get("endIssue");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			try {
				Date doStartTime = sdf.parse(MatchTime.matchIssue(startIssue, cycle));
				Date doEndTime = sdf.parse(MatchTime.matchIssue(endIssue, cycle));
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(doStartTime);
				String issue = "";
				while (doStartTime.compareTo(doEndTime) <= 0) {
					issue = MatchTime.matchIssue(sdf.format(doStartTime), cycle);
					if (!"".equals(issue) && !issueList.contains(issue)) {
						issueList.add(issue);
					}
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					doStartTime = calendar.getTime();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			issueList.add(MatchTime.matchIssue(inputIssue, cycle));
		}
		String inputRaster = null;
		for (String issue : issueList) {
			List<ProductInfoDTO> productFile = productInfoService.findProductExits(triggerParam.getProductId(), issue,
					cycle, atomicAlgorithmDTO.getModelIdentify(), inputRaster, areaID);
			if (!productFile.isEmpty()) {
				continue;
			}
			try {
				inputRaster = FileUtil.getPathByDate(inputPath, issueFormat.parse(issue));
				if (!new File(inputRaster).exists()) {
					continue;
				}
				List<XmlDTO> inputList = new ArrayList<>();
				List<XmlDTO> outputList = new ArrayList<>();
				String outputlogpath = triggerParam.getLogFileName();
				XxlJobLogger.logByfile(outputlogpath, "统计插件－xml 方式开始执行");
				/** 4.拼装map **/
				String tempFileName = rootTag + "_" + issue + "(" + df.format(new Date()) + ")";
				String inputxmlpath = outputPath + "inPutXml/" + tempFileName + ".xml";
				String outputxmlpath = outputxml + tempFileName + ".xml";
				String algLog = outputlog + tempFileName + ".log";
				inputList.add(XmlDTO.newStringXmlDTO(AREAIDSTR, AREAIDSTR,  areaID));
				inputList.add(XmlDTO.newStringXmlDTO("issue", "产品期次",  issue));
				inputList.add(XmlDTO.newStringXmlDTO("inputRaster", "输入文件路径", inputRaster));
				outputList.add(XmlDTO.newStringXmlDTO("outDir", "输出文件夹路径", outputPath));
				outputList.add(XmlDTO.newStringXmlDTO("dirxml", "输出xml目录", outputxmlpath));
				outputList.add(XmlDTO.newStringXmlDTO("dirlog", "log目录", algLog));
				
				if (!XmlUtils.createAlgorithmXml(rootTag, inputList, outputList, inputxmlpath)) {
					result.setMessage("create inputxml failed");
					return result;
				}
				/** 5.设置调度日志 **/
				triggerParam.setLogFileName(outputlogpath);
				/** 6.开始执行调度 **/
				ServerImpUtil.executeCmd(exePath, inputxmlpath);
				/** 7.调度入库 **/
//				outputxmlpath = "C:\\Users\\Administrator\\Desktop\\FH_201512310000(2018-12-06 17-21-10-189).xml";
				if (!XmlUtils.isSuccessByXml(outputxmlpath)) {
					result.setErrorMessage("产品生产失败");
					XxlJobLogger.logByfile(outputlogpath, "ParseProduct－产品生产失败");
					return result;
				}
				XxlJobLogger.logByfile(outputlogpath, "ParseProduct－产品生产成功");

				// 产品信息及文件信息入库
				ProductInfoDTO productInfoDTO = productUtil.saveProductInfo(triggerParam.getProductId(),
						areaID, issue, cycle, "", atomicAlgorithmDTO.getModelIdentify(),
						inputRaster);
				// 产品结果信息入库
				XxlJobLogger.logByfile(outputlogpath, "正在执行_入库");

				Element rootElement = XmlTools.getRootElement(outputxmlpath);
				Element tables = rootElement.element("tables");
				if (null != tables) {
					for (Object obj : tables.elements("tablename")) {
						Element tableElement = (Element) obj;
						String tablename = tableElement.attributeValue("identify");
						if (null == tablename || "".equals(tablename)) {
							/////////////////////////////////////////
							// 待补充 //
							/////////////////////////////////////////
							continue;
						}
						List<String> valueList = new ArrayList<>();
						for (Object valueElement : tableElement.elements("values")) {
							valueList.add(((Element) valueElement).getText());
						}
						/***============ 执行算法入库 =================**/
						String sql = "delete from "+tablename+" where product_info_id=?";
						baseDaoUtil.executeSql(sql, productInfoDTO.getId());
						ProductAnalysisTableInfo productAnalysisTableInfo = new ProductAnalysisTableInfo(
								tablename);
						String[] fields = (tableElement.element("field").getText()
								+ ",product_info_id,Cycle,model_identify,file_name").replace("'", "").trim().split(",");
						String otherValue = "," + productInfoDTO.getId() + "," + cycle + ","
								+ atomicAlgorithmDTO.getModelIdentify() + "," + inputRaster;

						for (int i = 0; i < fields.length; i++) {
							for (String s : valueList) {
								String[] values = (s + otherValue).replace("'", "").replace(",", " ").trim()
										.split("\\s+");
								productAnalysisTableInfo.addFieldAndValue(fields[i].trim(), values[i].trim());
							}
						}
						productUtil.saveProductDetail(productAnalysisTableInfo);
					}
				}
				XxlJobLogger.logByfile(outputlogpath, "数据入库成功");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		/** 返回结果 **/
		return result;
	}
}

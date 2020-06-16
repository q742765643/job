package com.htht.job.executor.hander.cs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;
import org.htht.util.ServerImpUtil;
import org.htht.util.XmlMakeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.MatchTime;
import com.htht.job.executor.model.product.ProductDTO;
import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.service.builder.ProductHandler;
import com.htht.job.executor.service.builder.ProductHandlerBuilder;
import com.htht.job.executor.service.builder.part.XmlInfo;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.util.XmlTools;

/**
 * CS产品调用Handler
 * 
 * @author Administrator
 *
 */
@JobHandler(value = "csProductHandler")
@Service
public class CSProductHandler extends IJobHandler {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductFileInfoService productFileInfoService;
	@Autowired
	private ProductUtil productUtil;
	private Logger logger = LoggerFactory.getLogger(CSProductHandler.class);
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ReturnT<String> returnT = new ReturnT<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		/** =======1.校验参数============== **/
//		ParsingUtil.argumentparsing(triggerParam, resultT);
//		if (!resultT.isSuccess()) {
//			return new ReturnT<>(ReturnT.FAIL_CODE, resultT.toString());
//		}
		String exePath = triggerParam.getFixedParameter().get("exePath").toString();
		String region = "370000000000";
		ProductDTO product = productService.findById(triggerParam.getProductId());
		if (null == product) {
			returnT.setCode(ReturnT.FAIL_CODE);
			returnT.setContent("未知的产品类型");
			return returnT;
		}
		/** =======2.执行业务============================= **/
		@SuppressWarnings("unchecked")
		ProductHandler handler = ProductHandlerBuilder.newBuilder().useJobDataMap(triggerParam.getDynamicParameter())
				.useIssueHandler((result, jobDataMap) -> Arrays
						.asList(MatchTime.matchIssue(triggerParam.getDynamicParameter().get("issue").toString(),
								triggerParam.getDynamicParameter().get("cycle").toString())))
				.useIsExistPart(issue -> false).useCreateXmlPart((issue, result, jobDataMap) -> {
					String productPath = product.getProductPath();
					jobDataMap.put("productIssue", issue);
					String tempFileName = product.getMark() + "_" + issue + "(" + df.format(new Date()) + ")";
					String inputxmlpath = productPath + "/inputxml/" + tempFileName + ".xml";
					String outputxmlpath = productPath + "/outputxml/" + tempFileName + ".xml";
					String outputlogpath = productPath + "/outputlog/" + tempFileName + ".log";
					Map<String, Object> inputXmlMap = new HashMap<>();
					inputXmlMap.put("issue", issue);
					inputXmlMap.put("inputFile", triggerParam.getDynamicParameter().get("inputFile"));
					inputXmlMap.put("outFolder", triggerParam.getDynamicParameter().get("outFolder"));
					inputXmlMap.put("BandList", triggerParam.getDynamicParameter().get("BandList"));
					inputXmlMap.put("ProductType", triggerParam.getDynamicParameter().get("ProductType"));
					inputXmlMap.put("InvalidValues", triggerParam.getDynamicParameter().get("InvalidValues"));
					inputXmlMap.put("outXMLPath", outputxmlpath);
					inputXmlMap.put("outLogPath", outputlogpath);
					try {
						if (!XmlMakeUtil.makeXml(inputXmlMap, inputxmlpath)) {
							result.setCode(ReturnT.FAIL_CODE);
							result.setContent("创建输入xml失败" + outputxmlpath);
						}
					} catch (Exception e) {
						result.setCode(ReturnT.FAIL_CODE);
						result.setContent("创建输入xml失败" + outputxmlpath);
						e.printStackTrace();
					}
					return new XmlInfo(inputxmlpath, outputxmlpath);
				}).useDoExcutePart((inputXml, result, jobDataMap) -> {
					logger.info("开始执行命令{} {}", exePath, inputXml);
					ServerImpUtil.executeCmd(exePath, inputXml);
				})
				.usePraseXmlPart((outputXml, result, jobDataMap) -> {
					try {
						String productIssue = jobDataMap.get("productIssue").toString();
						String productCycle = jobDataMap.get("cycle").toString();
						Element rootElement = XmlTools.getRootElement(outputXml);
						if (!XmlTools.isSuccess(rootElement)) {
							result.setCode(ReturnT.FAIL_CODE);
							result.setMsg("算法执行失败");
							return;
						}
						Element outFiles = rootElement.element("outputfiles");
						if (outFiles != null) {
							/*** ============ 产品信息入库 ================= **/
							ProductInfoDTO pinfo = productUtil.saveProductInfo(product.getId(), region, productIssue,
									productCycle, "", "", "");
							/*** ============ 产品文件信息入库 ================= **/
							for (Object fileObj : outFiles.elements("file")) {
								Element fileElemnt = (Element) fileObj;
								File file = new File(fileElemnt.getText());
								String fileType = fileElemnt.attributeValue("type").replaceAll("\\.", "");

								if (file.exists()) {
									ProductFileInfoDTO pfInfo = new ProductFileInfoDTO(product.getMark(), pinfo.getId(),
											file.getName(), file.length(), fileType, file.getPath(),
											file.getAbsolutePath(), productIssue, region, productCycle);
									productFileInfoService.save(pfInfo);
								}
							}
						}
					} catch (Exception e) {
						result.setCode(ReturnT.FAIL_CODE);
						result.setMsg("算法执行失败");
						return;
					}

				}).builer();

		handler.excute(triggerParam, returnT);
		returnT.setCode(ReturnT.SUCCESS_CODE);
		returnT.setContent(returnT.getContent()+"执行成功");
		return returnT;
	}

}

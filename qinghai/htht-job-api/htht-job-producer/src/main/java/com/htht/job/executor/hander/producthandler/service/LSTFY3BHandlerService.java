package com.htht.job.executor.hander.producthandler.service;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.htht.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Transactional
@Service("lSTFY3BHandlerService")
public class LSTFY3BHandlerService {
	@Autowired
	private ProductInfoService productInfoService;
	@Autowired
	private ProductUtil productUtil;
	@Autowired
	private AtomicAlgorithmService atomicAlgorithmService;
	@Autowired
	private DataMataInfoService dataMataInfoService;

	public ResultUtil<String> execute(TriggerParam triggerParam,
			ResultUtil<String> result) {
		try {
			String algorId = triggerParam.getAlgorId();
			LinkedHashMap dymap = triggerParam.getDynamicParameter();
			LinkedHashMap fixmap = triggerParam.getFixedParameter();
			String exePath = (String) fixmap.get("exePath");

			// 输出xml目录
			String outputxml = (String) dymap.get("dirxml");

			// 取出每次分片文件名
			String fileName = new File(triggerParam.getExecutorParams())
					.getName();

			// 根据文件名获取数据的时间
			Date fileDate = dataMataInfoService.findDataByFileName(fileName);

			// 获取期号
			String issue = MatchTime.matchIssue(fileDate,
					(String) dymap.get("cycle"));
			// 算法标识
			AtomicAlgorithm at = atomicAlgorithmService
					.findModelIdentifyById(algorId);

			// 查询是否做过
			List<ProductInfo> productFile = productInfoService
					.findProductExits(triggerParam.getProductId(), issue,
							(String) dymap.get("cycle"), at.getModelIdentify(),
							fileName, (String) dymap.get("areaID"));

			if (productFile.size() > 0) {
				result.setMessage("already done");
				return result;
			}

			/** =======1.拼装dymap=========== **/

			String outputxmlpath = outputxml + "outPutXml\\"
					+ at.getModelIdentify() + fileName + ".xml";
			String inputxmlpath = outputxml + "inPutXml\\"
					+ at.getModelIdentify() + fileName + ".xml";
			String outputlogpath = triggerParam.getLogFileName();

			/** =======2.创建日志文件=========== **/
			XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
			// 回调需要用到
			triggerParam.setLogFileName(outputlogpath);

			XxlJobLogger.logByfile(outputlogpath, "开始执行");
			/** =======4.生成文件=========== **/
			List<XmlDTO> inputList = new ArrayList<>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("issue", issue);
			map.put("areaID", dymap.get("areaID"));
			map.put("cycle",dymap.get("cycle"));
			map.put("inputFile", triggerParam.getExecutorParams());
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				XmlDTO to = new XmlDTO();
				Map.Entry entry = (Map.Entry) iter.next();
				to.setIdentify(entry.getKey().toString());
				to.setValue(entry.getValue().toString());
				to.setDescription(" ");
				to.setType("string");
				inputList.add(to);
			}

			List<XmlDTO> outputList = new ArrayList<>();
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("outFolder", dymap.get("outDir"));
			map2.put("outXMLPath", outputxmlpath);
			map2.put("outLogPath", outputlogpath);
			Iterator iter2 = map2.entrySet().iterator();
			while (iter2.hasNext()) {
				XmlDTO to = new XmlDTO();
				Map.Entry entry = (Map.Entry) iter2.next();
				to.setIdentify(entry.getKey().toString());
				to.setValue(entry.getValue().toString());
				to.setDescription(" ");
				to.setType("string");
				outputList.add(to);
			}
			
			XmlUtils XmlUtils = new XmlUtils();

			XmlUtils.createAlgorithmXml("LST_FY3BVIRR", inputList, outputList,inputxmlpath);
			//TODO at.getModelIdentify() 定义成算法标识
//			XmlUtils.createAlgorithmXml(at.getModelIdentify(), inputList, outputList,inputxmlpath);

			if (!result.isSuccess()) {
				return result;
			}
			/** =======5.执行脚本=========== **/
			// int exitValue = ScriptUtil.execToFile(exePath, scriptFile,
			// outputlog, inputxml);
			// int exitValue =
			// ScriptUtil.execCmd(exePath+" "+inputxmlpath,outputlogpath);
			ServerImpUtil.executeCmd(exePath, inputxmlpath);

			/** ========6.脚本 结束入库======= **/
			File outputXmlFile = new File(outputxmlpath);
			if (!outputXmlFile.exists()) {
				result.setErrorMessage("产品生产失败,输出xml不存在");
				return result;
			}
			boolean b = XmlUtils.isSuccessByXml(outputxmlpath);
			if (!result.isSuccess()) {
				result.setErrorMessage("产品生产失败");
				return result;
			}
			if (!b) {
				result.setErrorMessage("算法生成失败");
				return result;
			}

			XxlJobLogger.logByfile(outputlogpath, "正在执行_入库");

			Map<String,List<Element>> outputFiles = XmlUtils
					.outputFilesXmlToMap(outputxmlpath);
			List<String> regionIdList = XmlUtils.getXmlAttrVal(outputxmlpath, "region",
					"identify");

			List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(
					outputxmlpath, "mosaicFile");
			String mosaicFile = "";
			if (mosaicFiles.size() > 0) {
				mosaicFile = mosaicFiles.get(0);
			}

			// 产品信息及文件信息入库
			ProductInfo productInfo = new ProductInfo();
			if(regionIdList!=null&&regionIdList.size()>0) {
				for(String regionId:regionIdList) {
					 productInfo = productUtil.saveProductInfo(
							triggerParam.getProductId(), regionId, issue,
							(String) dymap.get("cycle"), mosaicFile,
							at.getModelIdentify(), fileName);// TODO

					List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(
							outputxmlpath, "region","identify",regionId);
					for (String file : lFiles) {
						productUtil.saveProductInfoFile(productInfo.getId(), file,
								(String) dymap.get("outDir"), regionId, issue,
								(String) dymap.get("cycle"));
					}
				}
			}
			
			// 产品结果信息入库
			XxlJobLogger.logByfile(outputlogpath, "正在执行_入库");
			List<Map<String, Object>> ls = new ArrayList<>();

			List<Element> xmllists = XmlUtils
					.getTablenameElements(outputxmlpath,"table");
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
				String otherValue = "," + productInfo.getId() + ","
						+ (String) dymap.get("cycle") + ","
						+ at.getModelIdentify() + "," + fileName;

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

			if (!result.isSuccess()) {
				result.setErrorMessage("入库出错");
				return result;
			}
			result.setResult("成功");
			XxlJobLogger.logByfile(outputlogpath, "执行成功");
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR
					.getValue());
			throw new RuntimeException();
		}
		return result;

	}

	public void batchSaveProductFileInfo(String productId, Map dymap,
			List<Map> list, ResultUtil<String> result) {
		if (null == productId) {
			result.setErrorMessage("产品id为空");
			return;
		}
	}

	public ResultUtil<String> parsingXml(String outputxml,
			ResultUtil<String> result, List<Map> list) {
		try {
			File f = new File(outputxml);
			if (!f.exists() || f.length() == 0) {
				result.setErrorMessage("outxml文件不存在");
				return result;
			}
			// 创建SAXReader对象
			SAXReader reader = new SAXReader();
			// 读取文件 转换成Document
			Document document = reader.read(f);
			// 获取根节点元素对象
			Element root = document.getRootElement();
			if (root == null) {
				result.setErrorMessage("outxml根节点获取错误");
				return result;
			}
			Element log = root.element("log");
			if (log == null) {
				result.setErrorMessage("outxml log节点获取错误");
				return result;
			}
			String loginfo = log.elementText("loginfo");
			if (!"success".equals(loginfo)) {
				result.setErrorMessage("outxml loginfo节点获取错误");
				return result;
			}

			Element outputfiles = root.element("outputfiles");
			if (outputfiles == null) {
				result.setErrorMessage("outxml outputfiles节点获取错误");
				return result;
			}

			Iterator<Element> file = outputfiles.elementIterator("file");
			while (file.hasNext()) {
				Element e = file.next();
				Map map = new HashMap();
				map.put("type", e.elementText("type"));
				map.put("path", e.elementText("path"));
				map.put("region", e.elementText("region"));
				list.add(map);

			}
			// listNodes(root);

		} catch (Exception e) {
			result.setErrorMessage("解析outputxml出错");

			throw new RuntimeException();
		}
		return result;
	}
}

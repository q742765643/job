package com.htht.job.executor.hander.producthandler.service;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.MatchTime;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.util.XmlUtils;

import org.htht.util.DataTimeHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

@Transactional
@Service("aodProductHandlerService")
public class AOD_H8_HandlerService extends StandardService {

//	@Autowired
//	private ProductInfoService productInfoService;
//	@Autowired
//	private ProductUtil productUtil;
//	@Autowired
//	private AtomicAlgorithmService atomicAlgorithmService;
//	@Autowired
//	private DataMataInfoService dataMataInfoService;
//
//	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
//		try {
//			String algorId = triggerParam.getAlgorId();
//			LinkedHashMap dymap = triggerParam.getDynamicParameter();
//			LinkedHashMap fixmap = triggerParam.getFixedParameter();
//			String exePath = (String) fixmap.get("exePath");
//
//			// 输出xml目录
//			String outputxml = (String) dymap.get("dirxml");
//
//			String cycle = (String) dymap.get("cycle");
//
//			// 取出每次分片文件名
//			String fileName = new File(triggerParam.getExecutorParams()).getName();
//			// String fileName = Arrays.asList(filenames.split("\\.")).get(0);
//
//			// 从文件名中获取时间作为产品编号
//			Date fileDate = dataMataInfoService.findDataByFileNameAndLevel(fileName, "L2");
//			String issue = MatchTime.matchIssue(fileDate, cycle);
//
//			// 算法标识
//			AtomicAlgorithmDTO at = atomicAlgorithmService.findModelIdentifyById(algorId);
//
//			/** =======1.拼装dymap=========== **/
//
//			String outputXmlpath = outputxml + File.separator + "outputXml" + File.separator + at.getModelIdentify()
//					+ File.separator + fileName + ".xml";
//			String outputlogpath = outputxml + File.separator + "outputXml" + File.separator + at.getModelIdentify()
//					+ File.separator + fileName + ".log";
//			ServerImpUtil.touchFile(outputXmlpath);
//			String inputXmlpath = outputxml + File.separator + "inputXml" + File.separator + at.getModelIdentify()
//					+ File.separator + fileName + ".xml";
//
//			/** =======2.创建日志文件=========== **/
//			XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
//			// 回调需要用到
//			triggerParam.setLogFileName(outputlogpath);
//
//			/** =======3.判断是否存在文件=========== **/
//			List<ProductInfoDTO> productFile = productInfoService.findProductExits(triggerParam.getProductId(), issue,
//					"", at.getModelIdentify(), fileName, (String) dymap.get("areaID"));
//			if (productFile.size() > 0) {
//				XxlJobLogger.logByfile(outputlogpath, " 产品已生产");
//				result.setMessage("already done");
//				return result;
//			}
//
//			/** =======4.生成文件=========== **/
//			XxlJobLogger.logByfile(outputlogpath, "开始执行_xml");
//
//			String inputdataPath = new File(triggerParam.getExecutorParams()).getParentFile().getParent();
//
//			String type = "";
//			List<XmlDTO> inputList = new ArrayList<>();
//			Map<String, Object> map = new HashMap<String, Object>();
//
//			type = "AOD_H8";
//			map.put("areaID", dymap.get("areaID"));
//			map.put("issue", issue);
//			map.put("inputFile", inputdataPath);
//			map.put("cycle", cycle);
//
//			Iterator iter = map.entrySet().iterator();
//			while (iter.hasNext()) {
//				XmlDTO to = new XmlDTO();
//				Map.Entry entry = (Map.Entry) iter.next();
//				to.setIdentify(entry.getKey().toString());
//				to.setValue(entry.getValue().toString());
//				to.setDescription(" ");
//				to.setType("string");
//				inputList.add(to);
//			}
//
//			List<XmlDTO> outputList = new ArrayList<>();
//			Map<String, Object> map2 = new HashMap<String, Object>();
//			map2.put("outFolder", dymap.get("outDir"));
//			map2.put("outXMLPath", outputXmlpath);
//			map2.put("outLogPath", outputlogpath);
//			Iterator iter2 = map2.entrySet().iterator();
//			while (iter2.hasNext()) {
//				XmlDTO to = new XmlDTO();
//				Map.Entry entry = (Map.Entry) iter2.next();
//				to.setIdentify(entry.getKey().toString());
//				to.setValue(entry.getValue().toString());
//				to.setDescription(" ");
//				to.setType("string");
//				outputList.add(to);
//			}
//
//			XmlUtils.createAlgorithmXml(type, inputList, outputList, inputXmlpath);
//
//			if (!result.isSuccess()) {
//				return result;
//			}
//			/** =======5.执行脚本=========== **/
//			XxlJobLogger.logByfile(outputlogpath, "正在执行_运行");
//			ServerImpUtil.executeCmd(exePath, inputXmlpath);
//
//			/** ========6.脚本 结束入库======= **/
//			boolean b = XmlUtils.isSuccessByXml(outputXmlpath);
//			if (!b) {
//				result.setErrorMessage("产品生产失败");
//				XxlJobLogger.logByfile(outputlogpath, "输出xml为空，生产失败");
//				return result;
//			}
//			XxlJobLogger.logByfile(outputlogpath, "正在执行_入库");
//
//			List<String> regionIdList = XmlUtils.getXmlAttrVal(outputXmlpath, "region", "identify");
//
//			List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(outputXmlpath, "mosaicFile");
//			String mosaicFile = "";
//			if (mosaicFiles.size() > 0) {
//				mosaicFile = mosaicFiles.get(0);
//			}
//
//			// 产品信息及文件信息入库
//			ProductInfoDTO productInfoDTO = new ProductInfoDTO();
//			if (regionIdList != null && regionIdList.size() > 0) {
//				for (String regionId : regionIdList) {
//					productInfoDTO = productUtil.saveProductInfo(triggerParam.getProductId(), regionId, issue,
//							(String) dymap.get("cycle"), mosaicFile, at.getModelIdentify(), fileName);
//
//					List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(outputXmlpath, "region", "identify",
//							regionId);
//					for (String file : lFiles) {
//						productUtil.saveProductInfoFile(productInfoDTO.getId(), file, (String) dymap.get("outDir"),
//								regionId, issue, (String) dymap.get("cycle"));
//					}
//				}
//			}
//			// 产品结果信息入库
//			XxlJobLogger.logByfile(outputlogpath, "正在执行_统计入库");
//
//			List<Map<String, Object>> ls = new ArrayList<>();
//			List<Element> xmllists = XmlUtils.getTablenameElements(outputXmlpath, "table");
//			for (Element e : xmllists) {
//				// 获取数据集
//				List<Element> list2 = e.elements();
//				List<String> cc = new ArrayList<>();
//				for (Element e2 : list2) {
//					if ("values".equals(e2.getName())) {
//						cc.add(e2.getText());
//					}
//				}
//				// 封数据执行入库
//				ProductAnalysisTableInfo productAnalysisTableInfo = new ProductAnalysisTableInfo(
//						e.attribute("identify").getValue());
//				String[] fields = (e.element("field").getText() + ",product_info_id,Cycle,model_identify,file_name")
//						.replace("'", "").trim().split(",");
//				String otherValue = "," + productInfoDTO.getId() + "," + (String) dymap.get("cycle") + ","
//						+ at.getModelIdentify() + "," + fileName;
//
//				for (int i = 0; i < fields.length; i++) {
//					for (String s : cc) {
//						String[] values = (s + otherValue).replace("'", "").replace(",", " ").trim().split("\\s+");
//						productAnalysisTableInfo.addFieldAndValue(fields[i].trim(), values[i].trim());
//					}
//				}
//				productUtil.saveProductDetail(productAnalysisTableInfo);
//			}
//
//			result.setResult("成功");
//			if (!result.isSuccess()) {
//				result.setErrorMessage("入库出错");
//				return result;
//			}
//		} catch (Exception e) {
//			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
//			throw new RuntimeException();
//		}
//		return result;
//
//	}
//
//	public void batchSaveProductFileInfo(String productId, Map dymap, List<Map> list, ResultUtil<String> result) {
//		if (null == productId) {
//			result.setErrorMessage("产品id为空");
//			return;
//		}
//	}
//
//	public ResultUtil<String> parsingXml(String outputxml, ResultUtil<String> result, List<Map> list) {
//		try {
//			File f = new File(outputxml);
//			if (!f.exists() || f.length() == 0) {
//				result.setErrorMessage("outxml文件不存在");
//				return result;
//			}
//			// 创建SAXReader对象
//			SAXReader reader = new SAXReader();
//			// 读取文件 转换成Document
//			Document document = reader.read(f);
//			// 获取根节点元素对象
//			Element root = document.getRootElement();
//			if (root == null) {
//				result.setErrorMessage("outxml根节点获取错误");
//				return result;
//			}
//			Element log = root.element("log");
//			if (log == null) {
//				result.setErrorMessage("outxml log节点获取错误");
//				return result;
//			}
//			String loginfo = log.elementText("loginfo");
//			if (!"success".equals(loginfo)) {
//				result.setErrorMessage("outxml loginfo节点获取错误");
//				return result;
//			}
//
//			Element outputfiles = root.element("outputfiles");
//			if (outputfiles == null) {
//				result.setErrorMessage("outxml outputfiles节点获取错误");
//				return result;
//			}
//
//			Iterator<Element> file = outputfiles.elementIterator("file");
//			while (file.hasNext()) {
//				Element e = file.next();
//				Map map = new HashMap();
//				map.put("type", e.elementText("type"));
//				map.put("path", e.elementText("path"));
//				map.put("region", e.elementText("region"));
//				list.add(map);
//
//			}
//			// listNodes(root);
//
//		} catch (Exception e) {
//			result.setErrorMessage("解析outputxml出错");
//
//			throw new RuntimeException();
//		}
//		return result;
//	}
//
//	public static void main(String[] args) {
//		boolean b = XmlUtils.isSuccessByXml("D:\\htht\\algorithm\\AOD\\outPutXml\\20181016153444.xml");
//	}


	@Override
	ResultUtil<String> makeXml(TriggerParam triggerParam, AtomicAlgorithmDTO at, ResultUtil<String> result) {
		LinkedHashMap dymap = triggerParam.getDynamicParameter();
		LinkedHashMap fixmap = triggerParam.getFixedParameter();

		String proMark = (String) fixmap.get("proMark");

		//取出每次分片文件名
		String fileName = new File(triggerParam.getExecutorParams()).getName();

		/** =======1.获取期次信息=========== **/
		String cycle = (String) dymap.get("cycle");
		proMark = StringUtils.trimAllWhitespace(proMark).toLowerCase();
		Date fileDate = null;
		if(proMark.equals("scanfolder")){
			String timePattern = (String) fixmap.get("timePattern");
			fileDate = new Date(DataTimeHelper.getDataTimeFromFileNameByPattern(fileName, timePattern));
		}else if(proMark.equals("scandatabase")){
			String dataLevel = (String)fixmap.get("dataLevel");
			fileDate = dataMataInfoService.findDataByFileNameAndLevel(fileName,StringUtils.isEmpty(dataLevel)? "L2":dataLevel);
		}

		if(fileDate==null){
			result.setCode(1);
			result.setMessage("获取当前期次时间信息失败");
			return result;
		}

		String outFolder = (String)dymap.get("outFolder");
		// 获取期号
		String issue = MatchTime.matchIssue(fileDate,cycle);
		String outputlogpath = outFolder + File.separator + "outputXml" + File.separator + at.getModelIdentify()+ File.separator + fileName + ".log";
		XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
		/** =======2.判断是否以生产=========== **/
		List<ProductInfoDTO> productFile =
				productInfoService.findProductExits(triggerParam.getProductId(),issue,"",at.getModelIdentify(),fileName,(String)dymap.get("areaID"));

		if (productFile.size() > 0){
			XxlJobLogger.logByfile(outputlogpath, " 产品已生产");
			result.setCode(1);
			result.setMessage("already done");
			return result;
		}
		String inputxmlpath = outFolder + File.separator + "inputXml" + File.separator + at.getModelIdentify()
				+ File.separator + fileName.substring(0,fileName.indexOf(".") ) + ".xml";

		String outputxmlpath = outFolder + File.separator + "outputXml" + File.separator + at.getModelIdentify()
				+ File.separator + fileName.substring(0,fileName.indexOf(".") ) + ".xml";
		
		XxlJobFileAppender.makeLogFileNameByPath(inputxmlpath);
		XxlJobFileAppender.makeLogFileNameByPath(outputxmlpath);
		XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
		// 回调需要用到
		triggerParam.setLogFileName(outputlogpath);

		/** =======3.生成输入xml=========== **/
		XxlJobLogger.logByfile(outputlogpath, "开始执行_xml");
		String type = (String)fixmap.get("identify");
		List<XmlDTO>  inputList = new ArrayList<>();
		List<XmlDTO>  outputList = new ArrayList<>();
		Iterator iterator = dymap.entrySet().iterator();
		while(iterator.hasNext()){
			XmlDTO to = new XmlDTO();
			Map.Entry entry = (Map.Entry) iterator.next();
			to.setIdentify(entry.getKey().toString());
			to.setValue(entry.getValue().toString());
			to.setDescription(" ");
			to.setType("string");
			if(entry.getKey().toString().equalsIgnoreCase("outFolder")){
				outputList.add(to);
			}else{
				inputList.add(to);
			}
		}

		XmlDTO inputFileXmlDTO = new XmlDTO();
		inputFileXmlDTO.setIdentify("inputFile");
		inputFileXmlDTO.setValue(new File(triggerParam.getExecutorParams()).getParentFile().getParent());
		inputFileXmlDTO.setDescription(" ");
		inputFileXmlDTO.setType("string");
		inputList.add(inputFileXmlDTO);
		XmlDTO issueXmlDTO = new XmlDTO();
		issueXmlDTO.setIdentify("issue");
		issueXmlDTO.setValue(issue);
		issueXmlDTO.setDescription(" ");
		issueXmlDTO.setType("string");
		inputList.add(issueXmlDTO);

		XmlDTO outXMLPathXmlDTO = new XmlDTO();
		outXMLPathXmlDTO.setIdentify("outXMLPath");
		outXMLPathXmlDTO.setValue(outputxmlpath);
		outXMLPathXmlDTO.setDescription(" ");
		outXMLPathXmlDTO.setType("string");
		outputList.add(outXMLPathXmlDTO);
		XmlDTO outLogPathXmlDTO = new XmlDTO();
		outLogPathXmlDTO.setIdentify("outLogPath");
		outLogPathXmlDTO.setValue(outputlogpath);
		outLogPathXmlDTO.setDescription(" ");
		outLogPathXmlDTO.setType("string");
		outputList.add(outLogPathXmlDTO);
		XmlUtils.createAlgorithmXml(type,inputList,outputList,inputxmlpath);
		fixmap.put("inputxmlpath",inputxmlpath);
		dymap.put("inputFile",new File(triggerParam.getExecutorParams()).getParentFile().getParent());
		dymap.put("outXMLPath",outputxmlpath);
		dymap.put("outLogPath",outputlogpath);
		dymap.put("issue",issue);
		result.setCode(0);
		return result;
	}
}

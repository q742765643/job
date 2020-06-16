package com.htht.job.executor.hander.producthandler.service;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;
import org.dom4j.Element;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Transactional
@Service("nDVIProductHandlerService")
public class NDVIHandlerService {

	@Autowired
	private ProductInfoService productInfoService;
	@Autowired
	private ProductFileInfoService productFileInfoService;
	@Autowired
	private DataMataInfoService dataMataInfoService;
	@Autowired
	private ProductUtil productUtil;
	@Autowired
	private AtomicAlgorithmService atomicAlgorithmService;

	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String algorId = triggerParam.getAlgorId();
			LinkedHashMap dymap = triggerParam.getDynamicParameter();
			LinkedHashMap fixmap = triggerParam.getFixedParameter();
			String exePath = (String) fixmap.get("exePath");

			// 输出xml目录
			String xmlDir = (String) dymap.get("dirxml");
			// 输出文件路径
			String outputDir = (String) dymap.get("outDir");
			// 周期
			String cycle = (String) dymap.get("cycle");
			// 取出每次分片文件名
			String fileName = new File(triggerParam.getExecutorParams()).getName();
			// 从文件名中获取期次
			// String issue = Arrays.asList(fileNames.split("_")).get(5);
			Date fileDate = dataMataInfoService.findDataByFileNameAndLevel(fileName, "L2");
			String issue = org.htht.util.MatchTime.matchIssue(fileDate, cycle);
			// 算法标识
			AtomicAlgorithm at = atomicAlgorithmService.findModelIdentifyById(algorId);

			/** =======1.拼装dymap=========== **/
			String outputXmlpath = xmlDir +File.separator+ "outputXml"+File.separator
					+ at.getModelIdentify() + fileName + ".xml";
			ServerImpUtil.touchFile(outputXmlpath);
			String inputXmlpath = xmlDir + File.separator+"inputXml"+File.separator
					+ at.getModelIdentify() + fileName + ".xml";
			ServerImpUtil.touchFile(inputXmlpath);

			String outputLogPath=triggerParam.getLogFileName();

			/** =======2.创建日志文件=========== **/
			XxlJobFileAppender.makeLogFileNameByPath(outputLogPath);
			// 回调需要用到
			triggerParam.setLogFileName(outputLogPath);

			/** =======3.判断是否存在文件=========== **/
			List<ProductInfo> productFile = productInfoService.findProductExits(triggerParam.getProductId(), issue, cycle, at.getModelIdentify(), fileName,(String)dymap.get("areaID"));
			if (productFile.size() > 0) {
				XxlJobLogger.logByfile(outputLogPath, " 产品已生产");
				result.setMessage("already done");
				return result;
			}

			/** =======4.生成文件(输入xml)=========== **/
			XxlJobLogger.logByfile(outputLogPath, "开始执行_xml");
			List<XmlDTO> inputList = new ArrayList<>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("areaID", dymap.get("areaID"));
			map.put("issue", issue);
			map.put("cycle", cycle);
			map.put("inputFY3B", triggerParam.getExecutorParams());
			map.put("mosaicPath",dymap.get("mosaicPath"));
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
			map2.put("outFolder", outputDir);
			map2.put("outXMLPath", outputXmlpath);
			map2.put("outLogPath", outputLogPath);
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
			String type = "NDVI_FY3BVIRR";
			
			XmlUtils XmlUtils = new XmlUtils();
			XmlUtils.createAlgorithmXml(type, inputList, outputList, inputXmlpath);
			//TODO
//			XmlUtils.createAlgorithmXml(at.getModelIdentify(), inputList, outputList, inputxmlpath);

			if (!result.isSuccess()) {
				return result;
			}
			
			/** =======5.执行脚本=========== **/
			XxlJobLogger.logByfile(outputLogPath, "正在执行_运行");
			ServerImpUtil.executeCmd(exePath, inputXmlpath);

			/** ========6.脚本 结束入库======= **/
			boolean b = XmlUtils.isSuccessByXml(outputXmlpath);
			if (!b) {
				result.setErrorMessage("产品生产失败");
				XxlJobLogger.logByfile(outputLogPath, "输出xml为空，生产失败");
				return result;
			}
			XxlJobLogger.logByfile(outputLogPath, "正在执行_入库");

			List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(outputXmlpath, "mosaicFile");
			String mosaicFile = "";
			if (mosaicFiles.size() > 0) {
				mosaicFile = mosaicFiles.get(0);
			}
			Map<String,List<Element>> outputFiles = XmlUtils
					.outputFilesXmlToMap(outputXmlpath);
			List<String> regionIdList = XmlUtils.getXmlAttrVal(outputXmlpath, "region",
					"identify");

			// 产品信息及文件信息入库
			ProductInfo productInfo = new ProductInfo();
			if(regionIdList!=null&&regionIdList.size()>0) {
				for(String regionId:regionIdList) {
					 productInfo = productUtil.saveProductInfo(
							triggerParam.getProductId(), regionId, issue,
							(String) dymap.get("cycle"), mosaicFile,
							at.getModelIdentify(), fileName);// TODO


					List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(
							outputXmlpath, "region","identify",regionId);
					for (String file : lFiles) {
						productUtil.saveProductInfoFile(productInfo.getId(), file,
								(String) dymap.get("outDir"), regionId, issue,
								(String) dymap.get("cycle"));
					}
				}
			}

			// 产品信息及文件信息入库 TODO 读xml
//			String path = outputDir +File.separator+issue.substring(0,6)+File.separator+issue+"_"+cycle;
//			List<File> lFiles = FileUtil.iteratorFile(new File(path), null);
//			for (File f : lFiles) {
//				String regionId = f.getParentFile().getName();
//				List<ProductInfo> pis = productInfoService.findProductExits(triggerParam.getProductId(), issue, cycle, at.getModelIdentify(), fileName,regionId);
//				ProductInfo productInfo =null;
//				if(pis!=null && pis.size()>0){
//					productInfo = pis.get(0);
//				}else{
//					productInfo = productUtil.saveProductInfo(triggerParam.getProductId(), regionId, issue, cycle, mosaicFile, at.getModelIdentify(), fileName);// TODO
//				}
////				F:\NDVI\outputfile\hubei\output\product\2018\201806250000_COOD\421126000000\VGT_NDVI_ZCUR_201806250000_421126000000.jpg
//				//增加判断是否存在
//				productUtil.saveProductInfoFile(productInfo.getId(), f.getAbsolutePath(), outputDir, regionId,issue,cycle);
//			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;

	}

}

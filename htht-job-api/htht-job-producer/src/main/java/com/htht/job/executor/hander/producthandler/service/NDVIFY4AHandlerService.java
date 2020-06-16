package com.htht.job.executor.hander.producthandler.service;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service("nDVIFY4AHandlerService")
public class NDVIFY4AHandlerService {

	private static final String String = null;
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

	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
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
			//分辨率
			//输入分辨率
			String inputData = (String) dymap.get("inputData");
			String resolution = inputData.split("_")[2];
			Date fileDate = new SimpleDateFormat("yyyyMMdd").parse(fileName);
			String issue = com.htht.job.core.util.MatchTime.matchIssue(fileDate, cycle);
			// 算法标识
			AtomicAlgorithmDTO at = atomicAlgorithmService.findModelIdentifyById(algorId);

			/** =======1.拼装dymap=========== **/
			String outputXmlpath = xmlDir +File.separator+ "outputXml"+File.separator
					+ at.getModelIdentify() + fileName +"(" + triggerParam.getLogId() +  ").xml";
			ServerImpUtil.touchFile(outputXmlpath);
			String inputXmlpath = xmlDir + File.separator+"inputXml"+File.separator
					+ at.getModelIdentify() + fileName + "(" + triggerParam.getLogId() +  ").xml";
			ServerImpUtil.touchFile(inputXmlpath);

			String outputLogPath=triggerParam.getLogFileName();

			/** =======2.创建日志文件=========== **/
			XxlJobFileAppender.makeLogFileNameByPath(outputLogPath);
			// 回调需要用到
			triggerParam.setLogFileName(outputLogPath);

			/** =======3.判断是否存在文件=========== **/
			List<ProductInfoDTO> productFile = productInfoService.findProductExits(triggerParam.getProductId(), issue, cycle, at.getModelIdentify(), "",(String)dymap.get("areaID"));
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
			map.put("inputFY4A", triggerParam.getExecutorParams().replaceAll("\\\\", "/")+"/");
			map.put("mosaicPath",dymap.get("mosaicPath"));
			map.put("pixel", resolution);
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
			map2.put("outDir", outputDir);
			map2.put("dirxml", outputXmlpath);
			map2.put("dirlog", outputLogPath);
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
			String type = "NDVI_DAILY_FY4AAGRI";
			XmlUtils.createAlgorithmXml(type, inputList, outputList, inputXmlpath);
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
			ProductInfoDTO productInfoDTO = new ProductInfoDTO();
			if(regionIdList!=null&&regionIdList.size()>0) {
				for(String regionId:regionIdList) {
					 productInfoDTO = productUtil.saveProductInfo(
							triggerParam.getProductId(), regionId, issue,
							(String) dymap.get("cycle"), mosaicFile,
							at.getModelIdentify(), fileName);


					List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(
							outputXmlpath, "region","identify",regionId);
					for (String file : lFiles) {
						productUtil.saveProductInfoFile(productInfoDTO.getId(), file,
								(String) dymap.get("outDir"), regionId, issue,
								(String) dymap.get("cycle"));
					}
				}
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;

	}

}

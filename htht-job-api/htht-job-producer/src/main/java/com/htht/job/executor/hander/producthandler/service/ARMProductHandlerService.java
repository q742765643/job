package com.htht.job.executor.hander.producthandler.service;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.MatchTime;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.util.XmlUtils;

import org.htht.util.DataTimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

/**
 * Created by atom on 2018/11/9.
 */

@Transactional
@Service("ARMProductHandlerService")
public class ARMProductHandlerService extends StandardService {

	@Autowired
	private ProductUtil productUtil;

	
	
	

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
        
        String timePattern = (String) fixmap.get("timePattern");
        fileDate = new Date(DataTimeHelper.getDataTimeFromFileNameByPattern(fileName, timePattern));
        
        String outFolder = (String)dymap.get("outFolder");
        // 获取期号
        String issue = MatchTime.matchIssue(fileDate,cycle);
        issue=issue.substring(0, 6)+"000000";
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
                + File.separator + fileName + ".xml";

        String outputxmlpath = outFolder + File.separator + "outputXml" + File.separator + at.getModelIdentify()
                + File.separator + fileName + ".xml";
        
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
        inputFileXmlDTO.setValue(new File(triggerParam.getExecutorParams()).getParent());
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
        dymap.put("inputFile",triggerParam.getExecutorParams());
        dymap.put("outXMLPath",outputxmlpath);
        dymap.put("outLogPath",outputlogpath);
        dymap.put("issue",issue);
        result.setCode(0);
        return result;
	}





	@Override
	void statistics(TriggerParam triggerParam, String outputxmlpath, String outputlogpath, AtomicAlgorithmDTO at, String fileName, String issue) {
		LinkedHashMap dymap=triggerParam.getDynamicParameter();
		List<String> regionIdList = XmlUtils.getXmlAttrVal(outputxmlpath, "region", "identify");
		List<String> mosaicFiles = XmlUtils.getXmlAttrFileElementVal(outputxmlpath, "mosaicFile");
		String mosaicFile = "";
		if (mosaicFiles.size() > 0) {
			mosaicFile = mosaicFiles.get(0);
		}

		// 产品信息及文件信息入库
		ProductInfoDTO productInfoDTO = new ProductInfoDTO();
		if (regionIdList != null && regionIdList.size() > 0) {
			for (String regionId : regionIdList) {
				productInfoDTO = productUtil.saveProductInfo(triggerParam.getProductId(), regionId, issue,
						(String) dymap.get("cycle"), mosaicFile, at.getModelIdentify(), fileName);

				List<String> lFiles = XmlUtils.getXmlAttrFileElementVal(outputxmlpath, "region", "identify",
						regionId);
				if (lFiles != null && lFiles.size() > 0) {
					for (String file : lFiles) {
						productUtil.saveProductInfoFile(productInfoDTO.getId(), file,
								(String) dymap.get("outFolder"), regionId, issue, (String) dymap.get("cycle"));
					}
				}

			}
		}
	}
}

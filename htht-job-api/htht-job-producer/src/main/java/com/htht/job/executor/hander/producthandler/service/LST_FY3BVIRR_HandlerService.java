package com.htht.job.executor.hander.producthandler.service;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.model.xml.XmlDTO;
import com.htht.job.executor.service.product.ProductUtil;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.util.XmlUtils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.htht.util.DataTimeHelper;
import com.htht.job.core.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

@Transactional
@Service("lSTFY3BHandlerService")
public class LST_FY3BVIRR_HandlerService extends StandardService {
	@Override
	ResultUtil<String> makeXml(TriggerParam triggerParam,AtomicAlgorithmDTO at,ResultUtil<String> result){
		LinkedHashMap dymap = triggerParam.getDynamicParameter();
		LinkedHashMap fixmap = triggerParam.getFixedParameter();

		String proMark = (String) fixmap.get("proMark");
		//取出每次分片文件名
		String fileName = new File(triggerParam.getExecutorParams()).getName();
		File f=new File(triggerParam.getExecutorParams());
		/** =======1.获取期次信息=========== **/
		String cycle = (String) dymap.get("cycle");
		proMark = StringUtils.trimAllWhitespace(proMark).toLowerCase();
		Date fileDate = null;
		String inputFolder="";
		String dataSuffix = (String) fixmap.get("dataSuffix");
		if(f.isDirectory()) {
    		fileName = fileName+dataSuffix;
    		inputFolder=triggerParam.getExecutorParams()+File.separator+fileName;
    	}else {
    		inputFolder=triggerParam.getExecutorParams();
    	}
		String dataLevel = (String)fixmap.get("dataLevel");
		fileDate = dataMataInfoService.findDataByFileNameAndLevel(fileName,StringUtils.isEmpty(dataLevel)? "L2":dataLevel);
		

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
				+ File.separator + fileName + ".xml";

		String outputxmlpath = outFolder + File.separator + "outputXml" + File.separator + at.getModelIdentify()
				+ File.separator + fileName + ".xml";
		fixmap.put("inputxmlpath",inputxmlpath);
		// 获取期号
		String month = issue.substring(4,6);
		String season = "";
		if(month.equals("03") || month.equals("04") || month.equals("05")){
			season = "LST_FY3BVIRRSP";
		}else if (month.equals("06") || month.equals("07") || month.equals("08")){
			season = "LST_FY3BVIRRSU";
		}else if(month.equals("09") || month.equals("10") || month.equals("11")){
			season = "LST_FY3BVIRRFA";
		}else if (month.equals("12") || month.equals("01") || month.equals("02")){
			season = "LST_FY3BVIRRWI";
		}
		
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
		inputFileXmlDTO.setValue(new File(triggerParam.getExecutorParams()).getAbsolutePath()+File.separator + fileName);
		inputFileXmlDTO.setDescription(" ");
		inputFileXmlDTO.setType("string");
		inputList.add(inputFileXmlDTO);
		XmlDTO sessionXmlDTO = new XmlDTO();
		sessionXmlDTO.setIdentify("season");
		sessionXmlDTO.setValue(season);
		sessionXmlDTO.setDescription(" ");
		sessionXmlDTO.setType("string");
		inputList.add(sessionXmlDTO);
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
		dymap.put("issue",issue);
		dymap.put("season",season);
		dymap.put("inputFile",triggerParam.getExecutorParams());
		dymap.put("outXMLPath",outputxmlpath);
		dymap.put("outLogPath",outputlogpath);
		result.setCode(0);
		return result;
	}
}

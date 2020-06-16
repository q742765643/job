package com.htht.job.executor.hander.h8.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import com.htht.job.core.util.MatchTime;
import org.htht.util.ServerImpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;

@Transactional
@Service("h8ProHanderService")
public class H8ProHanderService {
	private Logger logger = LoggerFactory.getLogger(H8ProHanderService.class);
//	@Autowired
//	private ProductInfoService productInfoService;
//	@Autowired
//	private ProductUtil productUtil;
//	@Autowired
//	private AtomicAlgorithmService atomicAlgorithmService;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
	private SimpleDateFormat issueFormat = new SimpleDateFormat("yyyyMMddHHmm");

	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		/** 1.获取参数列表 **/
		LinkedHashMap<?, ?> fixmap = triggerParam.getFixedParameter();
		LinkedHashMap<?, ?> dymap = triggerParam.getDynamicParameter();
		ArrayList<String> issueList = new ArrayList<>(); 
		/** 2.解析产品参数 **/
		String exePath = (String) fixmap.get("exePath");

		String inputIssue = (String) dymap.get("issue");
		String cycle = (String) dymap.get("cycle");
		String inputPath = FileUtil.formatePath((String) dymap.get("inputPath"));
		String outputlog = FileUtil.formatePath((String) dymap.get("outputlog"));
		String outputxml = FileUtil.formatePath((String) dymap.get("outputxml"));
		String outputPath = FileUtil.formatePath((String) dymap.get("outputPath"));
		if (null == inputIssue) {
			/** 期号不存在 **/
			String startIssue = (String) dymap.get("startIssue");
			String endIssue = (String) dymap.get("endIssue");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			try {
				Date doStartTime = sdf.parse(MatchTime.matchIssue(startIssue,cycle));
				Date doEndTime = sdf.parse(MatchTime.matchIssue(endIssue,cycle));
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(doStartTime);
				String issue = "";
				while(doStartTime.compareTo(doEndTime)<=0){
					issue = MatchTime.matchIssue(sdf.format(doStartTime),cycle);
					if(!"".equals(issue) && !issueList.contains(issue)){
						issueList.add(issue);
					}
					calendar.add(Calendar.MINUTE, 10);
					doStartTime = calendar.getTime();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}else {
			issueList.add(MatchTime.matchIssue(inputIssue, cycle));
		}
		
		/** 3.判断该期是否执行 **/
		for(String issue : issueList) {
			if (isExist(issue)) {
				result.setMessage("already done");
				return result;
			}
			try {
				Date issueDate = issueFormat.parse(issue);
				/** 4.拼装map **/
				String tempFileName = "H8Pro_" + issue + "(" + df.format(new Date()) + ")";
				String inputxmlpath = outputPath + "inPutXml/" + tempFileName + ".xml";
				String outputxmlpath = outputxml + tempFileName + ".xml";
				String issueInputPath = FileUtil.getPathByDate(inputPath, issueDate);
				if(!new File(issueInputPath).exists()) continue;
//				String outputxmlpath = "D:\\product\\h8Pro\\outputxml\\test.xml";
				String algLog = outputlog + tempFileName + ".log";
				HashMap<String, Object> inputxmlParamMap = new HashMap<>();
				inputxmlParamMap.put("InputFile", issueInputPath);
				inputxmlParamMap.put("DateTime", issue);
				inputxmlParamMap.put("FullMask_GridPath", (String) dymap.get("FullMask_GridPath"));
				inputxmlParamMap.put("Bands", (String) dymap.get("Bands"));
				inputxmlParamMap.put("Stripe", (String) dymap.get("Stripe"));
				inputxmlParamMap.put("OutputFile", outputPath);
				inputxmlParamMap.put("TempFile", FileUtil.formatePath((String) dymap.get("TempFile")));
				inputxmlParamMap.put("Resolution", (String) dymap.get("Resolution"));
				inputxmlParamMap.put("Region", (String) dymap.get("Region"));
				inputxmlParamMap.put("outputxml", outputxmlpath);
				inputxmlParamMap.put("outputlog", algLog);
				
				if (!createAlgorithmXml("H8ProcessParam", inputxmlParamMap, inputxmlpath)) {
					result.setMessage("create inputxml failed");
					return result;
				}
				/** 5.设置调度日志 **/
				String outputlogpath = triggerParam.getLogFileName();
				triggerParam.setLogFileName(outputlogpath);
				/** 6.开始执行调度 **/
				/** ========6.脚本 结束入库======= **/
				ServerImpUtil.executeCmd(exePath, inputxmlpath);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		
//		if (XmlUtils.isSuccessByXml(outputxmlpath)) {
//			result.setErrorMessage("产品生产失败");
//			return result;
//		}
		/** 返回结果 **/
		return result;
	}

	private boolean createAlgorithmXml(String rootTag, HashMap<String, Object> inputxmlParamMap, String inputxmlpath) {
		File file = new File(inputxmlpath);
		if(!FileUtil.createFile(file)) {
			return false;
		}
        
		// 创建文档对象
		Document doc = DocumentHelper.createDocument();
		// 创建根节点
		Element root = doc.addElement(rootTag);
		Set<String> keys = inputxmlParamMap.keySet();
		for (String key : keys) {
			root.addElement(key).setText((String) inputxmlParamMap.get(key));
		}
		// 设置XML文档格式
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		// 设置XML编码方式,即是用指定的编码方式保存XML文档到字符串(String),这里也可以指定为GBK或是ISO8859-1
		outputFormat.setEncoding("UTF-8");
		outputFormat.setNewlines(true); // 设置是否换行
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(outputFormat);
			FileOutputStream fos = new FileOutputStream(inputxmlpath);
			assert writer != null;
            writer.setOutputStream(fos);
            writer.write(doc);
            writer.close();
            logger.info("写入消灭了成功{}", inputxmlpath);
            return true;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isExist(String issue) {
		return false;
	}
}

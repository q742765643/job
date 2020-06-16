package com.htht.job.executor.plugin.preprocessing.shard;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import com.htht.job.executor.plugin.preprocessing.service.SunFlower8ProcessService;

@Service("sunFlower8ProcessHandlerShard")
public class SunFlower8ProcessHandlerShard implements SharingHandler{
	
	private static Logger logger = LoggerFactory.getLogger(SunFlower8ProcessHandlerShard.class.getName());
	@Autowired
	private SunFlower8ProcessService sunFlower8ProcessService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		ProductParam productParam = JSON.parseObject(params, ProductParam.class);
		
		/*** ======1.获取开始和结束日期========== ***/
		
		HashMap<String, Date> rangeDate = new HashMap<String, Date>();
		try {
			rangeDate = sunFlower8ProcessService.getRangeDate(productParam,dymap,"COOH");
		} catch (Exception e) {
			result.setErrorMessage("数据开始时间和结束时间获取失败");
			return result;
		}
		Date doStartTime = rangeDate.get("doStartTime");
		Date doEndTime = rangeDate.get("doEndTime");
		String filePath = (String) dymap.get("inputPath");
		String issue = (String) dymap.get("issue");
		issue = issue.replace("，", ",").replace(" ", ",").replace("、", ",");
		String fileFormat = (String) dymap.get("fileRangeFormat");
		logger.info("fileRangeFormat: " +fileFormat);

		/*** ======2.获取该时间段内文件的期号========== ***/
		System.out.println("doStartTime" + doStartTime);
		System.out.println("doEndTime" + doEndTime);
		List<String> issuees = sunFlower8ProcessService.getIssuees(productParam.getProdname(), filePath, doStartTime, doEndTime, issue, fileFormat);
		result.setResult(issuees);
		return result;
	}

}

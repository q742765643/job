package com.htht.job.executor.plugin.product.shard;

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
import com.htht.job.executor.plugin.product.service.HotWindForecastService;

@Service("hotWindForecastHandlerShard")
public class HotWindForecastHandlerShard implements SharingHandler {
	
	private static Logger logger = LoggerFactory.getLogger(HotWindForecastHandlerShard.class.getName());

	@Autowired
	private HotWindForecastService hotWindForecastService;

	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		
		logger.info("进入干热风预测分片广播~");
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		ProductParam productParam = JSON.parseObject(params, ProductParam.class);
		
		/*** ======1.获取开始和结束日期========== ***/
		HashMap<String, Date> rangeDate = new HashMap<String, Date>();
		try {
			rangeDate = hotWindForecastService.getRangeDate(productParam, dymap, null);
		} catch (Exception e) {
			result.setErrorMessage("数据开始时间和结束时间获取失败");
			return result;
		}
		Date doStartTime = rangeDate.get("doStartTime");
		Date doEndTime = rangeDate.get("doEndTime");
		String filePath = (String) dymap.get("inputPath");
		String issue = (String) dymap.get("issue");
		issue = issue.replace("，", ",").replace(" ", ",").replace("、", ",");
		
		/*** ======2.获取该时间段内文件的期号========== ***/
		List<String> issuees = hotWindForecastService.getIssuees(filePath, doStartTime, doEndTime, issue, null);
		result.setResult(issuees);
		
		return result;
	}

}

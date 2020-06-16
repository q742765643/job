package com.htht.job.executor.plugin.product.shard;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import com.htht.job.executor.plugin.product.service.SnowMonitorService;

@Service("snowMonitorHandlerShard")
public class SnowMonitorHandlerShard implements SharingHandler {

	@Autowired
	private SnowMonitorService snowMonitorService;

	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		System.out.println("进入积雪监测分片广播~");
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		ProductParam productParam = JSON.parseObject(params, ProductParam.class);
		
		/*** ======1.获取开始和结束日期========== ***/
		HashMap<String, Date> rangeDate = new HashMap<String, Date>();
		try {
			rangeDate = snowMonitorService.getRangeDate(productParam, dymap, null);
		} catch (Exception e) {
			result.setErrorMessage("数据开始时间和结束时间获取失败");
			return result;
		}
		Date doStartTime = rangeDate.get("doStartTime");
		Date doEndTime = rangeDate.get("doEndTime");
		String filePath = (String) dymap.get("inputPath");
		String issue = (String) dymap.get("issue");
		issue = issue.replace("，", ",").replace(" ", ",").replace("、", ",");
		String fileFormat = (String) dymap.get("fileFormat");
		if (null == fileFormat || "".equals(fileFormat)) {
			fileFormat = null;
		}
		
		/*** ======2.获取该时间段内文件的期号========== ***/
		List<String> issuees = snowMonitorService.getIssuees(filePath, doStartTime, doEndTime, issue, fileFormat);
		
		
		result.setResult(issuees);
		
		return result;
	}

}

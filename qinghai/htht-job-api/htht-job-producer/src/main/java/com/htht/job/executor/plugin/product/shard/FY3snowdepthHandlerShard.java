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
import com.htht.job.executor.plugin.product.service.FY3snowdepthService;

@Service("FY3snowdepthHandlerShard")
public class FY3snowdepthHandlerShard implements SharingHandler {

	@Autowired
	private FY3snowdepthService fy3snowdepthService;

	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		ProductParam productParam = JSON.parseObject(params, ProductParam.class);
		
		/*** ======1.获取开始和结束日期========= ***/
		HashMap<String, Date> rangeDate = new HashMap<String, Date>();
		try {
			rangeDate = fy3snowdepthService.getRangeDate(productParam, dymap, null);//{doStartTime=Tue Dec 04 00:00:00 CST 2018, doEndTime=Thu Dec 06 23:59:59 CST 2018}
		} catch (Exception e) {
			result.setErrorMessage("数据开始时间和结束时间获取失败");
			return result;
		}
		Date doStartTime = rangeDate.get("doStartTime");
		Date doEndTime = rangeDate.get("doEndTime");
		String filePath = (String) dymap.get("inputPath");
		String issue = (String) dymap.get("issue");//{yyyy}{MM}{dd-6}
		issue = issue.replace("，", ",").replace(" ", ",").replace("、", ",");//{yyyy}{MM}{dd-6}
		String fileFormat = (String) dymap.get("fileFormat");
		if (null == fileFormat || "".equals(fileFormat)) {
			fileFormat = null;
		}
		/*** ======2.获取该时间段内文件的期号========== ***/
		List<String> issuees = fy3snowdepthService.getIssuees(filePath, doStartTime, doEndTime, issue, fileFormat);
		
		result.setResult(issuees);
		
		return result;
	}

}

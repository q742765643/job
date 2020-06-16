package com.htht.job.executor.hander.shardinghandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import com.htht.job.executor.plugin.common.BusinessConst;

@Service("DrtSumFY3DHandlerShard")
public class DrtSumFY3DHandlerShard implements SharingHandler {
	
	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		ProductParam productParam = JSON.parseObject(params, ProductParam.class);
		
		/*** ======1.获取开始和结束日期========= ***/
		Date doStartTime = null;
		Date doEndTime = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		Calendar c = Calendar.getInstance();
		if (String.valueOf(BusinessConst.PROCESSTASKPRODUCT_DATETYPE_REAL_TIME).equals(productParam.getDateType())) {
			// 实时
			Date issueDate = new Date();
			c.setTime(issueDate);
			doEndTime = c.getTime();
			if (null != productParam.getProductRangeDay() && !"".equals(productParam.getProductRangeDay())) {
				c.add(Calendar.DATE, -(Integer.parseInt(productParam.getProductRangeDay())));
			}
			doStartTime = c.getTime();
		} else if (String.valueOf(BusinessConst.PROCESSTASKPRODUCT_DATETYPE_HISTORICAL_TIME)
				.equals(productParam.getDateType())) {
			// 历史
			String[] temp = productParam.getProductRangeDate().split(" - ");
			String stratTime = temp[0];
			String endTime = temp[1];
			
			doStartTime = sdf.parse(stratTime);
			doEndTime = sdf.parse(endTime);
		}
		
		List<String> issuees = new ArrayList<String>();
		List<Date> findDates = findDates(doStartTime,doEndTime);
		for(Date d:findDates){
			issuees.add(sdf1.format(d) + "0000");
		}
		
		result.setResult(issuees);
		
		return result;
	}
	
	private static List<Date> findDates(Date dBegin, Date dEnd) {
		List<Date> lDate = new ArrayList<Date>();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	}
}

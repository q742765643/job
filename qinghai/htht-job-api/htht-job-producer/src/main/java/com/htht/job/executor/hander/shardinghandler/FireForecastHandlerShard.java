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

@Service("FireForecastHandlerShard")
public class FireForecastHandlerShard implements SharingHandler {
	
	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		ProductParam productParam = JSON.parseObject(params, ProductParam.class);
		
		/*** ======1.获取开始和结束日期========= ***/
		Date doStartTime = null;
		Date doEndTime = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
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
			String stratTime = temp[0].replace("-", "");
			String endTime = temp[1].replace("-", "");
			
			doStartTime = sdf.parse(stratTime);
			doEndTime = sdf.parse(endTime);
		}
		
		List<String> issuees = getMonthBetween(doStartTime,doEndTime);
		ArrayList<String> isLst = new ArrayList<String>();
		for(String s:issuees){
			isLst.add(s.substring(0, 6) + "050000");
		}
		result.setResult(isLst);
		
		return result;
	}
	
	private static List<String> getMonthBetween(Date minDate, Date maxDate){
        ArrayList<String> result = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//格式化为年月
 
            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();
            min.setTime(minDate);
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
 
            max.setTime(maxDate);
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
 
            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return result;
    }
}

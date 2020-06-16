package com.htht.job.executor.hander.cimiss.handler;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.executor.hander.cimiss.service.CimissInterfaceService;
import com.htht.job.executor.util.CImissMatchTime;

@JobHandler(value = "CimissDownloadHandler")
@Service
public class CimissDownloadHandler extends IJobHandler {

	@Autowired
	private CimissInterfaceService cimissInterfaceService;

	@Override
	public ReturnT<String> execute(TriggerParam arg0) throws Exception {
		System.out.println("进入  CimissDownloadHandler...");
		ReturnT<String> result = new ReturnT<>();
		try {

			Map fixedParameter = arg0.getFixedParameter();
			String interfaceId = (String) fixedParameter.get("interfaceId");
			String dataCode = (String) fixedParameter.get("dataCode");
			String timesType = (String) fixedParameter.get("times");
			String adminCodes = (String) fixedParameter.get("adminCodes");
			String elements = (String) fixedParameter.get("elements");
			String dataFormat = (String) fixedParameter.get("dataFormat");
			String queryCondition = (String) fixedParameter.get("queryCondition");
			String limitCnt = (String) fixedParameter.get("limitCnt");
			String onlyAdd = (String) fixedParameter.get("onlyAdd");
			String startTime = null;
			String endTime = null;
			
			if (fixedParameter.containsKey("startTime")	
					&& StringUtils.isNotEmpty((String) fixedParameter.get("startTime"))) {
				startTime = (String) fixedParameter.get("startTime");
			}
			if (fixedParameter.containsKey("endTime")
					&& StringUtils.isNotEmpty((String) fixedParameter.get("endTime"))) {
				endTime = (String) fixedParameter.get("endTime");
			}
			Date startDate = new Date();
			if (startTime != null) {
				startDate = DateUtil.strToDate(startTime, "yyyy-MM-dd");
			}
			Date endDate = new Date();
			if (endTime != null) {
				endDate = DateUtil.strToDate(endTime, "yyyy-MM-dd");
			}
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);
			
			//新增补充	DAY MON TEN
			if(fixedParameter.containsKey("redo") ){
				String redo = (String) fixedParameter.get("redo");
				if(StringUtils.isNotEmpty(redo)){
					if("DAY".equalsIgnoreCase(redo)){
						int day = c.get(Calendar.DAY_OF_MONTH);
						c.set(Calendar.DAY_OF_MONTH, day/10);
						startDate = c.getTime();
						if(day/10 > 2){
							c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DATE));
						}else{
							c.set(Calendar.DAY_OF_MONTH, day/10 * 10);
						}
						endDate = c.getTime();
					}else if("TEN".equalsIgnoreCase(redo)){
						c.add(Calendar.MONTH, -1);
						c.set(Calendar.DAY_OF_MONTH, 1);
						startDate = c.getTime();
						c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DATE));
						endDate = c.getTime();
					}else if("MON".equalsIgnoreCase(redo)){
						c.add(Calendar.MONTH, -1);
						c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DATE));
						endDate = c.getTime();
						c.add(Calendar.MONTH, -1);
						c.set(Calendar.DAY_OF_MONTH, 1);
						startDate = c.getTime();
					}
					timesType = "{yyyy}{MM}{dd}000000";
				}
			}
			while (c.getTimeInMillis() <= endDate.getTime()) {
				String times = CImissMatchTime.match(c,timesType);
				cimissInterfaceService.getCimissData(interfaceId, dataCode,	times, adminCodes, elements, dataFormat,
						queryCondition, limitCnt,onlyAdd);
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
		} catch (Exception e) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}

}

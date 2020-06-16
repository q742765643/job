package com.htht.job.executor.hander.cimiss.handler;

import java.util.Map;

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
		Map fixedParameter = arg0.getFixedParameter();
		String interfaceId = (String) fixedParameter.get("interfaceId");
		String dataCode	 = (String) fixedParameter.get("dataCode");
		String times = CImissMatchTime.match((String) fixedParameter.get("times"));
		String adminCodes = (String) fixedParameter.get("adminCodes");
		String elements = (String) fixedParameter.get("elements");
		String dataFormat = (String) fixedParameter.get("dataFormat");
		String queryCondition = (String) fixedParameter.get("queryCondition");
		String limitCnt = (String) fixedParameter.get("limitCnt");
		cimissInterfaceService.getCimissData(interfaceId, dataCode, times, adminCodes, elements, dataFormat,queryCondition,limitCnt);
		return null;
	}

}

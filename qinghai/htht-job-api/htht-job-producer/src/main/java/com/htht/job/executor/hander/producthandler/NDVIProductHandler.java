package com.htht.job.executor.hander.producthandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.producthandler.service.NDVIHandlerService;

@JobHandler(value = "nDVIProductHandler")
@Service
public class NDVIProductHandler extends IJobHandler {
	
	@Autowired
	private NDVIHandlerService ndviHandlerService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {	
		ResultUtil<String> result = new ResultUtil<String>();
		result = ndviHandlerService.execute(triggerParam, result);
		if (!result.isSuccess()){
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
	
}

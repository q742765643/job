package com.htht.job.executor.hander.producthandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.producthandler.service.DrtSumH8HandlerService;

@JobHandler(value = "DrtSumH8Handler")
@Service
public class DrtSumH8Handler extends IJobHandler{

	@Autowired
	private DrtSumH8HandlerService drtSumH8HandlerService;
	
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		
		ResultUtil<String> result = new ResultUtil<String>();
		result = drtSumH8HandlerService.execute(triggerParam, result);
		
		if (!result.isSuccess())
		{
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}
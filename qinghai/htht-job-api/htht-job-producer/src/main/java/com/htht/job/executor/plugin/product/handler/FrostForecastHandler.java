package com.htht.job.executor.plugin.product.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.plugin.product.service.FrostForecastService;

@Service
@JobHandler("frostForecastHandler")
public class FrostForecastHandler extends IJobHandler {

	@Autowired
	private  FrostForecastService frostForecastService;
	
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<String>();
		result = frostForecastService.execute(triggerParam, result);
		
		if (!result.isSuccess())
		{
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		
		return ReturnT.SUCCESS;
	}

}

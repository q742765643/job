package com.htht.job.executor.hander.producthandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.producthandler.service.LSTHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@JobHandler(value = "lSTProductHandler")
@Service
public class LSTProductHandler extends IJobHandler
{
	@Autowired
	private LSTHandlerService lstHandlerService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception
	{
		ResultUtil<String> result = new ResultUtil<String>();
		
		result = lstHandlerService.execute(triggerParam, result);
		
		if (!result.isSuccess())
		{
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		
		return ReturnT.SUCCESS;
	}

}

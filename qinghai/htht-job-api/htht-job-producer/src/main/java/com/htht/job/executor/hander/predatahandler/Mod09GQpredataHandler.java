package com.htht.job.executor.hander.predatahandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.Mod09GQpredataHandlerService;

@JobHandler(value = "Mod09GQpredataHandler")
@Service
public class Mod09GQpredataHandler extends IJobHandler{

	@Autowired
	private Mod09GQpredataHandlerService mod09GQpredataHandlerService;
	
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		
		ResultUtil<String> result = new ResultUtil<String>();
		result = mod09GQpredataHandlerService.execute(triggerParam, result);
		
		if (!result.isSuccess())
		{
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}
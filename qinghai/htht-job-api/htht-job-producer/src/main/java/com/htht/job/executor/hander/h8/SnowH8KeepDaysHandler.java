package com.htht.job.executor.hander.h8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.h8.service.SnowH8KeepDaysHandlerService;

@JobHandler(value = "SnowH8KeepDaysHandler")
@Service
public class SnowH8KeepDaysHandler extends IJobHandler {
	@Autowired
	private SnowH8KeepDaysHandlerService snowH8KeepDaysHandlerService;
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result=new 	ResultUtil<String>();
		/**=======2.执行业务=============================**/
		result=snowH8KeepDaysHandlerService.execute(triggerParam,result);
		if(!result.isSuccess()){
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		
		return ReturnT.SUCCESS;
	}

}

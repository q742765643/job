package com.htht.job.executor.hander.h8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.h8.service.SnowDepartureH8HanderService;

@JobHandler(value = "SnowDepartureH8Handler")
@Service
public class SnowDepartureH8Handler extends IJobHandler {
	@Autowired
	private SnowDepartureH8HanderService snowDepartureH8HanderService;
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result=new 	ResultUtil<String>();
		/**=======2.执行业务=============================**/
		result=snowDepartureH8HanderService.execute(triggerParam,result);
		if(!result.isSuccess()){
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		
		return ReturnT.SUCCESS;
	}

}

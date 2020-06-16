package com.htht.job.executor.hander.h8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ParsingUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.h8.service.SnowDayH8HanderService;

@JobHandler(value = "SnowDayH8Handler")
@Service
public class SnowDayH8Handler extends IJobHandler {
	@Autowired
	private SnowDayH8HanderService snowDayH8HanderService;
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result=new 	ResultUtil<String>();
		/**=======2.执行业务=============================**/
		result=snowDayH8HanderService.execute(triggerParam,result);
		if(!result.isSuccess()){
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		
		return ReturnT.SUCCESS;
	}

}

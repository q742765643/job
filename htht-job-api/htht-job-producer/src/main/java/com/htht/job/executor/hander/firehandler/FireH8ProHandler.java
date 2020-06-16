package com.htht.job.executor.hander.firehandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ParsingUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.fireservice.FireH8ProService;


/**
 * Created by zzp on 2018/12/26.
 */
@JobHandler(value = "fireH8ProHandler")
@Service
public class FireH8ProHandler extends IJobHandler {
	@Autowired
	private FireH8ProService fireH8ProService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result=new 	ResultUtil<>();
        /**=======1.执行业务=============================**/
		result=fireH8ProService.excute(triggerParam,result);
		if(!result.isSuccess()){
			return new ReturnT<>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}

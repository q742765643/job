package com.htht.job.executor.hander.dataarchiving.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.handler.service.DataNearLineHandlerService;

/**
 * 近线备份插件
 * 
 * @author LY 2018-05-29
 *
 */
@JobHandler(value = "dataNearLineHandler")
@Service
public class DataNearLineHandler extends IJobHandler {
	@Autowired
	private DataNearLineHandlerService dataNearLineHandlerService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<String>();
		result = dataNearLineHandlerService.execute(triggerParam, result);
		if (!result.isSuccess()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}

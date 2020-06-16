package com.htht.job.executor.hander.dataarchiving.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.handler.service.DataBackUpCleanHandlerService;
import com.htht.job.executor.hander.dataarchiving.handler.service.DataBackUpHandlerService;

/**
 * 删除在线、近线过期数据
 * 
 * @author LY 2018-05-29
 *
 */
@JobHandler(value = "dataBackUpCleanHandler")
@Service
public class DataBackUpCleanHandler extends IJobHandler {
	@Autowired
	private DataBackUpCleanHandlerService dataBackUpCleanHandlerService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<String>();
		result = dataBackUpCleanHandlerService.excute(triggerParam, result);
		if (!result.isSuccess()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}

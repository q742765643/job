package com.htht.job.executor.hander.dataarchiving.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.handler.service.DataToCheckAgainHandlerService;
import com.htht.job.executor.hander.dataarchiving.util.CleanUtil;

/**
 * 入库插件-数据查重插件
 * 
 * @author LY 2018-03-29
 *
 */
@JobHandler(value = "dataToCheckAgainHandler")
@Service
public class DataToCheckAgainHandler extends IJobHandler {
	@Autowired
	private DataToCheckAgainHandlerService dataToCheckAgainHandlerService;
	@Autowired
	private CleanUtil cleanUtil;
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<String>();
		result = dataToCheckAgainHandlerService.execute(triggerParam, result);
		if (!result.isSuccess()) {
			// 清理缓存
			cleanUtil.clearArchiveData(triggerParam);
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}

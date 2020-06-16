package com.htht.job.executor.hander.jobhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.ModisComposeService;
import com.htht.job.executor.service.impl.SoilWeightWaterService;

@JobHandler(value = "soilWeightWaterHandler")
@Service
public class SoilWeightWaterHandler extends IJobHandler {
	@Autowired
	private SoilWeightWaterService soilWeightWaterService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<>();
		/** =======1.执行业务============================= **/
		result = soilWeightWaterService.execute(triggerParam, result);
		if (!result.isSuccess()) {
			return new ReturnT<>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}

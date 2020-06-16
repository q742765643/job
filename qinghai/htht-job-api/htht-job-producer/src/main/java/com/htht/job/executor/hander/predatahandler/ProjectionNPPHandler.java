package com.htht.job.executor.hander.predatahandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *气象数据投影处理
 *支持FY3A、FY3B、FY3C、FY3D和modis
 */
@JobHandler(value = "projectionNPPHandler")
@Service
public class ProjectionNPPHandler extends IJobHandler {
	@Autowired
	private ProjectionNPPService projectionNPPService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) {
		ResultUtil<String> result = new ResultUtil<String>();
		triggerParam.getModelParameters();
 		result = projectionNPPService.execute(triggerParam, result);

		if (!result.isSuccess()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}

		return ReturnT.SUCCESS;
	}

}
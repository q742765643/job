package com.htht.job.executor.hander.producthandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.producthandler.service.LST_FY3BVIRR_HandlerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHandler(value = "lST_FY3BVIRR_ProductHandler")
@Service
public class LST_FY3BVIRR_ProductHandler extends IJobHandler {
	@Autowired
	private LST_FY3BVIRR_HandlerService productHandlerService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<String>();

		result = productHandlerService.excute(triggerParam, result);

		if (!result.isSuccess()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}

		return ReturnT.SUCCESS;
	}

}

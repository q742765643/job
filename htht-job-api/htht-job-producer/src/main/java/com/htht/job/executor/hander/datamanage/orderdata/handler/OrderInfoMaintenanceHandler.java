package com.htht.job.executor.hander.datamanage.orderdata.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.datamanage.orderdata.handler.service.OrderInfoMaintenanceHandlerService;

/**
 * 订单信息维护插件
 * 
 * @author LY 2018-05-08
 *
 */
@JobHandler(value = "orderInfoMaintenanceHandler")
@Service
public class OrderInfoMaintenanceHandler extends IJobHandler {
	@Autowired
	private OrderInfoMaintenanceHandlerService orderInfoMaintenanceHandlerService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<String>();
		result = orderInfoMaintenanceHandlerService.excute(triggerParam, result);
		if (!result.isSuccess()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}

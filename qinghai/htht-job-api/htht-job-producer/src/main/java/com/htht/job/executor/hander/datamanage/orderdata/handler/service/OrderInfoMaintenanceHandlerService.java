package com.htht.job.executor.hander.datamanage.orderdata.handler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.datamanage.orderdata.module.Order;
import com.htht.job.executor.hander.datamanage.orderdata.service.OrderService;

@Transactional
@Service("orderInfoMaintenanceHandlerService")
public class OrderInfoMaintenanceHandlerService {
	@Autowired
	private OrderService orderService;

	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			// 获取状态为1或3的订单
			List<Order> orders = orderService.getOrder("1,3");

			for (int i = 0; i < orders.size(); i++) {
				orderService.updateOrderInfo(orders.get(i).getF_id());
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

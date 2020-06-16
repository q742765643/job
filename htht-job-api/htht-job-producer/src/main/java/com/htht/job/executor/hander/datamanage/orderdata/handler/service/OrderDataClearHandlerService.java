package com.htht.job.executor.hander.datamanage.orderdata.handler.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.hander.datamanage.orderdata.module.Order;
import com.htht.job.executor.hander.datamanage.orderdata.service.OrderService;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.service.dms.DiskService;

@Transactional
@Service("orderDataClearHandlerService")
public class OrderDataClearHandlerService {
	@Autowired
	private DiskService diskService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			
			List<Disk> orderDisk = diskService.getOrderDisk();
			if (orderDisk.size() == 0) {
				result.setErrorMessage("数据提取扫描-无可用订单磁盘!");
				return result;
			}

			Disk disk = orderDisk.get(0);
			boolean flag = ComputerConnUtil.login(disk.getLoginurl(), disk.getLoginname(), disk.getLoginpwd());
			if (!flag && !new File(disk.getLoginurl()).exists()) {
				result.setErrorMessage("数据提取扫描-订单磁盘无法连接!");
				return result;
			}
			// 获取订单数据保留天数
			List<String> orderdata_keepday = baseDaoUtil.getByJpql("SELECT paramvalue FROM htht_dms_sys_param  where paramcode='orderdata_keepday'",null);
			if(orderdata_keepday.size()==0 || null == orderdata_keepday.get(0) || "".equals(orderdata_keepday.get(0))) {
				result.setErrorMessage("数据备份扫描-近线备份流程ID未填写!");
				return result;
			}
			SimpleDateFormat timestemp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Order> orders = orderService.getOverdueOrder(orderdata_keepday.get(0));
			for (int i = 0; i < orders.size(); i++) {
				Order order =  orders.get(i);
				File orderFile = new File(disk.getLoginurl()+"/"+order.getF_id());
				if(orderFile.exists()) {
					FileUtils.deleteDirectory(orderFile);
				}
				order.setF_orderstate(6);
				order.setF_orderstateinfo(order.getF_orderstateinfo()+'\n'+timestemp.format(new Date())+"  订单已过期！");
				orderService.update(order);
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

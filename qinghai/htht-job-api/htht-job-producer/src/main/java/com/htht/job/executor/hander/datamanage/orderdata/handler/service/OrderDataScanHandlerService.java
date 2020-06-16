package com.htht.job.executor.hander.datamanage.orderdata.handler.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.hander.dataarchiving.util.UrlUtil;
import com.htht.job.executor.hander.dataarchiving.util.webservice.client.ClientUtil;
import com.htht.job.executor.hander.datamanage.orderdata.handler.module.DataManageParam;
import com.htht.job.executor.hander.datamanage.orderdata.module.Order;
import com.htht.job.executor.hander.datamanage.orderdata.module.OrderInfo;
import com.htht.job.executor.hander.datamanage.orderdata.service.OrderInfoService;
import com.htht.job.executor.hander.datamanage.orderdata.service.OrderService;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.service.dms.DiskService;

@Transactional
@Service("orderDataScanHandlerService")
public class OrderDataScanHandlerService {
	@Autowired
	private DiskService diskService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderInfoService orderInfoService;
	@Value("${xxl.job.admin.addresses}")
	private String addresses;
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
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
			// 获取状态为1或3的订单
			List<Order> order = orderService.getOrder("1,3");
			StringBuffer orderid = new StringBuffer();
			for (int i = 0; i < order.size(); i++) {
				orderid.append("'"+order.get(i).getF_id()+"'");
				if(i < order.size()-1) {
					orderid.append(",");
				}
			}
			if(!orderid.toString().equals("")) {
				// 订单数据提取任务流程ID
				List<Integer> orderextractionid = baseDaoUtil.getByJpql("SELECT paramvalue FROM htht_dms_sys_param  where paramcode='orderdata_extractionid'",null);
				if(orderextractionid.size()==0 || null == orderextractionid.get(0) || "".equals(orderextractionid.get(0))) {
					result.setErrorMessage("数据提取扫描-订单数据提取任务流程ID未填写!");
					return result;
				}
				List<OrderInfo> list = orderInfoService.getData(orderid.toString());
				for (int i = 0; i < list.size(); i++) {
					OrderInfo orderInfo = list.get(i);
					orderInfo.setF_isexist(10);
					orderInfoService.update(orderInfo);
					DataManageParam dmp = new DataManageParam();
					dmp.setOrderInfoId(orderInfo.getF_id());
					String resultMessage = UrlUtil.getURLEncoderString(JSON.toJSONString(dmp));
					// 触发指定入库流程
					ClientUtil.interfaceUtil(addresses+"/api/achive/cp?jobId="+orderextractionid.get(0)+"&json="+resultMessage, "");
				}
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

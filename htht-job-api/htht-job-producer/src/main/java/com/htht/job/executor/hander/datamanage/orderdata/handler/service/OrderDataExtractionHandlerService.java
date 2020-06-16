package com.htht.job.executor.hander.datamanage.orderdata.handler.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.hander.dataarchiving.util.ZipUtil;
import com.htht.job.executor.hander.datamanage.orderdata.handler.module.DataManageParam;
import com.htht.job.executor.hander.datamanage.orderdata.module.OrderInfo;
import com.htht.job.executor.hander.datamanage.orderdata.service.OrderInfoService;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.service.dms.DiskService;
import com.htht.job.executor.service.dms.MetaInfoService;

@Transactional
@Service("orderDataExtractionHandlerService")
public class OrderDataExtractionHandlerService {
	@Autowired
	private DiskService diskService;
	@Autowired
	private MetaInfoService metaInfoService;
	@Autowired
	private OrderInfoService orderInfoService;

	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			List<Disk> orderDisk = diskService.getOrderDisk();
			if (orderDisk.size() == 0) {
				result.setErrorMessage("订单数据提取-无可用订单磁盘!");
				return result;
			}
			Disk disk = orderDisk.get(0);
			boolean flag = ComputerConnUtil.login(disk.getLoginurl(), disk.getLoginname(), disk.getLoginpwd());
			if (!flag && !new File(disk.getLoginurl()).exists()) {
				result.setErrorMessage("订单数据提取-订单磁盘无法连接!");
				return result;
			}
			// 连接各存储磁盘(在线、近线、离线)
			List<Disk> archiveDisk = diskService.getAll();
			for (int i = 0; i < archiveDisk.size(); i++) {
				if (archiveDisk.get(i).getDisktype().equals("1") || archiveDisk.get(i).getDisktype().equals("5") || archiveDisk.get(i).getDisktype().equals("6")) {
					if(new File(archiveDisk.get(i).getLoginurl()).exists()) {
						continue;
					}
					ComputerConnUtil.login(archiveDisk.get(i).getLoginurl(), archiveDisk.get(i).getLoginname(),
							archiveDisk.get(i).getLoginpwd());
				}
			}
			// 根据参数找到数据
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			DataManageParam dataManageParam = JSON.parseObject(jsonString, DataManageParam.class);
			OrderInfo orderInfo = orderInfoService.getById(dataManageParam.getOrderInfoId());
			MetaInfo metaInfo = metaInfoService.getById(orderInfo.getF_dataid());
			File baseFile = metaInfo.getF_location()==null?null:new File(metaInfo.getF_location());
			File nearlineFile = metaInfo.getF_nearlinepath()==null?null:new File(metaInfo.getF_nearlinepath());
			File offlineFile = metaInfo.getF_offlinepath()==null?null:new File(metaInfo.getF_offlinepath());
			
			File sourceFile = null;
			if(null != baseFile && baseFile.exists()) {
				sourceFile = baseFile;
			} else if(null != nearlineFile && nearlineFile.exists()) {
				sourceFile = nearlineFile;
			} else if(null != offlineFile && offlineFile.exists()) {
				sourceFile = offlineFile;
			}
			// 判断归档文件是否存在
			if (null == sourceFile) {
				// 修改数据显示信息和数据提取完成状态
				orderInfo.setF_isexist(2);
				orderInfo.setF_datastateinfo("未找到归档数据!");
				orderInfoService.update(orderInfo);
				result.setErrorMessage("订单数据提取-订单数据没找到!");
				return result;
			}
			// 修改数据显示信息
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			orderInfo.setF_datastateinfo(sdf.format(new Date()) + "  数据提取开始");
			orderInfoService.update(orderInfo);
			File toDir = new File(disk.getLoginurl() + "/" + orderInfo.getF_orderid());
			if(!toDir.exists()) {
				toDir.mkdirs();
			}
			// 进行数据提取
			if(metaInfo.getF_flag()==0 && sourceFile.isDirectory()) {
				// 文件夹打包
				ZipUtil.fileToZip(sourceFile.getAbsolutePath(), disk.getLoginurl() + "/" + orderInfo.getF_orderid(),sourceFile.getName());
			} else {
				// 单文件拷贝
				FileUtils.copyFileToDirectory(sourceFile, new File(disk.getLoginurl() + "/" + orderInfo.getF_orderid()));
			}
			// 修改数据显示信息和数据提取完成状态
			orderInfo.setF_isexist(1);
			orderInfo.setF_datastateinfo(orderInfo.getF_datastateinfo() + '\n' + sdf.format(new Date()) + "  数据提取结束");
			orderInfoService.update(orderInfo);
			
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

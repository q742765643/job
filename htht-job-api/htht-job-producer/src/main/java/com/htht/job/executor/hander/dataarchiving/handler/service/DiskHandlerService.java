package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.service.dms.DiskService;

@Transactional
@Service("diskHandlerService")
public class DiskHandlerService {
	@Autowired
	private DiskService diskService;

	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			List<Disk> disks = diskService.getAll();
			Disk disk = null;
			boolean flag;
			File dir = null;
			for (int i = 0; i < disks.size(); i++) {
				disk = disks.get(i);
				flag = ComputerConnUtil.login(disk.getLoginurl(), disk.getLoginname(), disk.getLoginpwd());
				if (flag || new File(disk.getLoginurl()).exists()) {
					dir = new File(disk.getLoginurl());
					disk.setUpdateTime(new Date());
					disk.setDiskfreesize(dir.getUsableSpace());
					disk.setDisktotlesize(dir.getTotalSpace());
					disk.setDiskusesize(dir.getTotalSpace() - dir.getUsableSpace());
					disk.setUsagerate((int) ((dir.getUsableSpace() / (dir.getTotalSpace() * 1.0)) * 100));
					diskService.update(disk);
				}
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.service.dms.DiskService;
import com.htht.job.executor.service.dms.MetaInfoService;

@Transactional
@Service("dataBackUpCleanHandlerService")
public class DataBackUpCleanHandlerService {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Autowired
	private MetaInfoService metaInfoService;
	@Autowired
	private DiskService diskService;
	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
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
			// 获取需要清理的在线数据 
			List<String> online = baseDaoUtil.getByJpql(" select f_dataid from htht_dms_meta_info where (f_location is not null or f_location<>'') and f_importdate < date_sub(SYSDATE(), interval (SELECT paramvalue FROM `htht_dms_sys_param`  where paramcode='onlineday' ) day)",null);
			// 获取需要清理的近线数据 
			List<String> nearline = baseDaoUtil.getByJpql(" select f_dataid from htht_dms_meta_info where (f_nearlinepath is not null or f_nearlinepath<>'') and f_nearline_time < date_sub(SYSDATE(), interval (SELECT paramvalue FROM htht_dms_sys_param  where paramcode='nearlineday' ) day)",null);
			for (int i = 0; i < online.size(); i++) {
				MetaInfo metaInfo = metaInfoService.getById(online.get(i));
				File locationFile = new File(metaInfo.getF_location());
				FileUtils.deleteQuietly(locationFile);
				if(!locationFile.exists()) {
					metaInfo.setF_location(null);
					metaInfoService.update(metaInfo);
				}
			}
			
			for (int i = 0; i < nearline.size(); i++) {
				MetaInfo metaInfo = metaInfoService.getById(nearline.get(i));
				File nearlineFile = new File(metaInfo.getF_nearlinepath());
				FileUtils.deleteQuietly(nearlineFile);
				if(!nearlineFile.exists()) {
					metaInfo.setF_nearlinepath(null);
					metaInfo.setF_nearline_time(null);
					metaInfoService.update(metaInfo);
				}
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
		}
		return result;
	}

}

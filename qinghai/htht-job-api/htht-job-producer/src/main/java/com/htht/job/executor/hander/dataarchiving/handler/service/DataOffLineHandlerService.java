package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.service.dms.DiskService;
import com.htht.job.executor.service.dms.MetaInfoService;

@Transactional
@Service("dataOffLineHandlerService")
public class DataOffLineHandlerService {
	@Autowired
	private MetaInfoService metaInfoService;
	@Autowired
	private DiskService diskService;
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			HandlerParam handlerParam = JSON.parseObject(jsonString, HandlerParam.class);
			List<Disk> offLineDisk = diskService.getOffLineDisk();
			if (offLineDisk.size() == 0) {
				result.setErrorMessage("近线>离线-没有可用离线磁盘！");
				return result;
			}
			Disk disk = offLineDisk.get(0);
			boolean flag = ComputerConnUtil.login(disk.getLoginurl(), disk.getLoginname(), disk.getLoginpwd());
			if (!flag  && !new File(disk.getLoginurl()).exists()) {
				result.setErrorMessage("近线>离线-离线磁盘未连接！");
				return result;
			}
			
			File srcFile = new File(handlerParam.getBaseUrl());
			if(!srcFile.exists()) {
				result.setErrorMessage("近线>离线-此数据文件不存在:" + handlerParam.getBackupDataid());
				return result;
			}
//			String subDir = srcFile.getParent().substring(handlerParam.getBaseUrl().indexOf("/"));
			String subPathSql = "select  CONCAT(i.F_SATELLITEID,'/',i.F_SENSORID,'/LEVEL',i.F_LEVEL,'/',date_format(f.f_importdate,'%Y/%m/%d')) from htht_dms_meta_img i,htht_dms_meta_info f where i.F_DATAID=f.f_dataid AND f.f_dataid='"+handlerParam.getBackupDataid()+"'";
			// 获取子文件夹规则
			String subDir = (String) baseDaoUtil.getByJpql(subPathSql,null).get(0);
			if (srcFile.isDirectory()) {
				FileUtils.copyDirectoryToDirectory(srcFile, new File(disk.getLoginurl()+"/"+subDir));
			} else {
				FileUtils.copyFileToDirectory(srcFile, new File(disk.getLoginurl()+"/"+subDir));
			}
//			MetaInfo metaInfo = metaInfoService.getById(handlerParam.getBackupDataid());
//			if (null == metaInfo) {
//				result.setErrorMessage("近线>离线-此dataid无效:" + handlerParam.getBackupDataid());
//				return result;
//			}
//			metaInfo.setF_offline_time(new Date());
//			metaInfo.setF_offlinepath(disk.getLoginurl() + "/" + srcFile.getName());
//			metaInfoService.update(metaInfo);
			String sql = "update htht_dms_meta_info set F_offline_time=sysdate() , F_offlinepath='"+disk.getLoginurl()+"/"+subDir+"\\"+ srcFile.getName()+"' where f_dataid='"+handlerParam.getBackupDataid()+"'";
			Object[] obj = {};
			baseDaoUtil.executeSql(sql.replaceAll("\\\\", "\\\\\\\\") , obj);
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.UUIDTool;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.service.dms.MetaInfoService;

@Service("dataArchiveHandlerService")
public class DataArchiveHandlerService {
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private MetaInfoService metaInfoService;
	@Autowired
	private BaseDaoUtil baseDaoUtil;

	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			String isDeleteBaseFile = (String) triggerParam.getDynamicParameter().get("isDeleteBaseFile");
			HandlerParam handlerParam = JSON.parseObject(jsonString, HandlerParam.class);
			Disk baseDisk = handlerParam.getBaseDiskInfo();
			Disk archiveDisk = handlerParam.getArchiveDiskInfo();
			// 归档盘登录验证
			if(ComputerConnUtil.login(archiveDisk.getLoginurl(), archiveDisk.getLoginname(), archiveDisk.getLoginpwd()) || new File(archiveDisk.getLoginurl()).exists()) {
				// 数据盘登录验证
				if(ComputerConnUtil.login(baseDisk.getLoginurl(), baseDisk.getLoginname(), baseDisk.getLoginpwd()) || new File(baseDisk.getLoginurl()).exists()) {
					// 入库规则
					ArchiveRules archiveRules = handlerParam.getArchiveRules();
					// 原始文件路径
					File baseFile = new File(handlerParam.getBaseUrl());
					// 文件名称、格式
					String fileName = "",dataextname = "";
					// 复制文件到归档磁盘
					File secFile = null;
					if(archiveRules.getFiletype()==1) {
						secFile = new File(archiveDisk.getLoginurl()+"/"+handlerParam.getArchivePath());
						FileUtils.copyFileToDirectory(baseFile, secFile);
						fileName = fileUtil.getFileNameWithoutSuffix(baseFile);
						dataextname = fileUtil.getFileNameSuffix(baseFile);
					} else {
						secFile = new File(archiveDisk.getLoginurl()+"/"+handlerParam.getArchivePath()+fileUtil.getFileNameWithoutSuffix(baseFile));
						if(!secFile.exists()) {
							secFile.mkdirs();
						}
						String[] allFiles = handlerParam.getArchiveRules().getAllFile().split("#");
						boolean isFullFile = true;
						for (int i = 0; i < allFiles.length; i++) {
							if(!new File(handlerParam.getBaseUrl().replace(handlerParam.getArchiveRules().getFinalstr(), allFiles[i])).exists()) {
								isFullFile = false;
								break;
							}
						}
						if(!isFullFile) {
							result.setErrorMessage("入库插件-需要拷贝的文件不全！");
							return result;
						}
						
						for (int i = 0; i < allFiles.length; i++) {
							FileUtils.copyFileToDirectory(new File(handlerParam.getBaseUrl().replace(handlerParam.getArchiveRules().getFinalstr(), allFiles[i])), secFile);
						}
						
						fileName = baseFile.getName();
						dataextname = "";
					}
					
					String uuid = UUIDTool.getUUID();
					
					// 获取需要入库的主表名称
					
					List<String> maintable = baseDaoUtil.getByJpql("select F_MAINTABLENAME from htht_dms_archive_catalog where F_CATALOGCODE='"+archiveRules.getCatalogcode()+"'",null);
					if(maintable.size() == 0) {
						result.setErrorMessage("入库插件-【"+archiveRules.getCatalogcode()+"】没有主表名称！！");
						return result;
					}
					// 物理信息
					MetaInfo metaInfo = new MetaInfo();
					metaInfo.setId(uuid);
					metaInfo.setCreateTime(new Date());
					metaInfo.setF_dataid(uuid);
					metaInfo.setF_dataname(fileUtil.getFileNameWithoutSuffix(baseFile));
					metaInfo.setF_catalogcode(archiveRules.getCatalogcode());
					metaInfo.setF_importdate(new Date());
					metaInfo.setF_datasize(fileUtil.getDirSize(secFile));
					metaInfo.setF_dataunit("B");
					metaInfo.setF_dataextname(dataextname);
					metaInfo.setF_isfile(archiveRules.getFiletype());
					if(archiveRules.getFiletype() == 1) {
						metaInfo.setF_location(archiveDisk.getLoginurl()+"/"+handlerParam.getArchivePath()+baseFile.getName());
					} else {
						metaInfo.setF_location(secFile.getAbsolutePath());
					}
					metaInfo.setF_flag(0);
					metaInfo.setF_recycleflag(0);
					
					if (new File(handlerParam.getDelBlackPath()).exists()) {
						metaInfo.setF_extractdatapath(handlerParam.getDelBlackPath());
						metaInfo.setF_viewdatapath(handlerParam.getDelBlackPathNoIP());
					}
					metaInfo.setF_datasourcename(baseFile.getName());
					metaInfoService.save(metaInfo);
					// 影像信息
					Map<String, String> metaImg = handlerParam.getArchiveMap();
					String sql = "insert into "+maintable.get(0);
					StringBuffer colSql = new StringBuffer("(f_dataid");
					StringBuffer valSql = new StringBuffer("('" + uuid + "'");
					for (Map.Entry<String, String> entry : metaImg.entrySet()) {
						colSql.append("," + entry.getKey());
						valSql.append("," + entry.getValue());
						// System.out.println("Key = " + entry.getKey() + ", Value = " +
						// entry.getValue());
					}
					colSql.append(",f_catalogcode");
					valSql.append(",'" + archiveRules.getCatalogcode()+"'");
					colSql.append(",f_level");
					valSql.append(",'" + archiveRules.getDatalevel()+"'");
					colSql.append(")");
					valSql.append(")");
					Object[] obj = {};
					
					System.out.println("=========>"+sql + colSql.toString() + " values " + valSql.toString());
					baseDaoUtil.executeSql(sql + colSql.toString() + " values " + valSql.toString(), obj);
					
					// 删除原始数据文件
					if(null == isDeleteBaseFile || isDeleteBaseFile.equals("1")) {
						if(archiveRules.getFiletype()==0) {
							// 删除原始文件(多文件)
							String[] allFiles = handlerParam.getArchiveRules().getAllFile().split("#");
							for (int i = 0; i < allFiles.length; i++) {
								if(new File(handlerParam.getBaseUrl().replace(handlerParam.getArchiveRules().getFinalstr(), allFiles[i])).exists()) {
									FileUtils.deleteQuietly(new File(handlerParam.getBaseUrl().replace(handlerParam.getArchiveRules().getFinalstr(), allFiles[i])));
								}
							}
						} else {
							FileUtils.deleteQuietly(baseFile);
						}
					}
					
					
				} else {
					result.setErrorMessage("入库插件-数据磁盘不可用！！");
				}
			} else {
				result.setErrorMessage("入库插件-归档磁盘不可用！！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}
}

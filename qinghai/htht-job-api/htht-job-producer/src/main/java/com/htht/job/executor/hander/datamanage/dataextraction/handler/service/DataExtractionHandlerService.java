package com.htht.job.executor.hander.datamanage.dataextraction.handler.service;

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
import com.htht.job.executor.hander.dataarchiving.util.ZipUtil;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.service.dms.DiskService;
import com.htht.job.executor.service.dms.MetaInfoService;

@Transactional
@Service("dataExtractionHandlerService")
public class DataExtractionHandlerService {
	@Autowired
	private DiskService diskService;
	@Autowired
	private MetaInfoService metaInfoService;

	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String toPath = (String) triggerParam.getDynamicParameter().get("toPath");
			String ids = (String) triggerParam.getDynamicParameter().get("ids");
			// 判断数据ID是否存在
			if(ids.isEmpty()) {
				result.setErrorMessage("数据提取插件-源数据ID不能为空!");
				return result;
			}
			// 判断目标路径是否有效
			File toDir = new File(toPath);
			if (!toDir.exists()) {
				if(!toDir.mkdirs()) {
					result.setErrorMessage("数据提取插件-目标路径无效,且无法创建!");
					return result;
				}
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
			// 解析ID进行数据提取
			String[]idArr = ids.split(",");
			MetaInfo metaInfo = null;
			File baseFile = null;
			File nearlineFile = null;
			File offlineFile = null;
			File sourceFile = null;
			for (int i = 0; i < idArr.length; i++) {
				metaInfo = metaInfoService.getById(idArr[i]);
				// 判断源数据是否存在
				if (null == metaInfo) {
					result.setErrorMessage("数据提取插件-源数据ID未查询到对应数据!");
					return result;
				}
				baseFile = metaInfo.getF_location()==null?null:new File(metaInfo.getF_location());
				nearlineFile = metaInfo.getF_nearlinepath()==null?null:new File(metaInfo.getF_nearlinepath());
				offlineFile = metaInfo.getF_offlinepath()==null?null:new File(metaInfo.getF_offlinepath());
				
				if(null != baseFile && baseFile.exists()) {
					sourceFile = baseFile;
				} else if(null != nearlineFile && nearlineFile.exists()) {
					sourceFile = nearlineFile;
				} else if(null != offlineFile && offlineFile.exists()) {
					sourceFile = offlineFile;
				}
				// 判断归档文件是否存在
				if (null == sourceFile) {
					result.setErrorMessage("数据提取插件-源数据没找到!");
					return result;
				}
				// 进行数据提取
				if(metaInfo.getF_flag()==0 && sourceFile.isDirectory()) {
					// 文件夹打包
					ZipUtil.fileToZip(sourceFile.getAbsolutePath(), toPath ,sourceFile.getName());
				} else {
					// 单文件拷贝
					FileUtils.copyFileToDirectory(sourceFile, new File(toPath ));
				}
			}
			
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

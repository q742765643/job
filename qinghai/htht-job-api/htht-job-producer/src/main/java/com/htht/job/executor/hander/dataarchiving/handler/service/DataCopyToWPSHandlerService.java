package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.UnFileUtil;

@Transactional
@Service("dataCopyToWPSHandlerService")
public class DataCopyToWPSHandlerService {
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private UnFileUtil unFileUtil;

	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {

			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			HandlerParam handlerParam = JSON.parseObject(jsonString,
					HandlerParam.class);

			boolean flag = ComputerConnUtil.login(handlerParam.getBaseDiskInfo().getLoginurl(),
					handlerParam.getBaseDiskInfo().getLoginname(), handlerParam.getBaseDiskInfo().getLoginpwd());
			if (flag || new File(handlerParam.getBaseDiskInfo().getLoginurl()).exists()) {
				File baseFile = new File(handlerParam.getBaseUrl());
				if (baseFile.exists()) {
					/*20180831-弃用文件夹形式start*/
//					Disk workDisk = handlerParam.getWorkDiskInfo();
//					// 获取工作空间路径
//					String unPath = workDisk.getLoginurl()+handlerParam.getBaseUrl().substring(handlerParam.getBaseUrl().lastIndexOf("\\"));
//					File secFile = new File(unPath);
//					if(!secFile.exists()) {
//						secFile.mkdirs();
//					}
//					// 过滤需要拷贝到工作空间的文件
//					Iterator<File> iter = new FileUtil().listFiles(handlerParam.getBaseUrl(), handlerParam.getArchiveRules().getWspFile().split(","));
//					while (iter.hasNext()) {
//						// 如果存在，则调用next实现迭代
//						File j = (File) iter.next();
//						FileUtils.copyFileToDirectory(j, secFile);
//					}
					/*20180831-弃用文件夹形式end*/
					
					/*20180831-启用多文件同名形式start*/
					File secFile = new File(handlerParam.getWorkSpacePath());
					if(!secFile.exists()) {
						secFile.mkdirs();
					}
					String[] wspFiles = handlerParam.getArchiveRules().getWspFile().split("#");
					boolean isFullFile = true;
					for (int i = 0; i < wspFiles.length; i++) {
						if(!new File(handlerParam.getBaseUrl().replace(handlerParam.getArchiveRules().getFinalstr(), wspFiles[i])).exists()) {
							isFullFile = false;
							break;
						}
					}
					if(!isFullFile) {
						result.setErrorMessage("数据拷贝至工作空间-需要拷贝的文件不全！");
						return result;
					}
					
					for (int i = 0; i < wspFiles.length; i++) {
						FileUtils.copyFileToDirectory(new File(handlerParam.getBaseUrl().replace(handlerParam.getArchiveRules().getFinalstr(), wspFiles[i])), secFile);
					}
					/*20180831-启用多文件同名形式end*/
					
					
					String resultMessage = JSON.toJSONString(handlerParam);
					List<String> resultList = new ArrayList<>();
					resultList.add(resultMessage);
					triggerParam.setOutput(resultList);
				} else {
					result.setErrorMessage("数据拷贝至工作空间-实体文件不存在！");
				}
			} else {
				result.setErrorMessage("数据拷贝至工作空间-登录验证失败！");
			}

		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

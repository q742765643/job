package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.service.dms.DiskService;

@Transactional
@Service("imageCopyHandlerService")
public class ImageCopyHandlerService {
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private DiskService diskService;
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			HandlerParam handlerParam = JSON.parseObject(jsonString,
					HandlerParam.class);
			List<Disk> imgDisks = diskService.getImgDisk();
			if(imgDisks.size() > 0) {
				Disk imgDisk = imgDisks.get(0);
				handlerParam.setImgDiskInfo(imgDisk);
				boolean flag = ComputerConnUtil.login(imgDisk.getLoginurl(), imgDisk.getLoginname(), imgDisk.getLoginpwd());
				if (flag || new File(imgDisk.getLoginurl()).exists()) {
					// 数据解压路径
					String workPath = handlerParam.getWorkSpacePath();
					File workSpace = new File(workPath);
					if (workSpace.exists()) {
						// 文件过滤条件
						String[] extensions = new String[] { ".jpg" };
						Iterator<File> jpgs = fileUtil.listFiles(workPath, extensions);
						File j = null;
						// 入库规则
						ArchiveRules archiveRules = handlerParam.getArchiveRules();
						// 如果设置了jpg过滤条件
						if (null != archiveRules.getRegexpjpg() && !archiveRules.getRegexpjpg().equals("")) {
							// 通过正则匹配文件
							j = fileUtil.getFileByRegexp(jpgs, archiveRules.getRegexpjpg());
						} else if (jpgs.hasNext()) {
							// 未设置过滤条件,默认只读取第一个
							j = (File) jpgs.next();
						}
						// 判断是否有匹配的文件
						if (j != null) {
							// 图片拷贝后存储地址
							String imgDelBlackPath = handlerParam.getDelBlackPath();
							// 拷贝图片到图片磁盘
							FileUtils.copyFile(j, new File(imgDelBlackPath));
							String resultMessage = JSON.toJSONString(handlerParam);
							List<String> resultList = new ArrayList<>();
							resultList.add(resultMessage);
							triggerParam.setOutput(resultList);
						} else {
							result.setErrorMessage("JPG拷贝-实体文件不存在！");
						}
					} else {
						result.setErrorMessage("JPG拷贝-文件夹不存在！");
					}
				} else {
					result.setErrorMessage("JPG拷贝-图片服务磁盘未连接！");
				}
			} else {
				result.setErrorMessage("JPG拷贝-没有可用图片磁盘！");
			}

		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}
	
}

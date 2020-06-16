package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.hander.dataarchiving.util.UnFileUtil;
import com.htht.job.executor.model.dms.module.Disk;

@Transactional
@Service("dataUnpackHandlerService")
public class DataUnpackHandlerService {
	@Autowired
	private UnFileUtil unFileUtil;

	public ResultUtil<String> excute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {

			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			HandlerParam handlerParam = JSON.parseObject(jsonString,
					HandlerParam.class);

			boolean flag = ComputerConnUtil.login(handlerParam.getBaseDiskInfo().getLoginurl(),
					handlerParam.getBaseDiskInfo().getLoginname(), handlerParam.getBaseDiskInfo().getLoginpwd());
			if (flag || new File(handlerParam.getBaseDiskInfo().getLoginurl()).exists()) {
				File baseFile = new File(handlerParam.getBaseUrl());
				if (baseFile.exists()) {
					// 数据解压
					boolean unFlag = unFileUtil.unFile(handlerParam.getBaseUrl(),
							handlerParam.getWorkSpacePath(),handlerParam.getArchiveRules().getWspFile());
					if(!unFlag) {
						result.setErrorMessage("数据解压-实体文件解压失败！");
					}
					String resultMessage = JSON.toJSONString(handlerParam);
					List<String> resultList = new ArrayList<>();
					resultList.add(resultMessage);
					triggerParam.setOutput(resultList);
				} else {
					result.setErrorMessage("数据解压-实体文件不存在！");
				}
			} else {
				result.setErrorMessage("数据解压-登录验证失败！");
			}

		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

}

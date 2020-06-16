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
import com.htht.job.executor.service.dms.MetaInfoService;

@Transactional
@Service("dataToCheckAgainHandlerService")
public class DataToCheckAgainHandlerService {
	@Autowired
	private MetaInfoService metaInfoService;

	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
			HandlerParam handlerParam = JSON.parseObject(jsonString,
					HandlerParam.class);
			File baseFile = new File(handlerParam.getBaseUrl());
			boolean dataExists = metaInfoService.verifyDataExists(baseFile.getName());
			if (!dataExists) {
				String resultMessage = JSON.toJSONString(handlerParam);
				List<String> resultList = new ArrayList<>();
				resultList.add(resultMessage);
				triggerParam.setOutput(resultList);
				
			} else {
				result.setErrorMessage("数据查重-数据已入库！");
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}
}

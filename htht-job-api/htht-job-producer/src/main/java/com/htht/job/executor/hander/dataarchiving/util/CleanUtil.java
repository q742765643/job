package com.htht.job.executor.hander.dataarchiving.util;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.redis.RedisService;
@Repository(value = "cleanUtil")
public class CleanUtil {
	@Autowired
	private RedisService redisService;
	public void clearArchiveData(TriggerParam triggerParam) {
		String jsonString = (String) triggerParam.getDynamicParameter().get("jsonString");
		HandlerParam handlerParam = JSON.parseObject(jsonString, HandlerParam.class);
		// 删除工作空间文件
		handlerParam.deleteWorkSpaceFile();
		File f = new File(handlerParam.getBaseUrl());
		// 移除redis缓存
		redisService.remove("archive_"+f.getName());
	}
}

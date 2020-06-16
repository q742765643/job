package com.htht.job.executor.hander.firehandler;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.fireservice.FireH8ProService;


/**
 * Created by zzp on 2018/12/26
 */
@JobHandler(value = "fireH8ProHandlerShard")
@Service
public class FireH8ProHandlerShard implements SharingHandler {
	@Autowired
	private FireH8ProService fireH8ProService;

	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap,
			LinkedHashMap dymap) throws Exception {
		
		return fireH8ProService.execute(params, fixmap, dymap);
	}
}

package com.htht.job.executor.hander.shardinghandler;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.GrassYieldService;


@JobHandler(value = "grassYieldHandlerShard")
@Service
public class GrassYieldHandlerShard implements SharingHandler {
	@Autowired
	private GrassYieldService grassYieldService;

	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap,
			LinkedHashMap dymap) throws Exception {
		
		return grassYieldService.execute(params, fixmap, dymap);
	}
}

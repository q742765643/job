package com.htht.job.executor.hander.shardinghandler;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.ModisComposeService;


@JobHandler(value = "modisComposeHandlerShard")
@Service
public class ModisComposeHandlerShard implements SharingHandler {
	@Autowired
	private ModisComposeService modisComposeService;

	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap,
			LinkedHashMap dymap) throws Exception {
		
		return modisComposeService.execute(params, fixmap, dymap);
	}
}

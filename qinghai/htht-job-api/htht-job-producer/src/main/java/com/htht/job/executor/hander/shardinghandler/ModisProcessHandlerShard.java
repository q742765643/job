package com.htht.job.executor.hander.shardinghandler;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.ModisProcessService;


@JobHandler(value = "modisProcessHandlerShard")
@Service
public class ModisProcessHandlerShard implements SharingHandler {
	@Autowired
	private ModisProcessService modisProcessService;

	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap,
			LinkedHashMap dymap) throws Exception {
		
		return modisProcessService.execute(params, fixmap, dymap);
	}
}

package com.htht.job.executor.plugin.product.shard;


import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.impl.DroughtMonitorService;

@Service("droughtMonitorHandlerShard")
public class DroughtMonitorHandlerShard implements SharingHandler{
	
	
	@Autowired
	private DroughtMonitorService droughtMonitorService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		
		return droughtMonitorService.execute(params, fixmap, dymap);
	}

}

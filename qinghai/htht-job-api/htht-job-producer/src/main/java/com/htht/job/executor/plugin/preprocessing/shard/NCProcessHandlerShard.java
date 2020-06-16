package com.htht.job.executor.plugin.preprocessing.shard;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import com.htht.job.executor.plugin.preprocessing.service.NCProcessService;



@Service("ncProcessHandlerShard")
public class NCProcessHandlerShard implements SharingHandler  {
	
	@Autowired
	private NCProcessService ncProcessService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		return ncProcessService.execute(params, fixmap, dymap);
	}

}

package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.impl.CommService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("commHandlerShard")
public class CommHandlerShard implements SharingHandler {

	@Autowired
	private CommService commService;

	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		return commService.execute(params, fixmap, dymap);
	}
}



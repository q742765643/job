package com.htht.job.executor.hander.shardinghandler;

import java.util.LinkedHashMap;
import java.util.List;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.impl.NCComposeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * soilWeightWater 土壤重量含水率
 * @author zzp
 *
 */
@Service("ncComposeHandlerShard")
public class NCComposeHandlerShard implements SharingHandler {

	@Autowired
	private NCComposeService ncComposeService;


	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap,
			LinkedHashMap dymap) throws Exception {
		
		return ncComposeService.execute(params,fixmap,dymap);
	}
}



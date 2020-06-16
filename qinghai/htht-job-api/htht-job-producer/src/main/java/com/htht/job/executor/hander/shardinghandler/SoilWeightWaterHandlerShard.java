package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.impl.SoilWeightWaterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * soilWeightWater 土壤重量含水率
 * @author zzp
 *
 */
@Service("soilWeightWaterHandlerShard")
public class SoilWeightWaterHandlerShard implements SharingHandler {

	@Autowired
	private SoilWeightWaterService soilWeightWaterService;

	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap,
			LinkedHashMap dymap) throws Exception {
		
		return soilWeightWaterService.execute(params,fixmap,dymap);
	}
}



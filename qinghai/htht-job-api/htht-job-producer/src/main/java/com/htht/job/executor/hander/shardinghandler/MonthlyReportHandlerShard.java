package com.htht.job.executor.hander.shardinghandler;

import java.util.LinkedHashMap;
import java.util.List;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.impl.MonthlyReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Monthly report 旬月报
 * @author zzp
 *
 */
@Service("monthlyReportHandlerShard")
public class MonthlyReportHandlerShard implements SharingHandler {

	@Autowired
	private MonthlyReportService monthlyReportService;

	@Override
	public ResultUtil<List<String>> execute(String params,
			LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		// TODO Auto-generated method stub
		return monthlyReportService.execute(params, fixmap, dymap);
	}

}



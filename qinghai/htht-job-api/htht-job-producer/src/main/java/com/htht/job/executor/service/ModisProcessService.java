package com.htht.job.executor.service;

import java.util.LinkedHashMap;
import java.util.List;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;

public interface ModisProcessService {

	ResultUtil<String> execute(TriggerParam triggerParam,
			ResultUtil<String> result);

	ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap,
			LinkedHashMap dymap);

}

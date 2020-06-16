package com.htht.job.executor.service.fireservice;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;

public interface FireH8ProService {

	ResultUtil<String> excute(TriggerParam triggerParam,
			ResultUtil<String> result);

	ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap,
			LinkedHashMap dymap);

}

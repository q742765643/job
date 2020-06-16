package com.htht.job.core.handler;

import com.htht.job.core.util.ResultUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface SharingHandler {
	@SuppressWarnings("rawtypes")
	public abstract ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception;

}

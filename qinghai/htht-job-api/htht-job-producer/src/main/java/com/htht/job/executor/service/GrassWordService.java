package com.htht.job.executor.service;

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;

import java.util.LinkedHashMap;
import java.util.List;

public interface GrassWordService {

    /**
     * 执行业务
     *
     * @param triggerParam
     * @param result
     * @return
     */
    ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result);

    /**
     * 执行业务2
     *
     * @param params 任务参数
     * @param fixmap 固定参数
     * @param dymap  输入参数
     * @return
     */
    ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap);
}

package com.htht.job.executor.plugin.preprocessing.shard;


import java.util.LinkedHashMap;
import java.util.List;

import com.htht.job.executor.service.GrassWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;

@Service(value = "grassWordHandlerShard")
public class GrassWordHandlerShard implements SharingHandler{

    @Autowired
    private GrassWordService grassWordService;

    /**
     * 执行业务2
     *
     * @param params 任务参数
     * @param fixmap 固定参数
     * @param dymap  输入参数
     * @return
     * @throws Exception
     */
    @Override
    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
        return grassWordService.execute(params, fixmap, dymap);
    }
}

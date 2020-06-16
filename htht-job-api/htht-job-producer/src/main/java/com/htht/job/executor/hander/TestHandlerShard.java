package com.htht.job.executor.hander;/**
 * Created by zzj on 2018/11/20.
 */

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @program: htht-job
 * @description:
 * @author: zzj
 * @create: 2018-11-20 16:45
 **/
@Service("testHandlerShard")
public class TestHandlerShard implements SharingHandler {
    @Override
    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
        ResultUtil<List<String>> resultUtil=new ResultUtil<>();
        List<String> list=new ArrayList<>();
        list.add("111");
        list.add("22");
        list.add("33");
        list.add("44");

        resultUtil.setResult(list);
        return resultUtil;
    }
}


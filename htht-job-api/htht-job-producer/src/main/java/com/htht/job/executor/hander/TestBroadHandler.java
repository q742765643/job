package com.htht.job.executor.hander;/**
 * Created by zzj on 2018/11/21.
 */

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: htht-job
 * @description:
 * @author: zzj
 * @create: 2018-11-21 17:32
 **/
@JobHandler(value = "testBroadHandler")
@Service
public class TestBroadHandler extends IJobHandler{
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        List<String>  aa=new ArrayList<>();
        for(int i=0;i<3;i++){
            String b=i+"#HT#"+i+"#HT#"+i+"#HT#"+i+"#HT#";
            aa.add(b);
        }
        triggerParam.setOutput(aa);
        return null;
    }
}


package com.htht.job.executor.hander.producthandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.producthandler.service.ARMProductHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by atom on 2018/11/9.
 */

@JobHandler(value = "ARMProductHandler")
@Service
public class ARMProductHandler extends IJobHandler {
    @Autowired
    private ARMProductHandlerService acidRainProductHandlerService;

    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result = new ResultUtil<String>();

        result = acidRainProductHandlerService.execute(triggerParam, result);

        if (!result.isSuccess())
        {
            return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
        }

        return ReturnT.SUCCESS;
    }
}

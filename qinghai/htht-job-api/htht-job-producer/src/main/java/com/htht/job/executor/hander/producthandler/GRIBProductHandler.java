package com.htht.job.executor.hander.producthandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.producthandler.service.GRIBProductHandlerService;
import org.springframework.stereotype.Service;

/**
 * Created by atom on 2018/11/14.
 */

@JobHandler(value = "GRIBProductHandler")
@Service
public class GRIBProductHandler extends IJobHandler {

    private GRIBProductHandlerService gribProductHandlerService;
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result = new ResultUtil<String>();

        result = gribProductHandlerService.execute(triggerParam, result);

        if (!result.isSuccess())
        {
            return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
        }

        return ReturnT.SUCCESS;
    }
}

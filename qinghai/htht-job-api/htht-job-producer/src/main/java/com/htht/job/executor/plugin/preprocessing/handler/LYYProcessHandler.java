package com.htht.job.executor.plugin.preprocessing.handler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.LYYProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHandler(value = "lyyProcessHandler")
@Service
public class LYYProcessHandler extends IJobHandler {

    @Autowired
    private LYYProcessService lyyProcessService;


    /**
     * 执行业务
     *
     * @param triggerParam
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result = new ResultUtil<String>();

//        执行业务
        result = lyyProcessService.execute(triggerParam, result);

        if (!result.isSuccess()) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
        }

        return ReturnT.SUCCESS;
    }
}

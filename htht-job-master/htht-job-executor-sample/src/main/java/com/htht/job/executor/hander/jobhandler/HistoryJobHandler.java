package com.htht.job.executor.hander.jobhandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ParsingUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.service.hander.HistoryHanderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zzj on 2018/1/16.
 */
@JobHandler(value = "historyJobHandler")
@Service
public class HistoryJobHandler extends IJobHandler {
    @Autowired
    private HistoryHanderService historyHanderService;

    @SuppressWarnings({"rawtypes"})
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result = new ResultUtil<String>();
        /**=======1.校验参数==============**/
        ParsingUtil.argumentparsing(triggerParam, result);
        if (!result.isSuccess()) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
        }
        /**=======2.执行业务=============================**/
        result = historyHanderService.excute(triggerParam, result);
        if (!result.isSuccess()) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
        }

        return ReturnT.SUCCESS;
    }
}

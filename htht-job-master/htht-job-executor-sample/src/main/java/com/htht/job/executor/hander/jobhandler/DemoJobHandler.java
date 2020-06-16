package com.htht.job.executor.hander.jobhandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ParsingUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.hander.DemoHanderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务Handler的一个Demo（Bean模式）
 * <p>
 * 开发步骤： 1、继承 “IJobHandler” ； 2、装配到Spring，例如加 “@Service” 注解； 3、加 “@JobHandler”
 * 注解，注解value值为新增任务生成的JobKey的值;多个JobKey用逗号分割; 4、执行日志：需要通过 "XxlJobLogger.log"
 * 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHandler(value = "demoJobHandler")
@Service
public class DemoJobHandler extends IJobHandler {
    @Autowired
    private RedisService redisService;
    @Autowired
    private DemoHanderService demoHanderService;

    @SuppressWarnings({"rawtypes"})
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result = new ResultUtil();
        /**=======1.校验参数==============**/
        ParsingUtil.argumentparsing(triggerParam, result);
        if (!result.isSuccess()) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
        }
        /**=======2.执行业务=============================**/
        result = demoHanderService.excute(triggerParam, result);
        if (!result.isSuccess()) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
        }


        return ReturnT.SUCCESS;
    }

}

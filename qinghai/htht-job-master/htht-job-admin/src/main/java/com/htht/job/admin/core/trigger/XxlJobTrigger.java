package com.htht.job.admin.core.trigger;


import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.htht.job.admin.core.util.SpringContextUtil;
import com.htht.job.admin.service.DispatchService;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * xxl-job trigger
 * Created by xuxueli on 17/7/13.
 */

public class XxlJobTrigger {
    private static Logger logger = LoggerFactory.getLogger(XxlJobTrigger.class);

    /**
     * trigger job
     *
     * @param jobId
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void trigger(int jobId) {

        // load data
        XxlJobInfo jobInfo = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(jobId);
        // job info
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        DispatchService dispatchService = (DispatchService) SpringContextUtil.getBean("dispatchService");
        dispatchService.scheduler(jobInfo);

    }

    /**
     * run executor
     *
     * @param triggerParam
     * @param address
     * @return ReturnT.content: final address
     */
    public static ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = RealReference.getExecutorBiz(address);
            //设置参数执行ip地址
            triggerParam.setExecuteIp(address);
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            runResult = new ReturnT<String>(ReturnT.FAIL_CODE, "" + e);
        }

        StringBuffer runResultSB = new StringBuffer("触发调度：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMsg());

        runResult.setMsg(runResultSB.toString());
        runResult.setContent(address);
        return runResult;
    }


}

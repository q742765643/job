package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/4/16.
 */

import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.dao.XxlJobRegistryDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: htht-job
 * @description: 公用调度
 * @author: zzj
 * @create: 2018-04-16 20:54
 **/
@Service
public class SchedulerServiceImpl {
    private static Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Resource
    protected TaskParametersService taskParametersService;
    @Resource
    protected DubboService dubboService;
    @Resource
    protected XxlJobInfoDao xxlJobInfoDao;
    @Resource
    protected XxlJobLogDao xxlJobLogDao;
    @Resource
    protected AtomicAlgorithmService atomicAlgorithmService;
    @Resource
    protected XxlJobRegistryDao xxlJobRegistryDao;
    @Resource
    private CheckAliveService checkAliveService;

    public void acquireTriggerResult(ReturnT<String> triggerResult,
                                     ExecutorBlockStrategyEnum blockStrategy,
                                     ExecutorFailStrategyEnum failStrategy,
                                     ExecutorRouteStrategyEnum executorRouteStrategyEnum,
                                     ArrayList<String> addressList,
                                     StringBuffer triggerMsgSb) {
        triggerMsgSb.append("注册方式：").append("");
        triggerMsgSb.append("<br>阻塞处理策略：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>调度异常处理：").append(failStrategy.getTitle());
        triggerMsgSb.append("<br>地址列表：").append("");
        triggerMsgSb.append("<br>路由策略：").append(executorRouteStrategyEnum.getTitle());
        if (triggerResult.getCode() == ReturnT.SUCCESS_CODE && CollectionUtils.isEmpty(addressList)) {
            triggerResult.setCode(ReturnT.FAIL_CODE);
            triggerMsgSb.append("<br>----------------------<br>").append("调度失败：").append("执行器地址为空");
        }
    }

    public void updateTriggerInfo(XxlJobInfo jobInfo, XxlJobLog jobLog, ReturnT<String> triggerResult, StringBuffer triggerMsgSb) {

        jobLog.setExecutorAddress(triggerResult.getContent());
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        XxlJobDynamicScheduler.xxlJobLogDao.updateTriggerInfo(jobLog);
    }

    public void updateHandleInfo(HandleCallbackParam handleCallbackParam, XxlJobLog jobLog) {
        StringBuffer handleMsg = new StringBuffer();
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        jobLog.setHandleMsg(handleMsg.toString());
        jobLog.setHandleTime(new Date());
        jobLog.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        jobLog.setLogFileName(handleCallbackParam.getLogFileName());
        xxlJobLogDao.updateHandleInfo(jobLog);

    }



    public Integer saveJobLog(XxlJobInfo jobInfo, XxlJobLog jobLog) {
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        XxlJobDynamicScheduler.xxlJobLogDao.save(jobLog);
        logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());
        return jobLog.getId();
    }



    public ArrayList<String> findAddressList(String id,int dealAmount){
        ArrayList<String> addressList=dubboService.findAddressList(id,dealAmount);
        List<String> addressList_Use= null;
        try {
            addressList_Use = checkAliveService.checkAliveByAddressList(addressList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addressList.retainAll(addressList_Use);
        return addressList;
    }

}


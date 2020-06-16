package com.htht.job.admin.service;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/11/16.
 */
public interface SchedulerUtilService {
    public void saveJobLog(XxlJobInfo jobInfo, XxlJobLog jobLog);

    public void updateHandleInfo(HandleCallbackParam handleCallbackParam, XxlJobLog jobLog);

    public void updateTriggerInfo(XxlJobLog jobLog, ReturnT<String> triggerResult, StringBuilder triggerMsgSb);

    public void saveTriggerParam(TriggerParam triggerParam, XxlJobInfo jobInfo,
                                 AtomicAlgorithmDTO atomicAlgorithmDTO, XxlJobLog jobLog,
                                 Map fixmap, Map dymap, ParallelLogDTO parallelLogDTO);

    public List<String> findAddressList(String id, int dealAmount);

    public Map transformMap(String params);

    public Map<String, Object> failRestrtFive(ExecutorRouteStrategyEnum executorRouteStrategyEnum,
                                              TriggerParam triggerParam, StringBuilder triggerMsgSb, ReturnT<String> triggerResult, XxlJobInfo jobInfo,
                                              AtomicAlgorithmDTO atomicAlgorithmDTO);

    public void updateJobLog(XxlJobInfo jobInfo, XxlJobLog jobLog);

    public void updateHandleInfo(ProcessStepsDTO processStepsDTO, XxlJobLog jobLog, ReturnT<String> triggerResult);

    public void acquireTriggerResult(ReturnT<String> triggerResult,
                                     StringBuilder triggerMsgSb, List<String> addressList, Map methodMap);

    public Map<String, Object> failRestrt(ExecutorRouteStrategyEnum executorRouteStrategyEnum, TriggerParam triggerParam,
                                          StringBuilder triggerMsgSb, ReturnT<String> triggerResult, XxlJobInfo jobInfo,
                                          AtomicAlgorithmDTO atomicAlgorithmDTO);

    public void insertFailLogByFile(String msg,XxlJobInfo jobInfo ,int code);


}

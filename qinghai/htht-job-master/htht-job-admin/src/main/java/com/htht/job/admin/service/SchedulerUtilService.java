package com.htht.job.admin.service;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.processsteps.ProcessSteps;

import java.util.LinkedHashMap;
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
                                 AtomicAlgorithm atomicAlgorithm, XxlJobLog jobLog,
                                 LinkedHashMap fixmap, LinkedHashMap dymap, ParallelLog parallelLog);
    public List<String> findAddressList(String id, int dealAmount);
    public LinkedHashMap transformMap(String params);
    public Map<String, Object> failRestrtFive(ExecutorRouteStrategyEnum executorRouteStrategyEnum,
                                              TriggerParam triggerParam, StringBuilder triggerMsgSb, ReturnT<String> triggerResult, XxlJobInfo jobInfo,
                                              AtomicAlgorithm atomicAlgorithm);
    public void updateJobLog(XxlJobInfo jobInfo, XxlJobLog jobLog);
    public void updateHandleInfo(ProcessSteps processSteps, XxlJobLog jobLog, ReturnT<String> triggerResult);



    }

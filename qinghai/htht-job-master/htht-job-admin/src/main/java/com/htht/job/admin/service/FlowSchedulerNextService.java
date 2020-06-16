package com.htht.job.admin.service;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.core.biz.model.HandleCallbackParam;

import java.util.Map;

/**
 * Created by zzj on 2018/4/16.
 */
public interface FlowSchedulerNextService {
    public void nextStep(String[] nextIds, Map paramMap);

    public void callback(HandleCallbackParam handleCallbackParam);

    public void handScheduler(XxlJobInfo jobInfo,XxlJobLog jobLog,String dataId,String dynamicParameter);

    public void handNextStep(String nextId, XxlJobInfo jobInfo, XxlJobLog jobLog,String dynamicParameter,String parentFlowlogId,String flowChartId);
}

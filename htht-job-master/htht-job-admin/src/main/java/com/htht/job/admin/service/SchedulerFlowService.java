package com.htht.job.admin.service;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;

import java.util.Map;

/**
 * Created by zzj on 2018/11/16.
 */
public interface SchedulerFlowService {
    public void depositNextStepMap(Map paramMap, XxlJobLog jobLog, XxlJobInfo jobInfo, String flowChartId, String parentFlowlogId);

}

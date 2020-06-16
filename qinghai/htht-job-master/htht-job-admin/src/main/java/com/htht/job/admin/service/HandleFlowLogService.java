package com.htht.job.admin.service;

import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.model.app.FlowLogVo;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.processsteps.ProcessSteps;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/11/16.
 */
public interface HandleFlowLogService {
    public void recursiveSave(FlowLogVo flowLogVo, int jobLogId, String parentFlowlogId, FlowLog flowLog);
    public void matchingFlowLog(FlowLogVo flowLogVo,FlowLogVo flowLogVo1,List<CommonParameter> paralleDynamicParameter);
    public void setStartFlowLog(String dynamicParameter, ProcessSteps processSteps, FlowLogVo flowLogVo, String modelId);
    public void setFlowLog(String dynamicParameter,ProcessSteps processSteps,FlowLogVo flowLogVo,String modelId);
    public void setNextFlowLogIsProcess(String dynamicParameter,ProcessSteps processSteps,FlowLog flowLog,Map paramMap);
    public void setFlowLog(ProcessSteps processSteps,FlowLog flowLog,String parentFlowlogId,String modelId,int parallel,String isStart);
    public String  saveFlowLog(FlowLog flowLog, ProcessSteps processSteps, int jobLogId, AtomicAlgorithm atomicAlgorithm);
    public void updateOutValue(FlowLog newflowLog, HandleCallbackParam handleCallbackParam, List<ParallelLog> parallelLogList, List<CommonParameter> dynamicParameter);
    public void updateFlowLog(HandleCallbackParam handleCallbackParam,
                              List<CommonParameter> dynamicParameter,
                              XxlJobLog jobLog,
                              ParallelLog parallelLog,FlowLog nowflowLog);
    public void updateFailFlowLog(HandleCallbackParam handleCallbackParam, ParallelLog parallelLog, XxlJobLog jobLog, ResultUtil resultUtil);
    public void batchExecuteFlowlog(Map map,FlowLogVo flowLogVo,FlowLogVo flowLogVo1,XxlJobLog jobLog,int length, AtomicAlgorithm atomicAlgorithm );
    public void noBatchExecuteFlowlog(Map map,ParallelLog parallelLog,FlowLogVo flowLogVo,FlowLogVo flowLogVo1,XxlJobLog jobLog,AtomicAlgorithm atomicAlgorithm);
    public void handleFistStepParam(ProcessSteps processSteps, FlowLogVo flowLogVo, Map map, ResultUtil<String> resultUtil);
    public FlowLog saveStartFlow(String dynamicParameter,int jobLogId,String dataId,String parentFlowLogId,String flowChartId);













    }

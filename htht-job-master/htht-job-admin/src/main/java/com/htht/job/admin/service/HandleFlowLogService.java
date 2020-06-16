package com.htht.job.admin.service;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.model.app.FlowLogVo;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/11/16.
 */
public interface HandleFlowLogService {
    public void recursiveSave(FlowLogVo flowLogVo, int jobLogId, String parentFlowlogId, FlowLogDTO flowLogDTO);

    public void matchingFlowLog(FlowLogVo flowLogVo, FlowLogVo flowLogVo1, List<CommonParameter> paralleDynamicParameter);

    public void setStartFlowLog(String dynamicParameter, ProcessStepsDTO processStepsDTO, FlowLogVo flowLogVo, String modelId);

    public void setFlowLog(String dynamicParameter, ProcessStepsDTO processStepsDTO, FlowLogVo flowLogVo, String modelId);

    public void setNextFlowLogIsProcess(String dynamicParameter, ProcessStepsDTO processStepsDTO, FlowLogDTO flowLogDTO, Map paramMap);

    public void setFlowLog(ProcessStepsDTO processStepsDTO, FlowLogDTO flowLogDTO, String parentFlowlogId, String modelId, int parallel, String isStart);

    public String saveFlowLog(FlowLogDTO flowLogDTO, ProcessStepsDTO processStepsDTO, int jobLogId, AtomicAlgorithmDTO atomicAlgorithmDTO);

    public void updateOutValue(FlowLogDTO newflowLogDTO, HandleCallbackParam handleCallbackParam, List<ParallelLogDTO> parallelLogDTOList, List<CommonParameter> dynamicParameter);

    public void updateFlowLog(HandleCallbackParam handleCallbackParam,
                              List<CommonParameter> dynamicParameter,
                              XxlJobLog jobLog,
                              ParallelLogDTO parallelLogDTO, FlowLogDTO nowflowLogDTO);

    public void updateFailFlowLog(HandleCallbackParam handleCallbackParam, ParallelLogDTO parallelLogDTO, XxlJobLog jobLog, ResultUtil resultUtil);

    public void batchExecuteFlowlog(Map map, FlowLogVo flowLogVo, FlowLogVo flowLogVo1, XxlJobLog jobLog, int length, AtomicAlgorithmDTO atomicAlgorithmDTO);

    public void noBatchExecuteFlowlog(Map map, ParallelLogDTO parallelLogDTO, FlowLogVo flowLogVo, FlowLogVo flowLogVo1, XxlJobLog jobLog, AtomicAlgorithmDTO atomicAlgorithmDTO);

    public void handleFistStepParam(ProcessStepsDTO processStepsDTO, FlowLogVo flowLogVo, Map map, ResultUtil<String> resultUtil);

    public FlowLogDTO saveStartFlow(String dynamicParameter, int jobLogId, String dataId, String parentFlowLogId, String flowChartId);

    public String saveFlowLog(XxlJobLog jobLog, XxlJobInfo jobInfo);

    public String saveParallelLog(String flowId, Map dymap, XxlJobInfo jobInfo, String formatmodelParameters);


}

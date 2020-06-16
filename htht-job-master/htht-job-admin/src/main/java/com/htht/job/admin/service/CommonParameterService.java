package com.htht.job.admin.service;

import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/11/16.
 */
public interface CommonParameterService {
    public void repalceInListValueByUuid(List<CommonParameter> commonParametersOld, List<CommonParameter> commonParametersNew);

    public void repalceListValueByDataId(List<CommonParameter> commonParametersOld, List<CommonParameter> commonParametersNew);

    public void repalceInListValueByDataId(List<CommonParameter> commonParametersOld, List<CommonParameter> commonParametersNew);

    public void repalceListValueByDataIdReply(List<CommonParameter> commonParametersOld, List<CommonParameter> commonParametersNew);

    public void repalceListValueByUuid(List<CommonParameter> commonParametersOld, List<CommonParameter> commonParametersNew);

    public List<CommonParameter> findOutputParameter(int jobLogId, String flowId);

    public void replaceFlowDynamicParameter(List<CommonParameter> dynamicParameter, FlowLogDTO newflowLogDTO, ParallelLogDTO parallelLogDTO, Map<Integer, String> mapValue);

    public void replaceFlowDynamicParameterOut(CommonParameter commonParameter, int j, FlowLogDTO flowLogDTO, Map<Integer, String> mapValue);


}

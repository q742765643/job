package com.htht.job.core.api;

import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.RegistryParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/7/20.
 */
public interface DubboCallBackService {
    ReturnT<String> registry(RegistryParam registryParam);

    ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);

    ReturnT<String> registryRemove(RegistryParam registryParam);

    void broadScheduler(List<String> list, String methodMap, Map fixLinkMap, Map dyLinkMap);

    void schedulerRpc(ProcessStepsDTO processStepsDTO, List<CommonParameter> flowParams,
                      List<String> outputList, int jobId, AtomicAlgorithmDTO atomicAlgorithmDTO, String dynamicParameter);

    void insertFailLog(String msg,String methodMap,int code);

    void insertFlowFailLog(String msg,int jobId,int code);

}

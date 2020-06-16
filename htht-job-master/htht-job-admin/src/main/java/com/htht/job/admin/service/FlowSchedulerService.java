package com.htht.job.admin.service;

import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;

import java.util.List;

/**
 * Created by zzj on 2018/11/21.
 */
public interface FlowSchedulerService {
    void schedulerRpc(ProcessStepsDTO processStepsDTO, List<CommonParameter> flowParams,
                      List<String> outputList, int jobId, AtomicAlgorithmDTO atomicAlgorithmDTO, String dynamicParameter);
}

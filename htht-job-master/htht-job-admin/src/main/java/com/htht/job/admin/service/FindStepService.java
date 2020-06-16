package com.htht.job.admin.service;

import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;

import java.util.List;

/**
 * Created by zzj on 2018/11/16.
 */
public interface FindStepService {
    public List<String> getNextIds(String nextIdString);

    public List<ProcessStepsDTO> findNextFlowCeaselessly(List<String> nextIdlist, String modelId, ResultUtil<String> resultUtil);

    public ProcessStepsDTO findStartFlowCeaselessly(String modelId);

}

package com.htht.job.admin.service;

import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.processsteps.ProcessSteps;

import java.util.List;

/**
 * Created by zzj on 2018/11/16.
 */
public interface FindStepService {
    public List<String> getNextIds(String nextIdString);
    public List<ProcessSteps> findNextFlowCeaselessly(List<String> nextIdlist, String modelId, ResultUtil<String> resultUtil);
    public ProcessSteps findStartFlowCeaselessly(String modelId);

}

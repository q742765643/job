package com.htht.job.core.api.algorithm;

import com.htht.job.executor.model.algorithm.TaskParametersDTO;

import java.util.Map;


public interface TaskParametersService {
    public Map getJobParameter(String jobId, String parameterId, String mark);

    public TaskParametersDTO saveJobParameter(TaskParametersDTO taskParametersDTO);

    public TaskParametersDTO findJobParameterById(String id);

    public String getJobParameterMap(String jobId, String parameterId, String mark);

    public String formatJobModelParameters(String modelParameters);

    public String getLogDynamic(Map dymap, String modelId);
}

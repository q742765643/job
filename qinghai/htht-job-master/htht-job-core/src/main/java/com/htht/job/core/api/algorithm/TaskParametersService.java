package com.htht.job.core.api.algorithm;

import com.htht.job.executor.model.algorithm.TaskParameters;

import java.util.LinkedHashMap;
import java.util.Map;


public interface TaskParametersService {
	public LinkedHashMap getJobParameter(String jobId, String parameterId, String mark);
	public TaskParameters saveJobParameter(TaskParameters taskParameters);
	public TaskParameters findJobParameterById(String id);
	public String getJobParameterMap(String jobId, String parameterId, String mark);
	public String formatJobModelParameters(String modelParameters);
	public String getLogDynamic(Map dymap, String modelId);
}

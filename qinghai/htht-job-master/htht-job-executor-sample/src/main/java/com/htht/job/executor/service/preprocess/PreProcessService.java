package com.htht.job.executor.service.preprocess;

import java.util.List;

import com.htht.job.executor.model.preprocess.PreProcess;

public interface PreProcessService {

	/**
	 * 
	 * @param productId
	 * @param issues
	 * @return List<PreProcess>
	 */
	public List<PreProcess> findProcessByProductIdAndIssue(String productId, List<String> issues);
	
	/**
	 * 
	 * @param productId
	 * @param issue
	 * @return PreProcess
	 */
	public PreProcess findProcessByProductIdAndIssue(String productId, String issue);
	
	/**
	 * 
	 * @param preProcess
	 * @return PreProcess
	 */
	public PreProcess savePreProcess(PreProcess preProcess);
}

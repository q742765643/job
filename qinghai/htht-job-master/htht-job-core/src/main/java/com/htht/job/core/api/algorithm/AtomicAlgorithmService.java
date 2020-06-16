package com.htht.job.core.api.algorithm;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;

import java.util.List;
import java.util.Map;

public interface AtomicAlgorithmService {
	public AtomicAlgorithm saveParameter(AtomicAlgorithm atomicAlgorithm);
	public Map<String, Object> pageList(int start,int length,AtomicAlgorithm atomicAlgorithm);
	public List<AtomicAlgorithm> findListParameter();
	public AtomicAlgorithm findParameterById(String id);
	public ReturnT<String>  deleteParameter(String id);
	public List<Map> findTreeListBySql();
	public boolean updateForTree(String id, String treeId);
	public AtomicAlgorithm queryAogoInfo(String id);
	public AtomicAlgorithm findModelIdentifyById(String id);
}

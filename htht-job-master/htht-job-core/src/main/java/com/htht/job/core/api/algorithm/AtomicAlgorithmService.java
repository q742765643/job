package com.htht.job.core.api.algorithm;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;

import java.util.List;
import java.util.Map;

public interface AtomicAlgorithmService {
    public AtomicAlgorithmDTO saveParameter(AtomicAlgorithmDTO atomicAlgorithmDTO);

    public Map<String, Object> pageList(int start, int length, AtomicAlgorithmDTO atomicAlgorithmDTO);

    public List<AtomicAlgorithmDTO> findListParameter();

    public AtomicAlgorithmDTO findParameterById(String id);

    public ReturnT<String> deleteParameter(String id);

    public List<Map> findTreeListBySql();

    public boolean updateForTree(String id, String treeId);

    public AtomicAlgorithmDTO queryAogoInfo(String id);

    public AtomicAlgorithmDTO findModelIdentifyById(String id);
}

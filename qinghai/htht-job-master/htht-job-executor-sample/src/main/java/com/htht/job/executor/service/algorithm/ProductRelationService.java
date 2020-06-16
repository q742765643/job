package com.htht.job.executor.service.algorithm;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfo;

import java.util.List;

public interface ProductRelationService {

    public AlgorithmRelationInfo saveRelation(AlgorithmRelationInfo algorithmRelationInfo);
    public ReturnT<String> deleteRelation(String treeId, String lgoid);

    public List<AlgorithmRelationInfo> queryAogo(String treeid);

}


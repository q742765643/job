package com.htht.job.executor.service.algorithm;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfoDTO;

import java.util.List;

public interface ProductRelationService {

    public AlgorithmRelationInfoDTO saveRelation(AlgorithmRelationInfoDTO algorithmRelationInfoDTO);

    public ReturnT<String> deleteRelation(String treeId, String lgoid);

    public List<AlgorithmRelationInfoDTO> queryAogo(String treeid);

}


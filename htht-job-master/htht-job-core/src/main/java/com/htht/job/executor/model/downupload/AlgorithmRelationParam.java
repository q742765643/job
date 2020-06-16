package com.htht.job.executor.model.downupload;

import com.htht.job.core.util.BaseEntity;

import java.util.List;

public class AlgorithmRelationParam extends BaseEntity {

    private String treeId;
    private List<String> algoId;

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public List<String> getAlgoId() {
        return algoId;
    }

    public void setAlgoId(List<String> algoId) {
        this.algoId = algoId;
    }
}

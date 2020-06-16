package com.htht.job.executor.model.downupload;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "htht_cluster_schedule_algorithm_node_relation")
public class AlgorithmRelationInfoDTO extends BaseEntity {

    private String treeId;
    private String algoId;

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public String getAlgoId() {
        return algoId;
    }

    public void setAlgoId(String algoId) {
        this.algoId = algoId;
    }
}

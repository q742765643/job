package com.htht.job.executor.model.registryalgo;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "htht_cluster_schedule_registry_algo")
public class RegistryAlgo extends BaseEntity{
    @Column(name = "registry_id")
    private String registryId;
    @Column(name = "algo_id")
    private String algoId;
	public String getRegistryId() {
		return registryId;
	}
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}
	public String getAlgoId() {
		return algoId;
	}
	public void setAlgoId(String algoId) {
		this.algoId = algoId;
	}
    
}



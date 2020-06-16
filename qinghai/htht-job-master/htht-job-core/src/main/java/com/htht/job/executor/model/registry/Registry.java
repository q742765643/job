package com.htht.job.executor.model.registry;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zzj on 2018/1/30.
 */
@Entity
@Table(name = "htht_cluster_schedule_registry")
public class Registry  extends BaseEntity{
    @Column(name = "registry_key")
    private String registryKey;
    @Column(name = "registry_ip")
    private String registryIp;
    @Column(name = "concurrency")
    private int concurrency;
    @Column(name = "deploysystem")
    private String deploySystem;
    
    public String getDeploySystem() {
		return deploySystem;
	}

	public void setDeploySystem(String deploySystem) {
		this.deploySystem = deploySystem;
	}


    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryIp() {
        return registryIp;
    }

    public void setRegistryIp(String registryIp) {
        this.registryIp = registryIp;
    }


    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }
}

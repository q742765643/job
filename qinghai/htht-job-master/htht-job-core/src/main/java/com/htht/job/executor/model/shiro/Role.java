package com.htht.job.executor.model.shiro;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.*;


@Entity
@Table(name = "htht_cluster_schedule_role")
public class Role extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1894163644285296223L;

	/**
	 * 角色名称
	 */
	private String name;

	/**
	 * 角色key
	 */
	private String roleKey;

	/**
	 * 角色状态,0：正常；1：删除
	 */
	private Integer status;

	/**
	 * 角色描述
	 */
	private String description;


	@ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinTable(name = "htht_cluster_schedule_role_resource", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "resource_id") })
	private java.util.Set<Resource> resources;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoleKey() {
		return roleKey;
	}

	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public java.util.Set<Resource> getResources() {
		return resources;
	}

	public void setResources(java.util.Set<Resource> resources) {
		this.resources = resources;
	}

}

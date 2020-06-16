package com.htht.job.executor.model.uus;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.htht.job.core.util.BaseEntity;
import com.htht.job.executor.model.datacategory.DataCategory;


@Entity
@Table(name = "htht_uus_role")
public class UusRole extends BaseEntity {

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
	@JoinTable(name = "htht_uus_role_category", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "category_id") })
	private java.util.Set<DataCategory> category;		//功能目录

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

	public java.util.Set<DataCategory> getCategory() {
		return category;
	}

	public void setCategory(java.util.Set<DataCategory> category) {
		this.category = category;
	}
}

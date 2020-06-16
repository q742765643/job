package com.htht.job.executor.model.dbms;


import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
@Entity
@Table(name = "tb_uim_role")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class DbmsRole implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "F_ID",length=32)
	@GeneratedValue(generator = "jpa-uuid")
	private String id;
	@Column(name = "F_ROLENAME")
	private String name;
	@Column(name = "F_DESC",columnDefinition="TEXT")
	private String description;
	@Transient
	private Integer orderNum;
	@Transient
	private String systemKey;
	@ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinTable(name = "tb_uim_rolemodule", 
		joinColumns = { @JoinColumn(name = "F_ROLEID") }, 
		inverseJoinColumns = { @JoinColumn(name = "F_MODID")})
	private java.util.Set<DbmsModule> modules;
	@ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinTable(name = "tb_uim_roledata", 
		joinColumns = { @JoinColumn(name = "F_ROLEID") }, 
		inverseJoinColumns = { @JoinColumn(name = "F_CATALOGID")})
	private java.util.Set<DbmsArchiveCatalog> archiveCatalogs;
	
	/**
	 * 角色key
	 */
	@Column
	private String roleKey;

	/**
	 * 角色状态,0：正常；1：删除
	 */
	@Column
	private Integer status;
	/*
	 * 创建时间
	 */
	private Date createTime;
	/*
	 * 更新时间
	 */
	private Date updateTime;
	
	public java.util.Set<DbmsModule> getModules() {
		return modules;
	}
	public void setModules(java.util.Set<DbmsModule> modules) {
		this.modules = modules;
	}
	public java.util.Set<DbmsArchiveCatalog> getArchiveCatalogs() {
		return archiveCatalogs;
	}
	public void setArchiveCatalogs(java.util.Set<DbmsArchiveCatalog> archiveCatalogs) {
		this.archiveCatalogs = archiveCatalogs;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String roleName) {
		this.name = roleName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String memo) {
		this.description = memo;
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}
	public String getSystemKey() {
		return systemKey;
	}
	public void setSystemKey(String systemKey) {
		this.systemKey = systemKey;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}

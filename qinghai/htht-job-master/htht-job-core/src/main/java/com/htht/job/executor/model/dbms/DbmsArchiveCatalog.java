package com.htht.job.executor.model.dbms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Table(name = "tb_archive_catalog")
public class DbmsArchiveCatalog implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "F_ID", length = 32)
	@GeneratedValue(generator = "jpa-uuid")
	private java.lang.String id; // id
	@Column(name = "F_CATALOGNAME")
	private java.lang.String catalogName; // catalogname
	@Column(name = "F_CATALOGCODE")
	private java.lang.String catalogCode; // catalogcode
	@Column(name = "F_PID")
	private java.lang.String pid; // pid
	@Column(name = "F_MAINTABLENAME")
	private java.lang.String maintablename; // maintablename
	@Column(name = "F_SUBTABLENAME")
	private java.lang.String subtablename; // subtablename
	@Column(name = "F_NODETYPE")
	private java.lang.Long nodeType; // nodetype
	@Column(name = "F_NODEDESC")
	private java.lang.String nodedesc; // nodedesc
	@Column(name = "F_XSDID")
	private java.lang.String xsdid; // xsdid
	@Column(name = "F_CREATEDATE")
	private java.util.Date createTime; // createdate
	@Column(name = "F_STORINGRULE")
	private java.lang.String storingrule; // storingrule
	@Column(name = "F_METHOD",columnDefinition="TEXT")
	private java.lang.String method; // method
	/*
	 * 更新时间
	 */
	private Date updateTime;

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

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String id) {
		this.id = id;
	}

	public java.lang.String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(java.lang.String catalogName) {
		this.catalogName = catalogName;
	}

	public java.lang.String getCatalogCode() {
		return catalogCode;
	}

	public void setCatalogCode(java.lang.String catalogCode) {
		this.catalogCode = catalogCode;
	}

	public java.lang.String getPid() {
		return pid;
	}

	public void setPid(java.lang.String pid) {
		this.pid = pid;
	}

	public java.lang.String getMaintablename() {
		return maintablename;
	}

	public void setMaintablename(java.lang.String maintablename) {
		this.maintablename = maintablename;
	}

	public java.lang.String getSubtablename() {
		return subtablename;
	}

	public void setSubtablename(java.lang.String subtablename) {
		this.subtablename = subtablename;
	}

	public java.lang.Long getNodeType() {
		return nodeType;
	}

	public void setNodeType(java.lang.Long nodeType) {
		this.nodeType = nodeType;
	}

	public java.lang.String getNodedesc() {
		return nodedesc;
	}

	public void setNodedesc(java.lang.String nodedesc) {
		this.nodedesc = nodedesc;
	}

	public java.lang.String getXsdid() {
		return xsdid;
	}

	public void setXsdid(java.lang.String xsdid) {
		this.xsdid = xsdid;
	}

	public java.lang.String getStoringrule() {
		return storingrule;
	}

	public void setStoringrule(java.lang.String storingrule) {
		this.storingrule = storingrule;
	}

	public java.lang.String getMethod() {
		return method;
	}

	public void setMethod(java.lang.String method) {
		this.method = method;
	}

}

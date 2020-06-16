package com.htht.job.executor.model.dms.module;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.htht.job.core.util.BaseEntity;

/**
 * @date:2018年9月13日下午5:16:16
 * @author:yss
 */
@Entity
@Table(name="htht_dms_archive_catalog")
public class ArchiveCatalog extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Column(name = "F_ID")
	private String fid;
	@Column(name = "F_CATALOGNAME")
	private String catalogName;//目录名
	@Column(name = "F_CATALOGCODE")
	private String catalogCode;//目录编码
	@Column(name = "F_PID")
	private String pid;
	@Column(name = "F_MAINTABLENAME")
	private String mainTableName;//主表名称
	@Column(name = "F_SUBTABLENAME")
	private String subTableName;//副表名称
	@Column(name = "F_NODETYPE")
	private Integer yNodeType;
	@Column(name = "F_NODEDESC")
	private String nodeDesc;
	@Column(name = "F_XSDID")
	private String xsDid;
	@Column(name = "F_CREATEDATE")
	private Date createDate;
	@Column(name = "F_STORINGRULE")
	private String storingGrule;
	@Column(name = "F_METHOD")
	private String method;
	@Column(name = "F_ARCPATHID")
	private String arcpathid;//存放数据归档路径ID
	@Transient
    private List<ArchiveCatalog> nodes;
	@Transient
    private String text;
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public String getCatalogCode() {
		return catalogCode;
	}
	public void setCatalogCode(String catalogCode) {
		this.catalogCode = catalogCode;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getMainTableName() {
		return mainTableName;
	}
	public void setMainTableName(String mainTableName) {
		this.mainTableName = mainTableName;
	}
	public String getSubTableName() {
		return subTableName;
	}
	public void setSubTableName(String subTableName) {
		this.subTableName = subTableName;
	}
	public String getNodeDesc() {
		return nodeDesc;
	}
	public void setNodeDesc(String nodeDesc) {
		this.nodeDesc = nodeDesc;
	}
	public String getXsDid() {
		return xsDid;
	}
	public void setXsDid(String xsDid) {
		this.xsDid = xsDid;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getStoringGrule() {
		return storingGrule;
	}
	public void setStoringGrule(String storingGrule) {
		this.storingGrule = storingGrule;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getArcpathid() {
		return arcpathid;
	}
	public void setArcpathid(String arcpathid) {
		this.arcpathid = arcpathid;
	}
	public List<ArchiveCatalog> getNodes() {
		return nodes;
	}
	public void setNodes(List<ArchiveCatalog> nodes) {
		this.nodes = nodes;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Integer getyNodeType() {
		return yNodeType;
	}
	public void setyNodeType(Integer yNodeType) {
		this.yNodeType = yNodeType;
	}
	
	
	
	
}

package com.htht.job.executor.model.dbms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.htht.job.core.util.BaseEntity;

@Entity
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Table(name = "tb_uim_module")
public class DbmsModule extends BaseEntity{

	private static final long serialVersionUID = 1L;
	@Column(name = "F_MODNAME")
	private String name;
	@Column(name = "F_ID")
	private String sourceKey;
	@Column(name = "F_LOCALURL",columnDefinition="TEXT")
	private String sourceUrl;
	@Column(name = "F_DESC",columnDefinition="TEXT")
	private String description;
	@Column(name = "F_ICONPATH")
	private String icon;
	@Column(name = "F_MODULETYPE")
	private String parentId;
	@Column(name = "F_FLAG")
	private Integer isHide;
	@Transient
	private String remoteurl;
	@Transient
	private String siteId;
	@Transient
	private String targetButton;
	@Transient
	private String defaultPageUrl;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSourceKey() {
		return sourceKey;
	}
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Integer getIsHide() {
		return isHide;
	}
	public void setIsHide(Integer isHide) {
		this.isHide = isHide;
	}
	public String getRemoteurl() {
		return remoteurl;
	}
	public void setRemoteurl(String remoteurl) {
		this.remoteurl = remoteurl;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public String getTargetButton() {
		return targetButton;
	}
	public void setTargetButton(String targetButton) {
		this.targetButton = targetButton;
	}
	public String getDefaultPageUrl() {
		return defaultPageUrl;
	}
	public void setDefaultPageUrl(String defaultPageUrl) {
		this.defaultPageUrl = defaultPageUrl;
	}
	
}

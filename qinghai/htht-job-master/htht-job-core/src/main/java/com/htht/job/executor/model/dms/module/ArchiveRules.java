package com.htht.job.executor.model.dms.module;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.htht.job.core.util.BaseEntity;

/**
 * 入库规则--正则表达式匹配数据
 * 
 * @author LY 2018-03-29
 *
 */
@Entity
@Table(name = "HTHT_DMS_SYS_ARCHIVERULES")
public class ArchiveRules extends BaseEntity {
	private static final long serialVersionUID = -6842323751933590047L;
	private String id;// ID
	private String regexpstr; // 正则表达式 --匹配原始压缩包
	private String regexpxml; // 正则表达式 --指定原始文件中的xml,用于xml解析
								// 可为空,代表默认原始文件中只包含一个xml
	private String regexpjpg; // 正则表达式 --指定原始文件中的jpg,用于jpg处理
								// 可为空,代表默认原始文件中只包含一个jpg
	private String catalogcode;// 目录表ID
	private String archivdisk;// 归档磁盘 可为空,或指定归档磁盘ID 为空时默认使用第一个归档磁盘
	private int rulestatus;// 0:可用 1:不可用
	private int flowid; // 流程ID
	@Transient
	private String flowName;
	
	private int filetype; // 文件类型 0-单文件 1-文件夹
	private String finalstr;// 文件夹类型产品结束标识:ok、fin
	private String datalevel;// 数据级别--由于各类数据的信息中，数据级别命名不统一，在这里统一好
	private String wspFile;// 工作空间需要的数据类型:jpg,xml
	private String allFile;// 多文件产品的所有数据类型:jpg,xml....

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRegexpstr() {
		return regexpstr;
	}

	public void setRegexpstr(String regexpstr) {
		this.regexpstr = regexpstr;
	}

	public String getCatalogcode() {
		return catalogcode;
	}

	public void setCatalogcode(String catalogcode) {
		this.catalogcode = catalogcode;
	}

	public String getArchivdisk() {
		return archivdisk;
	}

	public void setArchivdisk(String archivdisk) {
		this.archivdisk = archivdisk;
	}

	public int getRulestatus() {
		return rulestatus;
	}

	public void setRulestatus(int rulestatus) {
		this.rulestatus = rulestatus;
	}

	public String getRegexpxml() {
		return regexpxml;
	}

	public void setRegexpxml(String regexpxml) {
		this.regexpxml = regexpxml;
	}

	public String getRegexpjpg() {
		return regexpjpg;
	}

	public void setRegexpjpg(String regexpjpg) {
		this.regexpjpg = regexpjpg;
	}

	public int getFlowid() {
		return flowid;
	}

	public void setFlowid(int flowid) {
		this.flowid = flowid;
	}

	public int getFiletype() {
		return filetype;
	}

	public void setFiletype(int filetype) {
		this.filetype = filetype;
	}

	public String getDatalevel() {
		return datalevel;
	}

	public void setDatalevel(String datalevel) {
		this.datalevel = datalevel;
	}

	public String getFinalstr() {
		return finalstr;
	}

	public void setFinalstr(String finalstr) {
		this.finalstr = finalstr;
	}

	public String getWspFile() {
		return wspFile;
	}

	public void setWspFile(String wspFile) {
		this.wspFile = wspFile;
	}

	public String getAllFile() {
		return allFile;
	}

	public void setAllFile(String allFile) {
		this.allFile = allFile;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

}

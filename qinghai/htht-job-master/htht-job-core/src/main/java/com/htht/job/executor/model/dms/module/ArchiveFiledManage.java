package com.htht.job.executor.model.dms.module;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.htht.job.core.util.BaseEntity;

/**
 * 数据库字段对照表
 * 
 * @author LY 2018-04-08
 *
 */
@Entity
@Table(name = "HTHT_DMS_ARCHIVE_FILED_MANAGE")
public class ArchiveFiledManage extends BaseEntity {
	private static final long serialVersionUID = 4130453873980010083L;
	private String f_id;// ID
	private String f_cname; // 中文名称
	private String f_name;// 属性名称
	private String f_valuetype;// 值类型
	private Long f_length;// 字段长度
	private int f_valtype;// 0字符串，1数字，2时间

	public String getF_id() {
		return f_id;
	}

	public void setF_id(String f_id) {
		this.f_id = f_id;
	}

	public String getF_cname() {
		return f_cname;
	}

	public void setF_cname(String f_cname) {
		this.f_cname = f_cname;
	}

	public String getF_name() {
		return f_name;
	}

	public void setF_name(String f_name) {
		this.f_name = f_name;
	}

	public String getF_valuetype() {
		return f_valuetype;
	}

	public void setF_valuetype(String f_valuetype) {
		this.f_valuetype = f_valuetype;
	}

	public Long getF_length() {
		return f_length;
	}

	public void setF_length(Long f_length) {
		this.f_length = f_length;
	}

	public int getF_valtype() {
		return f_valtype;
	}

	public void setF_valtype(int f_valtype) {
		this.f_valtype = f_valtype;
	}

}

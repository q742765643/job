package com.htht.job.executor.hander.datamanage.orderdata.module;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 订单任务信息表
 * 
 * @author LY 2018-05-07
 *
 */
@Entity
@Table(name = "HTHT_DMS_ORDER_ONETIMEINFO")
public class OrderInfo extends BaseEntity {
	private static final long serialVersionUID = -558337703288537147L;
	private String f_id; // 表id:guid
	private String f_dataid; // 元数据id
	private String f_orderid; // 订单id
	private int f_isdistribute; // 是否已下载   0：未下载  1：已下载
	private int f_isexist; // 0：待提取数据；10:正在提取的数据；1:提取完成；2:提取失败
	private String f_productname; // 数据名称
	private String f_datastateinfo; // 分发数据状态信息
	public String getF_id() {
		return f_id;
	}
	public void setF_id(String f_id) {
		this.f_id = f_id;
	}
	public String getF_dataid() {
		return f_dataid;
	}
	public void setF_dataid(String f_dataid) {
		this.f_dataid = f_dataid;
	}
	public String getF_orderid() {
		return f_orderid;
	}
	public void setF_orderid(String f_orderid) {
		this.f_orderid = f_orderid;
	}
	public int getF_isdistribute() {
		return f_isdistribute;
	}
	public void setF_isdistribute(int f_isdistribute) {
		this.f_isdistribute = f_isdistribute;
	}
	public int getF_isexist() {
		return f_isexist;
	}
	public void setF_isexist(int f_isexist) {
		this.f_isexist = f_isexist;
	}
	public String getF_productname() {
		return f_productname;
	}
	public void setF_productname(String f_productname) {
		this.f_productname = f_productname;
	}
	public String getF_datastateinfo() {
		return f_datastateinfo;
	}
	public void setF_datastateinfo(String f_datastateinfo) {
		this.f_datastateinfo = f_datastateinfo;
	}
	
}

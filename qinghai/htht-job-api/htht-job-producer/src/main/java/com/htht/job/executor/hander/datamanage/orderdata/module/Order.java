package com.htht.job.executor.hander.datamanage.orderdata.module;

import com.htht.job.core.util.BaseEntity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 订单任务信息表
 * 
 * @author LY 2018-05-07
 *
 */
@Entity
@Table(name = "HTHT_DMS_ORDER_ONETIME")
public class Order extends BaseEntity {
	private static final long serialVersionUID = -558337703288537147L;
	private String f_id; // 表id:guid
	private String f_ordername; // 订单名称
	private String f_userid;  // 用户id
	private String f_username; // 用户名称
	private String f_departmentid; // 部门id
	private String f_departmentname; // 部门名称  
	private int f_orderstate; // 0-待审核  1-审核通过  2-审核未通过  3-数据准备中  4-数据已准备    5-分发完成  6-订单过期（清理过期数据）  7-取消订单   8-分发失败   9订单已删除   10数据准备失败
	private Date f_orderdate; // 订单时间
	private String f_checkid; // 审核人
	private String f_feedback; // 反馈意见
	private long f_datasize; // 数据大小
	private String f_serialid; // 流水订单号
	private String f_remark; // 备注
	private String f_dataunit; // 数据单位
	private int f_datacount; // 数据数量
	private Date f_ordercompleted; // 订单数据准备完成时间
	private String f_orderstateinfo; // 订单数据提取详情
	public String getF_id() {
		return f_id;
	}
	public void setF_id(String f_id) {
		this.f_id = f_id;
	}
	public String getF_ordername() {
		return f_ordername;
	}
	public void setF_ordername(String f_ordername) {
		this.f_ordername = f_ordername;
	}
	public String getF_userid() {
		return f_userid;
	}
	public void setF_userid(String f_userid) {
		this.f_userid = f_userid;
	}
	public String getF_username() {
		return f_username;
	}
	public void setF_username(String f_username) {
		this.f_username = f_username;
	}
	public String getF_departmentid() {
		return f_departmentid;
	}
	public void setF_departmentid(String f_departmentid) {
		this.f_departmentid = f_departmentid;
	}
	public String getF_departmentname() {
		return f_departmentname;
	}
	public void setF_departmentname(String f_departmentname) {
		this.f_departmentname = f_departmentname;
	}
	public int getF_orderstate() {
		return f_orderstate;
	}
	public void setF_orderstate(int f_orderstate) {
		this.f_orderstate = f_orderstate;
	}
	public Date getF_orderdate() {
		return f_orderdate;
	}
	public void setF_orderdate(Date f_orderdate) {
		this.f_orderdate = f_orderdate;
	}
	public String getF_checkid() {
		return f_checkid;
	}
	public void setF_checkid(String f_checkid) {
		this.f_checkid = f_checkid;
	}
	public String getF_feedback() {
		return f_feedback;
	}
	public void setF_feedback(String f_feedback) {
		this.f_feedback = f_feedback;
	}
	public long getF_datasize() {
		return f_datasize;
	}
	public void setF_datasize(long f_datasize) {
		this.f_datasize = f_datasize;
	}
	public String getF_serialid() {
		return f_serialid;
	}
	public void setF_serialid(String f_serialid) {
		this.f_serialid = f_serialid;
	}
	public String getF_remark() {
		return f_remark;
	}
	public void setF_remark(String f_remark) {
		this.f_remark = f_remark;
	}
	public String getF_dataunit() {
		return f_dataunit;
	}
	public void setF_dataunit(String f_dataunit) {
		this.f_dataunit = f_dataunit;
	}
	public int getF_datacount() {
		return f_datacount;
	}
	public void setF_datacount(int f_datacount) {
		this.f_datacount = f_datacount;
	}
	public Date getF_ordercompleted() {
		return f_ordercompleted;
	}
	public void setF_ordercompleted(Date f_ordercompleted) {
		this.f_ordercompleted = f_ordercompleted;
	}
	public String getF_orderstateinfo() {
		return f_orderstateinfo;
	}
	public void setF_orderstateinfo(String f_orderstateinfo) {
		this.f_orderstateinfo = f_orderstateinfo;
	}
	
	
}

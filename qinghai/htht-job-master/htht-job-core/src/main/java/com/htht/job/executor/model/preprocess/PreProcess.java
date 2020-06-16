package com.htht.job.executor.model.preprocess;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.htht.job.core.util.BaseEntity;
import com.htht.job.core.util.PreProcessConstant;

@Entity
@Table(name="htht_cluster_schedule_preprocess")
public class PreProcess extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "execute_time")
	private Date executeTime;//当前任务最近一次开始执行时间
	
	@Column(name = "product_id")
	private String productId;//产品id
	
	@Column(name = "mark")
	private String mark;	//产品标识
	
	@Column(name = "issue")
	private String issue;	//产品期次
	
	@Column(name = "status")
	private String status;	//状态
	
	public PreProcess() {
		super();
		this.status = PreProcessConstant.STATUS_INIT;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getMark() {
		return mark;
	}
	
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public String getIssue() {
		return issue;
	}
	
	public void setIssue(String issue) {
		this.issue = issue;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

}

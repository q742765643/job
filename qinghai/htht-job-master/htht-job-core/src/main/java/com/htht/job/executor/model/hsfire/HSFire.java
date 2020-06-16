package com.htht.job.executor.model.hsfire;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.htht.job.core.util.BaseEntity;


@Entity
@Table(name="htht_cluster_schedule_h8fire")
public class HSFire extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="issue")
	private String issue; //期号
	
	@Column(name="lon")
	private double lon;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Column(name="lat")
	private double lat;
	
	@Column(name="tfire")
	private double tfire;
	
	@Column(name="tbg")
	private double tbg;
	
	@Column(name="area")
	private double area;
	
	@Column(name="frp")
	private int frp;
	
	@Column(name="frp_n")
	private int frpN;
	
	@Column(name="lc")
	private int lc;
	
	@Column(name="cred")
	private int cred;
	
	@Column(name="region_id")
	private String regionId;
	
	@Column(name="product_info")
	private String productInfo;
	

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getTfire() {
		return tfire;
	}

	public void setTfire(double tfire) {
		this.tfire = tfire;
	}

	public double getTbg() {
		return tbg;
	}

	public void setTbg(double tbg) {
		this.tbg = tbg;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public int getFrp() {
		return frp;
	}

	public void setFrp(int frp) {
		this.frp = frp;
	}

	public int getFrpN() {
		return frpN;
	}

	public void setFrpN(int frpN) {
		this.frpN = frpN;
	}

	public int getLc() {
		return lc;
	}

	public void setLc(int lc) {
		this.lc = lc;
	}

	public int getCred() {
		return cred;
	}

	public void setCred(int cred) {
		this.cred = cred;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}
	
}

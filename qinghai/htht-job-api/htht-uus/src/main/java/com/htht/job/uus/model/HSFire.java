package com.htht.job.uus.model;

import java.io.Serializable;

public class HSFire implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String issue; //期号
	
	private double lon;
	
	private double lat;
	
	private double tfire;
	
	private double tbg;
	
	private double area;
	
	private int frp;
	
	private int frpN;
	
	private int lc;
	
	private int cred;
	
	private String regionId;
	
	private String productInfoId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getProductInfoId() {
		return productInfoId;
	}

	public void setProductInfoId(String productInfoId) {
		this.productInfoId = productInfoId;
	}
	
}

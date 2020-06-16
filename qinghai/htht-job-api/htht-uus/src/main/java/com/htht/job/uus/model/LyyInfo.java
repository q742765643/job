package com.htht.job.uus.model;

import java.io.Serializable;

public class LyyInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String issue; //期号
	
	private double lon;
	
	private double lat;
	
	private String isLyy;
	
	private String productInfoId;
	
	private String cycle;
	
	private String modelIdentify;
	
	private String fileName;

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

	public String getIsLyy() {
		return isLyy;
	}

	public void setIsLyy(String isLyy) {
		this.isLyy = isLyy;
	}

	public String getProductInfoId() {
		return productInfoId;
	}

	public void setProductInfoId(String productInfoId) {
		this.productInfoId = productInfoId;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getModelIdentify() {
		return modelIdentify;
	}

	public void setModelIdentify(String modelIdentify) {
		this.modelIdentify = modelIdentify;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	

}

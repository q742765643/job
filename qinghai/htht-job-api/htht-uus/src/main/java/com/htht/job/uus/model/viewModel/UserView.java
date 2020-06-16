package com.htht.job.uus.model.viewModel;

public class UserView {
	
	private String id;
	private String userName;
	private String nickName;
	private String regionId;
	private String regionName;
	private int regionLevel;
	
	private Double longitudeCenter; // 中心经度
	private Double latitudeCenter; 	// 中心纬度
	private Double longitudeMin; 	// 最小经度
	private Double longitudeMax; 	// 最大精度
	private Double latitudeMin;	 	// 最小纬度
	private Double latitudeMax; 	// 最大纬度
	
	public UserView() {
		super();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public int getRegionLevel() {
		return regionLevel;
	}

	public void setRegionLevel(int regionLevel) {
		this.regionLevel = regionLevel;
	}

	public Double getLongitudeCenter() {
		return longitudeCenter;
	}

	public void setLongitudeCenter(Double longitudeCenter) {
		this.longitudeCenter = longitudeCenter;
	}

	public Double getLatitudeCenter() {
		return latitudeCenter;
	}

	public void setLatitudeCenter(Double latitudeCenter) {
		this.latitudeCenter = latitudeCenter;
	}

	public Double getLongitudeMin() {
		return longitudeMin;
	}

	public void setLongitudeMin(Double longitudeMin) {
		this.longitudeMin = longitudeMin;
	}

	public Double getLongitudeMax() {
		return longitudeMax;
	}

	public void setLongitudeMax(Double longitudeMax) {
		this.longitudeMax = longitudeMax;
	}

	public Double getLatitudeMin() {
		return latitudeMin;
	}

	public void setLatitudeMin(Double latitudeMin) {
		this.latitudeMin = latitudeMin;
	}

	public Double getLatitudeMax() {
		return latitudeMax;
	}

	public void setLatitudeMax(Double latitudeMax) {
		this.latitudeMax = latitudeMax;
	}
	
	
}

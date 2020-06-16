package com.htht.job.uus.model;

import java.util.List;

/**
 * @ClassName: HthtRegionInfo
 * @Description: 行政区域
 * @author chensi
 * @date 2018年5月11日
 * 
 */
public class RegionInfo {

	private String regionId; 		// 区域ID
	private String parentRegionId;  // 上一级区域ID
	private Integer regionLevel; 	// 区域级别（省市县）
	private String areaName; 		// 区域名称
	private String districtName; 	// 全名
	private Double longitudeCenter; // 中心经度
	private Double latitudeCenter; 	// 中心纬度
	private Double longitudeMin; 	// 最小经度
	private Double longitudeMax; 	// 最大精度
	private Double latitudeMin;	 	// 最小纬度
	private Double latitudeMax; 	// 最大纬度

	private List<RegionInfo> subRegion;

	public RegionInfo() {
		super();
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getParentRegionId() {
		return parentRegionId;
	}

	public void setParentRegionId(String parentRegionId) {
		this.parentRegionId = parentRegionId;
	}

	public Integer getRegionLevel() {
		return regionLevel;
	}

	public void setRegionLevel(Integer regionLevel) {
		this.regionLevel = regionLevel;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public List<RegionInfo> getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(List<RegionInfo> subRegion) {
		this.subRegion = subRegion;
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

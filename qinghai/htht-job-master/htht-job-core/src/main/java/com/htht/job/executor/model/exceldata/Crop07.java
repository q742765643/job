package com.htht.job.executor.model.exceldata;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.xuxueli.poi.excel.annotation.ExcelField;

/**
 * 
 * Description: 县产量水平
 * 
 * @author chensi
 * @date 2019年1月7日
 */
@Entity
@Table(name = "t_r_agme_c07_ele")
public class Crop07 implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ExcelField(name = "序号")
	private int id; // 主键
	@ExcelField(name = "区站号")
	private String areaStation; // 区站号
    
    @ExcelField(name = "纬度")
	private double latitude; // 纬度
    @ExcelField(name = "经度")
	private double longitude; // 经度
    @ExcelField(name = "测站高度")
	private double stationElevation;// 测站高度
    @ExcelField(name = "创建时间")
	private Date createTime; // 创建时间
    @ExcelField(name = "更新时间")
	private Date updateTime; // 更新时间
    @ExcelField(name = "修改次数")
	private int numAmendment; // 修改次数
    @ExcelField(name = "观测方式")
	private int viewingMode; // 观测方式
    
    @ExcelField(name = "年度")
	private int year; // 年度
	@ExcelField(name = "作物名称")
	private String crops; // 作物名称
	@ExcelField(name = "测站产量水平")
	private String stationOutputLevel; // 测站产量水平
	@ExcelField(name = "县平均单产")
	private String aveYieldPerCounty; // 县平均单产
	@ExcelField(name = "县产量增减产百分率")
	private String countyProductionRatio; // 县产量增减产百分率
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAreaStation() {
		return areaStation;
	}
	public void setAreaStation(String areaStation) {
		this.areaStation = areaStation;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getStationElevation() {
		return stationElevation;
	}
	public void setStationElevation(double stationElevation) {
		this.stationElevation = stationElevation;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getNumAmendment() {
		return numAmendment;
	}
	public void setNumAmendment(int numAmendment) {
		this.numAmendment = numAmendment;
	}
	public int getViewingMode() {
		return viewingMode;
	}
	public void setViewingMode(int viewingMode) {
		this.viewingMode = viewingMode;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getCrops() {
		return crops;
	}
	public void setCrops(String crops) {
		this.crops = crops;
	}
	public String getStationOutputLevel() {
		return stationOutputLevel;
	}
	public void setStationOutputLevel(String stationOutputLevel) {
		this.stationOutputLevel = stationOutputLevel;
	}
	public String getAveYieldPerCounty() {
		return aveYieldPerCounty;
	}
	public void setAveYieldPerCounty(String aveYieldPerCounty) {
		this.aveYieldPerCounty = aveYieldPerCounty;
	}
	public String getCountyProductionRatio() {
		return countyProductionRatio;
	}
	public void setCountyProductionRatio(String countyProductionRatio) {
		this.countyProductionRatio = countyProductionRatio;
	}

}

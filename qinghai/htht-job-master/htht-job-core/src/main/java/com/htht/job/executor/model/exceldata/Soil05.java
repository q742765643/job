package com.htht.job.executor.model.exceldata;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.xuxueli.poi.excel.annotation.ExcelField;

/**
 * 
 * Description: 土壤冻结与解冻
 * 
 * @author chensi
 * @date 2019年1月7日
 */
@Entity
@Table(name = "t_r_agme_s05_ele")
public class Soil05 implements Serializable {

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

    @ExcelField(name = "测定时间")
	private Date testTime; // 测定时间
    @ExcelField(name = " 地段类型")
	private String locationType; // 地段类型
    @ExcelField(name = "土层深度")
	private String soilDepth; // 土层深度
    @ExcelField(name = "土层状态")
	private String soilState; // 土层状态
	@Column(name="freezing / thawing_time")
	@ExcelField(name = "冻结/解冻时间")
	private Date freezingOrThawingTime; // 冻结/解冻时间

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

	public Date getTestTime() {
		return testTime;
	}

	public void setTestTime(Date testTime) {
		this.testTime = testTime;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getSoilDepth() {
		return soilDepth;
	}

	public void setSoilDepth(String soilDepth) {
		this.soilDepth = soilDepth;
	}

	public String getSoilState() {
		return soilState;
	}

	public void setSoilState(String soilState) {
		this.soilState = soilState;
	}

	public Date getFreezingOrThawingTime() {
		return freezingOrThawingTime;
	}

	public void setFreezingOrThawingTime(Date freezingOrThawingTime) {
		this.freezingOrThawingTime = freezingOrThawingTime;
	}

}

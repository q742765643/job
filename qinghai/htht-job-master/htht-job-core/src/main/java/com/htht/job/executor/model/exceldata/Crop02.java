package com.htht.job.executor.model.exceldata;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;

import com.xuxueli.poi.excel.annotation.ExcelField;
import com.xuxueli.poi.excel.annotation.ExcelSheet;

/**
 * 
 * Description: 干物质与叶表面积
 * 
 * @author chensi
 * @date 2019年01月07日
 */
@Entity
@Table(name = "t_r_agme_c02_ele")
@ExcelSheet(name = "干物质与叶表面积表", headColor = HSSFColorPredefined.LIGHT_GREEN)
public class Crop02 implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ExcelField(name = "序号")
	private int id; // 主键
	@ExcelField(name = "区站号")
	private String areaStation; // 站点
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
	@ExcelField(name = "测试时间")
	private Date testTime; // 测试时间
	@ExcelField(name = "作物名称")
	private String crops; // 作物名称
	@ExcelField(name = "发育期")
	private int maturity; // 发育期
	@ExcelField(name = "生长率")
	private String growthRate; // 生长率
	@ExcelField(name = "含水率")
	private String waterCentent; // 含水率
	@ExcelField(name = "叶面积指数")
	private String leafAreaIndex; // 叶面积指数
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
	public String getCrops() {
		return crops;
	}
	public void setCrops(String crops) {
		this.crops = crops;
	}
	public int getMaturity() {
		return maturity;
	}
	public void setMaturity(int maturity) {
		this.maturity = maturity;
	}
	public String getGrowthRate() {
		return growthRate;
	}
	public void setGrowthRate(String growthRate) {
		this.growthRate = growthRate;
	}
	public String getWaterCentent() {
		return waterCentent;
	}
	public void setWaterCentent(String waterCentent) {
		this.waterCentent = waterCentent;
	}
	public String getLeafAreaIndex() {
		return leafAreaIndex;
	}
	public void setLeafAreaIndex(String leafAreaIndex) {
		this.leafAreaIndex = leafAreaIndex;
	}

}

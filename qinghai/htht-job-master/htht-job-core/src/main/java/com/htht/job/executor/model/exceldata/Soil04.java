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
 * Description: 有效水分储存量
 * 
 * @author chensi
 * @date 2019年1月7日
 */
@Entity
@Table(name = "t_r_agme_s04_ele")
public class Soil04 implements Serializable {

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
    @ExcelField(name = "地段类型")
	private String locationType; // 地段类型
    @ExcelField(name = "作物名称")
	private String crops; // 作物名称
    @ExcelField(name = "发育期")
	private String maturity; // 发育期
	
	@Column(name="effective_moisture_storage_10cm")
	@ExcelField(name = "10cm有效水分储存量")
	private String effectiveMoistureStorage10cm;// 10cm有效水分储存量
	
	@Column(name="effective_moisture_storage_20cm")
	@ExcelField(name = "20cm有效水分储存量")
	private String effectiveMoistureStorage20cm;// 20cm有效水分储存量
	
	@Column(name="effective_moisture_storage_30cm")
	@ExcelField(name = "30cm有效水分储存量")
	private String effectiveMoistureStorage30cm;// 30cm有效水分储存量
	
	@Column(name="effective_moisture_storage_40cm")
	@ExcelField(name = "40cm有效水分储存量")
	private String effectiveMoistureStorage40cm;// 40cm有效水分储存量
	
	@Column(name="effective_moisture_storage_50cm")
	@ExcelField(name = "50cm有效水分储存量")
	private String effectiveMoistureStorage50cm;// 50cm有效水分储存量
	
	@Column(name="effective_moisture_storage_60cm")
	@ExcelField(name = "60cm有效水分储存量")
	private String effectiveMoistureStorage60cm;// 60cm有效水分储存量
	
	@Column(name="effective_moisture_storage_70cm")
	@ExcelField(name = "70cm有效水分储存量")
	private String effectiveMoistureStorage70cm;// 70cm有效水分储存量
	
	@Column(name="effective_moisture_storage_80cm")
	@ExcelField(name = "80cm有效水分储存量")
	private String effectiveMoistureStorage80cm;// 80cm有效水分储存量
	
	@Column(name="effective_moisture_storage_90cm")
	@ExcelField(name = "90cm有效水分储存量")
	private String effectiveMoistureStorage90cm;// 90cm有效水分储存量
	
	@Column(name="effective_moisture_storage_100cm")
	@ExcelField(name = "100cm有效水分储存量")
	private String effectiveMoistureStorage100cm;// 100cm有效水分储存量

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

	public String getCrops() {
		return crops;
	}

	public void setCrops(String crops) {
		this.crops = crops;
	}

	public String getMaturity() {
		return maturity;
	}

	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}

	public String getEffectiveMoistureStorage10cm() {
		return effectiveMoistureStorage10cm;
	}

	public void setEffectiveMoistureStorage10cm(String effectiveMoistureStorage10cm) {
		this.effectiveMoistureStorage10cm = effectiveMoistureStorage10cm;
	}

	public String getEffectiveMoistureStorage20cm() {
		return effectiveMoistureStorage20cm;
	}

	public void setEffectiveMoistureStorage20cm(String effectiveMoistureStorage20cm) {
		this.effectiveMoistureStorage20cm = effectiveMoistureStorage20cm;
	}

	public String getEffectiveMoistureStorage30cm() {
		return effectiveMoistureStorage30cm;
	}

	public void setEffectiveMoistureStorage30cm(String effectiveMoistureStorage30cm) {
		this.effectiveMoistureStorage30cm = effectiveMoistureStorage30cm;
	}

	public String getEffectiveMoistureStorage40cm() {
		return effectiveMoistureStorage40cm;
	}

	public void setEffectiveMoistureStorage40cm(String effectiveMoistureStorage40cm) {
		this.effectiveMoistureStorage40cm = effectiveMoistureStorage40cm;
	}

	public String getEffectiveMoistureStorage50cm() {
		return effectiveMoistureStorage50cm;
	}

	public void setEffectiveMoistureStorage50cm(String effectiveMoistureStorage50cm) {
		this.effectiveMoistureStorage50cm = effectiveMoistureStorage50cm;
	}

	public String getEffectiveMoistureStorage60cm() {
		return effectiveMoistureStorage60cm;
	}

	public void setEffectiveMoistureStorage60cm(String effectiveMoistureStorage60cm) {
		this.effectiveMoistureStorage60cm = effectiveMoistureStorage60cm;
	}

	public String getEffectiveMoistureStorage70cm() {
		return effectiveMoistureStorage70cm;
	}

	public void setEffectiveMoistureStorage70cm(String effectiveMoistureStorage70cm) {
		this.effectiveMoistureStorage70cm = effectiveMoistureStorage70cm;
	}

	public String getEffectiveMoistureStorage80cm() {
		return effectiveMoistureStorage80cm;
	}

	public void setEffectiveMoistureStorage80cm(String effectiveMoistureStorage80cm) {
		this.effectiveMoistureStorage80cm = effectiveMoistureStorage80cm;
	}

	public String getEffectiveMoistureStorage90cm() {
		return effectiveMoistureStorage90cm;
	}

	public void setEffectiveMoistureStorage90cm(String effectiveMoistureStorage90cm) {
		this.effectiveMoistureStorage90cm = effectiveMoistureStorage90cm;
	}

	public String getEffectiveMoistureStorage100cm() {
		return effectiveMoistureStorage100cm;
	}

	public void setEffectiveMoistureStorage100cm(String effectiveMoistureStorage100cm) {
		this.effectiveMoistureStorage100cm = effectiveMoistureStorage100cm;
	}

}

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
 * Description: 土壤相对湿度
 * 
 * @author chensi
 * @date 2019年1月7日
 */
@Entity
@Table(name = "t_r_agme_s02_ele")
public class Soil02 implements Serializable {

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
    @ExcelField(name = "干土层厚度")
	private String drySoilThickness; // 干土层厚度
	
	@Column(name="soil_relative_moisture_10cm")
	@ExcelField(name = "10cm土壤相对湿度")
	private String soilRelativeMoisture10cm; // 10cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_20cm")
	@ExcelField(name = "20cm土壤相对湿度")
	private String soilRelativeMoisture20cm; // 20cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_30cm")
	@ExcelField(name = "30cm土壤相对湿度")
	private String soilRelativeMoisture30cm; // 30cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_40cm")
	@ExcelField(name = "40cm土壤相对湿度")
	private String soilRelativeMoisture40cm; // 40cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_50cm")
	@ExcelField(name = "50cm土壤相对湿度")
	private String soilRelativeMoisture50cm; // 50cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_60cm")
	@ExcelField(name = "60cm土壤相对湿度")
	private String soilRelativeMoisture60cm; // 60cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_70cm")
	@ExcelField(name = "70cm土壤相对湿度")
	private String soilRelativeMoisture70cm; // 70cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_80cm")
	@ExcelField(name = "80cm土壤相对湿度")
	private String soilRelativeMoisture80cm; // 80cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_90cm")
	@ExcelField(name = "90cm土壤相对湿度")
	private String soilRelativeMoisture90cm; // 90cm土壤相对湿度
	
	@Column(name="soil_relative_moisture_100cm")
	@ExcelField(name = "100cm土壤相对湿度")
	private String soilRelativeMoisture100cm; // 100cm土壤相对湿度
	
	@ExcelField(name = "灌溉或降水")
	private String irrigationOrPrecipitation; // 灌溉或降水

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

	public String getDrySoilThickness() {
		return drySoilThickness;
	}

	public void setDrySoilThickness(String drySoilThickness) {
		this.drySoilThickness = drySoilThickness;
	}

	public String getSoilRelativeMoisture10cm() {
		return soilRelativeMoisture10cm;
	}

	public void setSoilRelativeMoisture10cm(String soilRelativeMoisture10cm) {
		this.soilRelativeMoisture10cm = soilRelativeMoisture10cm;
	}

	public String getSoilRelativeMoisture20cm() {
		return soilRelativeMoisture20cm;
	}

	public void setSoilRelativeMoisture20cm(String soilRelativeMoisture20cm) {
		this.soilRelativeMoisture20cm = soilRelativeMoisture20cm;
	}

	public String getSoilRelativeMoisture30cm() {
		return soilRelativeMoisture30cm;
	}

	public void setSoilRelativeMoisture30cm(String soilRelativeMoisture30cm) {
		this.soilRelativeMoisture30cm = soilRelativeMoisture30cm;
	}

	public String getSoilRelativeMoisture40cm() {
		return soilRelativeMoisture40cm;
	}

	public void setSoilRelativeMoisture40cm(String soilRelativeMoisture40cm) {
		this.soilRelativeMoisture40cm = soilRelativeMoisture40cm;
	}

	public String getSoilRelativeMoisture50cm() {
		return soilRelativeMoisture50cm;
	}

	public void setSoilRelativeMoisture50cm(String soilRelativeMoisture50cm) {
		this.soilRelativeMoisture50cm = soilRelativeMoisture50cm;
	}

	public String getSoilRelativeMoisture60cm() {
		return soilRelativeMoisture60cm;
	}

	public void setSoilRelativeMoisture60cm(String soilRelativeMoisture60cm) {
		this.soilRelativeMoisture60cm = soilRelativeMoisture60cm;
	}

	public String getSoilRelativeMoisture70cm() {
		return soilRelativeMoisture70cm;
	}

	public void setSoilRelativeMoisture70cm(String soilRelativeMoisture70cm) {
		this.soilRelativeMoisture70cm = soilRelativeMoisture70cm;
	}

	public String getSoilRelativeMoisture80cm() {
		return soilRelativeMoisture80cm;
	}

	public void setSoilRelativeMoisture80cm(String soilRelativeMoisture80cm) {
		this.soilRelativeMoisture80cm = soilRelativeMoisture80cm;
	}

	public String getSoilRelativeMoisture90cm() {
		return soilRelativeMoisture90cm;
	}

	public void setSoilRelativeMoisture90cm(String soilRelativeMoisture90cm) {
		this.soilRelativeMoisture90cm = soilRelativeMoisture90cm;
	}

	public String getSoilRelativeMoisture100cm() {
		return soilRelativeMoisture100cm;
	}

	public void setSoilRelativeMoisture100cm(String soilRelativeMoisture100cm) {
		this.soilRelativeMoisture100cm = soilRelativeMoisture100cm;
	}

	public String getIrrigationOrPrecipitation() {
		return irrigationOrPrecipitation;
	}

	public void setIrrigationOrPrecipitation(String irrigationOrPrecipitation) {
		this.irrigationOrPrecipitation = irrigationOrPrecipitation;
	}

}

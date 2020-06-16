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
 * Description: 水分总储存量
 * 
 * @author chensi
 * @date 2019年1月7日
 */
@Entity
@Table(name = "t_r_agme_s03_ele")
public class Soil03 implements Serializable {

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
	
	@Column(name="total_water_reserves_10cm")
	@ExcelField(name = "10cm水分总储存量")
	private String totalWaterReserves10cm; // 10cm水分总储存量
	
	@Column(name="total_water_reserves_20cm")
	@ExcelField(name = "20cm水分总储存量")
	private String totalWaterReserves20cm; // 20cm水分总储存量
	
	@Column(name="total_water_reserves_30cm")
	@ExcelField(name = "30cm水分总储存量")
	private String totalWaterReserves30cm; // 30cm水分总储存量
	
	@Column(name="total_water_reserves_40cm")
	@ExcelField(name = "40cm水分总储存量")
	private String totalWaterReserves40cm; // 40cm水分总储存量
	
	@Column(name="total_water_reserves_50cm")
	@ExcelField(name = "50cm水分总储存量")
	private String totalWaterReserves50cm; // 50cm水分总储存量
	
	@Column(name="total_water_reserves_60cm")
	@ExcelField(name = "60cm水分总储存量")
	private String totalWaterReserves60cm; // 60cm水分总储存量
	
	@Column(name="total_water_reserves_70cm")
	@ExcelField(name = "70cm水分总储存量")
	private String totalWaterReserves70cm; // 70cm水分总储存量
	
	@Column(name="total_water_reserves_80cm")
	@ExcelField(name = "80cm水分总储存量")
	private String totalWaterReserves80cm; // 80cm水分总储存量
	
	@Column(name="total_water_reserves_90cm")
	@ExcelField(name = "90cm水分总储存量")
	private String totalWaterReserves90cm; // 90cm水分总储存量
	
	@Column(name="total_water_reserves_100cm")
	@ExcelField(name = "100cm水分总储存量")
	private String totalWaterReserves100cm; // 100cm水分总储存量

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

	public String getTotalWaterReserves10cm() {
		return totalWaterReserves10cm;
	}

	public void setTotalWaterReserves10cm(String totalWaterReserves10cm) {
		this.totalWaterReserves10cm = totalWaterReserves10cm;
	}

	public String getTotalWaterReserves20cm() {
		return totalWaterReserves20cm;
	}

	public void setTotalWaterReserves20cm(String totalWaterReserves20cm) {
		this.totalWaterReserves20cm = totalWaterReserves20cm;
	}

	public String getTotalWaterReserves30cm() {
		return totalWaterReserves30cm;
	}

	public void setTotalWaterReserves30cm(String totalWaterReserves30cm) {
		this.totalWaterReserves30cm = totalWaterReserves30cm;
	}

	public String getTotalWaterReserves40cm() {
		return totalWaterReserves40cm;
	}

	public void setTotalWaterReserves40cm(String totalWaterReserves40cm) {
		this.totalWaterReserves40cm = totalWaterReserves40cm;
	}

	public String getTotalWaterReserves50cm() {
		return totalWaterReserves50cm;
	}

	public void setTotalWaterReserves50cm(String totalWaterReserves50cm) {
		this.totalWaterReserves50cm = totalWaterReserves50cm;
	}

	public String getTotalWaterReserves60cm() {
		return totalWaterReserves60cm;
	}

	public void setTotalWaterReserves60cm(String totalWaterReserves60cm) {
		this.totalWaterReserves60cm = totalWaterReserves60cm;
	}

	public String getTotalWaterReserves70cm() {
		return totalWaterReserves70cm;
	}

	public void setTotalWaterReserves70cm(String totalWaterReserves70cm) {
		this.totalWaterReserves70cm = totalWaterReserves70cm;
	}

	public String getTotalWaterReserves80cm() {
		return totalWaterReserves80cm;
	}

	public void setTotalWaterReserves80cm(String totalWaterReserves80cm) {
		this.totalWaterReserves80cm = totalWaterReserves80cm;
	}

	public String getTotalWaterReserves90cm() {
		return totalWaterReserves90cm;
	}

	public void setTotalWaterReserves90cm(String totalWaterReserves90cm) {
		this.totalWaterReserves90cm = totalWaterReserves90cm;
	}

	public String getTotalWaterReserves100cm() {
		return totalWaterReserves100cm;
	}

	public void setTotalWaterReserves100cm(String totalWaterReserves100cm) {
		this.totalWaterReserves100cm = totalWaterReserves100cm;
	}

}

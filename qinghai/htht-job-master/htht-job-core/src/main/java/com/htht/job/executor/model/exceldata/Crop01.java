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
 * Description: 作物生长发育
 * 
 * @author chensi
 * @date 2019年01月07日
 */
@Entity
@Table(name = "t_r_agme_c01_ele")
@ExcelSheet(name = "作物生长发育表", headColor = HSSFColorPredefined.LIGHT_GREEN)
public class Crop01 implements Serializable{

	/** serialVersionUID*/  
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
    
    @ExcelField(name = "作物名称")
	private String crops; // 作物名称
    @ExcelField(name = "作物种类")
	private String breedsOfCrops; // 作物种类
    @ExcelField(name = "发育期")
	private int maturity; // 发育期
    @ExcelField(name = "发育时间")
	private Date developmentTime; // 发育时间
    @ExcelField(name = "发育期距平")
	private String developmentalAnomaly; // 发育期距平
    @ExcelField(name = "发育百分率")
	private int developmentalPercentage; // 发育百分率
    @ExcelField(name = "生长状态")
	private int growthStatus; // 生长状态
    @ExcelField(name = "植株高度")
	private String plantHeight; // 植株高度
	@ExcelField(name = "植株密度")
	private String plantSpacing; // 植株密度

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
	public String getCrops() {
		return crops;
	}
	public void setCrops(String crops) {
		this.crops = crops;
	}
	public String getBreedsOfCrops() {
		return breedsOfCrops;
	}
	public void setBreedsOfCrops(String breedsOfCrops) {
		this.breedsOfCrops = breedsOfCrops;
	}
	public int getMaturity() {
		return maturity;
	}
	public void setMaturity(int maturity) {
		this.maturity = maturity;
	}
	public Date getDevelopmentTime() {
		return developmentTime;
	}
	public void setDevelopmentTime(Date developmentTime) {
		this.developmentTime = developmentTime;
	}
	public String getDevelopmentalAnomaly() {
		return developmentalAnomaly;
	}
	public void setDevelopmentalAnomaly(String developmentalAnomaly) {
		this.developmentalAnomaly = developmentalAnomaly;
	}
	public int getDevelopmentalPercentage() {
		return developmentalPercentage;
	}
	public void setDevelopmentalPercentage(int developmentalPercentage) {
		this.developmentalPercentage = developmentalPercentage;
	}
	public int getGrowthStatus() {
		return growthStatus;
	}
	public void setGrowthStatus(int growthStatus) {
		this.growthStatus = growthStatus;
	}
	public String getPlantHeight() {
		return plantHeight;
	}
	public void setPlantHeight(String plantHeight) {
		this.plantHeight = plantHeight;
	}
	public String getPlantSpacing() {
		return plantSpacing;
	}
	public void setPlantSpacing(String plantSpacing) {
		this.plantSpacing = plantSpacing;
	}

}

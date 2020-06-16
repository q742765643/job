package com.htht.job.uus.model;

import java.util.Date;

public class ProductInfo {
	
	private String id;				//主键ID
	private Date createTime;		//创建时间
	private Date updateTime;		//修改时间
	private	Integer version;		//版本号
	private String cycle;			//周期
	private String name;			//名称
	private String issue;			//期次号
	private String mark;			//标记
	private String mapUrl; 			//地图路径
	private String featureName;		//
	private String productPath;		//产品路径
	private String gdbPath;			//gdb路径
	private String productId;		//产品ID
	private String regionId;		//区域编码ID
	private	short isRelease;		//
	private	String bz;				//备注
	private	String mosaicFile;		//镶嵌数据集
	private	String inputFileName;	//处理的文件名
	private	String modelIdentify;	//算法标识
	
	public ProductInfo() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCycle() {
		return cycle;
	}
	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getMapUrl() {
		return mapUrl;
	}
	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getProductPath() {
		return productPath;
	}
	public void setProductPath(String productPath) {
		this.productPath = productPath;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getRegionId() {
		return regionId;
	}
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	public short getIsRelease() {
		return isRelease;
	}
	public void setIsRelease(short isRelease) {
		this.isRelease = isRelease;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	public String getMosaicFile() {
		return mosaicFile;
	}
	public void setMosaicFile(String mosaicFile) {
		this.mosaicFile = mosaicFile;
	}
	public String getInputFileName() {
		return inputFileName;
	}
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	public String getModelIdentify() {
		return modelIdentify;
	}
	public void setModelIdentify(String modelIdentify) {
		this.modelIdentify = modelIdentify;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getGdbPath() {
		return gdbPath;
	}
	public void setGdbPath(String gdbPath) {
		this.gdbPath = gdbPath;
	}
	
}

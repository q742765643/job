package com.htht.job.executor.hander.dataarchiving.util.img;

import java.util.LinkedList;


public class PointBean {
	
	private Long productId ;    //产品ID
	private String productIdStr ;  //产品idstr
	private double topLeftX ;   //左上角经度
	private double topLeftY ;   //左上角纬度
	private double topRightX ;  //右上角经度
	private double topRightY ;  //右上角纬度
	
	private double bottomRightX ;  //右下角经度
	private double bottomRightY ;  //右下角纬度
	private double bottomLeftX ;   //左下角经度
	private double bottomLeftY ;   //左下角纬度
	
	private double DATAUPPERLEFTLAT;
	public double getDATAUPPERLEFTLAT() {
		return DATAUPPERLEFTLAT;
	}
	public void setDATAUPPERLEFTLAT(double dATAUPPERLEFTLAT) {
		DATAUPPERLEFTLAT = dATAUPPERLEFTLAT;
	}
	public double getDATAUPPERLEFTLONT() {
		return DATAUPPERLEFTLONT;
	}
	public void setDATAUPPERLEFTLONT(double dATAUPPERLEFTLONT) {
		DATAUPPERLEFTLONT = dATAUPPERLEFTLONT;
	}
	public double getDATAUPPERRIGHTLAT() {
		return DATAUPPERRIGHTLAT;
	}
	public void setDATAUPPERRIGHTLAT(double dATAUPPERRIGHTLAT) {
		DATAUPPERRIGHTLAT = dATAUPPERRIGHTLAT;
	}
	public double getDATAUPPERRIGHTLONG() {
		return DATAUPPERRIGHTLONG;
	}
	public void setDATAUPPERRIGHTLONG(double dATAUPPERRIGHTLONG) {
		DATAUPPERRIGHTLONG = dATAUPPERRIGHTLONG;
	}
	public double getDATALOWERLEFTLAT() {
		return DATALOWERLEFTLAT;
	}
	public void setDATALOWERLEFTLAT(double dATALOWERLEFTLAT) {
		DATALOWERLEFTLAT = dATALOWERLEFTLAT;
	}
	public double getDATALOWERLEFTLONG() {
		return DATALOWERLEFTLONG;
	}
	public void setDATALOWERLEFTLONG(double dATALOWERLEFTLONG) {
		DATALOWERLEFTLONG = dATALOWERLEFTLONG;
	}
	public double getDATALOWERRIGHTLAT() {
		return DATALOWERRIGHTLAT;
	}
	public void setDATALOWERRIGHTLAT(double dATALOWERRIGHTLAT) {
		DATALOWERRIGHTLAT = dATALOWERRIGHTLAT;
	}
	public double getDATALOWERRIGHTLONG() {
		return DATALOWERRIGHTLONG;
	}
	public void setDATALOWERRIGHTLONG(double dATALOWERRIGHTLONG) {
		DATALOWERRIGHTLONG = dATALOWERRIGHTLONG;
	}


	private double DATAUPPERLEFTLONT;
	
	private double DATAUPPERRIGHTLAT;
	private double DATAUPPERRIGHTLONG;
	
	private double DATALOWERLEFTLAT;
	private double DATALOWERLEFTLONG;
	
	private double DATALOWERRIGHTLAT;
	private double DATALOWERRIGHTLONG;
	
	private Long imgEndTime;//图片结束时间 (库中存放为String）类型
    private Long imgStartTime;//图片开始时间(库中存放为String）类型
    private String satelliteId;//卫星ID
    private String sensorId;//传感器ID
    private String earthModel;//模式
	
	
	private String areaName;
	private Long dataid;
	private String dataname;
	private String satelliteid;
	private String sensorid;
	private String productdate;
	private String productlevel;
	private Long overaliquality;
	private String recstationid;
	private Long scenepath;
	private Long scenerow;
	private String scenedate;
	private String direction;
	private Long sceneid;
	
	private Long catalogId;
	
	private Double centerX;  //中心点x
	private Double centerY;  //中心点Y
	
	public Long getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(Long catalogId) {
		this.catalogId = catalogId;
	}
	public Long getSceneid() {
		if (sceneid == null) {
			sceneid = Long.valueOf(0l);
		}
		return sceneid;
	}
	public void setSceneid(Long sceneid) {
		this.sceneid = sceneid;
	}
	public String getProductdate() {
		return productdate;
	}
	public void setProductdate(String productdate) {
		this.productdate = productdate;
	}
	public String getScenedate() {
		return scenedate;
	}
	public void setScenedate(String scenedate) {
		this.scenedate = scenedate;
	}

	
	private Long oid;
	public Long getOid() {
		return oid;
	}
	public void setOid(Long oid) {
		this.oid = oid;
	}
	public Long getDataid() {
		return dataid;
	}
	public void setDataid(Long dataid) {
		this.dataid = dataid;
	}
	public String getDataname() {
		return dataname;
	}
	public void setDataname(String dataname) {
		this.dataname = dataname;
	}
	public String getSatelliteid() {
		return satelliteid;
	}
	public void setSatelliteid(String satelliteid) {
		this.satelliteid = satelliteid;
	}
	public String getSensorid() {
		return sensorid;
	}
	public void setSensorid(String sensorid) {
		this.sensorid = sensorid;
	}
	
	public String getProductlevel() {
		return productlevel;
	}
	public void setProductlevel(String productlevel) {
		this.productlevel = productlevel;
	}
	public Long getOveraliquality() {
		return overaliquality;
	}
	public void setOveraliquality(Long overaliquality) {
		this.overaliquality = overaliquality;
	}
	public String getRecstationid() {
		return recstationid;
	}
	public void setRecstationid(String recstationid) {
		this.recstationid = recstationid;
	}
	public Long getScenepath() {
		return scenepath;
	}
	public void setScenepath(Long scenepath) {
		this.scenepath = scenepath;
	}
	public Long getScenerow() {
		return scenerow;
	}
	public void setScenerow(Long scenerow) {
		this.scenerow = scenerow;
	}
	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	
	private LinkedList<PointPolygon> polygonList ;
	public double getTopLeftX() {
		return topLeftX;
	}
	public void setTopLeftX(double topLeftX) {
		this.topLeftX = topLeftX;
	}
	public double getTopLeftY() {
		return topLeftY;
	}
	public void setTopLeftY(double topLeftY) {
		this.topLeftY = topLeftY;
	}
	public double getBottomRightX() {
		return bottomRightX;
	}
	public void setBottomRightX(double bottomRightX) {
		this.bottomRightX = bottomRightX;
	}
	public double getBottomRightY() {
		return bottomRightY;
	}
	public void setBottomRightY(double bottomRightY) {
		this.bottomRightY = bottomRightY;
	}
	public double getTopRightX() {
		return topRightX;
	}
	public void setTopRightX(double topRightX) {
		this.topRightX = topRightX;
	}
	public double getTopRightY() {
		return topRightY;
	}
	public void setTopRightY(double topRightY) {
		this.topRightY = topRightY;
	}
	public double getBottomLeftX() {
		return bottomLeftX;
	}
	public void setBottomLeftX(double bottomLeftX) {
		this.bottomLeftX = bottomLeftX;
	}
	public double getBottomLeftY() {
		return bottomLeftY;
	}
	public void setBottomLeftY(double bottomLeftY) {
		this.bottomLeftY = bottomLeftY;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public LinkedList<PointPolygon> getPolygonList() {
		return polygonList;
	}
	public void setPolygonList(LinkedList<PointPolygon> polygonList) {
		this.polygonList = polygonList;
	}
	public Double getCenterX() {
		return centerX;
	}
	public void setCenterX(Double centerX) {
		this.centerX = centerX;
	}
	public Double getCenterY() {
		return centerY;
	}
	public void setCenterY(Double centerY) {
		this.centerY = centerY;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getProductIdStr() {
		return productIdStr;
	}
	public void setProductIdStr(String productIdStr) {
		this.productIdStr = productIdStr;
	}
	
	public Long getImgEndTime() {
		return imgEndTime;
	}
	public void setImgEndTime(Long imgEndTime) {
		this.imgEndTime = imgEndTime;
	}
	public Long getImgStartTime() {
		return imgStartTime;
	}
	public void setImgStartTime(Long imgStartTime) {
		this.imgStartTime = imgStartTime;
	}
	public String getSatelliteId() {
		return satelliteId;
	}
	public void setSatelliteId(String satelliteId) {
		this.satelliteId = satelliteId;
	}
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	public String getEarthModel() {
		return earthModel;
	}
	public void setEarthModel(String earthModel) {
		this.earthModel = earthModel;
	}
	
}

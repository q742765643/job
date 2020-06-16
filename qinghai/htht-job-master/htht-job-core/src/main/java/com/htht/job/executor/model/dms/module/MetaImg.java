package com.htht.job.executor.model.dms.module;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the htht_dms_meta_img database table.
 */
@Entity
@Table(name = "htht_dms_meta_img")
public class MetaImg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "F_DATAID")
	private String fDataid;

	@Column(name = "F_BAND")
	private String fBand;

	@Column(name = "F_CATALOGCODE")
	private String fCatalogcode;

	@Column(name = "F_CENTERLAT")
	private BigDecimal fCenterlat;

	@Column(name = "F_CENTERLONG")
	private BigDecimal fCenterlong;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "F_CENTERTIME")
	private Date fCentertime;

	@Column(name = "F_CLOUDAMOUNT")
	private Integer fCloudamount;

	@Column(name = "F_CYCLENUMBER")
	private Integer fCyclenumber;

	@Column(name = "F_DATALOWERLEFTLAT")
	private Double fDatalowerleftlat;

	@Column(name = "F_DATALOWERLEFTLONG")
	private Double fDatalowerleftlong;

	@Column(name = "F_DATALOWERRIGHTLAT")
	private Double fDatalowerrightlat;

	@Column(name = "F_DATALOWERRIGHTLONG")
	private Double fDatalowerrightlong;

	@Lob
	@Column(name = "F_DATASOURCE")
	private String fDatasource;

	@Column(name = "F_DATAUPPERLEFTLAT")
	private Double fDataupperleftlat;

	@Column(name = "F_DATAUPPERLEFTLONG")
	private Double fDataupperleftlong;

	@Column(name = "F_DATAUPPERRIGHTLAT")
	private Double fDataupperrightlat;

	@Column(name = "F_DATAUPPERRIGHTLONG")
	private Double fDataupperrightlong;

	@Column(name = "F_DATAVERSION")
	private String fDataversion;

	@Column(name = "F_EARTHELLIPSOID")
	private String fEarthellipsoid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "F_ENDTIME")
	private Date fEndtime;

	@Column(name = "F_FORMAT")
	private String fFormat;

	@Column(name = "F_HEIGHTINPIXELS")
	private Integer fHeightinpixels;

	@Column(name = "F_IMAGINGMODE")
	private String fImagingmode;

	@Column(name = "F_LEVEL")
	private String fLevel;

	@Column(name = "F_MAPPROJECTION")
	private String fMapprojection;

	@Column(name = "F_ORBITID")
	private Integer fOrbitid;

	@Column(name = "F_PASSNUMBER")
	private Integer fPassnumber;

	@Column(name = "F_PRODUCESOFTWARE")
	private String fProducesoftware;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "F_PRODUCETIME")
	private Date fProducetime;

	@Column(name = "F_PRODUCTID")
	private Integer fProductid;

	@Column(name = "F_PRODUCTUNIT")
	private String fProductunit;

	@Column(name = "F_QULITYTAG")
	private Integer fQulitytag;

	@Column(name = "F_RECEIVESTATION")
	private String fReceivestation;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "F_RECEIVETIME")
	private Date fReceivetime;

	@Column(name = "F_RESOLUTIONX")
	private BigDecimal fResolutionx;

	@Column(name = "F_RESOLUTIONY")
	private BigDecimal fResolutiony;

	@Column(name = "F_SATAD")
	private String fSatad;

	@Column(name = "F_SATELLITEID")
	private String fSatelliteid;

	@Column(name = "F_SCENEID")
	private Integer fSceneid;

	@Column(name = "F_SCENEPATH")
	private Integer fScenepath;

	@Column(name = "F_SCENEROW")
	private Integer fScenerow;

	@Column(name = "F_SENSORID")
	private String fSensorid;

	@Column(name = "F_ORBITIDENTIFY")
	private String fOrbitIdentify;

	@Column(name = "F_ISMOSAIC")
	private String fIsmosaic;

	@Column(name = "f_projectiontype")
	private String fProjectionType;//投影类型

	@Column(name = "F_DATATYPE")
	private String fDatatype;

	@Column(name = "F_DAYORNIGHT")
	private String dayOrNight;


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "F_STARTTIME")
	private Date fStarttime;

	@Column(name = "F_WIDTHINPIXELS")
	private Integer fWidthinpixels;


	public String getDayOrNight() {
		return dayOrNight;
	}

	public void setDayOrNight(String dayOrNight) {
		this.dayOrNight = dayOrNight;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getfIsmosaic() {
		return fIsmosaic;
	}

	public void setfIsmosaic(String fIsmosaic) {
		this.fIsmosaic = fIsmosaic;
	}

	public String getfOrbitIdentify() {
		return fOrbitIdentify;
	}

	public void setfOrbitIdentify(String fOrbitIdentify) {
		this.fOrbitIdentify = fOrbitIdentify;
	}

	public String getfDatatype() {
		return fDatatype;
	}

	public void setfDatatype(String fDatatype) {
		this.fDatatype = fDatatype;
	}

	public String getfDataid() {
		return fDataid;
	}

	public void setfDataid(String fDataid) {
		this.fDataid = fDataid;
	}

	public String getfBand() {
		return fBand;
	}

	public void setfBand(String fBand) {
		this.fBand = fBand;
	}

	public String getfCatalogcode() {
		return fCatalogcode;
	}

	public void setfCatalogcode(String fCatalogcode) {
		this.fCatalogcode = fCatalogcode;
	}

	public BigDecimal getfCenterlat() {
		return fCenterlat;
	}

	public void setfCenterlat(BigDecimal fCenterlat) {
		this.fCenterlat = fCenterlat;
	}

	public BigDecimal getfCenterlong() {
		return fCenterlong;
	}

	public void setfCenterlong(BigDecimal fCenterlong) {
		this.fCenterlong = fCenterlong;
	}

	public Date getfCentertime() {
		return fCentertime;
	}

	public void setfCentertime(Date fCentertime) {
		this.fCentertime = fCentertime;
	}

	public Integer getfCloudamount() {
		return fCloudamount;
	}

	public void setfCloudamount(Integer fCloudamount) {
		this.fCloudamount = fCloudamount;
	}

	public Integer getfCyclenumber() {
		return fCyclenumber;
	}

	public void setfCyclenumber(Integer fCyclenumber) {
		this.fCyclenumber = fCyclenumber;
	}

	public Double getfDatalowerleftlat() {
		return fDatalowerleftlat;
	}

	public void setfDatalowerleftlat(Double fDatalowerleftlat) {
		this.fDatalowerleftlat = fDatalowerleftlat;
	}

	public Double getfDatalowerleftlong() {
		return fDatalowerleftlong;
	}

	public void setfDatalowerleftlong(Double fDatalowerleftlong) {
		this.fDatalowerleftlong = fDatalowerleftlong;
	}

	public Double getfDatalowerrightlat() {
		return fDatalowerrightlat;
	}

	public void setfDatalowerrightlat(Double fDatalowerrightlat) {
		this.fDatalowerrightlat = fDatalowerrightlat;
	}

	public Double getfDatalowerrightlong() {
		return fDatalowerrightlong;
	}

	public void setfDatalowerrightlong(Double fDatalowerrightlong) {
		this.fDatalowerrightlong = fDatalowerrightlong;
	}

	public String getfDatasource() {
		return fDatasource;
	}

	public void setfDatasource(String fDatasource) {
		this.fDatasource = fDatasource;
	}

	public Double getfDataupperleftlat() {
		return fDataupperleftlat;
	}

	public void setfDataupperleftlat(Double fDataupperleftlat) {
		this.fDataupperleftlat = fDataupperleftlat;
	}

	public Double getfDataupperleftlong() {
		return fDataupperleftlong;
	}

	public void setfDataupperleftlong(Double fDataupperleftlong) {
		this.fDataupperleftlong = fDataupperleftlong;
	}

	public Double getfDataupperrightlat() {
		return fDataupperrightlat;
	}

	public void setfDataupperrightlat(Double fDataupperrightlat) {
		this.fDataupperrightlat = fDataupperrightlat;
	}

	public Double getfDataupperrightlong() {
		return fDataupperrightlong;
	}

	public void setfDataupperrightlong(Double fDataupperrightlong) {
		this.fDataupperrightlong = fDataupperrightlong;
	}

	public String getfDataversion() {
		return fDataversion;
	}

	public void setfDataversion(String fDataversion) {
		this.fDataversion = fDataversion;
	}

	public String getfEarthellipsoid() {
		return fEarthellipsoid;
	}

	public void setfEarthellipsoid(String fEarthellipsoid) {
		this.fEarthellipsoid = fEarthellipsoid;
	}

	public Date getfEndtime() {
		return fEndtime;
	}

	public void setfEndtime(Date fEndtime) {
		this.fEndtime = fEndtime;
	}

	public String getfFormat() {
		return fFormat;
	}

	public void setfFormat(String fFormat) {
		this.fFormat = fFormat;
	}

	public Integer getfHeightinpixels() {
		return fHeightinpixels;
	}

	public void setfHeightinpixels(Integer fHeightinpixels) {
		this.fHeightinpixels = fHeightinpixels;
	}

	public String getfImagingmode() {
		return fImagingmode;
	}

	public void setfImagingmode(String fImagingmode) {
		this.fImagingmode = fImagingmode;
	}

	public String getfLevel() {
		return fLevel;
	}

	public void setfLevel(String fLevel) {
		this.fLevel = fLevel;
	}

	public String getfMapprojection() {
		return fMapprojection;
	}

	public void setfMapprojection(String fMapprojection) {
		this.fMapprojection = fMapprojection;
	}

	public Integer getfOrbitid() {
		return fOrbitid;
	}

	public void setfOrbitid(Integer fOrbitid) {
		this.fOrbitid = fOrbitid;
	}

	public Integer getfPassnumber() {
		return fPassnumber;
	}

	public void setfPassnumber(Integer fPassnumber) {
		this.fPassnumber = fPassnumber;
	}

	public String getfProducesoftware() {
		return fProducesoftware;
	}

	public void setfProducesoftware(String fProducesoftware) {
		this.fProducesoftware = fProducesoftware;
	}

	public Date getfProducetime() {
		return fProducetime;
	}

	public void setfProducetime(Date fProducetime) {
		this.fProducetime = fProducetime;
	}

	public Integer getfProductid() {
		return fProductid;
	}

	public void setfProductid(Integer fProductid) {
		this.fProductid = fProductid;
	}

	public String getfProductunit() {
		return fProductunit;
	}

	public void setfProductunit(String fProductunit) {
		this.fProductunit = fProductunit;
	}

	public Integer getfQulitytag() {
		return fQulitytag;
	}

	public void setfQulitytag(Integer fQulitytag) {
		this.fQulitytag = fQulitytag;
	}

	public String getfReceivestation() {
		return fReceivestation;
	}

	public void setfReceivestation(String fReceivestation) {
		this.fReceivestation = fReceivestation;
	}

	public Date getfReceivetime() {
		return fReceivetime;
	}

	public void setfReceivetime(Date fReceivetime) {
		this.fReceivetime = fReceivetime;
	}

	public BigDecimal getfResolutionx() {
		return fResolutionx;
	}

	public void setfResolutionx(BigDecimal fResolutionx) {
		this.fResolutionx = fResolutionx;
	}

	public BigDecimal getfResolutiony() {
		return fResolutiony;
	}

	public void setfResolutiony(BigDecimal fResolutiony) {
		this.fResolutiony = fResolutiony;
	}

	public String getfSatad() {
		return fSatad;
	}

	public void setfSatad(String fSatad) {
		this.fSatad = fSatad;
	}

	public String getfSatelliteid() {
		return fSatelliteid;
	}

	public void setfSatelliteid(String fSatelliteid) {
		this.fSatelliteid = fSatelliteid;
	}

	public Integer getfSceneid() {
		return fSceneid;
	}

	public void setfSceneid(Integer fSceneid) {
		this.fSceneid = fSceneid;
	}

	public Integer getfScenepath() {
		return fScenepath;
	}

	public void setfScenepath(Integer fScenepath) {
		this.fScenepath = fScenepath;
	}

	public Integer getfScenerow() {
		return fScenerow;
	}

	public void setfScenerow(Integer fScenerow) {
		this.fScenerow = fScenerow;
	}

	public String getfSensorid() {
		return fSensorid;
	}

	public void setfSensorid(String fSensorid) {
		this.fSensorid = fSensorid;
	}

	public Date getfStarttime() {
		return fStarttime;
	}

	public void setfStarttime(Date fStarttime) {
		this.fStarttime = fStarttime;
	}

	public Integer getfWidthinpixels() {
		return fWidthinpixels;
	}

	public void setfWidthinpixels(Integer fWidthinpixels) {
		this.fWidthinpixels = fWidthinpixels;
	}

	public String getfProjectionType() {
		return fProjectionType;
	}

	public void setfProjectionType(String fProjectionType) {
		this.fProjectionType = fProjectionType;
	}

}

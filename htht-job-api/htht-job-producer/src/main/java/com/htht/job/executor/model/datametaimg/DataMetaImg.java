package com.htht.job.executor.model.datametaimg;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the htht_dms_meta_img database table.
 * 
 */
@Entity
@Table(name="htht_dms_meta_img")
//@NamedQuery(name="MetaImg.findAll", query="SELECT t FROM TbMetaImg t")
public class DataMetaImg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="F_DATAID")
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 这个是hibernate的注解/生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	private String fDataid;

	@Column(name="F_BAND")
	private String fBand;

	@Column(name="F_CATALOGCODE")
	private String fCatalogcode;

	@Column(name="F_CENTERLAT")
	private BigDecimal fCenterlat;

	@Column(name="F_CENTERLONG")
	private BigDecimal fCenterlong;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="F_CENTERTIME")
	private Date fCentertime;

	@Column(name="F_CLOUDAMOUNT")
	private int fCloudamount;

	@Column(name="F_CYCLENUMBER")
	private int fCyclenumber;

	@Column(name="F_DATALOWERLEFTLAT")
	private double fDatalowerleftlat;

	@Column(name="F_DATALOWERLEFTLONG")
	private double fDatalowerleftlong;

	@Column(name="F_DATALOWERRIGHTLAT")
	private double fDatalowerrightlat;

	@Column(name="F_DATALOWERRIGHTLONG")
	private double fDatalowerrightlong;

	@Lob
	@Column(name="F_DATASOURCE")
	private String fDatasource;

	@Column(name="F_DATAUPPERLEFTLAT")
	private double fDataupperleftlat;

	@Column(name="F_DATAUPPERLEFTLONG")
	private double fDataupperleftlong;

	@Column(name="F_DATAUPPERRIGHTLAT")
	private double fDataupperrightlat;

	@Column(name="F_DATAUPPERRIGHTLONG")
	private double fDataupperrightlong;

	@Column(name="F_DATAVERSION")
	private String fDataversion;

	@Column(name="F_EARTHELLIPSOID")
	private String fEarthellipsoid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="F_ENDTIME")
	private Date fEndtime;

	@Column(name="F_FORMAT")
	private String fFormat;

	@Column(name="F_HEIGHTINPIXELS")
	private int fHeightinpixels;

	@Column(name="F_IMAGINGMODE")
	private String fImagingmode;

	@Column(name="F_LEVEL")
	private String fLevel;

	@Column(name="F_MAPPROJECTION")
	private String fMapprojection;

	@Column(name="F_ORBITID")
	private int fOrbitid;

	@Column(name="F_PASSNUMBER")
	private int fPassnumber;

	@Column(name="F_PRODUCESOFTWARE")
	private String fProducesoftware;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="F_PRODUCETIME")
	private Date fProducetime;

	@Column(name="F_PRODUCTID")
	private int fProductid;

	@Column(name="F_PRODUCTUNIT")
	private String fProductunit;

	@Column(name="F_QULITYTAG")
	private int fQulitytag;

	@Column(name="F_RECEIVESTATION")
	private String fReceivestation;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="F_RECEIVETIME")
	private Date fReceivetime;

	@Column(name="F_RESOLUTIONX")
	private BigDecimal fResolutionx;

	@Column(name="F_RESOLUTIONY")
	private BigDecimal fResolutiony;

	@Column(name="F_SATAD")
	private String fSatad;

	@Column(name="F_SATELLITEID")
	private String fSatelliteid;

	@Column(name="F_SCENEID")
	private int fSceneid;

	@Column(name="F_SCENEPATH")
	private int fScenepath;

	@Column(name="F_SCENEROW")
	private int fScenerow;

	@Column(name="F_SENSORID")
	private String fSensorid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="F_STARTTIME")
	private Date fStarttime;

	@Column(name="F_WIDTHINPIXELS")
	private int fWidthinpixels;

	public DataMetaImg() {
	}

	public String getFDataid() {
		return this.fDataid;
	}

	public void setFDataid(String fDataid) {
		this.fDataid = fDataid;
	}

	public String getFBand() {
		return this.fBand;
	}

	public void setFBand(String fBand) {
		this.fBand = fBand;
	}

	public String getFCatalogcode() {
		return this.fCatalogcode;
	}

	public void setFCatalogcode(String fCatalogcode) {
		this.fCatalogcode = fCatalogcode;
	}

	public BigDecimal getFCenterlat() {
		return this.fCenterlat;
	}

	public void setFCenterlat(BigDecimal fCenterlat) {
		this.fCenterlat = fCenterlat;
	}

	public BigDecimal getFCenterlong() {
		return this.fCenterlong;
	}

	public void setFCenterlong(BigDecimal fCenterlong) {
		this.fCenterlong = fCenterlong;
	}

	public Date getFCentertime() {
		return this.fCentertime;
	}

	public void setFCentertime(Date fCentertime) {
		this.fCentertime = fCentertime;
	}

	public int getFCloudamount() {
		return this.fCloudamount;
	}

	public void setFCloudamount(int fCloudamount) {
		this.fCloudamount = fCloudamount;
	}

	public int getFCyclenumber() {
		return this.fCyclenumber;
	}

	public void setFCyclenumber(int fCyclenumber) {
		this.fCyclenumber = fCyclenumber;
	}

	public double getFDatalowerleftlat() {
		return this.fDatalowerleftlat;
	}

	public void setFDatalowerleftlat(double fDatalowerleftlat) {
		this.fDatalowerleftlat = fDatalowerleftlat;
	}

	public double getFDatalowerleftlong() {
		return this.fDatalowerleftlong;
	}

	public void setFDatalowerleftlong(double fDatalowerleftlong) {
		this.fDatalowerleftlong = fDatalowerleftlong;
	}

	public double getFDatalowerrightlat() {
		return this.fDatalowerrightlat;
	}

	public void setFDatalowerrightlat(double fDatalowerrightlat) {
		this.fDatalowerrightlat = fDatalowerrightlat;
	}

	public double getFDatalowerrightlong() {
		return this.fDatalowerrightlong;
	}

	public void setFDatalowerrightlong(double fDatalowerrightlong) {
		this.fDatalowerrightlong = fDatalowerrightlong;
	}

	public String getFDatasource() {
		return this.fDatasource;
	}

	public void setFDatasource(String fDatasource) {
		this.fDatasource = fDatasource;
	}

	public double getFDataupperleftlat() {
		return this.fDataupperleftlat;
	}

	public void setFDataupperleftlat(double fDataupperleftlat) {
		this.fDataupperleftlat = fDataupperleftlat;
	}

	public double getFDataupperleftlong() {
		return this.fDataupperleftlong;
	}

	public void setFDataupperleftlong(double fDataupperleftlong) {
		this.fDataupperleftlong = fDataupperleftlong;
	}

	public double getFDataupperrightlat() {
		return this.fDataupperrightlat;
	}

	public void setFDataupperrightlat(double fDataupperrightlat) {
		this.fDataupperrightlat = fDataupperrightlat;
	}

	public double getFDataupperrightlong() {
		return this.fDataupperrightlong;
	}

	public void setFDataupperrightlong(double fDataupperrightlong) {
		this.fDataupperrightlong = fDataupperrightlong;
	}

	public String getFDataversion() {
		return this.fDataversion;
	}

	public void setFDataversion(String fDataversion) {
		this.fDataversion = fDataversion;
	}

	public String getFEarthellipsoid() {
		return this.fEarthellipsoid;
	}

	public void setFEarthellipsoid(String fEarthellipsoid) {
		this.fEarthellipsoid = fEarthellipsoid;
	}

	public Date getFEndtime() {
		return this.fEndtime;
	}

	public void setFEndtime(Date fEndtime) {
		this.fEndtime = fEndtime;
	}

	public String getFFormat() {
		return this.fFormat;
	}

	public void setFFormat(String fFormat) {
		this.fFormat = fFormat;
	}

	public int getFHeightinpixels() {
		return this.fHeightinpixels;
	}

	public void setFHeightinpixels(int fHeightinpixels) {
		this.fHeightinpixels = fHeightinpixels;
	}

	public String getFImagingmode() {
		return this.fImagingmode;
	}

	public void setFImagingmode(String fImagingmode) {
		this.fImagingmode = fImagingmode;
	}

	public String getFLevel() {
		return this.fLevel;
	}

	public void setFLevel(String fLevel) {
		this.fLevel = fLevel;
	}

	public String getFMapprojection() {
		return this.fMapprojection;
	}

	public void setFMapprojection(String fMapprojection) {
		this.fMapprojection = fMapprojection;
	}

	public int getFOrbitid() {
		return this.fOrbitid;
	}

	public void setFOrbitid(int fOrbitid) {
		this.fOrbitid = fOrbitid;
	}

	public int getFPassnumber() {
		return this.fPassnumber;
	}

	public void setFPassnumber(int fPassnumber) {
		this.fPassnumber = fPassnumber;
	}

	public String getFProducesoftware() {
		return this.fProducesoftware;
	}

	public void setFProducesoftware(String fProducesoftware) {
		this.fProducesoftware = fProducesoftware;
	}

	public Date getFProducetime() {
		return this.fProducetime;
	}

	public void setFProducetime(Date fProducetime) {
		this.fProducetime = fProducetime;
	}

	public int getFProductid() {
		return this.fProductid;
	}

	public void setFProductid(int fProductid) {
		this.fProductid = fProductid;
	}

	public String getFProductunit() {
		return this.fProductunit;
	}

	public void setFProductunit(String fProductunit) {
		this.fProductunit = fProductunit;
	}

	public int getFQulitytag() {
		return this.fQulitytag;
	}

	public void setFQulitytag(int fQulitytag) {
		this.fQulitytag = fQulitytag;
	}

	public String getFReceivestation() {
		return this.fReceivestation;
	}

	public void setFReceivestation(String fReceivestation) {
		this.fReceivestation = fReceivestation;
	}

	public Date getFReceivetime() {
		return this.fReceivetime;
	}

	public void setFReceivetime(Date fReceivetime) {
		this.fReceivetime = fReceivetime;
	}

	public BigDecimal getFResolutionx() {
		return this.fResolutionx;
	}

	public void setFResolutionx(BigDecimal fResolutionx) {
		this.fResolutionx = fResolutionx;
	}

	public BigDecimal getFResolutiony() {
		return this.fResolutiony;
	}

	public void setFResolutiony(BigDecimal fResolutiony) {
		this.fResolutiony = fResolutiony;
	}

	public String getFSatad() {
		return this.fSatad;
	}

	public void setFSatad(String fSatad) {
		this.fSatad = fSatad;
	}

	public String getFSatelliteid() {
		return this.fSatelliteid;
	}

	public void setFSatelliteid(String fSatelliteid) {
		this.fSatelliteid = fSatelliteid;
	}

	public int getFSceneid() {
		return this.fSceneid;
	}

	public void setFSceneid(int fSceneid) {
		this.fSceneid = fSceneid;
	}

	public int getFScenepath() {
		return this.fScenepath;
	}

	public void setFScenepath(int fScenepath) {
		this.fScenepath = fScenepath;
	}

	public int getFScenerow() {
		return this.fScenerow;
	}

	public void setFScenerow(int fScenerow) {
		this.fScenerow = fScenerow;
	}

	public String getFSensorid() {
		return this.fSensorid;
	}

	public void setFSensorid(String fSensorid) {
		this.fSensorid = fSensorid;
	}

	public Date getFStarttime() {
		return this.fStarttime;
	}

	public void setFStarttime(Date fStarttime) {
		this.fStarttime = fStarttime;
	}

	public int getFWidthinpixels() {
		return this.fWidthinpixels;
	}

	public void setFWidthinpixels(int fWidthinpixels) {
		this.fWidthinpixels = fWidthinpixels;
	}

}
package com.htht.job.executor.model.datametainfo;



import com.htht.job.core.util.BaseEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.math.BigInteger;


/**
 * The persistent class for the htht_dms_meta_info database table.
 * 
 */
@Entity
@Table(name="htht_dms_meta_info")
public class DataMetaInfo extends BaseEntity {
	private static final long serialVersionUID = 1L;

//	//@Id
//	//@GenericGenerator(name = "idGenerator", strategy = "uuid") // 这个是hibernate的注解/生成32位UUID
//	//@GeneratedValue(generator = "idGenerator")
//	private String id;

//	//@Temporal(TemporalType.TIMESTAMP)
	//@Column(name="create_time")
//	private Date createTime;

	@Column(name="f_catalogcode")
	private String fCatalogcode;

	@Column(name="f_dataextname")
	private String fDataextname;

	@Column(name="f_dataid")
	private String fDataid;

	@Column(name="f_dataname")
	private String fDataname;

	@Column(name="f_datasize")
	private BigInteger fDatasize;

	@Column(name="f_datasourcename")
	private String fDatasourcename;

	@Column(name="f_dataunit")
	private String fDataunit;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="f_delete_time")
	private Date fDeleteTime;

	@Column(name="f_extractdatapath")
	private String fExtractdatapath;

	@Column(name="f_flag")
	private int fFlag;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="f_importdate")
	private Date fImportdate;

	@Column(name="f_isfile")
	private int fIsfile;

	@Column(name="f_location")
	private String fLocation;

	@Column(name="f_recycleflag")
	private int fRecycleflag;

	@Column(name="f_viewdatapath")
	private String fViewdatapath;

	//@Temporal(TemporalType.TIMESTAMP)
	//@Column(name="update_time")
//	private Date updateTime;

//	private int version;

	public DataMetaInfo() {
	}

//	public String getId() {
//		return this.id;
//	}

//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public Date getCreateTime() {
//		return this.createTime;
//	}
//
//	public void setCreateTime(Date createTime) {
//		this.createTime = createTime;
//	}

	public String getFCatalogcode() {
		return this.fCatalogcode;
	}

	public void setFCatalogcode(String fCatalogcode) {
		this.fCatalogcode = fCatalogcode;
	}

	public String getFDataextname() {
		return this.fDataextname;
	}

	public void setFDataextname(String fDataextname) {
		this.fDataextname = fDataextname;
	}

	public String getFDataid() {
		return this.fDataid;
	}

	public void setFDataid(String fDataid) {
		this.fDataid = fDataid;
	}

	public String getFDataname() {
		return this.fDataname;
	}

	public void setFDataname(String fDataname) {
		this.fDataname = fDataname;
	}

	public BigInteger getFDatasize() {
		return this.fDatasize;
	}

	public void setFDatasize(BigInteger fDatasize) {
		this.fDatasize = fDatasize;
	}

	public String getFDatasourcename() {
		return this.fDatasourcename;
	}

	public void setFDatasourcename(String fDatasourcename) {
		this.fDatasourcename = fDatasourcename;
	}

	public String getFDataunit() {
		return this.fDataunit;
	}

	public void setFDataunit(String fDataunit) {
		this.fDataunit = fDataunit;
	}

	public Date getFDeleteTime() {
		return this.fDeleteTime;
	}

	public void setFDeleteTime(Date fDeleteTime) {
		this.fDeleteTime = fDeleteTime;
	}

	public String getFExtractdatapath() {
		return this.fExtractdatapath;
	}

	public void setFExtractdatapath(String fExtractdatapath) {
		this.fExtractdatapath = fExtractdatapath;
	}

	public int getFFlag() {
		return this.fFlag;
	}

	public void setFFlag(int fFlag) {
		this.fFlag = fFlag;
	}

	public Date getFImportdate() {
		return this.fImportdate;
	}

	public void setFImportdate(Date fImportdate) {
		this.fImportdate = fImportdate;
	}

	public int getFIsfile() {
		return this.fIsfile;
	}

	public void setFIsfile(int fIsfile) {
		this.fIsfile = fIsfile;
	}

	public String getFLocation() {
		return this.fLocation;
	}

	public void setFLocation(String fLocation) {
		this.fLocation = fLocation;
	}

	public int getFRecycleflag() {
		return this.fRecycleflag;
	}

	public void setFRecycleflag(int fRecycleflag) {
		this.fRecycleflag = fRecycleflag;
	}

	public String getFViewdatapath() {
		return this.fViewdatapath;
	}

	public void setFViewdatapath(String fViewdatapath) {
		this.fViewdatapath = fViewdatapath;
	}

//	public Date getUpdateTime() {
//		return this.updateTime;
//	}
//
//	public void setUpdateTime(Date updateTime) {
//		this.updateTime = updateTime;
//	}
//
//	public int getVersion() {
//		return this.version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}

}
package com.htht.job.executor.model.downupload;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by zzj on 2018/1/15.
 */
@Entity
@Table(name = "htht_cluster_schedule_download_file_info")
public class DownResult extends BaseEntity {
    private String fileName;//下载后的文件名
    private String realFileName;//下载前的文件名
    private Long fileSize;//文件大小
    private String filePath;//下载后的路径
    private Date dataTime;//文件的时间
    private String format;//文件的类型
//    private String orbiteid;
    private String bz;//备注
    private String zt;//状态 0 准备 1成功 2失败
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRealFileName() {
		return realFileName;
	}
	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Date getDataTime() {
		return dataTime;
	}
	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	public String getZt() {
		return zt;
	}
	public void setZt(String zt) {
		this.zt = zt;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

}

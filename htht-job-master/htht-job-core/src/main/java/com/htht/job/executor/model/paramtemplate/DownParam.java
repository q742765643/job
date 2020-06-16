package com.htht.job.executor.model.paramtemplate;

/**
 * Created by zzj on 2018/1/11.
 */
public class DownParam {
    private String modisDataType;        //modis下载数据的数据类型
    private String forSouceType;//源类型 ftp file cmiss
    private String toSouceType;//目标类型 ftp file
    private String forPath;//下载原目录
    private String toPath;//下载目标目录
    private String downFileNamePattern;//文件正则
    private String dataTimePattern;//时间正则
    private String downloadFileName;//文件重命名规则
    private String forFtp; // 数据来源FTP
    private String toFtp; // 数据存放FTP
    private String downloadType;//下载类型 当前还是历史 
    private String downloadDays;//下载天数
    private String downloadDate;//下载日期
    private String catalog;//
    private String bz;

    public String getForSouceType() {
        return forSouceType;
    }

    public void setForSouceType(String forSouceType) {
        this.forSouceType = forSouceType;
    }

    public String getToSouceType() {
        return toSouceType;
    }

    public void setToSouceType(String toSouceType) {
        this.toSouceType = toSouceType;
    }

    public String getForPath() {
        return forPath;
    }

    public void setForPath(String forPath) {
        this.forPath = forPath;
    }

    public String getToPath() {
        return toPath;
    }

    public void setToPath(String toPath) {
        this.toPath = toPath;
    }

    public String getDownFileNamePattern() {
        return downFileNamePattern;
    }

    public void setDownFileNamePattern(String downFileNamePattern) {
        this.downFileNamePattern = downFileNamePattern;
    }

    public String getDataTimePattern() {
        return dataTimePattern;
    }

    public void setDataTimePattern(String dataTimePattern) {
        this.dataTimePattern = dataTimePattern;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    public String getForFtp() {
        return forFtp;
    }

    public void setForFtp(String forFtp) {
        this.forFtp = forFtp;
    }

    public String getToFtp() {
        return toFtp;
    }

    public void setToFtp(String toFtp) {
        this.toFtp = toFtp;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public String getDownloadDays() {
        return downloadDays;
    }

    public void setDownloadDays(String downloadDays) {
        this.downloadDays = downloadDays;
    }

    public String getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(String downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getModisDataType() {
        return modisDataType;
    }

    public void setModisDataType(String modisDataType) {
        this.modisDataType = modisDataType;
    }
}

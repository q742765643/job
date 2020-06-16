package com.htht.job.executor.model.paramtemplate;


public class CimissDownParam {

    private String cimissDataType;//资料类型
    private String dataCode;//资料名称
    private String interfaceId;//接口名称
    private String times;//时间
    private String isopen;//时间类型
    private String elements;//要素字段
    private String filename;//自定义的文件名及文件格式
    private String filePath;//自定义的下载文件保存路径

    private String adminCodes;//区域行政编号
    private String minLat;
    private String minLon;
    private String maxLat;
    private String maxLon;
    private String bz;//备注
    private String cimissDataTypeShow;
    private String interfaceIdShow;//资料名称
    private String dataCodeShow;//资料名称
    private String eleValueRanges;



    public String getCimissDataType() {
        return cimissDataType;
    }

    public void setCimissDataType(String cimissDataType) {
        this.cimissDataType = cimissDataType;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getIsopen() {
        return isopen;
    }

    public void setIsopen(String isopen) {
        this.isopen = isopen;
    }

    public String getElements() {
        return elements;
    }

    public void setElements(String elements) {
        this.elements = elements;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAdminCodes() {
        return adminCodes;
    }

    public void setAdminCodes(String adminCodes) {
        this.adminCodes = adminCodes;
    }

    public String getMinLat() {
        return minLat;
    }

    public void setMinLat(String minLat) {
        this.minLat = minLat;
    }

    public String getMinLon() {
        return minLon;
    }

    public void setMinLon(String minLon) {
        this.minLon = minLon;
    }

    public String getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(String maxLat) {
        this.maxLat = maxLat;
    }

    public String getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(String maxLon) {
        this.maxLon = maxLon;
    }

    public String getCimissDataTypeShow() {
        return cimissDataTypeShow;
    }

    public void setCimissDataTypeShow(String cimissDataTypeShow) {
        this.cimissDataTypeShow = cimissDataTypeShow;
    }

    public String getInterfaceIdShow() {
        return interfaceIdShow;
    }

    public void setInterfaceIdShow(String interfaceIdShow) {
        this.interfaceIdShow = interfaceIdShow;
    }

    public String getDataCodeShow() {
        return dataCodeShow;
    }

    public void setDataCodeShow(String dataCodeShow) {
        this.dataCodeShow = dataCodeShow;
    }

    public String getEleValueRanges() {
        return eleValueRanges;
    }

    public void setEleValueRanges(String eleValueRanges) {
        this.eleValueRanges = eleValueRanges;
    }
}

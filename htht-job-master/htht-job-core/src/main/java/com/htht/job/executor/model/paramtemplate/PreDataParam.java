package com.htht.job.executor.model.paramtemplate;

/**
 * @author yuguoqing
 * @Date 2018年4月13日 上午11:30:05
 * <p>
 * 数据预处理参数
 */
public class PreDataParam {
    private String preDataTaskName;// 任务名称 FY3B_VIRRX_1000
    private String period;// 执行规则 0 15 10 L * ? 正则表达式
    private String inputDataFilePath;// 数据输入路径
    private String outputDataFilePath;// 数据输出路径
    private String fileNamePattern;// 文件名规则 .*\.HDF$
    private String validEnvelopes;// 数据有效范围
    private String bands;// 卫星通道 1,2,3,4,5,6,7
    private String projectionIdentify;// 投影标识 GLL
    private String resolutionX;// 经度方向分辨率 0.01
    private String resolutionY;// 纬度方向分辨率0.01
    private String preDataTypes;// 操作类型projection,block,mosaic
    private String projectionExeLocaiton;// 投影算法位置
    private String mosaicExeLocation;
    private String blockExeLocation;
    private String envelopes;//输出数据范围
    private String projectionInputArgXml;// 投影生成xml文件，作为算法输入
//	private String dataFilePattern;//文件名称格式

    private String dateType;//1表示最近N天，2表示时间范围

    private String startDate;//开始时间
    private String endDate;//结束时间
    private Integer rangeDay;//处理天数，当前时间往前数几天

    private String projectioDate;//投影时间范围

    private String extArgs;//补充参数
    private String formate;//数据格式

 

	public String getFormate() {
		return formate;
	}

	public void setFormate(String formate) {
		this.formate = formate;
	}

	public String getExtArgs() {
        return extArgs;
    }

    public void setExtArgs(String extArgs) {
        this.extArgs = extArgs;
    }

    public String getProjectioDate() {
        return projectioDate;
    }

    public void setProjectioDate(String projectioDate) {
        this.projectioDate = projectioDate;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getRangeDay() {
        return rangeDay;
    }

    public void setRangeDay(Integer rangeDay) {
        this.rangeDay = rangeDay;
    }

    public String getProjectionInputArgXml() {
        return projectionInputArgXml;
    }

    public void setProjectionInputArgXml(String projectionInputArgXml) {
        this.projectionInputArgXml = projectionInputArgXml;
    }

    public String getEnvelopes() {
        return envelopes;
    }

    public void setEnvelopes(String envelopes) {
        this.envelopes = envelopes;
    }

    public String getPreDataTypes() {
        return preDataTypes;
    }

    public void setPreDataTypes(String preDataTypes) {
        this.preDataTypes = preDataTypes;
    }

    public String getPreDataTaskName() {
        return preDataTaskName;
    }

    public void setPreDataTaskName(String preDataTaskName) {
        this.preDataTaskName = preDataTaskName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getInputDataFilePath() {
        return inputDataFilePath;
    }

    public void setInputDataFilePath(String inputDataFilePath) {
        this.inputDataFilePath = inputDataFilePath;
    }

    public String getOutputDataFilePath() {
        return outputDataFilePath;
    }

    public void setOutputDataFilePath(String outputDataFilePath) {
        this.outputDataFilePath = outputDataFilePath;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    public String getValidEnvelopes() {
        return validEnvelopes;
    }

    public void setValidEnvelopes(String validEnvelopes) {
        this.validEnvelopes = validEnvelopes;
    }

    public String getBands() {
        return bands;
    }

    public void setBands(String bands) {
        this.bands = bands;
    }

    public String getProjectionIdentify() {
        return projectionIdentify;
    }

    public void setProjectionIdentify(String projectionIdentify) {
        this.projectionIdentify = projectionIdentify;
    }

    public String getResolutionX() {
        return resolutionX;
    }

    public void setResolutionX(String resolutionX) {
        this.resolutionX = resolutionX;
    }

    public String getResolutionY() {
        return resolutionY;
    }

    public void setResolutionY(String resolutionY) {
        this.resolutionY = resolutionY;
    }

    public String getProjectionExeLocaiton() {
        return projectionExeLocaiton;
    }

    public void setProjectionExeLocaiton(String projectionExeLocaiton) {
        this.projectionExeLocaiton = projectionExeLocaiton;
    }

    public String getMosaicExeLocation() {
        return mosaicExeLocation;
    }

    public void setMosaicExeLocation(String mosaicExeLocation) {
        this.mosaicExeLocation = mosaicExeLocation;
    }

    public String getBlockExeLocation() {
        return blockExeLocation;
    }

    public void setBlockExeLocation(String blockExeLocation) {
        this.blockExeLocation = blockExeLocation;
    }

}

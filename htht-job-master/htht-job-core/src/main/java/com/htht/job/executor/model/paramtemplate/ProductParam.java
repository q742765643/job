package com.htht.job.executor.model.paramtemplate;

/**
 * @author yuguoqing
 * @Date 2018年4月27日 上午11:46:44
 */
public class ProductParam {
    private String exePath;//可执行文件路径
    private String scriptFile;//算法源码
    private String inputxml;//xml存放路径
    private String prodname;//产品算法名称
    private String dateType;//1实时数据 2历史数据
    private String productRangeDate;//历史数据时间范围
    private String productRangeDay;//实时数据，天数

    public String getExePath() {
        return exePath;
    }

    public void setExePath(String exePath) {
        this.exePath = exePath;
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    public String getInputxml() {
        return inputxml;
    }

    public void setInputxml(String inputxml) {
        this.inputxml = inputxml;
    }

    public String getProdname() {
        return prodname;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getProductRangeDate() {
        return productRangeDate;
    }

    public void setProductRangeDate(String productRangeDate) {
        this.productRangeDate = productRangeDate;
    }

    public String getProductRangeDay() {
        return productRangeDay;
    }

    public void setProductRangeDay(String productRangeDay) {
        this.productRangeDay = productRangeDay;
    }


}

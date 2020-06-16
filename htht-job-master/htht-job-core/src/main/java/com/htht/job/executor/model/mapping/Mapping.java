package com.htht.job.executor.model.mapping;/**
 * Created by zzj on 2018/6/25.
 */

/**
 * @program: htht-job-api
 * @description: 匹配组合
 * @author: zzj
 * @create: 2018-06-25 14:46
 **/
public class Mapping {
    private String dataId;
    private String label;
    private String sfdataId;
    private String dydataId;
    private String parameterName;
    private String parameterDesc;
    private String parameterType;
    private String value;
    private String url;
    private String matchBefore;
    private String matchAfter;
    private int isOut;


    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSfdataId() {
        return sfdataId;
    }

    public void setSfdataId(String sfdataId) {
        this.sfdataId = sfdataId;
    }

    public String getDydataId() {
        return dydataId;
    }

    public void setDydataId(String dydataId) {
        this.dydataId = dydataId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterDesc() {
        return parameterDesc;
    }

    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMatchBefore() {
        return matchBefore;
    }

    public void setMatchBefore(String matchBefore) {
        this.matchBefore = matchBefore;
    }

    public String getMatchAfter() {
        return matchAfter;
    }

    public void setMatchAfter(String matchAfter) {
        this.matchAfter = matchAfter;
    }


    public int getIsOut() {
        return isOut;
    }

    public void setIsOut(int isOut) {
        this.isOut = isOut;
    }
}


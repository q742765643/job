package com.htht.job.admin.core.jobbean;

import com.htht.job.admin.core.model.XxlJobInfo;

public class MonitorJobInfoBean {
    private XxlJobInfo xxlJobInfo;
    private Integer lineNumber;
    private Integer operateNumber;

    public XxlJobInfo getXxlJobInfo() {
        return xxlJobInfo;
    }

    public void setXxlJobInfo(XxlJobInfo xxlJobInfo) {
        this.xxlJobInfo = xxlJobInfo;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getOperateNumber() {
        return operateNumber;
    }

    public void setOperateNumber(Integer operateNumber) {
        this.operateNumber = operateNumber;
    }


}



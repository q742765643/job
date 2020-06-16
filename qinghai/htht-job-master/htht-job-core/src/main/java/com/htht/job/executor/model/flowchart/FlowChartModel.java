package com.htht.job.executor.model.flowchart;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by zzj on 2018/3/19.
 */
@Entity
@Table(name="htht_cluster_schedule_flow_chart")
public class FlowChartModel extends BaseEntity {
    /**
     * 流程名称
     */
    @Column(name = "process_chname")
    private String processCHName;
    /**
     * 流程描述
     */
    @Column(name = "process_describe")
    private String processDescribe;
    /**
     * 流程xml
     */
    @Column(columnDefinition="MEDIUMTEXT",name = "process_figure")
    private String processFigure;
    /**
     * 流程图片
     */
    @Column(columnDefinition="BLOB",name = "process_picture")
    private  byte[] processPicture;
    @Transient
    private String picture;
    @Transient
    private String file;
    //图片的宽、高
    @Transient
    private int picWidth;
    @Transient
    private int picHeight;
    @Transient
    private String processId;



    public String getProcessCHName() {
        return processCHName;
    }

    public void setProcessCHName(String processCHName) {
        this.processCHName = processCHName;
    }

    public String getProcessDescribe() {
        return processDescribe;
    }

    public void setProcessDescribe(String processDescribe) {
        this.processDescribe = processDescribe;
    }

    public String getProcessFigure() {
        return processFigure;
    }

    public void setProcessFigure(String processFigure) {
        this.processFigure = processFigure;
    }

    public byte[] getProcessPicture() {
        return processPicture;
    }

    public void setProcessPicture(byte[] processPicture) {
        this.processPicture = processPicture;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getPicWidth() {
        return picWidth;
    }

    public void setPicWidth(int picWidth) {
        this.picWidth = picWidth;
    }

    public int getPicHeight() {
        return picHeight;
    }

    public void setPicHeight(int picHeight) {
        this.picHeight = picHeight;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}

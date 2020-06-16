package com.htht.job.executor.model.dms.module;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 数据物理信息表
 *
 * @author LY 2018-04-03
 */
@Entity
@Table(name = "HTHT_DMS_META_INFO")
public class MetaInfo extends BaseEntity {
    private static final long serialVersionUID = -558337703288537147L;
    private String f_dataid;// ID
    private String f_dataname; // 数据名称
    private String f_catalogcode;// 目录编码:A01
    private Date f_importdate;// 入库日期:到年月日时分秒
    private Long f_datasize;// 数据量大小 :采用b
    private String f_dataunit;// 数据单位 :默认b
    private String f_dataextname;// 数据扩展名:.rar.img.shp
    private int f_isfile;// 是否是文件:0文件夹，1文件
    private String f_location;// 数据绝对存储路径
    private int f_flag;// 数据存储状态标记:默认为在线0，近线1，离线2
    private int f_recycleflag;// 数据回收站状态标识:默认为0不在回收站，1在回收站
    private String f_extractdatapath;// 快视图文件路径:产品提取快视图使用 文件夹路径   \\1.1.1.1\\a\a01\fy1
    private Date f_delete_time;// 删除时间
    private String f_datasourcename;// 数据原始文件名称
    private String f_viewdatapath;// 快视图文件路径:产品提取快视图使用 文件夹路径 a\a01\fy1
    private String f_nearlinepath;// 近线路径
    private String f_offlinepath;// 离线路径
    private Date f_nearline_time;// 备份到近线时间
    private Date f_offline_time;// 备份到离线时间

    public String getF_dataid() {
        return f_dataid;
    }

    public void setF_dataid(String f_dataid) {
        this.f_dataid = f_dataid;
    }

    public String getF_dataname() {
        return f_dataname;
    }

    public void setF_dataname(String f_dataname) {
        this.f_dataname = f_dataname;
    }

    public String getF_catalogcode() {
        return f_catalogcode;
    }

    public void setF_catalogcode(String f_catalogcode) {
        this.f_catalogcode = f_catalogcode;
    }

    public Date getF_importdate() {
        return f_importdate;
    }

    public void setF_importdate(Date f_importdate) {
        this.f_importdate = f_importdate;
    }

    public Long getF_datasize() {
        return f_datasize;
    }

    public void setF_datasize(Long f_datasize) {
        this.f_datasize = f_datasize;
    }

    public String getF_dataunit() {
        return f_dataunit;
    }

    public void setF_dataunit(String f_dataunit) {
        this.f_dataunit = f_dataunit;
    }

    public String getF_dataextname() {
        return f_dataextname;
    }

    public void setF_dataextname(String f_dataextname) {
        this.f_dataextname = f_dataextname;
    }

    public int getF_isfile() {
        return f_isfile;
    }

    public void setF_isfile(int f_isfile) {
        this.f_isfile = f_isfile;
    }

    public String getF_location() {
        return f_location;
    }

    public void setF_location(String f_location) {
        this.f_location = f_location;
    }

    public int getF_flag() {
        return f_flag;
    }

    public void setF_flag(int f_flag) {
        this.f_flag = f_flag;
    }

    public int getF_recycleflag() {
        return f_recycleflag;
    }

    public void setF_recycleflag(int f_recycleflag) {
        this.f_recycleflag = f_recycleflag;
    }

    public String getF_extractdatapath() {
        return f_extractdatapath;
    }

    public void setF_extractdatapath(String f_extractdatapath) {
        this.f_extractdatapath = f_extractdatapath;
    }

    public Date getF_delete_time() {
        return f_delete_time;
    }

    public void setF_delete_time(Date f_delete_time) {
        this.f_delete_time = f_delete_time;
    }

    public String getF_datasourcename() {
        return f_datasourcename;
    }

    public void setF_datasourcename(String f_datasourcename) {
        this.f_datasourcename = f_datasourcename;
    }

    public String getF_viewdatapath() {
        return f_viewdatapath;
    }

    public void setF_viewdatapath(String f_viewdatapath) {
        this.f_viewdatapath = f_viewdatapath;
    }

    public String getF_nearlinepath() {
        return f_nearlinepath;
    }

    public void setF_nearlinepath(String f_nearlinepath) {
        this.f_nearlinepath = f_nearlinepath;
    }

    public String getF_offlinepath() {
        return f_offlinepath;
    }

    public void setF_offlinepath(String f_offlinepath) {
        this.f_offlinepath = f_offlinepath;
    }

    public Date getF_nearline_time() {
        return f_nearline_time;
    }

    public void setF_nearline_time(Date f_nearline_time) {
        this.f_nearline_time = f_nearline_time;
    }

    public Date getF_offline_time() {
        return f_offline_time;
    }

    public void setF_offline_time(Date f_offline_time) {
        this.f_offline_time = f_offline_time;
    }

}

package com.htht.job.executor.model.dms.module;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 磁盘
 *
 * @author LY 2018-03-29
 */
@Entity
@Table(name = "HTHT_DMS_SYS_DISK")
public class Disk extends BaseEntity {
    private static final long serialVersionUID = 7833630595041616884L;
    private String id;// ID
    private String diskname;// 磁盘名称
    private String diskdesc;// 磁盘描述
    private String loginname;// 登录名称
    private String loginpwd;// 登录密码
    private String loginurl;// 登录URL
    private Long disktotlesize;// 磁盘总大小
    private Long diskfreesize;// 磁盘可用大小
    private Long diskusesize;// 磁盘已使用大小
    private int diskstatus;// 0:可用 1:不可用
    private int usagerate;// 剩余使用率
    private String disktype;// 0:扫描磁盘 1:归档磁盘 2:图片磁盘 3:工作磁盘(数据解压、图片配准、图片去黑边、XML解析。。。) 4:订单磁盘(用于订单任务数据存放) 5:近线磁盘   6:离线磁盘
    private String diskdrive;// 磁盘盘符

    private Long mindiskfreesize;//磁盘最小可使用量

    private String unitType;//最小使用量选择单位


    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Long getMindiskfreesize() {
        return mindiskfreesize;
    }

    public void setMindiskfreesize(Long mindiskfreesize) {
        this.mindiskfreesize = mindiskfreesize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiskname() {
        return diskname;
    }

    public void setDiskname(String diskname) {
        this.diskname = diskname;
    }

    public String getDiskdesc() {
        return diskdesc;
    }

    public void setDiskdesc(String diskdesc) {
        this.diskdesc = diskdesc;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getLoginpwd() {
        return loginpwd;
    }

    public void setLoginpwd(String loginpwd) {
        this.loginpwd = loginpwd;
    }

    public String getLoginurl() {
        return loginurl;
    }

    public void setLoginurl(String loginurl) {
        this.loginurl = loginurl;
    }

    public Long getDisktotlesize() {
        return disktotlesize;
    }

    public void setDisktotlesize(Long disktotlesize) {
        this.disktotlesize = disktotlesize;
    }

    public Long getDiskfreesize() {
        return diskfreesize;
    }

    public void setDiskfreesize(Long diskfreesize) {
        this.diskfreesize = diskfreesize;
    }

    public Long getDiskusesize() {
        return diskusesize;
    }

    public void setDiskusesize(Long diskusesize) {
        this.diskusesize = diskusesize;
    }

    public int getDiskstatus() {
        return diskstatus;
    }

    public void setDiskstatus(int diskstatus) {
        this.diskstatus = diskstatus;
    }

    public int getUsagerate() {
        return usagerate;
    }

    public void setUsagerate(int usagerate) {
        this.usagerate = usagerate;
    }

    public String getDisktype() {
        return disktype;
    }

    public void setDisktype(String disktype) {
        this.disktype = disktype;
    }

    public String getDiskdrive() {
        return diskdrive;
    }

    public void setDiskdrive(String diskdrive) {
        this.diskdrive = diskdrive;
    }

}

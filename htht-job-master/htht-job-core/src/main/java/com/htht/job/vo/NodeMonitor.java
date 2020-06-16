package com.htht.job.vo;

import java.io.Serializable;

/**
 * Created by zzj on 2018/1/31.
 */
public class NodeMonitor implements Serializable {
    private String ip;
    private int port;
    private String appName;
    private long lineNum;
    private long operationNum;
    private int zNum;
    private String id;
    private String deploySystem;
    private int isRun;
    private Long cpuUsage;
    private Long memoryUsage;
    private Long hardDiskUsage;
    private Long jvmUsage;

    public String getDeploySystem() {
        return deploySystem;
    }

    public void setDeploySystem(String deploySystem) {
        this.deploySystem = deploySystem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getLineNum() {
        return lineNum;
    }

    public void setLineNum(long lineNum) {
        this.lineNum = lineNum;
    }

    public long getOperationNum() {
        return operationNum;
    }

    public void setOperationNum(long operationNum) {
        this.operationNum = operationNum;
    }

    public int getzNum() {
        return zNum;
    }

    public void setzNum(int zNum) {
        this.zNum = zNum;
    }

    public int getIsRun() {
        return isRun;
    }

    public void setIsRun(int isRun) {
        this.isRun = isRun;
    }

    public Long getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Long cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Long getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Long memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Long getHardDiskUsage() {
        return hardDiskUsage;
    }

    public void setHardDiskUsage(Long hardDiskUsage) {
        this.hardDiskUsage = hardDiskUsage;
    }

    public Long getJvmUsage() {
        return jvmUsage;
    }

    public void setJvmUsage(Long jvmUsage) {
        this.jvmUsage = jvmUsage;
    }
}

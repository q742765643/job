package com.htht.job.core.enums;

/**
 * 系统类型
 *
 * @author Administrator
 */
public enum SystemType {

    SYSTEM_TYPE_CLUSTER("支撑平台"),
    SYSTEM_TYPE_DMS("数管平台"),
    SYSTEM_TYPE_UUS("发布平台");

    private String name;

    private SystemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

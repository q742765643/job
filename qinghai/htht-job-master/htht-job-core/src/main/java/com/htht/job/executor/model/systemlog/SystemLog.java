package com.htht.job.executor.model.systemlog;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @program: htht-job-api
 * @description: 系统操作日志
 * @author: dingjiancheng
 * @create: 2018-09-17 10:51
 */
@Entity
@Table(name="htht_system_log")
public class SystemLog extends BaseEntity {
    public static final String SYSTEMLOG = "SYSTEMLOG";
    public static final String OPERATELOG = "OPERATELOG";


    private String category;        //日志类型

    private String username;        //用户名

    private String ip;               //ip

    private String content;         //日志内容

    public SystemLog() {
        super();
    }

    public SystemLog(String type, String description) {
        super();
        this.category = type;
        this.content = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


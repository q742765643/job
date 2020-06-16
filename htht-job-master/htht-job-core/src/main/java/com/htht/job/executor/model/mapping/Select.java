package com.htht.job.executor.model.mapping;/**
 * Created by zzj on 2018/6/25.
 */

import java.io.Serializable;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-06-25 16:49
 **/
public class Select implements Serializable {
    String id;
    String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}


package com.htht.job.executor.model.dms.module;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统参数
 *
 * @author LY 2018-07-19
 */
@Entity
@Table(name = "HTHT_DMS_SYS_PARAM")
public class SystemParam extends BaseEntity {
    private static final long serialVersionUID = 7833630595041616832L;
    private String id;// ID
    private String paramname;
    private String paramcode;
    private String paramvalue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParamname() {
        return paramname;
    }

    public void setParamname(String paramname) {
        this.paramname = paramname;
    }

    public String getParamcode() {
        return paramcode;
    }

    public void setParamcode(String paramcode) {
        this.paramcode = paramcode;
    }

    public String getParamvalue() {
        return paramvalue;
    }

    public void setParamvalue(String paramvalue) {
        this.paramvalue = paramvalue;
    }

}

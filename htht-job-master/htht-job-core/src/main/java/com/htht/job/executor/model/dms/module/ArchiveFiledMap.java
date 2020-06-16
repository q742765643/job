package com.htht.job.executor.model.dms.module;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * XML节点映射表
 *
 * @author LY 2018-03-29
 */
@Entity
@Table(name = "HTHT_DMS_ARCHIVE_FILED_MAP")
public class ArchiveFiledMap extends BaseEntity {
    private static final long serialVersionUID = 292899466241736695L;
    private String f_id;// ID
    private String f_archivefield; // 待入库数据的字段名称:xml节点名称
    private String f_fieldmanageid;// 经验表ID:HTHT_DMS_YH_DATA_TYPE表id(属性类型表)
    private int f_status;// 状态:备用字段
    private String f_catalogcode;// 目录编码:A01来源HTHT_DMS_ARCHIVE_CATALOG

    private String archive_rule_id;//来源于HTHT_DMS_SYS_ARCHIVERULES的ID

    private String default_val;//字段默认值
    

	public String getDefault_val() {
		return default_val;
	}

	public void setDefault_val(String default_val) {
		this.default_val = default_val;
	}

	public String getArchive_rule_id() {
        return archive_rule_id;
    }

    public void setArchive_rule_id(String archive_rule_id) {
        this.archive_rule_id = archive_rule_id;
    }

    public String getF_id() {
        return f_id;
    }

    public void setF_id(String f_id) {
        this.f_id = f_id;
    }

    public String getF_archivefield() {
        return f_archivefield;
    }

    public void setF_archivefield(String f_archivefield) {
        this.f_archivefield = f_archivefield;
    }

    public String getF_fieldmanageid() {
        return f_fieldmanageid;
    }

    public void setF_fieldmanageid(String f_fieldmanageid) {
        this.f_fieldmanageid = f_fieldmanageid;
    }

    public int getF_status() {
        return f_status;
    }

    public void setF_status(int f_status) {
        this.f_status = f_status;
    }

    public String getF_catalogcode() {
        return f_catalogcode;
    }

    public void setF_catalogcode(String f_catalogcode) {
        this.f_catalogcode = f_catalogcode;
    }

}

package com.htht.job.executor.model.downupload;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "htht_cluster_schedule_cimiss_data_info")
public class CimissDataInfo extends BaseEntity {

    private String type;
	private String dataCode;
	private String dataInfo;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDataCode() {
		return dataCode;
	}

	public void setDataCode(String dataCode) {
		this.dataCode = dataCode;
	}

	public String getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(String dataInfo) {
		this.dataInfo = dataInfo;
	}
}

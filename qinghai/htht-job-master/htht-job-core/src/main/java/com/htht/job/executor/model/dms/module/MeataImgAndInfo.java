package com.htht.job.executor.model.dms.module;

import javax.persistence.Entity;

import com.htht.job.core.util.BaseEntity;

/**
 * @author: yss
 * @time:2018年10月23日 上午11:13:17
 */
@Entity
public class MeataImgAndInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private MetaImg metaImg;
	private MetaInfo metaInfo;
	public MetaImg getMetaImg() {
		return metaImg;
	}
	public void setMetaImg(MetaImg metaImg) {
		this.metaImg = metaImg;
	}
	public MetaInfo getMetaInfo() {
		return metaInfo;
	}
	public void setMetaInfo(MetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

}

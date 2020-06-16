package com.htht.job.executor.model.dms.module;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Entity;

/**
 * @author: yss
 * @time:2018年10月23日 上午11:13:17
 */
@Entity
public class MeataImgAndInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private MetaImg metaImg;
    private MetaInfo metaInfo;
    private String viewPath;//快视图拼接后的路径
    
    

    public String getViewPath() {
		return viewPath;
	}

	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}

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

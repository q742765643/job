package com.htht.job.executor.model.fileinfo;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zzj on 2018/2/5.
 */
@Entity
@Table(name="htht_cluster_schedule_file_info")
public class FileInfo extends BaseEntity {
    private String filetype;
    private String filepath;
    private String filename;
    @Column(name = "product_file_info_id")
    private String productFileInfoId;


    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getProductFileInfoId() {
        return productFileInfoId;
    }

    public void setProductFileInfoId(String productFileInfoId) {
        this.productFileInfoId = productFileInfoId;
    }
}

package com.htht.job.executor.model.productfileinfo;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 产品文件表
 */
@Entity
@Table(name = "htht_cluster_schedule_product_file_info")
public class ProductFileInfoDTO extends BaseEntity {

    //产品类型，专题图、报告、其他文件等
    @Column(name = "product_type", length = 50)
    private String productType;
    //产品表里的id
    @Column(name = "product_Info_Id")
    private String productInfoId;
    //文件名称
    @Column(name = "file_name")
    private String fileName;
    //文件大小
    @Column(name = "file_size")
    private long fileSize;
    //文件的后缀
    @Column(name = "file_type")
    private String fileType;
    //文件的绝对(全路径)
    @Column(name = "file_path")
    private String filePath;
    //文件的相对路径
    @Column(name = "relative_path")
    private String relativePath;
    // 0有效， 1删除
    @Column(name = "is_del", length = 10)
    private String isDel;

    @Column(name = "issue")
    private String issue;

    @Column(name = "region")
    private String region;

    @Column(name = "cycle")
    private String cycle;


    public ProductFileInfoDTO() {
        super();
    }

    public ProductFileInfoDTO(String productType, String productInfoId, String fileName, long fileSize, String fileType,
                              String filePath, String relativePath, String issue, String region, String cycle) {
        super();
        this.productType = productType;
        this.productInfoId = productInfoId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.filePath = filePath;
        this.relativePath = relativePath;
        this.issue = issue;
        this.region = region;
        this.cycle = cycle;
    }


    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getProductInfoId() {
        return productInfoId;
    }

    public void setProductInfoId(String productInfoId) {
        this.productInfoId = productInfoId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

}
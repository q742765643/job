package com.htht.job.executor.model.productinfo;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统中产品实际保存信息 产品表
 */
@Entity
@Table(name = "htht_cluster_schedule_product_info")
public class ProductInfoDTO extends BaseEntity {
    // 产品的名称
    @Column(name = "name")
    private String name;
    // 关键字
    @Column(name = "mark")
    private String mark;
    // 产品周期类型：日周期、月周期、旬周期等
    @Column(name = "cycle")
    private String cycle;
    // 地图路径
    @Column(name = "map_url")
    private String mapUrl;
    // 图层名称
    @Column(name = "feature_name")
    private String featureName;
    // 产品路径
    @Column(name = "product_path")
    private String productPath;
    // gdb路径
    @Column(name = "gdb_path")
    private String gdbPath;
    // 期次
    @Column(name = "issue")
    private String issue;
    // 产品表关联id
    @Column(name = "product_id")
    private String productId;
    // 区域
    @Column(name = "region_id")
    private String regionId;
    // 镶嵌数据集文件名称
    @Column(name = "mosaic_file")
    private String mosaicFile;
    /**
     * 是否自动发布 0需要手动发布 1自动发布
     */
    @Column(name = "is_release")
    private Integer isRelease;
    // 备注
    @Column(name = "bz")
    private String bz;
    // 算法标识
    @Column(name = "model_identify")
    private String modelIdentify;
    // 处理的文件名
    @Column(name = "input_file_name")
    private String inputFileName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getProductPath() {
        return productPath;
    }

    public void setProductPath(String productPath) {
        this.productPath = productPath;
    }

    public String getGdbPath() {
        return gdbPath;
    }

    public void setGdbPath(String gdbPath) {
        this.gdbPath = gdbPath;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public Integer getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(Integer isRelease) {
        this.isRelease = isRelease;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getMosaicFile() {
        return mosaicFile;
    }

    public void setMosaicFile(String mosaicFile) {
        this.mosaicFile = mosaicFile;
    }

    public String getModelIdentify() {
        return modelIdentify;
    }

    public void setModelIdentify(String modelIdentify) {
        this.modelIdentify = modelIdentify;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

}

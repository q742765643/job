package com.htht.job.executor.model.product;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统中各类产品属性信息
 * 产品信息表
 */
@Entity
@Table(name = "htht_cluster_schedule_product")
public class Product extends BaseEntity {
	//产品的名称
    @Column(name = "name")
    private String name;
    //关键字
    @Column(name = "mark")
    private String mark;
    //产品周期类型：日周期、月周期、旬周期等
    @Column(name = "cycle")
    private String cycle;
    //地图路径
    @Column(name = "map_url")
    private String mapUrl;
    //图层名称
    @Column(name = "feature_name")
    private String featureName;
    //排序，主要用于发布平台
    @Column(name = "sort_no")
    private String sortNo;
    //图标路径
    @Column(name = "icon_path")
    private String iconPath;
    //产品路径
    @Column(name = "product_path")
    private String productPath;
    //gdb路径
    @Column(name = "gdb_path")
    private String gdbPath;
    //资源树的id
    @Column(name = "tree_id")
    private String treeId;

	@Column(name = "menu_id")
	private String menuId;
    /**
	 * 是否自动发布
	 * 0需要手动发布 1自动发布
	 */
    @Column(name = "is_release")
    private Integer isRelease;
    //备注
    @Column(name = "bz")
    private String bz;

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

	public String getSortNo() {
		return sortNo;
	}

	public void setSortNo(String sortNo) {
		this.sortNo = sortNo;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
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

	public String getTreeId() {
		return treeId;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
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

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
}

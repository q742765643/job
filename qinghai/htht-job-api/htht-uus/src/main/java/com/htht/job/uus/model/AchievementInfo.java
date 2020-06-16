package com.htht.job.uus.model;

import java.io.Serializable;

public class AchievementInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	
	/**
	 * 菜单ID
	 */
	private String menuId;
	
	/**
	 * 区域ID
	 */
	private String regionId;
	
	/**
	 * 区域ID
	 */
	private String regionName;
	
	
	/**
	 * 图片名
	 */
	private String imageName;
	
	/**
	 * 图片路径
	 */
	private String imagePath;
	
	/**
	 * 备用字段1
	 */
	private String extend1;
	
	/**
	 * 备用字段1
	 */
	private String extend2;
	
	/**
	 * 备用字段1
	 */
	private String extend3;
	
	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}
	
	
}

package com.htht.job.uus.model;

import java.io.Serializable;
import java.util.List;

public class AchievementMenu implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String DefaultPid = "-1";

	/**
	 * 菜单id
	 */
	private String id;
	
	/**
	 * 菜单父id
	 */
	private String parentId;
	
	/**
	 * 菜单名
	 */
	private String menuName;
	
	/**
	 * 排序号
	 */
	private String orderNum;
	
	/**
	 * 图标路径
	 */
	private String iconPath;
	
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
	
	
	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	private List<AchievementMenu> subTree;
	
	public List<AchievementMenu> getSubTree() {
		return subTree;
	}

	public void setSubTree(List<AchievementMenu> subTree) {
		this.subTree = subTree;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
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

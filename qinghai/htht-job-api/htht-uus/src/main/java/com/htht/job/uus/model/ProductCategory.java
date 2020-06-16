package com.htht.job.uus.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: ProductCategory
 * @Description: 产品目录树
 * @author chensi
 * @date 2018年9月26日
 * 
 */
public class ProductCategory implements Serializable
{

	/** serialVersionUID*/  
	private static final long serialVersionUID = 1L;
	
	private String id; 				// id
	private Date createTime;		// 创建时间
	private Date updateTime;		// 修改时间
	private	Integer version;		// 版本号
	private String iconPath; 		// 图标路径
	private String menu; 			// 菜单
	private String menuId; 			// 菜单id
	private String parentId; 		// 父id
	private String text; 			// 文本
	private String treeKey; 		// 类型

	public ProductCategory()
	{
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTreeKey() {
		return treeKey;
	}

	public void setTreeKey(String treeKey) {
		this.treeKey = treeKey;
	}

}

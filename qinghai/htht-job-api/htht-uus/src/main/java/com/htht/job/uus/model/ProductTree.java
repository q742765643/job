package com.htht.job.uus.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: HthtProductTree
 * @Description: 产品目录树
 * @author chensi
 * @date 2018年5月10日
 * 
 */
public class ProductTree implements Serializable
{

	/** serialVersionUID*/  
	private static final long serialVersionUID = 1L;
	
	private String id; 							// id
	private String parentId; 					// 父id
	private String iconPath; 					// 图标路径
	private String name; 						// 文本内容
	private String mapurl;						//
	private String sortno;
	
	private String virtualId;					//虚拟id （仅用于发布平台展示）
	private String virtualParentId;				//虚拟父id（仅用于发布平台展示）

	private List<ProductTree> subTree;		//子产品目录	

	public ProductTree()
	{
		super();
	}

	public List<ProductTree> getSubTree()
	{
		return subTree;
	}

	public void setSubTree(List<ProductTree> subTree)
	{
		this.subTree = subTree;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId()
	{
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	/**
	 * @return the iconPath
	 */
	public String getIconPath()
	{
		return iconPath;
	}

	/**
	 * @param iconPath
	 *            the iconPath to set
	 */
	public void setIconPath(String iconPath)
	{
		this.iconPath = iconPath;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public String getMapurl() {
		return mapurl;
	}

	public void setMapurl(String mapurl) {
		this.mapurl = mapurl;
	}

	public String getSortno() {
		return sortno;
	}

	public void setSortno(String sortno) {
		this.sortno = sortno;
	}
	
	public String getVirtualId() {
		return virtualId;
	}

	public void setVirtualId(String virtualId) {
		this.virtualId = virtualId;
	}

	public String getVirtualParentId() {
		return virtualParentId;
	}

	public void setVirtualParentId(String virtualParentId) {
		this.virtualParentId = virtualParentId;
	}

	@Override
	public String toString()
	{
		return "HthtProductTree [id=" + id + ", parentId=" + parentId + ", iconPath=" + iconPath + ", name=" + name
				+ ", subTree=" + subTree + "]";
	}

}

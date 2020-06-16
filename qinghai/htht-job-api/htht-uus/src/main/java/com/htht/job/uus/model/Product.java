package com.htht.job.uus.model;

import java.io.Serializable;

public class Product implements Serializable{
	
	/** serialVersionUID*/  
	private static final long serialVersionUID = 1L;
	
	private String id; 							// id
	private String parentId; 					// 父id
	private String name; 						// 文本内容
	private String bz;							//备注
	
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBz() {
		return bz;
	}
	
	public void setBz(String bz) {
		this.bz = bz;
	}
	
	
}

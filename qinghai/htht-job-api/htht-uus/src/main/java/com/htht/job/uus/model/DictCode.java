package com.htht.job.uus.model;

public class DictCode {

	private String id;			// 主键Id
	private String dictCode; 	// 编码
	private String dictName; 	// 名称
	private String parentId; 	// 父Id

	public DictCode() {
		super();
	}

	public DictCode(String dictCode) {
		super();
		this.dictCode = dictCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDictCode() {
		return dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}

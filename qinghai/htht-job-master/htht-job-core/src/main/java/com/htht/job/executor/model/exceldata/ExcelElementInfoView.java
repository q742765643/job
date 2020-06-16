package com.htht.job.executor.model.exceldata;

import java.util.List;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class ExcelElementInfoView {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id; // 主键
	
	private String name; // 元素名简称
	private String tableName; // 表名
	private String nickName; // 中文名称
	
	private List<Map<String, String>> excelHeaders; // 元素字段信息
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public List<Map<String, String>> getExcelHeaders() {
		return excelHeaders;
	}

	public void setExcelHeaders(List<Map<String, String>> excelHeaders) {
		this.excelHeaders = excelHeaders;
	}

	
}
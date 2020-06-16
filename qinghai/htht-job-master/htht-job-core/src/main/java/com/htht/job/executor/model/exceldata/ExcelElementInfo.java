package com.htht.job.executor.model.exceldata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_r_element_info")
public class ExcelElementInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id; // 主键
	
	private String name; // 元素名简称
	private String tableName; // 表名
	private String tableChineseName; // 中文名称
	private String className;// 类名
	private String excelTitle; // excel标题
	private String excelHeader; // excel表头
	
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
	
	public String getTableChineseName() {
		return tableChineseName;
	}
	
	public void setTableChineseName(String tableChineseName) {
		this.tableChineseName = tableChineseName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getExcelTitle() {
		return excelTitle;
	}
	
	public void setExcelTitle(String excelTitle) {
		this.excelTitle = excelTitle;
	}
	
	public String getExcelHeader() {
		return excelHeader;
	}
	
	public void setExcelHeader(String excelHeader) {
		this.excelHeader = excelHeader;
	}
    	
}

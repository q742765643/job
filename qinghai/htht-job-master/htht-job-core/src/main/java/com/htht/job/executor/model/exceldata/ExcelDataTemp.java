package com.htht.job.executor.model.exceldata;

/**
 * 
 * Description: execl表格数据导出  模板
 * @author shenlan  
 * @date 2018年11月9日
 */
public class ExcelDataTemp {
	
	private String name;
	private String tableName;
	private String className;
	private String excelTitle;
	
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
	
//	public List<String> getExcelHeaderList() {
//		return excelHeaderList;
//	}
//	
//	public void setExcelHeader(String excelHeader) {
//		
//		if (null == excelHeaderList) {
//			excelHeaderList = new ArrayList<>();
//		}
//		excelHeaderList.add(excelHeader);
//	}

}

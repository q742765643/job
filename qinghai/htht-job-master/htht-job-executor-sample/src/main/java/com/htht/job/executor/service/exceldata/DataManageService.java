package com.htht.job.executor.service.exceldata;

import java.util.List;

import com.htht.job.executor.model.exceldata.ExcelDataPageInfo;
import com.htht.job.executor.model.exceldata.ExcelElementInfo;

public interface DataManageService {
	
	public ExcelDataPageInfo findData(String shortName, int pageNum, int pageSize,String[] params);
	
	/**
	 * 查找所有元素
	 * @return List<ExcelElementInfo>
	 */
	public List<ExcelElementInfo> findAllElement();
	
	/**
	 * 根据元素名查询站点信息
	 * @return List<String>
	 */
	public List<String> findStationByName(String name);
	
}

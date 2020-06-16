package com.htht.job.uus.dao;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.DictCode;

public interface DictCodeDao {
	
	/**
	 * <p>Description: 根据字典编码查找字典名称</p>  
	 * @param dictCode
	 * @return
	 */
	public DictCode getNameByDictCode(@Param("dictCode")String dictCode); 
	
	/**
	 * 
	 * <p>Description: 根据字典名称查找字典编码</p>  
	 * @param dictName
	 * @return
	 */
	public DictCode getDictCodeByName(@Param("dictName")String dictName); 

}

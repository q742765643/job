package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.ProductTree;

/**
 * @ClassName: ProductTreeDao
 * @Description: 产品目录树
 * @author chensi
 * @date 2018年5月10日
 * 
 */
public interface ProductTreeDao {
	
	public List<String> selectCycleById(@Param("id") String id);
	
	public List<ProductTree> selectProductTreeByUserId(@Param("userId") String userId);
	
}

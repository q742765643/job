package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.ProductFileInfo;

/**
 * @ClassName: ProductFileInfoDao
 * @Description: 产品文件信息 
 * @author chensi
 * @date 2018年5月15日
 * 
 */
public interface ProductFileInfoDao {
	
	public List<ProductFileInfo> selectProductFileInfoByIssueAndCycle(@Param("cycle") String cycle,
			@Param("productInfoId")String productInfoId, @Param("regionIds")String[] regionIds, @Param("issue") String issue);

}

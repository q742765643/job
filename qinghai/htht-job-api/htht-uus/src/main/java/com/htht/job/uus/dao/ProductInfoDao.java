package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.ProductInfo;

public interface ProductInfoDao {

	public List<ProductInfo> selectProductInfoDistinct(@Param("beginTime") String begin,
			@Param("endTime") String endTime, @Param("cycle") String cycle, @Param("productId") String productId,
			@Param("regionId") String regionId);

}

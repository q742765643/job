package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.Product;
import com.htht.job.uus.model.ProductCategory;

public interface ProductCategoryDao {

	public List<ProductCategory>  selectCategoryByUserId( @Param("userId")String userId);
	
	
	public Product  selectProductByProductInfoId( @Param("productInfoId")String productInfoId);
	
	public Product  selectProductByProductId( @Param("productId")String productId);
	
}

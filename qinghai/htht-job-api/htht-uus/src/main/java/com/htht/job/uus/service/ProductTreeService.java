package com.htht.job.uus.service;

import java.util.List;

import com.htht.job.uus.model.Product;
import com.htht.job.uus.model.ProductCycle;
import com.htht.job.uus.model.ProductTree;

/**
 * @ClassName: ProductTreeService
 * @Description:
 * @author chensi
 * @date 2018年5月10日
 * 
 */
public interface ProductTreeService {
	
	/**
	 * @Description: 根据产品主键Id查询该产品的周期类型
	 * @param id
	 * @return List<HthtProductCycle>
	 * @throws 
	 * 
	 */
	public List<ProductCycle> findCycleById(String id); 
	
	/**
	 * @Description: 根据用户id查询该用户拥有权限的产品id
	 * @param userId
	 * @return List<String>
	 */
	public List<ProductTree> findProductTreeByUserId(String userId);
	
	/**
	 * 
	 * <p>Description: </p>  
	 * @param productInfoId
	 * @return Product
	 */
	public Product findProductByProductInfoId(String productInfoId);
}

package com.htht.job.executor.service.productinfo;

import com.htht.job.executor.model.productinfo.ProductInfo;

import java.util.List;
import java.util.Map;

public interface ProductInfoService {
	public Map<String, Object> pageList(int start, int length, String productId);

	public void deleteProductInfo(String id);

	public ProductInfo save(ProductInfo productInfo);

	/**
	 * 产品查询
	 * 	Ⅰ：正常查询
	 * 	Ⅱ：无 inputFileName查询
	 * 	Ⅲ：无 inputFileName、regionId 查询
	 * @param productId
	 * @param issue
	 * @param cycle
	 * @param modelIdentify
	 * @param inputFileName
	 * @param regionId
	 * @return
	 */
	public List<ProductInfo> findProductExits(String productId, String issue,
			String cycle, String modelIdentify, String inputFileName, String regionId);

	public List<ProductInfo> findProductInfoListByIssueRange(String id, String startIssue, String endIssue);
	
	public ProductInfo findProductInfoById(String id);
	
	public int countProductInfoByPid(String pid , String regionId);
	
	public List<ProductInfo> findProductByMark(String mark, List<String> issue,String regionId);
	
}

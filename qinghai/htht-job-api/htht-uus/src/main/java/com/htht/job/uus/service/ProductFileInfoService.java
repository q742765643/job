package com.htht.job.uus.service;

import java.util.Map;

public interface ProductFileInfoService {
	
	/**
	 * @Description: 根据周期、产品Id、区域Id、期号、分页查询产品文件信息
	 * @param cycle
	 * @param productId
	 * @param regionIds
	 * @param issue
	 * @param pageNum
	 * @param pageSize
	 * @return Map<String,Object>
	 * @throws 
	 * 
	 */
	public Map<String, Object> findProductFileInfoByIssueAndCycle(String cycle,
			String productInfoId, String[] regionIds, String issue, int pageNum, int pageSize);
	
}

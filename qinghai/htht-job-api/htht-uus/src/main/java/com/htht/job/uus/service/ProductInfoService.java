package com.htht.job.uus.service;

import java.util.Date;
import java.util.Map;

public interface ProductInfoService {

	/**
	 * @Description: 根据时间范围、周期、产品Id、区域Id、分页查询产品信息
	 * @param beginTime
	 * @param endTime
	 * @param cycle
	 * @param productId
	 * @param regionIds
	 * @param pageNum
	 * @param pageSize
	 * @return Map<String,Object>
	 * @throws 
	 * 
	 */
	public Map<String, Object> findProductInfoDistinct(Date beginTime,  Date endTime,
			 String cycle, String productId, String[] regionIds, int pageNum, int pageSize);
	
}

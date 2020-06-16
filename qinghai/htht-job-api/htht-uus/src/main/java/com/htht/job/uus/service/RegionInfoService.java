package com.htht.job.uus.service;

import java.util.List;

import com.htht.job.uus.model.RegionInfo;

/**
 * @ClassName: RegionInfoService
 * @Description: 
 * @author chensi
 * @date 2018年5月11日
 * 
 */
public interface RegionInfoService {
	
	/**
	 * @Description: 根据父级区域ID查询区域信息
	 * @param parentId		父类区域ID
	 * @return List<HthtRegionInfo>
	 * @throws 
	 * 
	 */
	public List<RegionInfo> findRegionInfosByParentId(String parentRegionId);
	
	/**
	 * @Description: 根据区域ID查询区域信息
	 * @param parentId		区域ID
	 * @return List<HthtRegionInfo>
	 * @throws 
	 * 
	 */
	public RegionInfo findRegionInfoByRegionId(String regionId);
	
}

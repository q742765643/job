package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.RegionInfo;

/**
 * @ClassName: RegionInfoDao
 * @Description: 行政区域
 * @author chensi
 * @date 2018年5月11日
 * 
 */
public interface RegionInfoDao {

	public RegionInfo selectRegionInfoByRegionId(@Param("regionId")String parentRegionId);
	
	public List<RegionInfo> selectRegionInfosByParentRegionId(@Param("regionId")String regionId);
}

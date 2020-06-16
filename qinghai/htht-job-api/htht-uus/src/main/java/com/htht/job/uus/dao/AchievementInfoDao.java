package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.AchievementInfo;
import com.htht.job.uus.model.AchievementUser;
import com.htht.job.uus.model.RegionInfo;

public interface AchievementInfoDao {

	List<AchievementInfo> queryImgInfo(@Param("menuId") String menuId,@Param("regionId") String regionId);

	List<RegionInfo> findRegionInfosByMenuId(@Param("menuId") String menuId);

	List<AchievementInfo> queryNewest(@Param("menuName") String menuName,@Param("regionId") String regionId);

	List queryAchTab(@Param("tabName") String tabName,@Param("orderCol") String orderCol);

}

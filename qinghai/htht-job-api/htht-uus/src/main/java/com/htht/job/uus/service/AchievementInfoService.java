package com.htht.job.uus.service;

import java.util.List;

import com.htht.job.uus.model.AchievementInfo;
import com.htht.job.uus.model.AchievementUser;
import com.htht.job.uus.model.RegionInfo;

public interface AchievementInfoService {

	List<AchievementInfo> queryImgInfo(String menuId,String regionId);

	List<RegionInfo> findRegionInfosByMenuId(String menuId);

	List<AchievementInfo> queryNewest(String menuName,String regionId);

	List queryAchTab(String tabName,String orderCol);

}

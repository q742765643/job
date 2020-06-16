package com.htht.job.uus.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.AchievementInfoDao;
import com.htht.job.uus.model.AchievementInfo;
import com.htht.job.uus.model.AchievementUser;
import com.htht.job.uus.model.RegionInfo;
import com.htht.job.uus.service.AchievementInfoService;

@Service
public class AchievementInfoServiceImpl implements AchievementInfoService{

	@Autowired
	private AchievementInfoDao achievementInfoDao;
	
	@Override
	public List<AchievementInfo> queryImgInfo(String menuId, String regionId) {
		return achievementInfoDao.queryImgInfo(menuId, regionId);
	}

	@Override
	public List<RegionInfo> findRegionInfosByMenuId(String menuId) {
		return achievementInfoDao.findRegionInfosByMenuId(menuId);
	}

	@Override
	public List<AchievementInfo> queryNewest(String menuName, String regionId) {
		return achievementInfoDao.queryNewest(menuName,regionId);
	}

	@Override
	public List queryAchTab(String tabName,String orderCol) {
		return achievementInfoDao.queryAchTab(tabName,orderCol);
	}


}

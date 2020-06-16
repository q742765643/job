package com.htht.job.uus.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.AchievementMenuDao;
import com.htht.job.uus.model.AchievementMenu;
import com.htht.job.uus.service.AchievementMenuService;

@Service
public class AchievementMenuServiceImpl implements AchievementMenuService{
	
	@Autowired
	private AchievementMenuDao achievementMenuDao;

	@Override
	public List<AchievementMenu> queryMenus(String pid) {
		return achievementMenuDao.queryMenus(pid);
	}

}

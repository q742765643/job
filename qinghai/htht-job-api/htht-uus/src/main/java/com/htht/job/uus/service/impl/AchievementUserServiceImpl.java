package com.htht.job.uus.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.AchievementUserDao;
import com.htht.job.uus.model.AchievementUser;
import com.htht.job.uus.service.AchievementUserService;

@Service
public class AchievementUserServiceImpl implements AchievementUserService{

	@Autowired
	private AchievementUserDao achievementUserDao;
	
	@Override
	public List<AchievementUser> queryAchUser() {
		return achievementUserDao.queryAchUser();
	}

}

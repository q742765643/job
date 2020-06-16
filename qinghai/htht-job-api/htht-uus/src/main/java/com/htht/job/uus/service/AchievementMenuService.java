package com.htht.job.uus.service;

import java.util.List;

import com.htht.job.uus.model.AchievementMenu;

public interface AchievementMenuService {

	List<AchievementMenu> queryMenus(String pid);
}

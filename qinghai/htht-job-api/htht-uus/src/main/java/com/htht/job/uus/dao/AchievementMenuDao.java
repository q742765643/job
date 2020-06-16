package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.AchievementMenu;

public interface AchievementMenuDao {
	
	List<AchievementMenu> queryMenus(@Param("pid") String pid);

}

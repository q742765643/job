package com.htht.job.uus.dao;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.User;

public interface UserDao {

	public User selectUserInfoByUsername(@Param("userName")String userName);
}

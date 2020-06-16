package com.htht.job.uus.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.UserDao;
import com.htht.job.uus.model.User;
import com.htht.job.uus.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserDao userDao;
	
	@Override
	public User findUserInfoByUsername(String userName) {
		User user = userDao.selectUserInfoByUsername(userName);
		return user;
	}

}

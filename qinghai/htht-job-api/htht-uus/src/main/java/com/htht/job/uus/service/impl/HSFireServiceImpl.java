package com.htht.job.uus.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.HSFireDao;
import com.htht.job.uus.model.HSFire;
import com.htht.job.uus.service.HSFireService;

@Service
public class HSFireServiceImpl implements HSFireService {

	@Autowired
	private HSFireDao hsFireDao;
	@Override
	public List<HSFire> findHSFireByIssue(String issue) {

		List<HSFire> HSFires = hsFireDao.selectHSFireByIssue(issue);
		return HSFires;
	}
	@Override
	public List<HSFire> findHSFireByIssueAndProductInfoId(String issue,
			String productInfo) {
		List<HSFire> HSFires = hsFireDao.findHSFireByIssueAndProductInfoId(issue,productInfo);
		return HSFires;
	}

}

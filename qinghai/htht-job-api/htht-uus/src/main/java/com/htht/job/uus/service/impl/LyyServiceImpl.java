package com.htht.job.uus.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.LyyInfoDao;
import com.htht.job.uus.model.LyyInfo;
import com.htht.job.uus.service.LyyService;

@Service
public class LyyServiceImpl implements LyyService{

	@Autowired
	private LyyInfoDao lyyInfoDao;
	
	@Override
	public List<LyyInfo> findLyyByIssueAndProductInfoId(String issue,
			String productInfo) {
		return lyyInfoDao.findLyyByIssueAndProductInfoId(issue, productInfo);
	}

}

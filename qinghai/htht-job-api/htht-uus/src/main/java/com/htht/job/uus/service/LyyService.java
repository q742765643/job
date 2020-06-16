package com.htht.job.uus.service;

import java.util.List;

import com.htht.job.uus.model.LyyInfo;


public interface LyyService {

	public List<LyyInfo> findLyyByIssueAndProductInfoId(String issue,String productInfo);
	
}

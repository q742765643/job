package com.htht.job.uus.service;

import java.util.List;

import com.htht.job.uus.model.HSFire;

public interface HSFireService {

	public List<HSFire> findHSFireByIssue(String issue);
	public List<HSFire> findHSFireByIssueAndProductInfoId(String issue,String productInfo);
	
}

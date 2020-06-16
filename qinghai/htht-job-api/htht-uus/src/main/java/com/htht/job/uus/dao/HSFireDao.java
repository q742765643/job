package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.HSFire;

public interface HSFireDao {
	
	public List<HSFire> selectHSFireByIssue(@Param("issue")String issue);

	public List<HSFire> findHSFireByIssueAndProductInfoId(@Param("issue") String issue,	@Param("productInfoId")String productInfoId);

}

package com.htht.job.uus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.htht.job.uus.model.LyyInfo;

public interface LyyInfoDao {
	
	public List<LyyInfo> findLyyByIssueAndProductInfoId(@Param("issue") String issue,	@Param("productInfoId")String productInfoId);

}

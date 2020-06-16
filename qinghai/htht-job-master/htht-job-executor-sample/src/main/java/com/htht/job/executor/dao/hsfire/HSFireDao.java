package com.htht.job.executor.dao.hsfire;

import java.util.List;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Query;

import com.htht.job.executor.model.hsfire.HSFire;

public interface HSFireDao extends BaseDao<HSFire>{
	
	@Query(nativeQuery = true,value = "select * from htht_cluster_schedule_h8fire where issue = ?1")
	public List<HSFire> selectFireByIssue(String issue);

}

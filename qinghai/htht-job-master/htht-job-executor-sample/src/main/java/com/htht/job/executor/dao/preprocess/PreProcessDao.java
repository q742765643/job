package com.htht.job.executor.dao.preprocess;

import java.util.List;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Query;

import com.htht.job.executor.model.preprocess.PreProcess;

public interface PreProcessDao extends BaseDao<PreProcess> {
	
	@Query(nativeQuery = true,value = "select * FROM htht_cluster_schedule_preprocess WHERE " +
	        "product_id = ?1 and issue in ?2 ")
	public List<PreProcess> selectProcessByProductIdAndIssue(String productId, List<String> issue);
	
	@Query(nativeQuery = true,value = "select * FROM htht_cluster_schedule_preprocess WHERE " +
            "product_id = ?1 and issue = ?2 ") 
	public PreProcess selectProcessByProductIdAndIssue(String productId, String issue);
	
}

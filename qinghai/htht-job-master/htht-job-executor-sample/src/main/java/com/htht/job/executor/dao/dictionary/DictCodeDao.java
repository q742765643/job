package com.htht.job.executor.dao.dictionary;

import java.util.List;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.htht.job.executor.model.dictionary.DictCode;
import org.springframework.stereotype.Repository;

/**
 * @date:2018年6月27日下午2:54:50
 * @author:yss
 */
@Repository
public interface DictCodeDao extends BaseDao<DictCode> {
    
	@Modifying
	@Query(nativeQuery = true,value = "select * from htht_cluster_schedule_dict_code where parent_id = (select id from htht_cluster_schedule_dict_code WHERE dict_name=:dictName)")
	List<DictCode> findChildren(@Param("dictName") String dictName);
     
	DictCode findByDictName(String string);
}

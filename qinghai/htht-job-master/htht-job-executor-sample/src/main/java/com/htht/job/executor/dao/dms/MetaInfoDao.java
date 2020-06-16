package com.htht.job.executor.dao.dms;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.htht.job.executor.model.dms.module.MetaInfo;

/**
 * 
 * @author LY 2018-04-03
 * 
 */
@Repository
public interface MetaInfoDao extends BaseDao<MetaInfo> {

	@Modifying
	@Query(nativeQuery = true, value = "update HTHT_DMS_META_INFO a set a.f_recycleflag=:f_recycleflag where id=:id")
	void updateRecycleflag(@Param("f_recycleflag")Integer f_recycleflag, @Param("id")String id);

}

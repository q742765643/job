package com.htht.job.executor.dao.shiro;

import com.htht.job.executor.model.shiro.Resource;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/1/24.
 */
@Repository
public interface ResourceDao extends BaseDao<Resource> {
    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM htht_cluster_schedule_role_resource WHERE resource_id = :id")
    void deleteGrant(@Param("id") String id);


}

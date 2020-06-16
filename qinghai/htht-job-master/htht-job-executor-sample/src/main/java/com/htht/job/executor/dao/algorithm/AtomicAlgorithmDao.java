package com.htht.job.executor.dao.algorithm;

import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtomicAlgorithmDao extends BaseDao<AtomicAlgorithm> {

    @Modifying
    @Query(nativeQuery = true,value = "select * from htht_master_schedule_trigger_info where model_id=:id")
    List findById(@Param("id") String id);

    AtomicAlgorithm findInfoById(String id);

    AtomicAlgorithm findModelIdentifyById(String id);

}

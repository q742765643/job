package com.htht.job.executor.dao.registryalgo;

import com.htht.job.executor.model.registryalgo.RegistryAlgo;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistryAlgoDao extends BaseDao<RegistryAlgo>{
	
    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM htht_cluster_schedule_registry_algo WHERE " +
            "  algo_id = :algo_id ")
	void deleteRegistryAlgoByAlgoId(@Param("algo_id") String algo_id);

    @Modifying
    @Query(nativeQuery = true,value = "DELETE FROM htht_cluster_schedule_registry_algo WHERE " +
            "  registry_id = :registry_id ")
	void delAlgoRegByRegId(@Param("registry_id") String id);
	
}



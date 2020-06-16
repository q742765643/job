package com.htht.job.executor.dao.processsteps;

import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/3/29.
 */
@Repository
public interface ProcessStepsDao extends BaseDao<ProcessStepsDTO> {
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM htht_cluster_schedule_process_steps WHERE flow_id=:flowId")
    void deleteByParameterId(@Param("flowId") String flowId);
}

package com.htht.job.executor.dao.systemlog;

import com.htht.job.executor.model.systemlog.SystemLog;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogDao extends BaseDao<SystemLog> {

    Page<SystemLog> findByCategory(String category, Pageable pageable);
}

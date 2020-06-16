package com.htht.job.executor.service.systemlog;

import com.htht.job.executor.model.systemlog.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SystemLogService {

    SystemLog save(SystemLog systemLog);

    Page<SystemLog> getSystemLogsByPage(Pageable pageable);

    Page<SystemLog> findSystemLogsByCategory(String category, Pageable pageable);

    int deleteSystemLog(String id);
}

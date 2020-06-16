package com.htht.job.executor.service.systemlog;

import com.htht.job.executor.dao.systemlog.SystemLogDao;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: htht-job-api
 * @description: 系统日志
 * @author: dingjiancheng
 * @create: 2018-09-17 11:15
 */
@Transactional
@Service("SystemLogService")
public class SystemLogServiceImpl implements SystemLogService {
    @Autowired
    private SystemLogDao systemLogDao;

    @Override
    public SystemLog save(SystemLog systemLog) {
        return systemLogDao.save(systemLog);
    }

    @Override
    public Page<SystemLog> getSystemLogsByPage(Pageable pageable) {
        return systemLogDao.findAll(pageable);
    }

    @Override
    public Page<SystemLog> findSystemLogsByCategory(String category, Pageable pageable) {
        return systemLogDao.findByCategory(category, pageable);
    }

    @Override
    public int deleteSystemLog(String id) {
        int num = 1;
        try {
            systemLogDao.delete(id);
        } catch (Exception e) {
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }
}

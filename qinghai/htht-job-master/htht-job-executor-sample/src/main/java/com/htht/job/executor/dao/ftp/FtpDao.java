package com.htht.job.executor.dao.ftp;

import com.htht.job.executor.model.ftp.Ftp;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/1/16.
 */
@Repository
public interface FtpDao extends BaseDao<Ftp>{

    Page<Ftp> findAll(Pageable pageable);
}

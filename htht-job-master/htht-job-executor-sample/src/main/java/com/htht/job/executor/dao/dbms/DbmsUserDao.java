package com.htht.job.executor.dao.dbms;

import com.htht.job.executor.model.dbms.DbmsUser;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/1/24.
 */
@Repository
public interface DbmsUserDao extends BaseDao<DbmsUser> {
    DbmsUser findByUserName(String username);

}

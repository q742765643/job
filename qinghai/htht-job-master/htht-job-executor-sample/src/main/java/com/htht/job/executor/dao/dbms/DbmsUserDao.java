package com.htht.job.executor.dao.dbms;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

import com.htht.job.executor.model.dbms.DbmsUser;

/**
 * Created by zzj on 2018/1/24.
 */
@Repository
public interface DbmsUserDao extends BaseDao<DbmsUser>{
	DbmsUser findByUserName(String username);

}

package com.htht.job.executor.dao.uus;

import com.htht.job.executor.model.shiro.User;
import com.htht.job.executor.model.uus.UusUser;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/1/24.
 */
@Repository
public interface UusUserDao extends BaseDao<UusUser> {
    User findByUserName(String username);

}	

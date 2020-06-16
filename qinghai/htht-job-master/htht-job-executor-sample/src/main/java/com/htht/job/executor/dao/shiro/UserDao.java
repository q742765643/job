package com.htht.job.executor.dao.shiro;

import com.htht.job.executor.model.shiro.User;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/1/24.
 */
@Repository
public interface UserDao extends BaseDao<User>{
    User findByUserName(String username);

}

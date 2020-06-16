package com.htht.job.executor.dao.mapping;/**
 * Created by zzj on 2018/7/5.
 */

import com.htht.job.executor.model.mapping.MatchRelation;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * @program: htht-job-api
 * @description: 匹配关系
 * @author: zzj
 * @create: 2018-07-05 10:37
 **/
@Repository
public interface MatchRelationDao extends BaseDao<MatchRelation> {
    MatchRelation findByJobIdAndDataId(int jobId, String dataId);
}


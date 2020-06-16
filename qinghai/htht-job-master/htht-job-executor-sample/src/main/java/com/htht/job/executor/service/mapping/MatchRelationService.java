package com.htht.job.executor.service.mapping;/**
 * Created by zzj on 2018/7/5.
 */

import com.htht.job.executor.dao.mapping.MatchRelationDao;
import com.htht.job.executor.model.mapping.MatchRelation;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: htht-job-api
 * @description: 匹配关系
 * @author: zzj
 * @create: 2018-07-05 10:37
 **/
@Transactional
@Service("matchRelationService")
public class MatchRelationService extends BaseService<MatchRelation> {
    @Autowired
    private MatchRelationDao relationDao;
    @Override
    public BaseDao<MatchRelation> getBaseDao() {
        return relationDao;
    }

    public List<MatchRelation> saveMatchRelation(List<MatchRelation>  matchRelations){
        for(int i=0;i<matchRelations.size();i++){
            MatchRelation oldMatchRelation=this.findMatchRelationByJobIdAndDataId(matchRelations.get(i).getJobId(),matchRelations.get(i).getDataId());
            if(null!=oldMatchRelation) {
                this.delete(oldMatchRelation);
            }
        }
        return this.save(matchRelations);
    }

    public MatchRelation findMatchRelationByJobIdAndDataId(int jobId,String dataId){
       return relationDao.findByJobIdAndDataId(jobId,dataId);
    }
}


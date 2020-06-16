package com.htht.job.executor.service.flowlog;/**
 * Created by zzj on 2018/3/30.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.flowlog.FlowLogDao;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.service.parallellog.ParallelLogService;
import com.htht.job.executor.util.ProcessConstant;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job
 * @description: 流程日志逻辑层
 * @author: zzj
 * @create: 2018-03-30 17:40
 **/
@Transactional
@Service("flowLogService")
public class FlowLogService  extends BaseService<FlowLog> {
    @Autowired
    private FlowLogDao flowLogDao;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ParallelLogService parallelLogService;
    @Override
    public BaseDao<FlowLog> getBaseDao() {
        return flowLogDao;
    }
    public FlowLog saveFlowLog(FlowLog flowLog){
         if(!StringUtils.isEmpty(flowLog.getId())){
             flowLog.setUpdateTime(new Date());
         }
        flowLog.setUpdateTime(new Date());
        return flowLogDao.save(flowLog);
    }

    public FlowLog findByFlowLogId(String flowLogId){
        return this.getById(flowLogId);
    }
    public long  getFlowLogCount(String nextId,int jobLogId){
        String sql="SELECT * from htht_cluster_schedule_flow_log where 1=1 and  job_log_id="+jobLogId+" and code!='200'";
        if(!StringUtils.isEmpty(nextId)){
            sql+=" data_id in ('"+nextId+"')";
        }
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(org.hibernate.SQLQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> list = query.getResultList();
        return  list.size();
    }
    public FlowLog findByJobLogIdAndDataIdAndParentFlowlogId(int jobLogId,String dataId,String parentFlowlogId){
        return flowLogDao.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId,dataId,parentFlowlogId);
    }

    public List<FlowLog> findFlowLogList(int jobLogId,String parentFlowlogId){
        SimpleSpecificationBuilder<FlowLog> specification=new SimpleSpecificationBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        specification.add("jobLogId","eq",jobLogId);
        specification.add("dataId","notEqual", ProcessConstant.STRATFIGURE);
        specification.add("dataId","notEqual", ProcessConstant.ENDFIGURE);
        if(!StringUtils.isEmpty(parentFlowlogId)){
            specification.add("parentFlowlogId","eq", parentFlowlogId);
        }
        List<FlowLog> flowLogList=flowLogDao.findAll(specification.generateSpecification(),sort);
        return flowLogList;
    }

    public List<CommonParameter> findEnd(int jobLogId){
        SimpleSpecificationBuilder<FlowLog> specification=new SimpleSpecificationBuilder();
        specification.add("jobLogId","eq",jobLogId);
        specification.add("dataId","eq", ProcessConstant.ENDFIGURE);
        List<FlowLog> flowLogList=flowLogDao.findAll(specification.generateSpecification());
        List<CommonParameter> commonParameterList=new ArrayList<CommonParameter>();
        for(int i=0;i<flowLogList.size();i++){
              List<CommonParameter> commonParameters= JSON.parseArray(flowLogList.get(i).getDynamicParameter(),CommonParameter.class);
              commonParameterList.addAll(commonParameters);
        }

        return commonParameterList;

    }
    public ReturnT<String> deleteFlowLog(String id) {
        try {
            String sql_p = "select p.id from htht_cluster_schedule_parallel_log p where  p.flow_id ='"+id+"' ";
            List<String> pList = entityManager.createNativeQuery(sql_p).getResultList();
            parallelLogService.deleteByIds(pList);
            this.delete(id);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnT.FAIL;
    }

    public void deleteParallelLogAndFlowLog(int jobLodId){
        String sql_p = "select p.id from htht_cluster_schedule_parallel_log p,htht_cluster_schedule_flow_log f where f.job_log_id="+jobLodId+" and f.id= p.flow_id ";
        List<String> pList = entityManager.createNativeQuery(sql_p).getResultList();
        String sql_f = "select f.id from htht_cluster_schedule_flow_log f where f.job_log_id="+jobLodId+"";
        List<String> fList = entityManager.createNativeQuery(sql_f).getResultList();
        parallelLogService.deleteByIds(pList);
        this.deleteByIds(fList);


    }
}


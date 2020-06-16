package com.htht.job.executor.service.flowlog;/**
 * Created by zzj on 2018/3/30.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.flowlog.FlowLogDao;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.service.parallellog.ParallelLogService;
import com.htht.job.executor.util.ParameterConstant;
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
public class FlowLogService extends BaseService<FlowLogDTO> {

    @Autowired
    private FlowLogDao flowLogDao;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ParallelLogService parallelLogService;

    @Override
    public BaseDao<FlowLogDTO> getBaseDao() {
        return flowLogDao;
    }

    public FlowLogDTO saveFlowLog(FlowLogDTO flowLogDTO) {
        if (!StringUtils.isEmpty(flowLogDTO.getId())) {
            flowLogDTO.setUpdateTime(new Date());
        }
        flowLogDTO.setUpdateTime(new Date());
        return flowLogDao.save(flowLogDTO);
    }

    public FlowLogDTO findByFlowLogId(String flowLogId) {
        return this.getById(flowLogId);
    }

    public long getFlowLogCount(String nextId, int jobLogId) {
        String sql = "SELECT * from htht_cluster_schedule_flow_log where 1=1 and  job_log_id=:jobLogId and code!='200'";
        if (!StringUtils.isEmpty(nextId)) {
            sql += " data_id in (:nextId)";
        }
        Query query = entityManager.createNativeQuery(sql).setParameter(ParameterConstant.JOB_LOG_ID,jobLogId);
        if (!StringUtils.isEmpty(nextId)) {
            query.setParameter("nextId",nextId);
        }
        query.unwrap(org.hibernate.SQLQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> list = query.getResultList();
        return list.size();
    }

    public FlowLogDTO findByJobLogIdAndDataIdAndParentFlowlogId(int jobLogId, String dataId, String parentFlowlogId) {
        return flowLogDao.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId, dataId, parentFlowlogId);
    }

    public List<FlowLogDTO> findFlowLogList(int jobLogId, String parentFlowlogId) {
        SimpleSpecificationBuilder<FlowLogDTO> specification = new SimpleSpecificationBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        specification.add("jobLogId", "eq", jobLogId);
        specification.add(ParameterConstant.DATA_ID, "notEqual", ProcessConstant.STRATFIGURE);
        specification.add(ParameterConstant.DATA_ID, "notEqual", ProcessConstant.ENDFIGURE);
        if (!StringUtils.isEmpty(parentFlowlogId)) {
            specification.add("parentFlowlogId", "eq", parentFlowlogId);
        }
        return flowLogDao.findAll(specification.generateSpecification(), sort);
    }

    public List<CommonParameter> findEnd(int jobLogId) {
        SimpleSpecificationBuilder<FlowLogDTO> specification = new SimpleSpecificationBuilder();
        specification.add("jobLogId", "eq", jobLogId);
        specification.add("dataId", "eq", ProcessConstant.ENDFIGURE);
        List<FlowLogDTO> flowLogDTOList = flowLogDao.findAll(specification.generateSpecification());
        List<CommonParameter> commonParameterList = new ArrayList<>();
        for (int i = 0; i < flowLogDTOList.size(); i++) {
            List<CommonParameter> commonParameters = JSON.parseArray(flowLogDTOList.get(i).getDynamicParameter(), CommonParameter.class);
            commonParameterList.addAll(commonParameters);
        }

        return commonParameterList;

    }

    public ReturnT<String> deleteFlowLog(String id) {
        try {
            String sqlP = "select p.id from htht_cluster_schedule_parallel_log p where  p.flow_id =:id ";
            List<String> pList = entityManager.createNativeQuery(sqlP).setParameter("id",id).getResultList();
            parallelLogService.deleteByIds(pList);
            this.delete(id);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return ReturnT.FAIL;
    }

    public void deleteParallelLogAndFlowLog(int jobLodId) {
        String sqlP = "select p.id from htht_cluster_schedule_parallel_log p,htht_cluster_schedule_flow_log f where f.job_log_id=:jobLodId and f.id= p.flow_id ";
        List<String> pList = entityManager.createNativeQuery(sqlP).setParameter("jobLodId",jobLodId).getResultList();
        String sqlF = "select f.id from htht_cluster_schedule_flow_log f where f.job_log_id=:jobLodId";
        List<String> fList = entityManager.createNativeQuery(sqlF).setParameter("jobLodId",jobLodId).getResultList();
        parallelLogService.deleteByIds(pList);
        this.deleteByIds(fList);


    }
}


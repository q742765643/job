package com.htht.job.executor.service.parallellog;/**
 * Created by zzj on 2018/4/18.
 */

import com.htht.job.executor.dao.parallellog.ParallelLogDao;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.parallellog.ParallelLogResult;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * @program: htht-job
 * @description: 流程并行日志
 * @author: zzj
 * @create: 2018-04-18 10:37
 **/
@Service
public class ParallelLogService extends BaseService<ParallelLog> {
    @Autowired
    private ParallelLogDao parallelLogDao;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public BaseDao<ParallelLog> getBaseDao() {
        return parallelLogDao;
    }
    public ParallelLog saveParallelLog(ParallelLog parallelLog){
        return parallelLogDao.save(parallelLog);
    }
    public ParallelLog findParallelLogById(String id){
        return this.getById(id);
    }
    public List<ParallelLog> findParallelLogList(ParallelLog parallelLog){
        SimpleSpecificationBuilder<ParallelLog> specification=new SimpleSpecificationBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");

        if(!StringUtils.isEmpty(parallelLog.getFlowId())) {
            specification.add("flowId", "eq", parallelLog.getFlowId());
        }
        specification.add("code", "eq", parallelLog.getCode());

        List<ParallelLog> list= parallelLogDao.findAll(specification.generateSpecification(),sort);
        return list;

    }
	public Map findParallelLogPage(int logId, int start, int length, int logStatus) {
		Map pageMap = new HashMap();
//		String sql = "select * from htht_parallel_log p,htht_flow_log f,htht_cluster_schedule_process_steps c where f.job_log_id="+logId+" and f.data_id=c.data_id and f.id= p.flow_id GROUP BY p.id ORDER BY c.sort limit "+start+","+length;
//		Query query = entityManager.createNativeQuery(sql,ParallelLogResult.class);
//		List<ParallelLogResult> resultList = query.getResultList();
//		return resultList;
		String sql = "";
		String totalSql ="";
		if(logStatus == -1) {
			 sql = "select p.id,f.label,p.code,p.create_time,p.update_time,p.ip,p.handle_msg,p.dynamic_parameter,p.model_parameters from htht_cluster_schedule_parallel_log p,htht_cluster_schedule_flow_log f where f.job_log_id="+logId+" and  f.id= p.flow_id ORDER BY p.update_time asc limit "+start+","+length;
			 totalSql = "select p.id,f.label,p.code,p.create_time,p.update_time,p.ip,p.handle_msg,p.dynamic_parameter from htht_cluster_schedule_parallel_log p,htht_cluster_schedule_flow_log f where f.job_log_id="+logId+" and f.id= p.flow_id ";
		}else {
			sql = "select p.id,f.label,p.code,p.create_time,p.update_time,p.ip,p.handle_msg,p.dynamic_parameter,p.model_parameters from htht_cluster_schedule_parallel_log p,htht_cluster_schedule_flow_log f where f.job_log_id="+logId+" and p.code="+logStatus+" and f.id= p.flow_id ORDER BY f.sort limit "+start+","+length;
			totalSql = "select p.id,f.label,p.code,p.create_time,p.update_time,p.ip,p.handle_msg,p.dynamic_parameter from htht_cluster_schedule_parallel_log p,htht_cluster_schedule_flow_log f where f.job_log_id="+logId+" and p.code="+logStatus+" and  f.id= p.flow_id ";
		}
		List resultList = entityManager.createNativeQuery(sql).getResultList();
		List<ParallelLogResult> listResult = new ArrayList<ParallelLogResult>();
        for(int i=0;i<resultList.size();i++) {         
        	ParallelLogResult result = new ParallelLogResult();
            Object[] obj = (Object[]) resultList.get(i);   
             result.setId(obj[0]+"");
             result.setLabel(obj[1]+"");
             result.setCode((Integer)obj[2]);
             result.setCreateTime((Date)obj[3]);
             result.setUpdateTime((Date)obj[4]);
             result.setIp(obj[5]+"");
             if( null == obj[6]){
            	 result.setHandleMsg(null);
             }
             result.setDynamicParameter(obj[7]+"");
             if(null != obj[7] && obj[7].toString().indexOf("]")<5){
            	 result.setDynamicParameter(obj[8]+"");
             }
             result.setLogId(logId);
             listResult.add(result);
             //使用obj[0],obj[1],obj[2]取出属性   
        }
        pageMap.put("list", listResult);	
        List totalList = entityManager.createNativeQuery(totalSql).getResultList();
        pageMap.put("total", totalList.size());
		return pageMap;
	}



}


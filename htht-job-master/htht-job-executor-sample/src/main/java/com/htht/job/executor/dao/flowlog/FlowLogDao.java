package com.htht.job.executor.dao.flowlog;/**
 * Created by zzj on 2018/3/30.
 */

import com.htht.job.executor.model.flowlog.FlowLogDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * @program: htht-job
 * @description: 流程日志dao
 * @author: zzj
 * @create: 2018-03-30 17:38
 **/
@Repository
public interface FlowLogDao extends BaseDao<FlowLogDTO> {

    FlowLogDTO findByJobLogIdAndDataIdAndParentFlowlogId(int jobLogId, String dataId, String parentFlowlogId);

}


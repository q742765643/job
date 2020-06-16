package com.htht.job.admin.service.impl;


import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.service.DispatchService;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @program: htht-job
 * @description: 调度参数拼装
 * @author: zzj
 * @create: 2018-03-28 13:55
 **/
@Service("dispatchService")
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    @Qualifier("broadCastSchedulerService")
    private SchedulerService broadCastSchedulerService;
    @Autowired
    @Qualifier("singleSchedulerService")
    private SchedulerService singleSchedulerService;
    @Autowired
    @Qualifier("flowSchedulerService")
    private SchedulerService flowSchedulerService;
    @Resource
    private AtomicAlgorithmService atomicAlgorithmService;
    @Resource
    private DubboService dubboService;

    @Override
    public void scheduler(XxlJobInfo jobInfo) {
        //1:算法任务  2.遥感数据汇集 3.CIMISS数据汇集 4.产品生产  5：代表流程任务       6：代表气象卫星预处理(暂时改为算法任务)         
        //7：代表高分预处理流程 8:数管调度任务（算法任务） 9：数管流程任务
        if (5 == jobInfo.getTasktype() || 7 == jobInfo.getTasktype() || 9 == jobInfo.getTasktype()) {
            flowSchedulerService.scheduler(jobInfo);
        } else {
            AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(jobInfo.getModelId());
            boolean flag = dubboService.checkBroadCast(atomicAlgorithmDTO.getModelIdentify());
            if (flag) {
                broadCastSchedulerService.scheduler(jobInfo);
            } else {
                singleSchedulerService.scheduler(jobInfo);
            }

        }
    }


}


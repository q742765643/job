package com.htht.job.admin.service.impl;


import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.service.DispatchService;
import com.htht.job.admin.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * @program: htht-job
 * @description: 调度参数拼装
 * @author: zzj
 * @create: 2018-03-28 13:55
 **/
@Service("dispatchService")
public class DispatchServiceImpl implements DispatchService {
    private static Logger logger = LoggerFactory.getLogger(DispatchServiceImpl.class);

    @Autowired
    @Qualifier("broadCastSchedulerService")
    private SchedulerService broadCastSchedulerService;
    //@Autowired
    //@Qualifier("flowSchedulerSevice")
    //private SchedulerService flowSchedulerSevice;
    @Autowired
    @Qualifier("singleSchedulerService")
    private SchedulerService singleSchedulerService;
    @Autowired
    @Qualifier("flowSchedulerService")
    private SchedulerService flowSchedulerService;

    @Override
    public void scheduler(XxlJobInfo jobInfo) {
        //1:算法任务  2.遥感数据汇集 3.CIMISS数据汇集 4.产品生产  5：代表流程任务       6：代表气象卫星预处理(暂时改为算法任务)         7：代表高分预处理流程
        if (5 == jobInfo.getTasktype() || 7 == jobInfo.getTasktype()) {
            flowSchedulerService.scheduler(jobInfo);
        } else {
            if ("SHARDING_BROADCAST".equals(jobInfo.getExecutorRouteStrategy())) {
                broadCastSchedulerService.scheduler(jobInfo);
            } else {
                singleSchedulerService.scheduler(jobInfo);
            }

        }
    }


}


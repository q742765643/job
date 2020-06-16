package com.htht.job.admin.service.impl.scheduler;

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.util.RedisUtil;
import com.htht.job.admin.dao.XxlJobBadNodeDao;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.admin.service.SchedulerUtilService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.vo.NodeMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MonitorScheduler {
    @Resource
    private CheckAliveService checkAliveService;
    @Resource
    private DubboService dubboService;
    @Autowired
    private XxlJobInfoDao xxlJobInfoDao;
    @Autowired
    private SchedulerUtilService schedulerUtilService;
    @Resource
    private XxlJobBadNodeDao xxlJobBadNodeDao;
    private static Logger logger = LoggerFactory.getLogger(MonitorScheduler.class);

    public MonitorScheduler() {
        ScheduledExecutorService cachedThreadPool = Executors.newScheduledThreadPool(3);
        cachedThreadPool.scheduleAtFixedRate(()-> {
                try {
                    List<NodeMonitor> list = dubboService.findAllMonitor();
                    if(null==list||list.isEmpty()){
                        return;
                    }
                    List<String> badList = new ArrayList<>();
                    for(NodeMonitor nodeMonitor:list){
                        badList.add(nodeMonitor.getIp());
                    }
                    List<String> adressList = checkAliveService.checkAliveByMonitors(list);
                    if(null!=adressList&&!adressList.isEmpty()){
                        badList.removeAll(adressList);
                    }
                    if(badList.isEmpty()){
                        return;
                    }
                    for(String ip : badList){
                        this.execute(ip);
                    }

                } catch (Exception e) {
                    logger.error("坏节点迁移异常",e);
                }

        }, 0, 3, TimeUnit.MINUTES);
    }
    public void  execute(String ip){
        boolean flag = RedisUtil.tryGetDistributedLock(ip + "badNodeJobQuene");
        if (!flag) {
            return;
        }
        if (xxlJobBadNodeDao.get(ip) == null) {
            xxlJobBadNodeDao.save(ip);
        }
        List<Object> jobQueue = dubboService.getBadNodeJobQueue(ip);
        if (jobQueue != null) {
            for (Object object : jobQueue) {
                // 任务迁移
                TriggerParam triggerParam = JSON.parseObject((String) object,
                        TriggerParam.class);
                XxlJobInfo jobInfo = xxlJobInfoDao.loadById(triggerParam.getJobId());
                ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(triggerParam.getAlgorId(), triggerParam.getDealAmount());
                if (addressList == null) {
                    return;
                }
                // 已执行坏节点任务移除
                ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum
                        .match(jobInfo.getExecutorRouteStrategy(), null);
                executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
                dubboService.removeBadNodeJobQueue(ip, triggerParam.getParallelLogId());
            }
            //清理redis
            dubboService.delAll(ip);

        }

    }
}

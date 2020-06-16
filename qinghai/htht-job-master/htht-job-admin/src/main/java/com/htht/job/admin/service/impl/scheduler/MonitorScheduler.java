package com.htht.job.admin.service.impl.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.htht.job.admin.service.SchedulerUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.util.RedisUtil;
import com.htht.job.admin.dao.XxlJobBadNodeDao;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.vo.NodeMonitor;

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

	{
		ScheduledExecutorService cachedThreadPool = Executors.newScheduledThreadPool(3);
		cachedThreadPool.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					List<NodeMonitor> list = dubboService.findAllMonitor();
					List<String> adressList = null;
					if (list != null && list.size() > 0) {
						adressList = checkAliveService.checkAliveByMonitors(list);
					}
					if (adressList != null && adressList.size() > 0) {
						for (NodeMonitor nodeMonitor : list) {
							String ip = nodeMonitor.getIp();
							if (!adressList.contains(ip)) {
								boolean flag = RedisUtil.tryGetDistributedLock(ip + "badNodeJobQuene");
								if (flag == true) {
									// 坏节点入库
									if (xxlJobBadNodeDao.get(ip) == null) {
										xxlJobBadNodeDao.save(ip);
									}
									System.out.println(ip + "节点宕机");
									// 获取IP节点任务清单
									List<Object> jobQueue = dubboService.getBadNodeJobQueue(ip);
									if (jobQueue != null) {
										for (Object object : jobQueue) {
											// 任务迁移
											TriggerParam triggerParam = JSON.parseObject((String) object,
													TriggerParam.class);
											XxlJobInfo jobInfo = xxlJobInfoDao.loadById(triggerParam.getJobId());
											ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService
													.findAddressList(triggerParam.getAlgorId(),triggerParam.getDealAmount());
											if (addressList == null) {
												System.out.println("无可用节点");
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
							} else {
								System.out.println(ip + "节点可用");
								// 移除复活坏节点
								xxlJobBadNodeDao.remove(ip);

							}
						}
					}
				} catch (Exception e) {
					System.out.println("无可用节点");
				}

			}
		}, 0, 3, TimeUnit.MINUTES);
	}

}

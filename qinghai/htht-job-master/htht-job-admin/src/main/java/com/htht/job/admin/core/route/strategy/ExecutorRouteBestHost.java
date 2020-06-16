package com.htht.job.admin.core.route.strategy;

import com.htht.job.admin.core.route.ExecutorRouter;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.core.trigger.XxlJobTrigger;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobRegistryDao;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.vo.NodeMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yss on 18/8/8.
 */
public class ExecutorRouteBestHost extends ExecutorRouter {
	
	private  ExecutorService executor = Executors.newFixedThreadPool(30);

	//查询各个节点CPU使用率和内存使用率
    public String route(int jobId, ArrayList<String> addressList) {
    	//查询各个节点CPU使用率和内存使用率
		List<NodeMonitor> list1=Collections.synchronizedList(new ArrayList<NodeMonitor>());
		if(list1.size()==1){
			return list1.get(0).getIp();
		}
		final  CountDownLatch latch = new CountDownLatch(addressList.size());
		for(String address:addressList){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					ExecutorBiz executorBiz = RealReference.getExecutorBiz(address);
					ReturnT<NodeMonitor> returnT=executorBiz.getSystemMessage();
					NodeMonitor nodeMonitor=returnT.getContent();
					nodeMonitor.setIp(address);
					list1.add(nodeMonitor);
					latch.countDown();
				}
			});
			
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		double min=list1.get(0).getCpuUsage()+list1.get(0).getMemoryUsage();
		NodeMonitor minMonitor = list1.get(0);
		//找出cpu和内存最小的节点
		for (NodeMonitor monitor : list1) {
			if(min>monitor.getCpuUsage()+monitor.getMemoryUsage()) {
				min=monitor.getCpuUsage()+monitor.getMemoryUsage();
				minMonitor=monitor;
			}
		}
		
        return minMonitor.getIp();
    }

    @Override
    public ReturnT<String> routeRun(TriggerParam triggerParam, ArrayList<String> addressList) {

        // address
        String address = route(triggerParam.getJobId(), addressList);

        // run executor
        ReturnT<String> runResult = XxlJobTrigger.runExecutor(triggerParam, address);
        runResult.setContent(address);
        return runResult;
    }
}

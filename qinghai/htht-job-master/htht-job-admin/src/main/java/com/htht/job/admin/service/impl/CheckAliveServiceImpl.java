package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/8/8.
 */

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.monitor.Monitor;
import com.alibaba.dubbo.monitor.MonitorFactory;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.vo.NodeMonitor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-08-08 11:53
 **/
@Service
public class CheckAliveServiceImpl implements CheckAliveService {
    private ExecutorService executor = Executors.newCachedThreadPool();

    public static boolean isAvailable(String address, MonitorFactory monitorFactory, Monitor monitor) {
        boolean flag = false;

        if (StringUtils.isEmpty(address)) {
            return flag;
        }

        try {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 60000) {
                monitor = monitorFactory.getMonitor(URL.valueOf("dubbo://" + address + "?heartbeat=1000"));
                if (null != monitor) {
                    break;
                }
            }

            if (null != monitor) {
                try {
                    flag = monitor.isAvailable();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return flag;
    }

    public List<String> checkAliveByAddressList(List<String> addressList) {

        final CountDownLatch latch = new CountDownLatch(addressList.size());
        List<String> list = Collections.synchronizedList(new ArrayList<String>());
        MonitorFactory monitorFactory = ExtensionLoader.getExtensionLoader(MonitorFactory.class).getAdaptiveExtension();

        for (String address : addressList) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    //

                    Monitor monitor = null;
                    boolean flag=false;

                    try {
                        flag = CheckAliveServiceImpl.isAvailable(address, monitorFactory, monitor);

                        // if (true == flag) {
                        // flag= RealReference.getExecutorBiz(address,1000);
                        // }
                    } catch (Exception e) {
                    } finally {
                        if (true == flag){
                            list.add(address);
                        }
                        latch.countDown();

                    }
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;


    }





    public List<String> checkAliveByMonitors(List<NodeMonitor> monitors) {
        final CountDownLatch latch = new CountDownLatch(monitors.size());
        List<String> list = Collections.synchronizedList(new ArrayList<String>());
        MonitorFactory monitorFactory = ExtensionLoader.getExtensionLoader(MonitorFactory.class).getAdaptiveExtension();


        for (NodeMonitor nodeMonitor : monitors) {
            executor.execute(new Runnable() {
                @Override
                public void run() {

                    //
                    Monitor monitor = null;
                    //Exporter<MonitorService> exporter=null;
                    String address = nodeMonitor.getIp();
                    boolean flag=false;
                    try {

                        flag = CheckAliveServiceImpl.isAvailable(address, monitorFactory, monitor);

                    } catch (Exception e) {
                    } finally {
                        if (true == flag){
                            list.add(address);
                        }
                        if(null!=monitor){
                            monitor.destroy();
                        }
                        latch.countDown();

                    }
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;


    }
}


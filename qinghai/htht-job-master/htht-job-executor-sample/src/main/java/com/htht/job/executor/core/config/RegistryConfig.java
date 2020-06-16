package com.htht.job.executor.core.config;

import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.util.IpUtil;
import com.htht.job.executor.activemq.ReceiveJMSThread;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.util.DubboIpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created by zzj on 2018/1/30.
 */
@Component
public class RegistryConfig  implements ApplicationRunner {
    @Autowired
    private RegistryService registryService;
    @Autowired
    private ReceiveJMSThread receiveJMSThread;
    @Autowired
    private RedisService redisService;
    @Value("${cluster.job.executor.appname}")
    private String appname;

    @Value("${cluster.concurrency}")
    int concurrency;



    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        String ip= DubboIpUtil.getIp();
        Registry registry=new Registry();
        registry.setRegistryKey(appname);
        registry.setRegistryIp(ip);
        registry.setConcurrency(concurrency);
        registry.setDeploySystem(DubboIpUtil.getOsName());
        redisService.set(ip+MonitorQueue.NODE_DEAL_QUEUE,0);
        registryService.saveOrUpdate(registry);
        receiveJMSThread.start();

    }
}

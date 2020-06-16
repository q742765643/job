package com.htht.job.executor.core.config;

import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.util.DubboIpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RedisInitConfig implements ApplicationRunner {
    @Autowired
    private RedisService redisService;

    @Override
    public void run(ApplicationArguments applicationarguments) throws Exception {

        String ip = DubboIpUtil.getIp();
        redisService.fuzzyRemove(ip);
    }
}



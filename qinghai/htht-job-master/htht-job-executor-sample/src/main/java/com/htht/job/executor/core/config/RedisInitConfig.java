package com.htht.job.executor.core.config;

import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.util.DubboIpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RedisInitConfig implements ApplicationRunner{
	@Autowired
	private RedisService redisService;

	@Override
	public void run(ApplicationArguments applicationarguments) throws Exception {
		//项目启动删除单机串行键值
		//redisService.fuzzyRemove(MonitorQueue.NODE_SERIAL_QUEUE+"");
		//项目启动删除节点排队任务
		//redisService.fuzzyRemove(MonitorQueue.NODE_LINE_QUEUE+"");
		//项目启动删除节点运行任务
		//redisService.fuzzyRemove(MonitorQueue.NODE_OPERATION_QUEUE+"");
		String ip= DubboIpUtil.getIp();
		redisService.fuzzyRemove(ip);
	}
}



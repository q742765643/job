package com.htht.job.executor.activemq;


import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JMSConsumer {
    private final static Logger logger = LoggerFactory.getLogger(JMSConsumer.class);
    @Autowired
    private JMSProducer jmsProducer;
    @Autowired
    private RedisService redisService;
    //@JmsListener(destination = Queue.destination, concurrency = Queue.concurrency)
    public void receiveQueue(TriggerParam triggerParam) throws InterruptedException {
        

    }
}

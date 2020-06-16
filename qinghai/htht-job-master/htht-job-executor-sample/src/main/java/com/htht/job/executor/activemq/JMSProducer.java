package com.htht.job.executor.activemq;


import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.util.DubboIpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JMSProducer {
    /*@Autowired
    private JmsTemplate jmsTemplate;*/
    @Autowired
    private RedisService redisService;
    @Autowired
    private RegistryService registryService;
    @Autowired
    private DubboService dubboService;
    private static Logger logger = LoggerFactory.getLogger(JMSProducer.class);

    public  ReturnT<String>  sendMessage( TriggerParam triggerParam) {
        try {
            String ip= DubboIpUtil.getIp();
            ParallelLog parallelLog = dubboService.findParallelLogById(triggerParam.getParallelLogId());
            parallelLog.setIp(ip);
            dubboService.saveParallelLog(parallelLog);
            //存储节点正在排队的job
            //  redisService.lPush(MonitorQueue.NODE_LINE_QUEUE+key, triggerParam.getJobId());
            // Destination destination = new ActiveMQQueue(ip);
            //jmsTemplate.setPriority(triggerParam.getPriority());
            redisService.zAdd(ip, triggerParam,triggerParam.getPriority()+1);
            //this.jmsTemplate.convertAndSend(destination,triggerParam);
            /*Connection connection = jmsTemplate.getConnectionFactory().createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = new ActiveMQQueue(ip);
            MessageProducer producer = session.createProducer(dest);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setPriority(triggerParam.getPriority());;
            Message message = session.createObjectMessage(triggerParam);
            producer.send(message);
            connection.close();*/
        } catch (Exception e) {
            logger.error("发送异常",e);
            return  ReturnT.FAIL;
        }
        return  ReturnT.SUCCESS;
    }

}
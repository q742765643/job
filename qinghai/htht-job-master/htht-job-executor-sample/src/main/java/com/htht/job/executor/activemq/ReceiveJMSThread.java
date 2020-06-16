package com.htht.job.executor.activemq;/**
 * Created by zzj on 2018/6/8.
 */

import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.util.IpUtil;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.util.DubboIpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: htht-job
 * @description:
 * @author: zzj
 * @create: 2018-06-08 11:01
 **/
@Service
public class ReceiveJMSThread {
    private volatile boolean toStop = false;
    private volatile boolean isRun = false;
    private Thread jmsThread;
  /*  @Autowired
    private JmsTemplate jmsTemplate;*/
    @Autowired
    private RedisService redisService;
    @Autowired
    private JMSProducer jmsProducer;
    @Autowired
    private ConsumerThread consumerThread;
    @Autowired
    private RegistryService registryService;

    private ExecutorService singleThreadExecutor=Executors.newSingleThreadExecutor();
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private static Logger logger = LoggerFactory.getLogger(ReceiveJMSThread.class);

    public void start() {
        if(!isRun) {
            singleThreadExecutor.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            isRun=true;
                            while (!toStop) {
                                receiveQueue();
                            }
                            toStop=false;
                            isRun=false;
                        }
                    });
        }
    }

    public void receiveQueue(){
        try {
            String ip= DubboIpUtil.getIp();
            Object obj=  redisService.zRevRangeByScore(ip);
            if(null != obj){
                /**===========1.获取mq接受对象=========*/

                TriggerParam triggerParam= (TriggerParam) obj;
                int dealAmount=triggerParam.getDealAmount();
                int runSl=0;
                boolean isEx=  redisService.exists(ip+MonitorQueue.NODE_DEAL_QUEUE);
                if(isEx){
                    runSl= (int) redisService.get(ip+MonitorQueue.NODE_DEAL_QUEUE);
                }
                Registry registry=registryService.findByRegistryIp(ip);

                if(registry.getConcurrency()-runSl>=dealAmount){
                    redisService.zrem(ip,triggerParam);
                    //removeMessage(message);
                   // message.acknowledge();
                    redisService.dealSl(ip+MonitorQueue.NODE_DEAL_QUEUE,dealAmount);
                    //jmsProducer.sendMessage_run(triggerParam);
                    cachedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            consumerThread.onMessage(triggerParam);
                        }
                    });

                }else {
                    toStop=true;
                }

            }else{
                toStop=true;
            }

            //session.close();
           // connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public void removeMessage(ObjectMessage message){
        try {
            message.acknowledge();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }*/

    public void toStop() {
        toStop = true;
        // interrupt and wait
        jmsThread.interrupt();
        try {
            jmsThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}


/*
package com.htht.job.executor.activemq;*/
/**
 * Created by zzj on 2018/6/6.
 *//*


import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.executor.XxlJobExecutor;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.thread.TriggerCallbackThread;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.util.DateUtil;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.*;
import java.util.Iterator;
import java.util.Set;

*/
/**
 * @program: htht-job
 * @description: 主动消费Mq
 * @author: zzj
 * @create: 2018-06-06 14:53
 **//*

public class ReceiveJMS11 {



    public static void main(String[] args) {
        try {
            ActiveMQConnectionFactory factory =  new org.apache.activemq.ActiveMQConnectionFactory("admin",
                    "admin",
                    "tcp://10.37.129.4:61616");
            factory.setTrustAllPackages(true);
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, 4);
            Destination destination = session.createQueue("127.0.0.1_wait");
            MessageConsumer consumer = session.createConsumer(destination);
            System.out.println("收到消息：  ");
                ObjectMessage message = (ObjectMessage) consumer.receive();
                TriggerParam triggerParam= (TriggerParam) message.getObject();
                System.out.println("收到消息：  " + JSON.toJSONString(triggerParam) );


            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}


*/

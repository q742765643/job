/*
package com.htht.job.executor.activemq;*/
/**
 * Created by zzj on 2018/6/6.
 *//*


import com.alibaba.druid.util.StringUtils;
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
import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import org.apache.activemq.ActiveMQConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

*/
/**
 * @program: htht-job
 * @description: 主动消费Mq
 * @author: zzj
 * @create: 2018-06-06 14:53
 **//*

@Service
public class ReceiveJMS {

    @Value("${xxl.job.executor.ip}")
    private String ip;
    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    @Value("${spring.activemq.user}")
    private String user;
    @Value("${spring.activemq.password}")
    private String password;

    public void receiveQueue(){
        TriggerParam triggerParam = null;
        ReturnT<String> executeResult = null;
        try {
            ConnectionFactory factory =  new org.apache.activemq.ActiveMQConnectionFactory(user,password, brokerUrl);
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, 4);
            Destination destination = session.createQueue(ip+"_wait");
            MessageConsumer consumer = session.createConsumer(destination);
            TextMessage message = (TextMessage) consumer.receive();
            if(null != message){
                */
/**===========1.获取mq接受对象=========*//*

                //triggerParam=(TriggerParam)((ObjectMessage) message).getObject();
                //int dealAmount=triggerParam.getDealAmount();
                System.out.println(message.getText());


            }
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeMessage(ObjectMessage message){
        try {
            message.acknowledge();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}



*/

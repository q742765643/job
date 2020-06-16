/*
package com.htht.job.executor.activemq;

import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
public class ActiveMqConfig {
	public CachingConnectionFactory connectionFactory(ActiveMQConnectionFactory connectionFactory) {
		CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
		return factory;
	}
    @Value("${xxl.job.executor.ip}")
    private String ip;
    @Value("${mqConcurrency}")
    int mqConcurrency;
	@Bean
    public DefaultMessageListenerContainer listenerContainer(ActiveMQConnectionFactory connectionFactory){  
        DefaultMessageListenerContainer m =new DefaultMessageListenerContainer();  
        m.setConnectionFactory(connectionFactory(connectionFactory));  
        Destination d = new ActiveMQQueue(ip);
        m.setDestination(d); 
        //m.setConcurrency(mqConcurrency);
        m.setConcurrentConsumers(100);
        m.setMessageListener(new QueueMessageListener());  
        return m;         
    }  
}
*/

package com.xxl.job.executor.test;

import com.htht.job.executor.Application;
import com.htht.job.executor.activemq.JMSProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)  
@SpringBootTest(classes = Application.class)
public class JmsTest{
    @Autowired
    private JMSProducer jmsProducer;

    @Test
    public void testJms() {

        for (int i=0;i<10;i++) {
        }
    }
}
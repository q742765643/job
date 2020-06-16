package com.xxl.job.executor.test;

import com.htht.job.executor.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)  
@SpringBootTest(classes = Application.class)
public class JmsTest{

    @Test
    public void testJms() {

        for (int i=0;i<10;i++) {
        }
    }
}
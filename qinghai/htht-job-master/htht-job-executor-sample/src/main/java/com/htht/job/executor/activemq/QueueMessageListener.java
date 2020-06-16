/*
package com.htht.job.executor.activemq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.executor.service.registry.RegistryService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.executor.XxlJobExecutor;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.thread.TriggerCallbackThread;
import com.htht.job.core.util.ScriptUtil;
import com.htht.job.executor.model.parameter.CommonParameter;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.util.DateUtil;
import com.htht.job.executor.util.SpringContextUtil;

@Component
public class QueueMessageListener implements MessageListener {
	public void onMessage(Message message) {  
		TriggerParam triggerParam = null;
        ReturnT<String> executeResult = null;
        RedisService redisService = (RedisService) SpringContextUtil.getBean("redisService");
        RegistryService registryService = (RegistryService) SpringContextUtil.getBean("registryService");
		ReceiveJMSThread receiveJMSThread = (ReceiveJMSThread) SpringContextUtil.getBean("receiveJMSThread");

		Registry registry=registryService.findByMqDestination(registryService.getMqDestination());
        String key=registry.getRegistryKey()+registry.getRegistryIp()+registry.getPort();
		try {
			*/
/**===========1.获取mq接受对象=========*//*

			triggerParam = (TriggerParam)((ObjectMessage) message).getObject();
			//移出节点排队job
			Set<String> fuzzyQuery = redisService.fuzzyQuery(MonitorQueue.NODE_LINE_QUEUE+key);
			if(fuzzyQuery.size()>0){
				Iterator<String> iterator = fuzzyQuery.iterator();
					redisService.remove(iterator.next());
			}
			//存储正在执行的job
            redisService.set(MonitorQueue.NODE_OPERATION_QUEUE+key+DateUtil.getCurrentDateToMillisecond(), triggerParam.getJobId(),36000000L);
            //存储调度任务的job
            redisService.set(MonitorQueue.JOB_OPERATION_QUEUE+key+DateUtil.getCurrentDateToMillisecond(), triggerParam.getJobId(),36000000L);
			
			*/
/**===========2.获取执行器=========*//*

        	String handler=triggerParam.getExecutorHandler();
        	IJobHandler newJobHandler = XxlJobExecutor.loadJobHandler(handler);
        	*/
/**===========3执行器执行业务代码=========*//*

        	executeResult=newJobHandler.execute(triggerParam);
        	
			*/
/**===========4.回调=========*//*

            TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(),executeResult,triggerParam.getLogFileName(),triggerParam.getParallelLogId(),triggerParam.getOutput(),triggerParam.isFlow()));
			Thread.sleep(1000);
		} catch (Exception e) {
			ReturnT<String> stopResult=null;
			if(!StringUtils.isEmpty(executeResult.getMsg())){
				 stopResult = new ReturnT<String>(ReturnT.FAIL_CODE,   executeResult.toString());
			}else{
				 stopResult = new ReturnT<String>(ReturnT.FAIL_CODE,"mq消费异常");
			}
            TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(),stopResult,triggerParam.getLogFileName(),triggerParam.getParallelLogId(),triggerParam.getOutput(),triggerParam.isFlow()));
             throw new RuntimeException();
		}finally {
			synchronized(this){
			//移出节点正在执行的job
			Set<String> fuzzyQuery = redisService.fuzzyQuery(MonitorQueue.NODE_OPERATION_QUEUE+key);
			if(fuzzyQuery.size()>0){
				Iterator<String> iterator = fuzzyQuery.iterator();
					redisService.remove(iterator.next());
			}
			//移出调度任务队列的job
			Set<String> jobOperationQueues = redisService.fuzzyQuery(MonitorQueue.JOB_OPERATION_QUEUE+key);
			if(jobOperationQueues.size()>0){
				Iterator<String> iterator = jobOperationQueues.iterator();
					redisService.remove(iterator.next());
			}
			//执行完成,参数串行队列任务标识
			String simpleKey = MonitorQueue.NODE_SERIAL_QUEUE+triggerParam.getExecuteIp()+triggerParam.getAlgorId();
			if(redisService.exists(simpleKey)){
				redisService.remove(simpleKey);
			}
				redisService.dealSl(MonitorQueue.NODE_DEAL_QUEUE+registry.getRegistryIp(),-10);
				receiveJMSThread.start();

			}
		}
	}
  
}  */

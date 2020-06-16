package com.htht.job.executor.hander.mqhandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.executor.activemq.JMSProducer;
import com.htht.job.executor.activemq.ReceiveJMSThread;
import com.htht.job.executor.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHandler(value="mqHandler")
@Service
public class MqHandler extends IJobHandler{

	    @Autowired
	    private JMSProducer jmsProducer;
	    @Autowired
	    private RedisService redisService;
	    @Autowired
		private ReceiveJMSThread receiveJMSThread;
	    private static Logger logger = LoggerFactory.getLogger(MqHandler.class);

		@Override
		public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
			ReturnT<String> executeResult = null;
			try {
				executeResult=jmsProducer.sendMessage(triggerParam);
				receiveJMSThread.start();
				
			} catch (Exception e) {
				logger.error(e.getMessage());
				return ReturnT.FAIL;			
			} 
			return executeResult;

			
		}
}

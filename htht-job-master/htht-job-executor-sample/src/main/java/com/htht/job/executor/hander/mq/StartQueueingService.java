package com.htht.job.executor.hander.mq;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.thread.TriggerCallbackThread;
import com.htht.job.executor.activemq.ReceiveJMSThread;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.util.DubboIpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class StartQueueingService {
    @Autowired
    private static Logger logger = LoggerFactory.getLogger(StartQueueingService.class);


    @Autowired
    private ReceiveJMSThread receiveJMSThread;
    @Autowired
    private DubboService dubboService;
    @Autowired
    private RedisService redisService;


    private ReturnT<String> execute(TriggerParam triggerParam) {
        try {
                String ip = DubboIpUtil.getIp();
                ParallelLogDTO parallelLogDTO = dubboService.findParallelLogById(triggerParam.getParallelLogId());
                parallelLogDTO.setIp(ip);
                dubboService.saveParallelLog(parallelLogDTO);
                double scope= Double.valueOf(triggerParam.getPriority())+1;
                redisService.zAdd(ip, triggerParam, scope);
                receiveJMSThread.start();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;


    }

    public ReturnT<String> run(TriggerParam triggerParam) {
        ReturnT<String> executeResult = null;
        try {
            if (triggerParam != null) {
                //如果是单机串行,进行单机串行拦截
                //汇总任务清单
                String ip = dubboService.getIp();
                dubboService.hmPut(ip + MonitorQueue.BAD_NODE_QUEUE, triggerParam.getParallelLogId(), JSON.toJSONString(triggerParam));
                dubboService.setRedisSimple(ip + MonitorQueue.NODE_LINE_QUEUE + triggerParam.getParallelLogId(), String.valueOf(triggerParam.getJobId()));
                if ("SERIAL_EXECUTION".equals(triggerParam.getExecutorBlockStrategy())) {
                    synchronized (this) {
                        String simpleKey = ip + MonitorQueue.NODE_SERIAL_QUEUE + triggerParam.getAlgorId();
                        boolean existSimpleKey = dubboService.existSimpleKey(simpleKey);
                        if (existSimpleKey) {
                            dubboService.lPush(ip + MonitorQueue.NODE_SERIAL_QUEUE_LIST + triggerParam.getAlgorId(), JSON.toJSONString(triggerParam));
                            return ReturnT.SUCCESS;
                        } else {
                            //不存在,则该节点未执行该jobid任务
                            String unsimpleKey = ip + MonitorQueue.NODE_SERIAL_QUEUE + triggerParam.getAlgorId();
                            dubboService.setRedisSimple(unsimpleKey, triggerParam.getAlgorId());
                        }
                    }
                }
                executeResult=this.execute(triggerParam);
                if (executeResult == null) {
                    executeResult = ReturnT.FAIL;
                }
                /**======mq发送失败回调========**/
                if (500 == executeResult.getCode()) {
                    ReturnT<String> stopResult = new ReturnT<>(ReturnT.FAIL_CODE, " [调度失败]");
                    TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(), triggerParam.getOutput()));

                }
            }
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            String errorMsg = stringWriter.toString();
            executeResult = new ReturnT<>(ReturnT.FAIL_CODE, errorMsg);
        }
        return executeResult;
    }



}

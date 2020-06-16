package com.htht.job.executor.activemq;/**
 * Created by zzj on 2018/6/11.
 */

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.executor.XxlJobExecutor;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.thread.TriggerCallbackThread;
import com.htht.job.executor.hander.mq.StartQueueingService;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.util.DubboIpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;

/**
 * @program: htht-job
 * @description: 消费端
 * @author: zzj
 * @create: 2018-06-11 10:16
 **/
@Service
public class ConsumerThread {
    private static Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private RegistryService registryService;
    @Autowired
    private ReceiveJMSThread receiveJMSThread;
    @Value("${cluster.job.executor.logpath}")
    private String logpath;
    @Autowired
    private StartQueueingService startQueueingService;

    public void onMessage(TriggerParam triggerParam) {

        String ip = DubboIpUtil.getIp();
        ReturnT<String> executeResult = null;
        try{
            /**===========1.获取mq接受对象=========*/
            //移出节点排队job
            Set<String> fuzzyQuery = redisService.fuzzyQuery(ip + MonitorQueue.NODE_LINE_QUEUE + triggerParam.getParallelLogId());
            if (null!=fuzzyQuery&&!fuzzyQuery.isEmpty()) {
                Iterator<String> iterator = fuzzyQuery.iterator();
                redisService.remove(iterator.next());
            }
            //存储正在执行的job
            redisService.set(ip + MonitorQueue.NODE_OPERATION_QUEUE + triggerParam.getParallelLogId(), triggerParam.getJobId(), 36000000L);
            //存储调度任务的job
            redisService.set(ip + MonitorQueue.JOB_OPERATION_QUEUE + triggerParam.getParallelLogId(), triggerParam.getJobId(), 36000000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowFormat = sdf.format(triggerParam.getLogDateTim());
            String outputLog = logpath + nowFormat + "/" + triggerParam.getParallelLogId() + ".log";
            XxlJobFileAppender.makeLogFileNameByPath(outputLog);
            triggerParam.setLogFileName(outputLog);
            /**===========2.获取执行器=========*/
            String handler = triggerParam.getExecutorHandler();
            IJobHandler newJobHandler = XxlJobExecutor.loadJobHandler(handler);
            if(null==newJobHandler){
                ReturnT<String> stopResult = new ReturnT<>(ReturnT.FAIL_CODE, "handler不存在");
                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(), triggerParam.getOutput()));
                return;
            }
            /**===========3执行器执行业务代码=========*/
            executeResult = newJobHandler.execute(triggerParam);
            /**===========4.回调=========*/
            TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), executeResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(), triggerParam.getOutput()));

        } catch (Exception e) {
            ReturnT<String> stopResult = null;
            if (null != executeResult && !StringUtils.isEmpty(executeResult.getMsg())) {
                stopResult = new ReturnT<>(ReturnT.FAIL_CODE, executeResult.toString());
            } else {
                try (StringWriter stringWriter = new StringWriter()){
                    e.printStackTrace(new PrintWriter(stringWriter));
                    String errorMsg = stringWriter.toString();
                    stopResult = new ReturnT<>(ReturnT.FAIL_CODE, errorMsg);
                } catch (Exception e1) {
                    logger.error(e1.getMessage(),e1);
                }

            }
            TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(), triggerParam.getOutput()));

        } finally {
            synchronized (this) {
                deleteRedisKey(ip,triggerParam);
            }
        }
    }

    private void deleteRedisKey(String ip,TriggerParam triggerParam){
        String operationKey = ip + MonitorQueue.NODE_OPERATION_QUEUE + triggerParam.getParallelLogId();
        if (redisService.exists(operationKey)) {
            redisService.remove(operationKey);
        }
        //移出调度任务队列的job
        String jobOperationQueues = ip + MonitorQueue.JOB_OPERATION_QUEUE + triggerParam.getParallelLogId();
        if (redisService.exists(jobOperationQueues)) {
            redisService.remove(jobOperationQueues);
        }
        //执行完成,参数串行队列任务标识
        String simpleKey = ip + MonitorQueue.NODE_SERIAL_QUEUE + triggerParam.getAlgorId();
        if (redisService.exists(simpleKey)) {
            redisService.remove(simpleKey);
        }
        //任务清单中删除已经执行的任务
        redisService.hmDel(ip + MonitorQueue.BAD_NODE_QUEUE, triggerParam.getParallelLogId());
        redisService.dealSl(ip + MonitorQueue.NODE_DEAL_QUEUE, -triggerParam.getDealAmount());
        receiveJMSThread.start();
        if ("SERIAL_EXECUTION".equals(triggerParam.getExecutorBlockStrategy())) {
            String value = redisService.rpop(ip + MonitorQueue.NODE_SERIAL_QUEUE_LIST + triggerParam.getAlgorId());
            if (null!=value&&!StringUtils.isEmpty(value)) {
                TriggerParam newTriggerParam = JSON.parseObject(value, TriggerParam.class);
                startQueueingService.run(newTriggerParam);
            }
        }
    }
}


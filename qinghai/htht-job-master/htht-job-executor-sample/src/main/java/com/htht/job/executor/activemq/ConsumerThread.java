package com.htht.job.executor.activemq;/**
 * Created by zzj on 2018/6/11.
 */

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.executor.XxlJobExecutor;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.thread.TriggerCallbackThread;
import com.htht.job.core.util.IpUtil;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.ExecutorBizImpl;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.util.DateUtil;
import com.htht.job.executor.util.DubboIpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    @Autowired
    private RedisService redisService;
    @Autowired
    private RegistryService registryService;
    @Autowired
    private ReceiveJMSThread receiveJMSThread;
    @Value("${cluster.job.executor.logpath}")
    private String logpath;


    public void onMessage(TriggerParam triggerParam) {

        String ip = DubboIpUtil.getIp();
        ReturnT<String> executeResult = null;
        Registry registry = registryService.findByRegistryIp(ip);
        String key = registry.getRegistryIp();
        try {
            /**===========1.获取mq接受对象=========*/
            //移出节点排队job
            Set<String> fuzzyQuery = redisService.fuzzyQuery(key+MonitorQueue.NODE_LINE_QUEUE+triggerParam.getParallelLogId() );
            if (fuzzyQuery.size() > 0) {
                Iterator<String> iterator = fuzzyQuery.iterator();
                redisService.remove(iterator.next());
            }
            //存储正在执行的job
            redisService.set(key +MonitorQueue.NODE_OPERATION_QUEUE +  triggerParam.getParallelLogId(), triggerParam.getJobId(), 36000000L);
            //存储调度任务的job
            redisService.set(key + MonitorQueue.JOB_OPERATION_QUEUE +  triggerParam.getParallelLogId(), triggerParam.getJobId(), 36000000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowFormat = sdf.format(triggerParam.getLogDateTim());
            String outputLog = logpath + nowFormat + "/" + triggerParam.getParallelLogId() + ".log";
            XxlJobFileAppender.makeLogFileNameByPath(outputLog);
            triggerParam.setLogFileName(outputLog);
            /**===========2.获取执行器=========*/
            String handler = triggerParam.getExecutorHandler();
            IJobHandler newJobHandler = XxlJobExecutor.loadJobHandler(handler);
            /**===========3执行器执行业务代码=========*/
            executeResult = newJobHandler.execute(triggerParam);
          /*  if(null!=triggerParam.getOutput()&&triggerParam.getOutput().size()>0){
                redisService.setList(triggerParam.getLogId()+"_output"+triggerParam.getParallelLogId(), triggerParam.getOutput());
            }*/
            /**===========4.回调=========*/
            TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), executeResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(), triggerParam.getOutput()));

        } catch (Exception e) {
            e.printStackTrace();
            ReturnT<String> stopResult = null;
            if (null != executeResult && !StringUtils.isEmpty(executeResult.getMsg())) {
                stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, executeResult.toString());
            } else {
                stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, e.getStackTrace().toString());
            }
            TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(), triggerParam.getOutput()));
        } finally {
            synchronized (this) {
                //移出节点正在执行的job
                Set<String> fuzzyQuery = redisService.fuzzyQuery(key+MonitorQueue.NODE_OPERATION_QUEUE +triggerParam.getParallelLogId());
                if (fuzzyQuery.size() > 0) {
                    Iterator<String> iterator = fuzzyQuery.iterator();
                    redisService.remove(iterator.next());
                }
                //移出调度任务队列的job
                Set<String> jobOperationQueues = redisService.fuzzyQuery(key+MonitorQueue.JOB_OPERATION_QUEUE +triggerParam.getParallelLogId());
                if (jobOperationQueues.size() > 0) {
                    Iterator<String> iterator = jobOperationQueues.iterator();
                    redisService.remove(iterator.next());
                }
                //执行完成,参数串行队列任务标识
                String simpleKey = triggerParam.getExecuteIp() +MonitorQueue.NODE_SERIAL_QUEUE +  triggerParam.getAlgorId();
                if (redisService.exists(simpleKey)) {
                    redisService.remove(simpleKey);
                }
                //任务清单中删除已经执行的任务
                redisService.hmDel(triggerParam.getExecuteIp() + MonitorQueue.BAD_NODE_QUEUE, triggerParam.getParallelLogId());
                redisService.dealSl( registry.getRegistryIp()+MonitorQueue.NODE_DEAL_QUEUE, -triggerParam.getDealAmount());
                receiveJMSThread.start();
                if ("SERIAL_EXECUTION".equals(triggerParam.getExecutorBlockStrategy())) {
                    String value = redisService.rpop(triggerParam.getExecuteIp() +MonitorQueue.NODE_SERIAL_QUEUE_LIST +  triggerParam.getAlgorId());
                    if (!StringUtils.isEmpty(value)) {
                        TriggerParam newTriggerParam = JSON.parseObject(value, TriggerParam.class);
                        ExecutorBiz executorBiz = new ExecutorBizImpl();
                        executorBiz.run(newTriggerParam);
                    }
                }

            }
        }
    }
}


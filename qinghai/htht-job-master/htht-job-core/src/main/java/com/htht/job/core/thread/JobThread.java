package com.htht.job.core.thread;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.executor.XxlJobExecutor;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.util.ConcurrentHashSet;
import com.htht.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * handler thread
 *
 * @author xuxueli 2016-1-16 19:52:47
 */

public class JobThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(JobThread.class);


    private int jobId;
    private IJobHandler handler;
    private DubboService dubboService;
    private LinkedBlockingQueue<TriggerParam> triggerQueue;
    private ConcurrentHashSet<Integer> triggerLogIdSet;        // avoid repeat trigger for the same TRIGGER_LOG_ID

    private boolean toStop = false;
    private String stopReason;

    private boolean running = false;    // if running job
    private int idleTimes = 0;            // idel times


    public JobThread(int jobId, IJobHandler handler, DubboService dubboService) {
        this.dubboService = dubboService;
        this.jobId = jobId;
        this.handler = handler;
        this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
        this.triggerLogIdSet = new ConcurrentHashSet<Integer>();
    }

    public IJobHandler getHandler() {
        return handler;
    }

    /**
     * new trigger to queue
     *
     * @param triggerParam
     * @return
     */
    public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
        if (StringUtils.isEmpty(triggerParam.getParallelLogId())) {
            // avoid repeat
            if (triggerLogIdSet.contains(triggerParam.getLogId())) {
                logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
                return new ReturnT<String>(ReturnT.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
            }
        }
        triggerLogIdSet.add(triggerParam.getLogId());
        triggerQueue.add(triggerParam);
        return ReturnT.SUCCESS;
    }

    /**
     * kill job thread
     *
     * @param stopReason
     */
    public void toStop(String stopReason) {
        /**
         * Thread.interrupt只支持终止线程的阻塞状态(wait、join、sleep)，
         * 在阻塞出抛出InterruptedException异常,但是并不会终止运行的线程本身；
         * 所以需要注意，此处彻底销毁本线程，需要通过共享变量方式；
         */
        this.toStop = true;
        this.stopReason = stopReason;
    }

    /**
     * is running job
     *
     * @return
     */
    public boolean isRunningOrHasQueue() {
        return running || triggerQueue.size() > 0;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
    @Override
    public void run() {
    	List<String> keyList = new ArrayList<>();
        while (!toStop) {
            running = false;
            idleTimes++;

            TriggerParam triggerParam = null;
            ReturnT<String> executeResult = null;
            try {
                //SampleInterface
                // to check toStop signal, we need cycle, so wo cannot use queue.take(), instand of poll(timeout)
                triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
                if (triggerParam != null) {
                    running = true;
                    idleTimes = 0;
                    triggerLogIdSet.remove(triggerParam.getLogId());
                    keyList.add(JSON.toJSONString(triggerParam));
                    //如果是单机串行,进行单机串行拦截
                    //汇总任务清单
                    String ip= dubboService.getIp();
                    dubboService.hmPut(ip+MonitorQueue.BAD_NODE_QUEUE, triggerParam.getParallelLogId(), JSON.toJSONString(triggerParam));
                    dubboService.setRedisSimple(ip+MonitorQueue.NODE_LINE_QUEUE+triggerParam.getParallelLogId() , String.valueOf(triggerParam.getJobId()));
                    if ("SERIAL_EXECUTION".equals(triggerParam.getExecutorBlockStrategy())) {
                        synchronized (this) {
                            String simpleKey =ip + MonitorQueue.NODE_SERIAL_QUEUE + triggerParam.getAlgorId();
                            boolean existSimpleKey = dubboService.existSimpleKey(simpleKey);
                            if (existSimpleKey) {
                                dubboService.lPush(ip +MonitorQueue.NODE_SERIAL_QUEUE_LIST + triggerParam.getAlgorId(), JSON.toJSONString(triggerParam));
                                //pushTriggerQueue(triggerParam);
                                //triggerQueue.add(triggerParam);
                                continue;
                            } else {
                                //不存在,则该节点未执行该jobid任务
                                String unsimpleKey = ip +MonitorQueue.NODE_SERIAL_QUEUE +  triggerParam.getAlgorId();
                                boolean success = dubboService.setRedisSimple(unsimpleKey, triggerParam.getAlgorId());
                            }
                        }
                    }

                    executeResult = handler.execute(triggerParam);
                    if (executeResult == null) {
                        executeResult = ReturnT.FAIL;
                    }
                    /**======mq发送失败回调========**/
                    if (500 == executeResult.getCode()) {
                        ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, " [mq发送失败]");
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(),triggerParam.getOutput()));

                    }
                } else {
                    if (idleTimes > 30) {
                        XxlJobExecutor.removeJobThread(jobId, "excutor idel times over limit.");
                    }
                }
            } catch (Throwable e) {
                if (toStop) {
                }

                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                String errorMsg = stringWriter.toString();
                executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, errorMsg);

            } finally {
                if (triggerParam != null) {
                    // callback handler info
                    if (!toStop) {
                        // commonm
                        // TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), executeResult,logFileName));
                    } else {
                        // is killed
                        ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [业务运行中，被强制终止]");
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(),triggerParam.getOutput()));
                    }
                }
            }
        }

        // callback trigger request in queue
        while (triggerQueue != null && triggerQueue.size() > 0) {
            TriggerParam triggerParam = triggerQueue.poll();
            if (triggerParam != null) {
                // is killed
                ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [任务尚未执行，在调度队列中被终止]");
                TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), stopResult, triggerParam.getLogFileName(), triggerParam.getParallelLogId(), triggerParam.isFlow(),triggerParam.getOutput()));
            }
        }

        logger.info(">>>>>>>>>>> xxl-job JobThread stoped, hashCode:{}", Thread.currentThread());
    }
}

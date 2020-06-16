package com.htht.job.core.thread;

import com.htht.job.core.api.DubboCallBackService;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xuxueli on 16/7/22.
 */
public class TriggerCallbackThread {
    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static TriggerCallbackThread instance = new TriggerCallbackThread();
    /**
     * job results callback queue
     */
    private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<>();
    /**
     * callback thread
     */
    private Thread thread;
    private volatile boolean toStop = false;

    public static TriggerCallbackThread getInstance() {
        return instance;
    }

    public static void pushCallBack(HandleCallbackParam callback) {
        getInstance().callBackQueue.add(callback);
        logger.debug(">>>>>>>>>>> xxl-job, push callback request, logId:{}", callback.getLogId());
    }

    public void start() {

        // valid

        thread = new Thread(() -> {

            // normal callback
            while (!toStop) {
                addCallbackParamList();
            }

            // last callback
            try {
                List<HandleCallbackParam> callbackParamList = new ArrayList<>();
                getInstance().callBackQueue.drainTo(callbackParamList);
                if (callbackParamList != null && !callbackParamList.isEmpty()) {
                    doCallback(callbackParamList);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            logger.info(">>>>>>>>>>> xxl-job, executor callback thread destory.");

        }
        );
        thread.setDaemon(true);
        thread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        thread.interrupt();
        try {
            thread.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    private void addCallbackParamList(){
        try {
            HandleCallbackParam callback = getInstance().callBackQueue.take();
            if (callback != null) {

                // callback list param
                List<HandleCallbackParam> callbackParamList = new ArrayList<>();
                getInstance().callBackQueue.drainTo(callbackParamList);
                callbackParamList.add(callback);

                // callback, will retry if error
                if (!callbackParamList.isEmpty()) {
                    doCallback(callbackParamList);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    /**
     * do callback, will retry if error
     *
     * @param callbackParamList
     */
    private void doCallback(List<HandleCallbackParam> callbackParamList) {
        // callback, will retry if error
        DubboCallBackService dubboCallBackService = (DubboCallBackService) SpringContextUtil.getBean("dubboCallBackService");
        try {
            ReturnT<String> callbackResult = dubboCallBackService.callback(callbackParamList);
            if (callbackResult != null && ReturnT.SUCCESS_CODE == callbackResult.getCode()) {
                callbackLog(callbackParamList, "<br>----------- htht-job  callback success");
            } else {
                callbackLog(callbackParamList, "<br>----------- htht-job  callback fail, callbackResult:" + callbackResult);
            }
        } catch (Exception e) {
            callbackLog(callbackParamList, "<br>----------- htht-job callback error, errorMsg:" + e.getMessage());
        }

    }

    private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent) {
        for (HandleCallbackParam callbackParam : callbackParamList) {
            String outputLog = callbackParam.getLogFileName();
            XxlJobFileAppender.makeLogFileNameByPath(outputLog);
            XxlJobLogger.logByfileNoname(outputLog, logContent);
        }
    }

}

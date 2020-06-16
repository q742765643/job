package com.htht.job.core.executor;

import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.thread.TriggerCallbackThread;
import com.htht.job.core.util.MyOwnRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */

public class XxlJobExecutor implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);
    // ---------------------- applicationContext ----------------------
    private static ApplicationContext applicationContext;
    // ---------------------- job handler repository ----------------------
    private static ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();

    private String logPath;
    // ---------------------- executor-server(jetty) ----------------------

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        XxlJobExecutor.applicationContext = applicationContext;
    }



    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    public static IJobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }

    private static void initJobHandlerRepository(ApplicationContext applicationContext) {
        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);

        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler) {
                    String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                    IJobHandler handler = (IJobHandler) serviceBean;
                    if (loadJobHandler(name) != null) {
                        throw new MyOwnRuntimeException("xxl-job jobhandler naming conflicts.");
                    }
                    registJobHandler(name, handler);
                }
            }
        }
    }






    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    // ---------------------- start + stop ----------------------
    public void start(){
        // init admin-client

        // init executor-jobHandlerRepository
        if (applicationContext != null) {
            initJobHandlerRepository(applicationContext);
        }
        TriggerCallbackThread.getInstance().start();
        // init logpath
        if (logPath != null && logPath.trim().length() > 0) {
            XxlJobFileAppender.logPath = logPath;
        }

        // init executor-server
    }

    public void destroy() {
        // destory JobThreadRepository

        TriggerCallbackThread.getInstance().toStop();
        // destory executor-server
    }



}

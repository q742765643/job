package com.htht.job.admin.core.rpc;/**
 * Created by zzj on 2018/7/23.
 */

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.rpc.Protocol;
import com.htht.job.admin.service.impl.XxlJobServiceImpl;
import com.htht.job.core.biz.ExecutorBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-07-23 12:28
 **/
public class RealReference {
    private static ConcurrentHashMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();
    private static Logger logger = LoggerFactory.getLogger(RealReference.class);
    private static final Protocol refprotocol = (Protocol) ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();


    public static ExecutorBiz getExecutorBiz(String ip) {
        try {
            ExecutorBiz executorBiz = executorBizRepository.get(ip);
            if (executorBiz != null) {
                return executorBiz;
            }
            ApplicationConfig applicationConfig=new ApplicationConfig();
            applicationConfig.setName("consumer");
            String url = "dubbo://"+ip+"/com.htht.job.core.biz.ExecutorBiz";//更改不同的Dubbo服务暴露的ip地址&端口
            ReferenceConfig<ExecutorBiz> referenceConfig = new ReferenceConfig<ExecutorBiz>();
            referenceConfig.setInterface(ExecutorBiz.class);
            referenceConfig.setUrl(url);
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setTimeout(50000);
            executorBiz =referenceConfig.get();
            executorBizRepository.put(ip, executorBiz);
        return executorBiz;
        } catch (Exception e) {
            logger.error(ip+"获取节点错误");
        }
        return null;
    }

    public static  boolean  getExecutorBiz(String ip,int timeout) {
        ReferenceConfig<ExecutorBiz> referenceConfig = new ReferenceConfig<ExecutorBiz>();
        try {
            ExecutorBiz executorBiz = null;
            ApplicationConfig applicationConfig=new ApplicationConfig();
            applicationConfig.setName("consumer");
            String url = "dubbo://"+ip+"/com.htht.job.core.biz.ExecutorBiz";//更改不同的Dubbo服务暴露的ip地址&端口
            referenceConfig.setInterface(ExecutorBiz.class);
            referenceConfig.setUrl(url);
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setTimeout(timeout);
            executorBiz =referenceConfig.get();
            return true;
        } catch (Exception e) {
            referenceConfig.destroy();
            //logger.error(ip+"获取节点错误");
            return false;
        }
    }




}


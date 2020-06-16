package com.htht.job.admin.core.rpc;/**
 * Created by zzj on 2018/7/23.
 */

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
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
    private RealReference() {
    }

    private static ConcurrentHashMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<>();
    private static Logger logger = LoggerFactory.getLogger(RealReference.class);

    public static ExecutorBiz getExecutorBiz(String ip) {
        try {
            ExecutorBiz executorBiz = executorBizRepository.get(ip);
            if (executorBiz != null) {
                return executorBiz;
            }
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("consumer");
            String url = "dubbo://" + ip + "/com.htht.job.core.biz.ExecutorBiz";//更改不同的Dubbo服务暴露的ip地址&端口
            ReferenceConfig<ExecutorBiz> referenceConfig = new ReferenceConfig<>();
            referenceConfig.setInterface(ExecutorBiz.class);
            referenceConfig.setUrl(url);
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setTimeout(50000);
            executorBiz = referenceConfig.get();
            executorBizRepository.put(ip, executorBiz);
            return executorBiz;
        } catch (Exception e) {
            logger.error("获取节点错误",e);
        }
        return null;
    }



}


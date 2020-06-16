package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/7/20.
 */

import com.htht.job.core.api.DubboCallBackService;
import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.biz.AdminBiz;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.RegistryParam;
import com.htht.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-07-20 13:00
 **/
@Service("dubboCallBackService")
public class DubboCallBackServiceImpl implements DubboCallBackService{
    @Autowired
    private AdminBiz adminBiz;
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList){
        return adminBiz.callback(callbackParamList);
    };
    public ReturnT<String> registry(RegistryParam registryParam){
        return adminBiz.registry(registryParam);
    }

    public ReturnT<String> registryRemove(RegistryParam registryParam){
        return adminBiz.registryRemove(registryParam);
    }

}


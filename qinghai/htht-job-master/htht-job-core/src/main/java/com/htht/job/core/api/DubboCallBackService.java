package com.htht.job.core.api;

import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.RegistryParam;
import com.htht.job.core.biz.model.ReturnT;

import java.util.List;

/**
 * Created by zzj on 2018/7/20.
 */
public interface DubboCallBackService {
     ReturnT<String> registry(RegistryParam registryParam);
     ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);
     ReturnT<String> registryRemove(RegistryParam registryParam);
}

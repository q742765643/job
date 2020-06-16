package com.htht.job.admin.service;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/11/20.
 */
public interface BroadCastSchedulerService {
    void broadScheduler(List<String> list, String methodMap, Map fixLinkMap, Map dyLinkMap);
}

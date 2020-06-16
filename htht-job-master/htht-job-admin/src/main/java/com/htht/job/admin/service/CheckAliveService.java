package com.htht.job.admin.service;

import com.htht.job.vo.NodeMonitor;

import java.util.List;

/**
 * @author zzj
 * @date 2018/8/8
 */
public interface CheckAliveService {
    public List<String> checkAliveByAddressList(List<String> addressList);

    public List<String> checkAliveByMonitors(List<NodeMonitor> monitors);
}

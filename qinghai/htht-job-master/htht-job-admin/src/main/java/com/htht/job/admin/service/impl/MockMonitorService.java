package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/8/8.
 */

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.monitor.MonitorService;

import java.util.Arrays;
import java.util.List;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-08-08 18:07
 **/
public class MockMonitorService implements MonitorService {

    private URL statistics;

    public void collect(URL statistics) {
        this.statistics = statistics;
    }

    public URL getStatistics() {
        return statistics;
    }

    public List<URL> lookup(URL query) {
        return Arrays.asList(statistics);
    }

}

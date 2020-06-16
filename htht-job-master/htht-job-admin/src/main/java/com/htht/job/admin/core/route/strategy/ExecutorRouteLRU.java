package com.htht.job.admin.core.route.strategy;

import com.htht.job.admin.core.route.ExecutorRouter;
import com.htht.job.admin.core.trigger.XxlJobTrigger;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单个JOB对应的每个执行器，最久为使用的优先被选举
 * a、LFU(Least Frequently Used)：最不经常使用，频率/次数
 * b(*)、LRU(Least Recently Used)：最近最久未使用，时间
 * <p>
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLRU extends ExecutorRouter {

    private static ConcurrentHashMap<Integer, LinkedHashMap<String, String>> jobLRUMap = new ConcurrentHashMap<>();
    private static long cacheValidTime = 0;

    public synchronized String route(int jobId, List<String> addressList) {

        // cache clear
        if (System.currentTimeMillis() > cacheValidTime) {
            jobLRUMap.clear();
            cacheValidTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }

        // init lru
        LinkedHashMap<String, String> lruItem = jobLRUMap.computeIfAbsent(jobId, k -> new LinkedHashMap<>());
        if (lruItem == null) {
            /**
             * LinkedHashMap
             *      a、accessOrder：ture=访问顺序排序（get/put时排序）；false=插入顺序排期；
             *      b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             */
            lruItem = new LinkedHashMap<>(16, 0.75f, true);
            jobLRUMap.put(jobId, lruItem);
        }

        // put
        for (String address : addressList) {
            if (!lruItem.containsKey(address)) {
                lruItem.put(address, address);
            }
        }

        // load
        String eldestKey = lruItem.entrySet().iterator().next().getKey();
        String eldestValue;
        eldestValue = lruItem.get(eldestKey);
        return eldestValue;
    }


    @Override
    public ReturnT<String> routeRun(TriggerParam triggerParam, List<String> addressList) {

        // address
        String address = route(triggerParam.getJobId(), addressList);

        // run executor
        ReturnT<String> runResult = XxlJobTrigger.runExecutor(triggerParam, address);
        runResult.setContent(address);
        return runResult;
    }

}

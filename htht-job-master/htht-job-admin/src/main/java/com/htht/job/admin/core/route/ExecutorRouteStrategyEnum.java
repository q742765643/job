package com.htht.job.admin.core.route;

import com.htht.job.admin.core.route.strategy.ExecutorRouteBestHost;
import com.htht.job.admin.core.route.strategy.ExecutorRouteFirst;
import com.htht.job.admin.core.route.strategy.ExecutorRouteRandom;

/**
 * Created by xuxueli on 17/3/10.
 */
public enum ExecutorRouteStrategyEnum {

    FIRST("选择第一个节点执行任务", new ExecutorRouteFirst()),
    //    LAST("最后一个", new ExecutorRouteLast()),
//    ROUND("轮询", new ExecutorRouteRound()),
    RANDOM("随机选择节点执行任务", new ExecutorRouteRandom()),
    //    CONSISTENT_HASH("一致性HASH", new ExecutorRouteConsistentHash()),
//    LEAST_FREQUENTLY_USED("最不经常使用", new ExecutorRouteLFU()),
//    LEAST_RECENTLY_USED("最近最久未使用", new ExecutorRouteLRU()),
//    FAILOVER("故障转移", new ExecutorRouteFailover()),
//    BUSYOVER("忙碌转移", new ExecutorRouteBusyover()),
    //SHARDING_BROADCAST("分片广播", null),
    BEST_HOST("选择CPU空闲的节点执行", new ExecutorRouteBestHost());

    private String title;
    private ExecutorRouter router;

    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorRouteStrategyEnum item : ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

    public String getTitle() {
        return title;
    }

    public ExecutorRouter getRouter() {
        return router;
    }

}

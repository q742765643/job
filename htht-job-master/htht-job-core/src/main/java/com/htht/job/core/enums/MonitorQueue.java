package com.htht.job.core.enums;

/**
 * Created by zzj on 2018/1/31.
 */
public enum MonitorQueue {
    JOB_OPERATION_QUEUE("调度任务队列"),
    NODE_LINE_QUEUE("节点排队任务"),
    NODE_OPERATION_QUEUE("节点运行任务"),
    NODE_SERIAL_QUEUE("节点串行队列"),
    NODE_SERIAL_QUEUE_LIST("节点串行队列列表"),
    NODE_DEAL_QUEUE("节点运行核"),
    BAD_NODE_QUEUE("坏节点任务队列");

    private String name;

    private MonitorQueue(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

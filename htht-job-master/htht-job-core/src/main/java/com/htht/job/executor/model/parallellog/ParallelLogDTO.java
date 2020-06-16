package com.htht.job.executor.model.parallellog;/**
 * Created by zzj on 2018/4/18.
 */

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @program: htht-job
 * @description: 并行条数
 * @author: zzj
 * @create: 2018-04-18 10:31
 **/
@Entity
@Table(name = "htht_cluster_schedule_parallel_log", indexes = {@Index(columnList = "flowId", unique = false),
        @Index(columnList = "id", unique = false)})
public class ParallelLogDTO extends BaseEntity {
    /**
     * 算法所需参数
     */
    @Column(columnDefinition = "TEXT", name = "dynamic_parameter")
    private String dynamicParameter;
    /**
     * 数据汇集、预处理所需参数
     */
    @Column(columnDefinition = "TEXT", name = "model_parameters")
    private String modelParameters;
    /**
     * 执行结果0执行中，200成功，500失败
     */
    private int code;
    /**
     * 流程图id
     */
    private String flowId;
    /**
     * 调度信息
     */
    @Column(columnDefinition = "TEXT", name = "trigger_msg")
    private String triggerMsg;
    /**
     * 执行信息
     */
    @Column(columnDefinition = "TEXT", name = "handle_msg")
    private String handleMsg;
    /**
     * 执行ip
     */
    private String ip;
    private String parentFlowLogId;

    public String getModelParameters() {
        return modelParameters;
    }

    public void setModelParameters(String modelParameters) {
        this.modelParameters = modelParameters;
    }

    public String getDynamicParameter() {
        return dynamicParameter;
    }

    public void setDynamicParameter(String dynamicParameter) {
        this.dynamicParameter = dynamicParameter;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTriggerMsg() {
        return triggerMsg;
    }

    public void setTriggerMsg(String triggerMsg) {
        this.triggerMsg = triggerMsg;
    }

    public String getParentFlowLogId() {
        return parentFlowLogId;
    }

    public void setParentFlowLogId(String parentFlowLogId) {
        this.parentFlowLogId = parentFlowLogId;
    }
}


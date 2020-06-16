package com.htht.job.executor.model.flowlog;/**
 * Created by zzj on 2018/3/30.
 */

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @program: htht-job
 * @description: 流程日志
 * @author: zzj
 * @create: 2018-03-30 17:19
 **/
@Entity
@Table(name = "htht_cluster_schedule_flow_log", indexes = {@Index(columnList = "data_id", unique = false),
        @Index(columnList = "job_log_id", unique = false),
        @Index(columnList = "id", unique = false)})
public class FlowLogDTO extends BaseEntity {
    /**
     * 下一步data_id
     */
    @Column(name = "next_id")
    private String nextId;
    /**
     * 触发时间
     */
    @Column(name = "trigger_time")
    private String triggerTime;
    /**
     * 执行地址
     */
    @Column(name = "executor_address")
    private String executorAddress;
    /**
     * 流程节点id
     */
    @Column(name = "data_id")
    private String dataId;
    /**
     * 任务日志id
     */
    @Column(name = "job_log_id")
    private int jobLogId;
    /**
     * 结果0执行中，200成功，500失败
     */
    @Column(name = "code")
    private int code;
    /**
     * 是否流程第一步0是，1否
     */
    @Column(name = "isStart")
    private String isStart;
    /**
     * java模型需要参数
     */
    @Column(columnDefinition = "TEXT", name = "fixed_parameter")
    private String fixedParameter;
    /**
     * 算法需要参数
     */
    @Column(columnDefinition = "TEXT", name = "dynamic_parameter")
    private String dynamicParameter;
    /**
     * 是否批量
     */
    @Column(name = "isPl")
    private String isPl;
    /**
     * 并行个数
     */
    @Column(name = "parallel")
    private int parallel;
    /**
     * 执行返回信息
     */
    @Column(columnDefinition = "TEXT", name = "handle_msg")
    private String handleMsg;
    /**
     * 流程节点名称
     */
    @Column(name = "label")
    private String label;
    /**
     * 流程步骤排序
     */
    @Column(name = "sort")
    private int sort;
    /**
     * 是否流程
     */
    @Column(name = "isProcess")
    private String isProcess;

    @Column(name = "flowChartId")
    private String flowChartId;


    private String parentFlowlogId;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getExecutorAddress() {
        return executorAddress;
    }

    public void setExecutorAddress(String executorAddress) {
        this.executorAddress = executorAddress;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public int getJobLogId() {
        return jobLogId;
    }

    public void setJobLogId(int jobLogId) {
        this.jobLogId = jobLogId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getIsStart() {
        return isStart;
    }

    public void setIsStart(String isStart) {
        this.isStart = isStart;
    }

    public String getFixedParameter() {
        return fixedParameter;
    }

    public void setFixedParameter(String fixedParameter) {
        this.fixedParameter = fixedParameter;
    }

    public String getDynamicParameter() {
        return dynamicParameter;
    }

    public void setDynamicParameter(String dynamicParameter) {
        this.dynamicParameter = dynamicParameter;
    }

    public String getIsPl() {
        return isPl;
    }

    public void setIsPl(String isPl) {
        this.isPl = isPl;
    }

    public int getParallel() {
        return parallel;
    }

    public void setParallel(int parallel) {
        this.parallel = parallel;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public String getIsProcess() {
        return isProcess;
    }

    public void setIsProcess(String isProcess) {
        this.isProcess = isProcess;
    }

    public String getParentFlowlogId() {
        return parentFlowlogId;
    }

    public void setParentFlowlogId(String parentFlowlogId) {
        this.parentFlowlogId = parentFlowlogId;
    }

    public String getFlowChartId() {
        return flowChartId;
    }

    public void setFlowChartId(String flowChartId) {
        this.flowChartId = flowChartId;
    }

}


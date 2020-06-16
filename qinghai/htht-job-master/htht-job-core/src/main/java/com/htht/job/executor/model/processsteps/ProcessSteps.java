package com.htht.job.executor.model.processsteps;/**
 * Created by zzj on 2018/3/29.
 */

import com.htht.job.core.util.BaseEntity;
import com.htht.job.executor.model.parallellog.ParallelLog;

import javax.persistence.*;
import java.util.List;

/**
 * @program: htht-joFlowCeaselesslyb
 * @description: 流程步骤
 * @author: zzj
 * @create: 2018-03-29 09:52
 **/
@Entity
@Table(name="htht_cluster_schedule_process_steps",
indexes = {@Index(columnList="data_id", unique = false)})
public class ProcessSteps extends BaseEntity {
    /**
     * 原子算法id
     */
    @Column(name = "service_id")
    private String serviceId;
    /**
     *java所需参数
     */
    @Column(columnDefinition="TEXT",name = "fixed_parameter")
    private String fixedParameter;
    /**
     * 算法所需参数
     */
    @Column(columnDefinition="TEXT",name = "dynamic_parameter")
    private String dynamicParameter;
    /**
     * 下一步id
     */
    @Column(name = "next_id")
    private String nextId;
    /**
     * 流程id
     */
    @Column(name = "flow_id")
    private String flowId;
    /**
     * 流程节点id
     */
    @Column(name = "data_id")
    private String dataId;
    /**
     * 是否为第一步
     */
    @Column(name = "isStart")
    private String isStart;
    /**
     * 是否批量
     */
    @Column(name = "isPl")
    private String isPl;
    /**
     * 流程节点名称
     */
    @Column(name = "label")
    private String label;
    /**
     * 步骤排序
     */
    @Column(name = "sort")
    private int sort;
    /**
     * 是否流程
     */
    @Column(name = "isProcess")
    private String isProcess;


    @Transient
    private List<String> nextIds;
    @Transient
    private List<ParallelLog> parallelLogs;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
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

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getIsStart() {
        return isStart;
    }

    public void setIsStart(String isStart) {
        this.isStart = isStart;
    }

    public List<String> getNextIds() {
        return nextIds;
    }

    public void setNextIds(List<String> nextIds) {
        this.nextIds = nextIds;
    }

    public String getIsPl() {
        return isPl;
    }

    public void setIsPl(String isPl) {
        this.isPl = isPl;
    }

    public List<ParallelLog> getParallelLogs() {
        return parallelLogs;
    }

    public void setParallelLogs(List<ParallelLog> parallelLogs) {
        this.parallelLogs = parallelLogs;
    }

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

    public String getIsProcess() {
        return isProcess;
    }

    public void setIsProcess(String isProcess) {
        this.isProcess = isProcess;
    }
}


package com.htht.job.executor.model.algorithm;

import com.htht.job.core.util.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "htht_cluster_schedule_task_parameters")
public class TaskParametersDTO extends BaseEntity {
    /**
     * 任务id
     */
    @Column(name = "job_id")
    private String jobId;
    /**
     * 对应 AtomicAlgorithm id
     */
    @Column(name = "parameter_id")
    private String parameterId;
    /**
     * java模型所需参数
     */
    @Column(columnDefinition = "TEXT", name = "fixed_parameter")
    private String fixedParameter;
    /**
     * 算法所需参数
     */
    @Column(columnDefinition = "TEXT", name = "dynamic_parameter")
    private String dynamicParameter;
    /**
     * 保存页面实体参数
     */
    @Column(columnDefinition = "TEXT", name = "model_parameters")
    private String modelParameters;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
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

    public String getModelParameters() {
        return modelParameters;
    }

    public void setModelParameters(String modelParameters) {
        this.modelParameters = modelParameters;
    }


}

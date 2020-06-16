package com.htht.job.admin.core.model.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by zzj on 2018/3/15.
 */
@ApiModel(value = "任务对象", description = "添加任务")
public class AppXxlJobInfo {

    private int id;                // 主键ID	    (JobKey.name)
    @ApiModelProperty(value = "执行器主健", required = true)
    private int jobGroup;        // 执行器主键ID	(JobKey.group)
    @ApiModelProperty(value = "任务cron表达式", required = true)
    private String jobCron;        // 任务执行CRON表达式 【base on quartz】
    @ApiModelProperty(value = "任务描述", required = true)
    private String jobDesc;

    private Date addTime;
    private Date updateTime;

    private String author;        // 负责人
    private String alarmEmail;    // 报警邮件
    @ApiModelProperty(value = "路由策略", required = true)
    private String executorRouteStrategy;    // 执行器路由策略
    @ApiModelProperty(value = "执行器", required = true)
    private String executorHandler;            // 执行器，任务Handler名称
    @ApiModelProperty(value = "任务参数", required = true)
    private String executorParam;            // 执行器，任务参数
    @ApiModelProperty(value = "阻塞处理策略", required = true)
    private String executorBlockStrategy;    // 阻塞处理策略
    @ApiModelProperty(value = "调度异常处理", required = true)
    private String executorFailStrategy;    // 失败处理策略

    private String glueType;        // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    private String glueSource;        // GLUE源代码
    private String glueRemark;        // GLUE备注
    private Date glueUpdatetime;    // GLUE更新时间

    private String childJobKey;        // 子任务Key

    // copy from quartz
    private String jobStatus;        // 任务状态 【base on quartz】

    private String modelParameters;

    private String modelId;
    private String productId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(int jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobCron() {
        return jobCron;
    }

    public void setJobCron(String jobCron) {
        this.jobCron = jobCron;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlarmEmail() {
        return alarmEmail;
    }

    public void setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
    }

    public String getExecutorRouteStrategy() {
        return executorRouteStrategy;
    }

    public void setExecutorRouteStrategy(String executorRouteStrategy) {
        this.executorRouteStrategy = executorRouteStrategy;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public String getExecutorBlockStrategy() {
        return executorBlockStrategy;
    }

    public void setExecutorBlockStrategy(String executorBlockStrategy) {
        this.executorBlockStrategy = executorBlockStrategy;
    }

    public String getExecutorFailStrategy() {
        return executorFailStrategy;
    }

    public void setExecutorFailStrategy(String executorFailStrategy) {
        this.executorFailStrategy = executorFailStrategy;
    }

    public String getGlueType() {
        return glueType;
    }

    public void setGlueType(String glueType) {
        this.glueType = glueType;
    }

    public String getGlueSource() {
        return glueSource;
    }

    public void setGlueSource(String glueSource) {
        this.glueSource = glueSource;
    }

    public String getGlueRemark() {
        return glueRemark;
    }

    public void setGlueRemark(String glueRemark) {
        this.glueRemark = glueRemark;
    }

    public Date getGlueUpdatetime() {
        return glueUpdatetime;
    }

    public void setGlueUpdatetime(Date glueUpdatetime) {
        this.glueUpdatetime = glueUpdatetime;
    }

    public String getChildJobKey() {
        return childJobKey;
    }

    public void setChildJobKey(String childJobKey) {
        this.childJobKey = childJobKey;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }


    public String getModelParameters() {
        return modelParameters;
    }

    public void setModelParameters(String modelParameters) {
        this.modelParameters = modelParameters;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}

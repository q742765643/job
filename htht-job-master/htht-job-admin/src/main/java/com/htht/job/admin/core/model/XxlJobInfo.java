package com.htht.job.admin.core.model;


import java.util.Date;
import java.util.Objects;

/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
public class XxlJobInfo {

    private int id;                // 主键ID	    (JobKey.name)
    private int jobGroup;        // 执行器主键ID	(JobKey.group)
    private String jobCron;        // 任务执行CRON表达式 【base on quartz】
    private String jobCronName;        // 任务执行CRON表达式 【base on quartz】
    private String jobDesc;

    private Date addTime;
    private Date updateTime;

    private String author;        // 负责人
    private String alarmEmail;    // 报警邮件
    private String executorRouteStrategy;    // 执行器路由策略
    private String executorHandler;            // 执行器，任务Handler名称
    private String executorParam;            // 执行器，任务参数
    private String executorBlockStrategy;    // 阻塞处理策略
    private String executorFailStrategy;    // 失败处理策略

    private String glueType;        // GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
    private String glueSource;        // GLUE源代码
    private String glueRemark;        // GLUE备注
    private Date glueUpdatetime;    // GLUE更新时间

    private String childJobKey;        // 子任务Key

    // copy from quartz
    private String jobStatus;        // 任务状态 【base on quartz】
    private String fixedParameter;
    private String dynamicParameter;
    private String modelParameters;

    private String modelId;
    private String productId;
    private XxlJobTriggers triggers;
    //1:算法任务  2.遥感数据汇集 3.CIMISS数据汇集 4.产品生产  5：代表流程任务
    // 6：代表气象卫星预处理流程         7：代表高分预处理流程 8: 数管调度 9：数管流程
    private int tasktype;
    private int priority;
    private int operation;

    //失败重试次数
    private int failRetryTimes;
    private String jsonString;         // 入库流程专用

    public int getFailRetryTimes() {
        return failRetryTimes;
    }

    public void setFailRetryTimes(int failRetryTimes) {
        this.failRetryTimes = failRetryTimes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

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

    public XxlJobTriggers getTriggers() {
        return triggers;
    }

    public void setTriggers(XxlJobTriggers triggers) {
        this.triggers = triggers;
    }

    public int getTasktype() {
        return tasktype;
    }

    public void setTasktype(int tasktype) {
        this.tasktype = tasktype;
    }


    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof XxlJobInfo)) {
            return false;
        }
        XxlJobInfo user = (XxlJobInfo) o;
        return id == user.id &&
                Objects.equals(jobGroup, user.jobGroup) &&
                Objects.equals(jobDesc, user.jobDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobGroup, jobDesc);
    }

    public String getJobCronName() {
        return jobCronName;
    }

    public void setJobCronName(String jobCronName) {
        this.jobCronName = jobCronName;
    }
}

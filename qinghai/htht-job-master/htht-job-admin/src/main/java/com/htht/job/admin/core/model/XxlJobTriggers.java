package com.htht.job.admin.core.model;

/**
 * Created by zzj on 2018/1/19.
 */
public class XxlJobTriggers {
    private String schedName;
    private String triggerName;
    private String triggerGroup;
    private String jobGroup;
    private String jobName;
    private String description;
    private long nextfireTime;
    private long prevfireTime;
    private int priority;
    private String triggerState;
    private String triggerType;
    private long startTime;


    public String getSchedName() {
        return schedName;
    }

    public void setSchedName(String schedName) {
        this.schedName = schedName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getNextfireTime() {
        return nextfireTime;
    }

    public void setNextfireTime(long nextfireTime) {
        this.nextfireTime = nextfireTime;
    }

    public long getPrevfireTime() {
        return prevfireTime;
    }

    public void setPrevfireTime(long prevfireTime) {
        this.prevfireTime = prevfireTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}

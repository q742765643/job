package com.htht.job.core.biz.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuxueli on 17/3/2.
 */
public class HandleCallbackParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private int logId;
    private ReturnT<String> executeResult;

    private String logFileName;

    private String parallelLogId;

    private boolean isFlow;

    private List<String> output;

    public HandleCallbackParam() {
    }

    public HandleCallbackParam(int logId, ReturnT<String> executeResult) {
        this.logId = logId;
        this.executeResult = executeResult;
    }

    public HandleCallbackParam(int logId, ReturnT<String> executeResult, String logFileName, String parallelLogId, boolean isflow, List<String> output) {
        this.logId = logId;
        this.executeResult = executeResult;
        this.logFileName = logFileName;
        this.parallelLogId = parallelLogId;
        this.isFlow = isflow;
        this.output = output;
    }

    public String getParallelLogId() {
        return parallelLogId;
    }

    public void setParallelLogId(String parallelLogId) {
        this.parallelLogId = parallelLogId;
    }

    public boolean isFlow() {
        return isFlow;
    }

    public void setFlow(boolean isFlow) {
        this.isFlow = isFlow;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }


    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public ReturnT<String> getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(ReturnT<String> executeResult) {
        this.executeResult = executeResult;
    }

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "HandleCallbackParam{" +
                "logId=" + logId +
                ", executeResult=" + executeResult +
                '}';
    }
}

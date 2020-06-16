package com.htht.job.admin.core.util;

/**
 * 流程实例执行消息
 *
 * @author ytwps
 */
public class InstanceMessage {
    public static final int MESSAGE_TYPE_RECEIVE = 0;
    public static final int MESSAGE_TYPE_REPLY = 2;
    public static final int MESSAGE_TYPE_INVOKE = 1;
    /**
     * 请求消息
     */
    public static final String MESSAGE_TYPE_REQUEST = "request";

    /**
     * 流程运行状态
     */
    public static final String PROCESS_RUN_STATUS = "ProcessRunStatus";

    /**
     * 响应消息
     */
    public static final String MESSAGE_TYPE_RESPONSE = "response";

    private String iid;
    private String appointedId;
    private String type = MESSAGE_TYPE_REQUEST;
    private String endPoint;
    private String msg;
    private int status;// 执行中，错误，完成
    private String figureId;
    private String childAppointedId;
    private String startTime;
    private String endTime;
    private String output;
    private String input;
    private String error;


    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getAppointedId() {
        return appointedId;
    }

    public void setAppointedId(String appointedId) {
        this.appointedId = appointedId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFigureId() {
        return figureId;
    }

    public void setFigureId(String figureId) {
        this.figureId = figureId;
    }

    public String getChildAppointedId() {
        return childAppointedId;
    }

    public void setChildAppointedId(String childAppointedId) {
        this.childAppointedId = childAppointedId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

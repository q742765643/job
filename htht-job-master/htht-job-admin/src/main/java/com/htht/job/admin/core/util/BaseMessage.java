package com.htht.job.admin.core.util;

/**
 * 消息实体类
 *
 * @author xlj
 */
public class BaseMessage {
    private String msgType;
    private String msgId;
    private String msgBody;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }
}

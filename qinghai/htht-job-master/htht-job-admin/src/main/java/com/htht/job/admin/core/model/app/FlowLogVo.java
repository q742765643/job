package com.htht.job.admin.core.model.app;/**
 * Created by zzj on 2018/10/10.
 */

import com.htht.job.executor.model.flowlog.FlowLog;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-10 14:41
 **/
public class FlowLogVo {
    private FlowLog flowLog;
    private FlowLog startFlowLog;
    private FlowLogVo flowLogVo;

    public FlowLog getFlowLog() {
        return flowLog;
    }

    public void setFlowLog(FlowLog flowLog) {
        this.flowLog = flowLog;
    }

    public FlowLog getStartFlowLog() {
        return startFlowLog;
    }

    public void setStartFlowLog(FlowLog startFlowLog) {
        this.startFlowLog = startFlowLog;
    }

    public FlowLogVo getFlowLogVo() {
        return flowLogVo;
    }

    public void setFlowLogVo(FlowLogVo flowLogVo) {
        this.flowLogVo = flowLogVo;
    }
}


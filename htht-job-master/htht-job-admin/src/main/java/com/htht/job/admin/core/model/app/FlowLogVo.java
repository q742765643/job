package com.htht.job.admin.core.model.app;/**
 * Created by zzj on 2018/10/10.
 */

import com.htht.job.executor.model.flowlog.FlowLogDTO;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-10 14:41
 **/
public class FlowLogVo {
    private FlowLogDTO flowLogDTO;
    private FlowLogDTO startFlowLogDTO;
    private FlowLogVo flowLogVo;

    public FlowLogDTO getFlowLogDTO() {
        return flowLogDTO;
    }

    public void setFlowLogDTO(FlowLogDTO flowLogDTO) {
        this.flowLogDTO = flowLogDTO;
    }

    public FlowLogDTO getStartFlowLogDTO() {
        return startFlowLogDTO;
    }

    public void setStartFlowLogDTO(FlowLogDTO startFlowLogDTO) {
        this.startFlowLogDTO = startFlowLogDTO;
    }

    public FlowLogVo getFlowLogVo() {
        return flowLogVo;
    }

    public void setFlowLogVo(FlowLogVo flowLogVo) {
        this.flowLogVo = flowLogVo;
    }
}


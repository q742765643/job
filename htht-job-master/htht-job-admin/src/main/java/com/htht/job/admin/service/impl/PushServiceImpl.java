package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/5/10.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.util.BaseMessage;
import com.htht.job.admin.core.util.InstanceMessage;
import com.htht.job.admin.core.util.RedisUtil;
import com.htht.job.admin.core.websocket.FirstWebSocketHandler;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.service.FlowSchedulerNextService;
import com.htht.job.admin.service.PushService;
import com.htht.job.admin.service.SchedulerFlowService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: htht-job
 * @description: 推送
 * @author: zzj
 * @create: 2018-05-10 16:38
 **/
@Service
public class PushServiceImpl implements PushService {
    @Resource
    private DubboService dubboService;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobLogDao xxlJobLogDao;
    @Resource
    private FlowSchedulerNextService flowSchedulerNextService;
    @Qualifier("schedulerFlowServiceImpl")
    @Resource
    private SchedulerFlowService schedulerFlowService;
    @Override
    public void pushWeb(String jsonStr, WebSocketSession session) {
        try {
            JSONObject jobj = JSONObject.parseObject(jsonStr);
            String msgId = jobj.getString("msgId");
            String msgType = jobj.getString("msgType");
            XxlJobLog xxlJobLog = xxlJobLogDao.load(Integer.parseInt(msgId));
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(xxlJobLog.getJobId());
            if ("monitor".equals(msgType)) {
                ProcessStepsDTO findVo = new ProcessStepsDTO();
                findVo.setFlowId(jobInfo.getModelId());
                List<ProcessStepsDTO> processStepsDTOList = dubboService.findFlowCeaselesslyList(findVo);
                List<String> instanceMessages = new ArrayList<String>();
                this.push(processStepsDTOList, xxlJobLog, session, msgId, instanceMessages);
                Thread.sleep(500);
                while (processStepsDTOList.size() > 0) {
                    if (FirstWebSocketHandler.users.contains(session)) {
                        this.push(processStepsDTOList, xxlJobLog, session, msgId, instanceMessages);
                        Thread.sleep(500);
                    } else {
                        return;
                    }

                }

            } else if ("start".equals(msgType)) {

                xxlJobLogDao.updateSuspend(xxlJobLog.getId(), 0);
                FlowLogDTO flowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(xxlJobLog.getId(), FlowConstant.STARTFIGURE, "1");
                Set<String> nextIds = new HashSet<String>();
                this.findNextFlowLog(flowLogDTO, xxlJobLog.getId(), nextIds, jobInfo);
                String[] nextflowId = new String[nextIds.size()];
                Iterator<String> it = nextIds.iterator();
                int i = 0;
                while (it.hasNext()) {
                    nextflowId[i] = it.next();
                    i++;
                }
                if (nextIds.size() > 0) {
                    Map paramMap = new HashMap();
                    schedulerFlowService.depositNextStepMap(paramMap, xxlJobLog, jobInfo, flowLogDTO.getFlowChartId(), flowLogDTO.getParentFlowlogId());
                    flowSchedulerNextService.nextStep(nextflowId, paramMap);
                }
            } else if ("suspend".equals(msgType)) {
                xxlJobLogDao.updateSuspend(xxlJobLog.getId(), 1);
            } else if ("retry".equals(msgType)) {

                String msgBody = jobj.getString("msgBody");
                JSONObject paramsJson = JSONObject.parseObject(msgBody);

                List<Map> mapList = JSON.parseArray(paramsJson.getString("params"), Map.class);

                String dataId = paramsJson.getString("id");
                String dynamicParameter = "";
                FlowLogDTO startflowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(xxlJobLog.getId(), FlowConstant.STARTFIGURE, "1");


                FlowLogDTO flowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(xxlJobLog.getId(), dataId, "1");
                Set<String> nextIds = new HashSet<String>();
                this.findNextFlowLogList(xxlJobLog.getId(), nextIds, dataId, "1");
                Iterator<String> it = nextIds.iterator();
                while (it.hasNext()) {
                    dubboService.deleteFlowLog(it.next());
                }
                if (FlowConstant.STARTFIGURE.equals(dataId)) {
                    List<CommonParameter> commonParameters = JSON.parseArray(startflowLogDTO.getDynamicParameter(), CommonParameter.class);
                    for (int i = 0; i < commonParameters.size(); i++) {
                        for (Map map : mapList) {
                            String dataIdS = (String) map.get("dataId");
                            String value = (String) map.get("value");
                            if (dataIdS.equals(commonParameters.get(i).getDataID())) {
                                commonParameters.get(i).setValue(value);
                                break;
                            }
                        }

                    }
                    dynamicParameter = JSON.toJSONString(commonParameters);
                    jobInfo.setOperation(1);

                } else {
                    List<CommonParameter> commonParameters = JSON.parseArray(flowLogDTO.getDynamicParameter(), CommonParameter.class);
                    for (int i = 0; i < commonParameters.size(); i++) {
                        commonParameters.get(i).setValue("");
                        for (Map map : mapList) {
                            String dataIdS = (String) map.get("dataId");
                            String value = (String) map.get("value");
                            if (dataIdS.equals(commonParameters.get(i).getDataID())) {
                                commonParameters.get(i).setValue(value);
                                break;
                            }

                        }
                    }
                    dynamicParameter = JSON.toJSONString(commonParameters);
                    if ("1".equals(flowLogDTO.getIsStart())) {
                        jobInfo.setOperation(1);
                    }


                }
                RedisUtil.delete(String.valueOf(xxlJobLog.getId()));
                if (FlowConstant.STARTFIGURE.equals(dataId)) {
                    jobInfo.setOperation(3);
                    flowSchedulerNextService.handScheduler(jobInfo, xxlJobLog, dataId, dynamicParameter);
                }

                if (flowLogDTO.getIsStart().equals("0") && !FlowConstant.STARTFIGURE.equals(dataId)) {
                    jobInfo.setOperation(2);
                    flowSchedulerNextService.handScheduler(jobInfo, xxlJobLog, dataId, dynamicParameter);
                }
                if (!flowLogDTO.getIsStart().equals("0") && !FlowConstant.STARTFIGURE.equals(dataId)) {
                    flowSchedulerNextService.handNextStep(dataId, jobInfo, xxlJobLog, dynamicParameter, flowLogDTO.getParentFlowlogId(), flowLogDTO.getFlowChartId());
                }

                ProcessStepsDTO findVo = new ProcessStepsDTO();
                findVo.setFlowId(jobInfo.getModelId());
                List<ProcessStepsDTO> processStepsDTOList = dubboService.findFlowCeaselesslyList(findVo);
                List<String> instanceMessages = new ArrayList<String>();
                this.push(processStepsDTOList, xxlJobLog, session, msgId, instanceMessages);
                Thread.sleep(500);
                while (processStepsDTOList.size() > 0) {
                    if (FirstWebSocketHandler.users.contains(session)) {
                        this.push(processStepsDTOList, xxlJobLog, session, msgId, instanceMessages);
                        Thread.sleep(500);
                    } else {
                        return;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findNextFlowLogList(int jobLogId, Set<String> nextIds, String dataId, String parentFlowlogId) {
        FlowLogDTO flowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId, dataId, parentFlowlogId);

        if (flowLogDTO != null) {
            if ("true".equals(flowLogDTO.getIsProcess())) {
                FlowLogDTO flowLogDTO1 = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId, FlowConstant.STARTFIGURE, flowLogDTO.getId());
                nextIds.add(flowLogDTO1.getId());
                if (!StringUtils.isEmpty(flowLogDTO1.getNextId())) {
                    String[] dataIds = flowLogDTO1.getNextId().split(",");
                    for (String dataId1 : dataIds) {
                        this.findNextFlowLogList(jobLogId, nextIds, dataId1, flowLogDTO1.getParentFlowlogId());
                    }
                }
            }
            nextIds.add(flowLogDTO.getId());
            if (StringUtils.isEmpty(flowLogDTO.getNextId())) {
                return;
            }
            String[] dataIds = flowLogDTO.getNextId().split(",");
            for (String dataId1 : dataIds) {
                this.findNextFlowLogList(jobLogId, nextIds, dataId1, parentFlowlogId);
            }

        }


    }

    private void findNextFlowLog(FlowLogDTO flowLogDTO, int jobLogId, Set<String> nextIds, XxlJobInfo jobInfo) {
        String[] dataIds = flowLogDTO.getNextId().split(",");
        for (String dataId : dataIds) {
            FlowLogDTO lastFlowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId, dataId, "1");
            if (lastFlowLogDTO != null) {
                if (ReturnT.SUCCESS_CODE != lastFlowLogDTO.getCode()) {
                    dubboService.deleteFlowLog(lastFlowLogDTO.getId());
                    nextIds.add(dataId);
                } else {
                    this.findNextFlowLog(lastFlowLogDTO, jobLogId, nextIds, jobInfo);
                }

            } else {

                ProcessStepsDTO findVo = new ProcessStepsDTO();
                findVo.setFlowId(jobInfo.getModelId());
                findVo.setDataId(dataId);
                List<ProcessStepsDTO> processStepsDTOList = dubboService.findFlowCeaselesslyList(findVo);
                boolean flag = true;
                for (ProcessStepsDTO processStepsDTO : processStepsDTOList) {
                    FlowLogDTO allFlowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(flowLogDTO.getJobLogId(), processStepsDTO.getDataId(), "1");
                    if (allFlowLogDTO == null || ReturnT.SUCCESS_CODE != allFlowLogDTO.getCode()) {
                        flag = false;
                    }
                }
                if (flag) {
                    nextIds.add(dataId);
                }
            }
        }


    }

    private void push(List<ProcessStepsDTO> processStepsDTOList, XxlJobLog xxlJobLog, WebSocketSession session, String msgId, List<String> instanceMessages) throws Exception {
        List<ProcessStepsDTO> processed = new ArrayList<ProcessStepsDTO>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ProcessStepsDTO processStepsDTO : processStepsDTOList) {
            BaseMessage baseMessage = new BaseMessage();

            FlowLogDTO flowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(xxlJobLog.getId(), processStepsDTO.getDataId(), "1");
            if (flowLogDTO != null) {
                InstanceMessage instanceMessage = new InstanceMessage();
                if (200 == flowLogDTO.getCode()) {
                    instanceMessage.setStatus(30);
                    processed.add(processStepsDTO);
                } else if (500 == flowLogDTO.getCode()) {
                    instanceMessage.setStatus(40);
                    instanceMessage.setError(flowLogDTO.getHandleMsg());
                    processed.add(processStepsDTO);
                } else {
                    instanceMessage.setStatus(20);
                }
                List<CommonParameter> commonParameterList = JSON.parseArray(flowLogDTO.getDynamicParameter(), CommonParameter.class);
                List<CommonParameter> outputParameter = new ArrayList<CommonParameter>();
                for (CommonParameter commonParameter : commonParameterList) {
                    if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType()) || FlowConstant.OUTSTRING.equals(commonParameter.getParameterType())) {
                        outputParameter.add(commonParameter);
                    }
                }
                commonParameterList.removeAll(outputParameter);
                instanceMessage.setFigureId(processStepsDTO.getDataId());
                instanceMessage.setAppointedId(processStepsDTO.getDataId());
                instanceMessage.setInput(JSON.toJSONString(commonParameterList));
                instanceMessage.setOutput(JSON.toJSONString(outputParameter));
                if (null != flowLogDTO.getCreateTime()) {
                    instanceMessage.setStartTime(sdf.format(flowLogDTO.getCreateTime()));
                }
                if (null != flowLogDTO.getUpdateTime()) {
                    instanceMessage.setEndTime(sdf.format(flowLogDTO.getUpdateTime()));
                }
                baseMessage.setMsgId(msgId);
                baseMessage.setMsgType("");
                baseMessage.setMsgBody(JSON.toJSONString(instanceMessage));
                if (instanceMessages.contains(processStepsDTO.getDataId() + flowLogDTO.getCode())) {
                    session.sendMessage(new TextMessage(""));
                } else {
                    session.sendMessage(new TextMessage(JSON.toJSONString(baseMessage)));
                }
                instanceMessages.add(processStepsDTO.getDataId() + flowLogDTO.getCode());
            } else {
                session.sendMessage(new TextMessage(""));

            }


        }
        processStepsDTOList.removeAll(processed);
    }
}


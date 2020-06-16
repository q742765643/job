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
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.processsteps.ProcessSteps;
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
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public void pushWeb(String jsonStr, WebSocketSession session) {
        try {
            JSONObject jobj = JSONObject.parseObject(jsonStr);
            String msgId = jobj.getString("msgId");
            String msgType = jobj.getString("msgType");
            XxlJobLog xxlJobLog = xxlJobLogDao.load(Integer.parseInt(msgId));
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(xxlJobLog.getJobId());
            if ("monitor".equals(msgType)) {
                ProcessSteps findVo = new ProcessSteps();
                findVo.setFlowId(jobInfo.getModelId());
                List<ProcessSteps> processStepsList = dubboService.findFlowCeaselesslyList(findVo);
                List<String> instanceMessages = new ArrayList<String>();
                this.push(processStepsList, xxlJobLog, session, msgId, instanceMessages);
                Thread.sleep(500);
                while (processStepsList.size() > 0) {
                    if (FirstWebSocketHandler.users.contains(session)) {
                        this.push(processStepsList, xxlJobLog, session, msgId, instanceMessages);
                        Thread.sleep(500);
                    } else {
                        return;
                    }

                }

            } else if ("start".equals(msgType)) {
             /*   ClassLoader loader = Thread.currentThread().getContextClassLoader();
                try {
                    Class clazz = loader.loadClass("com.htht.job.core.biz.ExecutorBiz");
                } catch (ClassNotFoundException e) {
                    Thread.currentThread().setContextClassLoader(ExecutorBiz.class.getClassLoader());
                }*/


                xxlJobLogDao.updateSuspend(xxlJobLog.getId(), 0);
                FlowLog flowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(xxlJobLog.getId(), FlowConstant.STARTFIGURE, "1");
                Set<String> nextIds = new HashSet<String>();
                this.findNextFlowLog(flowLog, xxlJobLog.getId(), nextIds, jobInfo);
                String[] nextflowId = new String[nextIds.size()];
                Iterator<String> it = nextIds.iterator();
                int i = 0;
                while (it.hasNext()) {
                    nextflowId[i] = it.next();
                    i++;
                }
                if (nextIds.size() > 0) {
                    Map paramMap = new HashMap();
                    schedulerFlowService.depositNextStepMap(paramMap, xxlJobLog, jobInfo, flowLog.getFlowChartId(), flowLog.getParentFlowlogId());
                    flowSchedulerNextService.nextStep(nextflowId, paramMap);
                }
            } else if ("suspend".equals(msgType)) {
                xxlJobLogDao.updateSuspend(xxlJobLog.getId(), 1);
            } else if ("retry".equals(msgType)) {
                /*    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    try {
                        Class clazz = loader.loadClass("com.htht.job.core.biz.ExecutorBiz");
                    } catch (ClassNotFoundException e) {
                        Thread.currentThread().setContextClassLoader(ExecutorBiz.class.getClassLoader());
                    }*/

                String msgBody = jobj.getString("msgBody");
                JSONObject paramsJson = JSONObject.parseObject(msgBody);

                List<Map> mapList = JSON.parseArray(paramsJson.getString("params"), Map.class);

                String dataId = paramsJson.getString("id");
                String dynamicParameter = "";
                FlowLog startflowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(xxlJobLog.getId(), FlowConstant.STARTFIGURE, "1");


                FlowLog flowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(xxlJobLog.getId(), dataId, "1");
                Set<String> nextIds = new HashSet<String>();
                this.findNextFlowLogList(xxlJobLog.getId(), nextIds, dataId, "1");
                Iterator<String> it = nextIds.iterator();
                while (it.hasNext()) {
                    dubboService.deleteFlowLog(it.next());
                }
                if (FlowConstant.STARTFIGURE.equals(dataId)) {
                    List<CommonParameter> commonParameters = JSON.parseArray(startflowLog.getDynamicParameter(), CommonParameter.class);
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
                    List<CommonParameter> commonParameters = JSON.parseArray(flowLog.getDynamicParameter(), CommonParameter.class);
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
                    if ("1".equals(flowLog.getIsStart())) {
                        jobInfo.setOperation(1);
                    }


                }
                RedisUtil.delete(String.valueOf(xxlJobLog.getId()));
                if (FlowConstant.STARTFIGURE.equals(dataId)) {
                    jobInfo.setOperation(3);
                    flowSchedulerNextService.handScheduler(jobInfo, xxlJobLog, dataId, dynamicParameter);
                }

                if (flowLog.getIsStart().equals("0") && !FlowConstant.STARTFIGURE.equals(dataId)) {
                    jobInfo.setOperation(2);
                    flowSchedulerNextService.handScheduler(jobInfo, xxlJobLog, dataId, dynamicParameter);
                }
                if (!flowLog.getIsStart().equals("0") && !FlowConstant.STARTFIGURE.equals(dataId)) {
                    flowSchedulerNextService.handNextStep(dataId, jobInfo, xxlJobLog, dynamicParameter, flowLog.getParentFlowlogId(), flowLog.getFlowChartId());
                }

                ProcessSteps findVo = new ProcessSteps();
                findVo.setFlowId(jobInfo.getModelId());
                List<ProcessSteps> processStepsList = dubboService.findFlowCeaselesslyList(findVo);
                List<String> instanceMessages = new ArrayList<String>();
                this.push(processStepsList, xxlJobLog, session, msgId, instanceMessages);
                Thread.sleep(500);
                while (processStepsList.size() > 0) {
                    if (FirstWebSocketHandler.users.contains(session)) {
                        this.push(processStepsList, xxlJobLog, session, msgId, instanceMessages);
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
        FlowLog flowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId, dataId, parentFlowlogId);

        if (flowLog != null) {
            if ("true".equals(flowLog.getIsProcess())) {
                FlowLog flowLog1 = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId, FlowConstant.STARTFIGURE, flowLog.getId());
                nextIds.add(flowLog1.getId());
                if (!StringUtils.isEmpty(flowLog1.getNextId())) {
                    String[] dataIds = flowLog1.getNextId().split(",");
                    for (String dataId1 : dataIds) {
                        this.findNextFlowLogList(jobLogId, nextIds, dataId1, flowLog1.getParentFlowlogId());
                    }
                }
            }
            nextIds.add(flowLog.getId());
            if (StringUtils.isEmpty(flowLog.getNextId())) {
                return;
            }
            String[] dataIds = flowLog.getNextId().split(",");
            for (String dataId1 : dataIds) {
                this.findNextFlowLogList(jobLogId, nextIds, dataId1, parentFlowlogId);
            }

        }


    }

    private void findNextFlowLog(FlowLog flowLog, int jobLogId, Set<String> nextIds, XxlJobInfo jobInfo) {
        String[] dataIds = flowLog.getNextId().split(",");
        for (String dataId : dataIds) {
            FlowLog lastFlowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId, dataId, "1");
            if (lastFlowLog != null) {
                if (ReturnT.SUCCESS_CODE != lastFlowLog.getCode()) {
                    dubboService.deleteFlowLog(lastFlowLog.getId());
                    nextIds.add(dataId);
                } else {
                    this.findNextFlowLog(lastFlowLog, jobLogId, nextIds, jobInfo);
                }

            } else {

                ProcessSteps findVo = new ProcessSteps();
                findVo.setFlowId(jobInfo.getModelId());
                findVo.setDataId(dataId);
                List<ProcessSteps> processStepsList = dubboService.findFlowCeaselesslyList(findVo);
                boolean flag = true;
                for (ProcessSteps processSteps : processStepsList) {
                    FlowLog allFlowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(flowLog.getJobLogId(), processSteps.getDataId(), "1");
                    if (allFlowLog == null || ReturnT.SUCCESS_CODE != allFlowLog.getCode()) {
                        flag = false;
                    }
                }
                if (flag) {
                    nextIds.add(dataId);
                }
            }
        }


    }

    private void push(List<ProcessSteps> processStepsList, XxlJobLog xxlJobLog, WebSocketSession session, String msgId, List<String> instanceMessages) throws Exception {
        List<ProcessSteps> processed = new ArrayList<ProcessSteps>();
        for (ProcessSteps processSteps : processStepsList) {
            BaseMessage baseMessage = new BaseMessage();

            FlowLog flowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(xxlJobLog.getId(), processSteps.getDataId(), "1");
            if (flowLog != null) {
                InstanceMessage instanceMessage = new InstanceMessage();
                if (200 == flowLog.getCode()) {
                    instanceMessage.setStatus(30);
                    processed.add(processSteps);
                } else if (500 == flowLog.getCode()) {
                    instanceMessage.setStatus(40);
                    instanceMessage.setError(flowLog.getHandleMsg());
                    processed.add(processSteps);
                } else {
                    instanceMessage.setStatus(20);
                }
                List<CommonParameter> commonParameterList = JSON.parseArray(flowLog.getDynamicParameter(), CommonParameter.class);
                List<CommonParameter> outputParameter = new ArrayList<CommonParameter>();
                for (CommonParameter commonParameter : commonParameterList) {
                    if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType()) || FlowConstant.OUTSTRING.equals(commonParameter.getParameterType())) {
                        outputParameter.add(commonParameter);
                    }
                }
                commonParameterList.removeAll(outputParameter);
                instanceMessage.setFigureId(processSteps.getDataId());
                instanceMessage.setAppointedId(processSteps.getDataId());
                instanceMessage.setInput(JSON.toJSONString(commonParameterList));
                instanceMessage.setOutput(JSON.toJSONString(outputParameter));
                if (null != flowLog.getCreateTime()) {
                    instanceMessage.setStartTime(sdf.format(flowLog.getCreateTime()));
                }
                if (null != flowLog.getUpdateTime()) {
                    instanceMessage.setEndTime(sdf.format(flowLog.getUpdateTime()));
                }
                baseMessage.setMsgId(msgId);
                baseMessage.setMsgType("");
                baseMessage.setMsgBody(JSON.toJSONString(instanceMessage));
                if (instanceMessages.contains(processSteps.getDataId() + flowLog.getCode())) {
                    session.sendMessage(new TextMessage(""));
                } else {
                    session.sendMessage(new TextMessage(JSON.toJSONString(baseMessage)));
                }
                instanceMessages.add(processSteps.getDataId() + flowLog.getCode());
            } else {
                session.sendMessage(new TextMessage(""));

            }


        }
        processStepsList.removeAll(processed);
    }
}


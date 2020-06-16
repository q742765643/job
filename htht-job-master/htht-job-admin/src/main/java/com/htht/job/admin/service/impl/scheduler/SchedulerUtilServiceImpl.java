package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/10/30.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.admin.service.SchedulerUtilService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-30 13:39
 **/
@Service
public class SchedulerUtilServiceImpl implements SchedulerUtilService {
    private static Logger logger = LoggerFactory.getLogger(SchedulerUtilServiceImpl.class);
    @Resource
    private CheckAliveService checkAliveService;
    @Resource
    private XxlJobLogDao xxlJobLogDao;
    @Resource
    private DubboService dubboService;


    @Override
    public void saveJobLog(XxlJobInfo jobInfo, XxlJobLog jobLog) {
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        XxlJobDynamicScheduler.xxlJobLogDao.save(jobLog);
        logger.debug(">>>>>>>>>>> xxl-job trigger start, jobId:{}", jobLog.getId());
    }

    @Override
    public void updateHandleInfo(HandleCallbackParam handleCallbackParam, XxlJobLog jobLog) {
        StringBuilder handleMsg = new StringBuilder();
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        jobLog.setHandleMsg(handleMsg.toString());
        jobLog.setHandleTime(new Date());
        jobLog.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        jobLog.setLogFileName(handleCallbackParam.getLogFileName());
        xxlJobLogDao.updateHandleInfo(jobLog);

    }

    @Override
    public void updateTriggerInfo(XxlJobLog jobLog, ReturnT<String> triggerResult, StringBuilder triggerMsgSb) {

        jobLog.setExecutorAddress(triggerResult.getContent());
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        XxlJobDynamicScheduler.xxlJobLogDao.updateTriggerInfo(jobLog);
    }

    @Override
    public void saveTriggerParam(TriggerParam triggerParam, XxlJobInfo jobInfo,
                                 AtomicAlgorithmDTO atomicAlgorithmDTO, XxlJobLog jobLog,
                                 Map fixmap, Map dymap, ParallelLogDTO parallelLogDTO) {
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(atomicAlgorithmDTO.getModelIdentify());
        triggerParam.setExecutorParams("");
        triggerParam.setExecutorBlockStrategy(atomicAlgorithmDTO.getExecutorBlockStrategy());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setBroadcastIndex(0);
        triggerParam.setBroadcastTotal(1);
        triggerParam.setFixedParameter((LinkedHashMap) fixmap);
        triggerParam.setDynamicParameter((LinkedHashMap) dymap);
        triggerParam.setParallelLogId(parallelLogDTO.getId());
        triggerParam.setAlgorId(atomicAlgorithmDTO.getId());
        triggerParam.setPriority(jobInfo.getPriority());
        triggerParam.setFlow(true);

    }

    @Override
    public List<String> findAddressList(String id, int dealAmount) {
        List<String> addressList = dubboService.findAddressList(id, dealAmount);
        List<String> addressListUse = new ArrayList<>();
        try {
            addressListUse = checkAliveService.checkAliveByAddressList(addressList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        addressList.retainAll(addressListUse);
        return addressList;
    }

    @Override
    public Map transformMap(String params) {
        LinkedHashMap map = new LinkedHashMap(20);
        List<CommonParameter> commonParameters = JSON.parseArray(params, CommonParameter.class);
        for (CommonParameter commonParameter : commonParameters) {
            map.put(commonParameter.getParameterName(), commonParameter.getValue());
        }
        return map;

    }

    //失败重试五次
    @Override
    public Map<String, Object> failRestrtFive(ExecutorRouteStrategyEnum executorRouteStrategyEnum,
                                              TriggerParam triggerParam, StringBuilder triggerMsgSb, ReturnT<String> triggerResult, XxlJobInfo jobInfo,
                                              AtomicAlgorithmDTO atomicAlgorithmDTO) {
        HashMap<String, Object> map = new HashMap();
        int maxRetryTime = 5;
        int time = 0;
        do {
            time++;
            try {
                //检查存活节点重新获取
                ArrayList<String> addressList = (ArrayList<String>) this.findAddressList(jobInfo.getModelId(),
                        atomicAlgorithmDTO.getDealAmount());
                triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
                triggerMsgSb.append("<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试<<<<<<<<<<< </span><br>")
                        .append(triggerResult.getMsg());
                if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
                    map.put(JobConstant.TRIGGERRESULT, triggerResult);
                    map.put(JobConstant.TRIGGERMSGSB, triggerMsgSb);
                    return map;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } while (time < maxRetryTime);
        map.put(JobConstant.TRIGGERRESULT, triggerResult);
        map.put(JobConstant.TRIGGERMSGSB, triggerMsgSb);
        return map;
    }

    @Override
    public void updateJobLog(XxlJobInfo jobInfo, XxlJobLog jobLog) {
        jobLog.setGlueType(jobInfo.getGlueType());
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setTriggerTime(new Date());
    }

    @Override
    public void updateHandleInfo(ProcessStepsDTO processStepsDTO, XxlJobLog jobLog, ReturnT<String> triggerResult) {
        if (triggerResult.getCode() == ReturnT.FAIL_CODE) {
            HandleCallbackParam handleCallbackParam = new HandleCallbackParam();
            ReturnT<String> executeResult = new ReturnT(ReturnT.FAIL_CODE, "调度失败，请查看" + processStepsDTO.getLabel());
            handleCallbackParam.setExecuteResult(executeResult);
            this.updateHandleInfo(handleCallbackParam, jobLog);
        }
    }

    @Override
    public void acquireTriggerResult(ReturnT<String> triggerResult,
                                     StringBuilder triggerMsgSb, List<String> addressList, Map methodMap) {
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = (ExecutorRouteStrategyEnum) methodMap.get(JobConstant.EXECUTORROUTESTRATEGYENUM);
        ExecutorBlockStrategyEnum blockStrategy = (ExecutorBlockStrategyEnum) methodMap.get(JobConstant.BLOCK_STRATEGY);
        ExecutorFailStrategyEnum failStrategy = (ExecutorFailStrategyEnum) methodMap.get(JobConstant.FAIL_STRATEGY);
        triggerMsgSb.append("注册方式：").append("");
        triggerMsgSb.append("<br>阻塞处理策略：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>调度异常处理：").append(failStrategy.getTitle());
        triggerMsgSb.append("<br>地址列表：").append("");
        triggerMsgSb.append("<br>路由策略：").append(executorRouteStrategyEnum.getTitle());
        if (triggerResult.getCode() == ReturnT.SUCCESS_CODE && CollectionUtils.isEmpty(addressList)) {
            triggerResult.setCode(ReturnT.FAIL_CODE);
            triggerMsgSb.append("<br>----------------------<br>").append("调度失败：").append("执行器地址为空");
        }
    }

    @Override
    public Map<String, Object> failRestrt(ExecutorRouteStrategyEnum executorRouteStrategyEnum, TriggerParam triggerParam,
                                          StringBuilder triggerMsgSb, ReturnT<String> triggerResult, XxlJobInfo jobInfo,
                                          AtomicAlgorithmDTO atomicAlgorithmDTO) {
        HashMap<String, Object> map = new HashMap();
        int maxRetryTime = jobInfo.getFailRetryTimes();
        int time = 0;
        do {
            time++;
            try {
                //检查存活节点重新获取
                ArrayList<String> addressList = (ArrayList<String>) this.findAddressList(jobInfo.getModelId(),
                        atomicAlgorithmDTO.getDealAmount());
                triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
                triggerMsgSb.append("<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试<<<<<<<<<<< </span><br>")
                        .append(triggerResult.getMsg());
                if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
                    map.put(JobConstant.TRIGGERRESULT, triggerResult);
                    map.put(JobConstant.TRIGGERMSGSB, triggerMsgSb);
                    return map;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } while (time < maxRetryTime);
        map.put(JobConstant.TRIGGERRESULT, triggerResult);
        map.put(JobConstant.TRIGGERMSGSB, triggerMsgSb);
        return map;

    }

    public void insertFailLogByFile(String msg,XxlJobInfo jobInfo ,int code){
        XxlJobLog jobLog = new XxlJobLog();
        this.saveJobLog(jobInfo, jobLog);
        jobLog.setHandleMsg(msg);
        jobLog.setTriggerCode(200);
        jobLog.setHandleCode(code);
        jobLog.setHandleTime(new Date());
        this.updateJobLog(jobInfo,jobLog);
        xxlJobLogDao.updateTriggerInfo(jobLog);
        xxlJobLogDao.updateHandleInfo(jobLog);

    }
}


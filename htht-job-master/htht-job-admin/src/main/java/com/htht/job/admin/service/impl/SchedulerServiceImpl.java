package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/4/16.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.thread.JobFailMonitorHelper;
import com.htht.job.admin.service.SchedulerUtilService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job
 * @description: 公用调度
 * @author: zzj
 * @create: 2018-04-16 20:54
 **/
@Service
public class SchedulerServiceImpl {
    private static Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Resource
    protected TaskParametersService taskParametersService;
    @Resource
    protected DubboService dubboService;
    @Resource
    protected AtomicAlgorithmService atomicAlgorithmService;
    @Resource
    protected SchedulerUtilService schedulerUtilService;


    public void depositMap(XxlJobInfo jobInfo, Map methodMap) {
        Map fixmap = new LinkedHashMap();
        Map dymap = new LinkedHashMap();
        String modelParameters = "";
        TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
        if (jobInfo.getOperation() == 0) {
            fixmap = taskParametersService.getJobParameter(jobInfo.getExecutorParam(), jobInfo.getModelId(), "1");
            dymap = taskParametersService.getJobParameter(jobInfo.getExecutorParam(), jobInfo.getModelId(), "2");
            modelParameters = taskParametersDTO.getModelParameters();
        } else {
            List<CommonParameter> commonParametersDy = JSON.parseArray(jobInfo.getDynamicParameter(), CommonParameter.class);
            for (CommonParameter commonParameter : commonParametersDy) {
                dymap.put(commonParameter.getParameterName(), commonParameter.getValue());
            }
            List<CommonParameter> commonParametersFix = JSON.parseArray(jobInfo.getFixedParameter(), CommonParameter.class);
            for (CommonParameter commonParameter : commonParametersFix) {
                fixmap.put(commonParameter.getParameterName(), commonParameter.getValue());
            }
            modelParameters = jobInfo.getModelParameters();
        }
        String formatmodelParameters = taskParametersService.formatJobModelParameters(modelParameters);
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        ExecutorFailStrategyEnum failStrategy = ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), ExecutorFailStrategyEnum.FAIL_ALARM);
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(jobInfo.getModelId());
        ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(jobInfo.getModelId(), atomicAlgorithmDTO.getDealAmount());
        methodMap.put(JobConstant.FIX_MAP, fixmap);
        methodMap.put(JobConstant.DY_MAP, dymap);
        methodMap.put(JobConstant.TASK_PARAMETERS, taskParametersDTO);
        methodMap.put(JobConstant.MODEL_PARAMETERS, modelParameters);
        methodMap.put(JobConstant.FORMAT_MODEL_PARAMETERS, formatmodelParameters);
        methodMap.put(JobConstant.BLOCK_STRATEGY, blockStrategy);
        methodMap.put(JobConstant.FAIL_STRATEGY, failStrategy);
        methodMap.put(JobConstant.EXECUTORROUTESTRATEGYENUM, executorRouteStrategyEnum);
        methodMap.put(JobConstant.JOBINFO, jobInfo);
        methodMap.put(JobConstant.ATOMICALGORITHM, atomicAlgorithmDTO);
        methodMap.put(JobConstant.ADDRESSLIST, addressList);


    }

    public void excute(Map methodMap, XxlJobLog jobLog, String executorParams, String parallelLogId, LinkedHashMap fixmap) {
        ArrayList<String> addressList = (ArrayList<String>) methodMap.get(JobConstant.ADDRESSLIST);
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = (ExecutorRouteStrategyEnum) methodMap.get(JobConstant.EXECUTORROUTESTRATEGYENUM);
        ExecutorFailStrategyEnum failStrategy = (ExecutorFailStrategyEnum) methodMap.get(JobConstant.FAIL_STRATEGY);
        AtomicAlgorithmDTO atomicAlgorithmDTO = (AtomicAlgorithmDTO) methodMap.get(JobConstant.ATOMICALGORITHM);
        XxlJobInfo jobInfo = (XxlJobInfo) methodMap.get(JobConstant.JOBINFO);
        ReturnT<String> triggerResult = new ReturnT<>(null);
        StringBuilder triggerMsgSb = new StringBuilder();
        schedulerUtilService.acquireTriggerResult(triggerResult, triggerMsgSb, addressList, methodMap);
        schedulerUtilService.updateJobLog(jobInfo, jobLog);
        if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
            TriggerParam triggerParam = new TriggerParam();
            /** =======3.获取调度参数=========== **/
            this.acquireTriggerParam(triggerParam, jobLog, executorParams, fixmap, parallelLogId, methodMap);
            /** =======4.调度执行器=========== **/
            triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
            triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());
            if (triggerResult.getCode() != ReturnT.SUCCESS_CODE && failStrategy == ExecutorFailStrategyEnum.FAIL_RETRY_FIVE) {
                schedulerUtilService.failRestrtFive(executorRouteStrategyEnum, triggerParam, triggerMsgSb, triggerResult, jobInfo, atomicAlgorithmDTO);
            }
            if (triggerResult.getCode() != ReturnT.SUCCESS_CODE && failStrategy == ExecutorFailStrategyEnum.FAIL_RETRY) {
                schedulerUtilService.failRestrt(executorRouteStrategyEnum, triggerParam, triggerMsgSb, triggerResult, jobInfo, atomicAlgorithmDTO);
            }

        }
        /** =======5.保存 trigger-info=========== **/
        schedulerUtilService.updateTriggerInfo(jobLog, triggerResult, triggerMsgSb);
        /** =======6.发送警告=========== **/
        JobFailMonitorHelper.monitor(jobLog.getId());
        logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
    }


    private void acquireTriggerParam(TriggerParam triggerParam, XxlJobLog jobLog, String executorParams, LinkedHashMap dymap, String parallelLogId, Map methodMap) {
        LinkedHashMap fixmap = (LinkedHashMap) methodMap.get(JobConstant.FIX_MAP);
        XxlJobInfo jobInfo = (XxlJobInfo) methodMap.get(JobConstant.JOBINFO);
        AtomicAlgorithmDTO atomicAlgorithmDTO = (AtomicAlgorithmDTO) methodMap.get(JobConstant.ATOMICALGORITHM);
        String modelParameters = (String) methodMap.get(JobConstant.MODEL_PARAMETERS);
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(atomicAlgorithmDTO.getModelIdentify());
        triggerParam.setExecutorParams(executorParams);
        triggerParam.setExecutorBlockStrategy(atomicAlgorithmDTO.getExecutorBlockStrategy());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setBroadcastIndex(0);
        triggerParam.setBroadcastTotal(1);
        triggerParam.setModelParameters(modelParameters);
        triggerParam.setFixedParameter(fixmap);
        triggerParam.setDynamicParameter(dymap);
        triggerParam.setModelId(jobInfo.getModelId());
        triggerParam.setProductId(jobInfo.getProductId());
        triggerParam.setAlgorId(atomicAlgorithmDTO.getId());
        triggerParam.setPriority(jobInfo.getPriority());
        triggerParam.setParallelLogId(parallelLogId);
        triggerParam.setFlow(false);
    }
}


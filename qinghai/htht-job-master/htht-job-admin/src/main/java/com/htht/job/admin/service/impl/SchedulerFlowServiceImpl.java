package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/4/16.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.jobbean.ParamClassifyBean;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.model.app.FlowLogVo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.service.SchedulerFlowService;
import com.htht.job.admin.service.impl.scheduler.SettleDocumentsServiceImpl;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.constant.BooleanConstant;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowchart.FlowChartModel;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.processsteps.ProcessSteps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: htht-job
 * @description: 公用调度
 * @author: zzj
 * @create: 2018-04-16 20:54
 **/
@Service
public class SchedulerFlowServiceImpl extends SettleDocumentsServiceImpl implements SchedulerFlowService {
    private static Logger logger = LoggerFactory.getLogger(SchedulerFlowServiceImpl.class);

    protected void firstStepParam(Map paramMap, String modelId, ResultUtil resultUtil) {
        ProcessSteps processSteps = (ProcessSteps) paramMap.get(JobConstant.processSteps);
        String dynamicParameter = (String) paramMap.get(JobConstant.dynamicParameter);
        List<ParallelLog> parallelLogList = new ArrayList<>();
        List<CommonParameter> taskParams = JSON.parseArray(dynamicParameter, CommonParameter.class);
        /**======1获取流程参数==========**/
        List<CommonParameter> flowParams = JSON.parseArray(processSteps.getDynamicParameter(), CommonParameter.class);
        commonParameterService.repalceListValueByDataId(flowParams, taskParams);
        processSteps.setDynamicParameter(JSON.toJSONString(flowParams));
        /**======2处理输入参数==========**/
        ParamClassifyBean paramClassifyBean = new ParamClassifyBean();
        paramMap.put(JobConstant.label, processSteps.getLabel());
        this.handleParam(paramMap,flowParams,paramClassifyBean,modelId,resultUtil);
        if (!resultUtil.isSuccess()) {
            return;
        }
        if (!paramClassifyBean.getOutFolder().isEmpty()) {
            this.setFolder(paramClassifyBean.getOutFolder(), flowParams, paramMap);
        }

        if (!paramClassifyBean.getFileList().isEmpty()) {
            //设置输入和输出
            this.setOutInputValue(paramMap, paramClassifyBean, parallelLogList);

        } else {
            ParallelLog parallelLog = new ParallelLog();
            parallelLog.setDynamicParameter(JSON.toJSONString(flowParams));
            parallelLogList.add(parallelLog);
        }

        processSteps.setParallelLogs(parallelLogList);
        paramMap.put(JobConstant.processSteps, processSteps);
    }

    protected  void handleParam(Map paramMap,List<CommonParameter> flowParams,ParamClassifyBean paramClassifyBean,String modelId,ResultUtil<String> resultUtil){
        String outputDirectory = (String) paramMap.get(JobConstant.outputDirectory);
        Map mapDataId = new HashMap(20);
        this.paramClassifyBean(paramClassifyBean, flowParams, mapDataId);

        /**======1扫描文件列表变换值==========**/
        if (!paramClassifyBean.getInFile().isEmpty()) {
            this.addFileParam(paramClassifyBean.getInFile().get(0), paramClassifyBean, resultUtil);
            if (!resultUtil.isSuccess()) {
                return;
            }
        }

        /**======2扫描子文件夹变换值==========**/
        if (!paramClassifyBean.getInFolder().isEmpty() && paramClassifyBean.getFileList().isEmpty()) {
            this.addFolderParam(paramClassifyBean.getInFolder().get(0), paramClassifyBean, resultUtil);
            if (!resultUtil.isSuccess()) {
                return;
            }
        }

        /**======3生成参数和输出==========**/
        FlowChartModel flowChartModel = dubboService.getFlowById(modelId);
        paramMap.put(JobConstant.mapDataId, mapDataId);
        paramMap.put(JobConstant.flowChart, flowChartModel);
        paramMap.put(JobConstant.flowParams, flowParams);
        paramMap.put(JobConstant.outputDirectory, outputDirectory);

    }
    protected void handleFirstStep(Map map, FlowLogVo flowLogVo, FlowLogVo flowLogVo1, ResultUtil<String> resultUtil) {
        ProcessSteps processSteps = (ProcessSteps) map.get(JobConstant.processSteps);
        XxlJobInfo jobInfo = (XxlJobInfo) map.get(JobConstant.jobInfo);
        try {
            /**=======1.如果节点是一个流程，对节点进行处理===========**/
            if (BooleanConstant.TRUE.equals(processSteps.getIsProcess())) {
                /**=======2.处理子流程参数===========**/
                handleFlowLogService.handleFistStepParam(processSteps, flowLogVo, map, resultUtil);
                if (!resultUtil.isSuccess()) {
                    return;
                }
                /**=======3.进行递归处理===========**/
                handleFirstStep(map, flowLogVo.getFlowLogVo(), flowLogVo1, resultUtil);
            } else {
                /**=======4.处理第一步参数===========**/
                map.put(JobConstant.jobId, jobInfo.getId());
                map.put(JobConstant.operation, jobInfo.getOperation());
                this.firstStepParam(map, jobInfo.getModelId(), resultUtil);
                if (!resultUtil.isSuccess()) {
                    return;
                }
                ProcessSteps dealProcessSteps = (ProcessSteps) map.get(JobConstant.processSteps);
                List<ParallelLog> parallelLogList = dealProcessSteps.getParallelLogs();
                /**=======5.递归完成发起调度===========**/
                this.execute(map, dealProcessSteps, flowLogVo, flowLogVo1, parallelLogList);

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    protected void batchExecute(Map map, List<ParallelLog> parallelLogList, FlowLogVo flowLogVo, FlowLogVo flowLogVo1) {
        ProcessSteps processSteps = (ProcessSteps) map.get(JobConstant.processSteps);
        XxlJobInfo jobInfo = (XxlJobInfo) map.get(JobConstant.jobInfo);
        /**=======1.获取原子算法===========**/
        AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(processSteps.getServiceId());
        /**=======2.获取地址===========**/
        ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithm.getId(), atomicAlgorithm.getDealAmount());
        map.put(JobConstant.atomicAlgorithm, atomicAlgorithm);
        map.put(JobConstant.addressList, addressList);
        /**=======3.保存调度日志===========**/
        XxlJobLog jobLog = (XxlJobLog) map.get(JobConstant.jobLog);
        if (null == jobLog) {
            jobLog = new XxlJobLog();
            schedulerUtilService.saveJobLog(jobInfo, jobLog);
        }
        /**=======4.处理流程日志===========**/
        handleFlowLogService.batchExecuteFlowlog(map, flowLogVo, flowLogVo1, jobLog, parallelLogList.size(), atomicAlgorithm);
        /**=======5.修改调度日志===========**/
        schedulerUtilService.updateJobLog(jobInfo, jobLog);
        /**=======6.注册方式，各种策略，地址列表===========**/
        ReturnT<String> triggerResult = new ReturnT<>(null);
        StringBuilder triggerMsgSb = new StringBuilder();
        if (triggerResult.getCode() == ReturnT.SUCCESS_CODE && CollectionUtils.isEmpty(addressList)) {
            triggerResult.setCode(ReturnT.FAIL_CODE);
            triggerMsgSb.append("<br>----------------------<br>").append("调度失败：").append("执行器地址为空");
        }
        /**=======7.发起调度===========**/
        for (int i = 0; i < parallelLogList.size(); i++) {
            this.startExecute(parallelLogList.get(i), map, jobLog, triggerResult, triggerMsgSb);
        }
        /**=======8.保存 trigger-info===========**/
        schedulerUtilService.updateTriggerInfo(jobLog, triggerResult, triggerMsgSb);
    }


    protected void noBatchExecute(Map map, List<ParallelLog> parallelLogList, FlowLogVo flowLogVo, FlowLogVo flowLogVo1) {
        ProcessSteps processSteps = (ProcessSteps) map.get(JobConstant.processSteps);
        XxlJobInfo jobInfo = (XxlJobInfo) map.get(JobConstant.jobInfo);
        /**=======1.获取原子算法===========**/
        AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(processSteps.getServiceId());
        /**=======2.获取地址===========**/
        ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithm.getId(), atomicAlgorithm.getDealAmount());
        map.put(JobConstant.atomicAlgorithm, atomicAlgorithm);
        map.put(JobConstant.addressList, addressList);
        for (int i = 0; i < parallelLogList.size(); i++) {
            /**=======3.保存调度日志===========**/
            XxlJobLog jobLog = (XxlJobLog) map.get(JobConstant.jobLog);
            if (null == jobLog) {
                jobLog = new XxlJobLog();
                schedulerUtilService.saveJobLog(jobInfo, jobLog);
            }
            /**=======4.处理流程日志===========**/
            handleFlowLogService.noBatchExecuteFlowlog(map,parallelLogList.get(i),flowLogVo,flowLogVo1,jobLog,atomicAlgorithm);
            /**=======5.修改调度日志===========**/
            schedulerUtilService.updateJobLog(jobInfo, jobLog);
            /**=======6.注册方式，各种策略，地址列表===========**/
            ReturnT<String> triggerResult = new ReturnT(null);
            StringBuilder triggerMsgSb = new StringBuilder();
            if (triggerResult.getCode() == ReturnT.SUCCESS_CODE && CollectionUtils.isEmpty(addressList)) {
                triggerResult.setCode(ReturnT.FAIL_CODE);
                triggerMsgSb.append("<br>----------------------<br>").append("调度失败：").append("执行器地址为空");
            }
            /**=======7.发起调度===========**/
            this.startExecute(parallelLogList.get(i), map, jobLog, triggerResult, triggerMsgSb);

            /**=======8.保存 trigger-info===========**/
            schedulerUtilService.updateTriggerInfo(jobLog, triggerResult, triggerMsgSb);
        }


    }


    protected void execute(Map map, ProcessSteps processSteps, FlowLogVo flowLogVo, FlowLogVo flowLogVo1, List<ParallelLog> parallelLogList) {
        if (BooleanConstant.TRUE.equals(processSteps.getIsPl())) {

            this.batchExecute(map, parallelLogList, flowLogVo, flowLogVo1);

        } else {
            this.noBatchExecute(map, parallelLogList, flowLogVo, flowLogVo1);
        }

    }

    /**
     * @Description: 发起调度
     * @Param: [parallelLog, map, jobLog, triggerResult, triggerMsgSb]
     * @return: void
     * @Author: zzj
     * @Date: 2018/10/30
     */
    public void startExecute(ParallelLog parallelLog, Map map, XxlJobLog jobLog, ReturnT<String> triggerResult, StringBuilder triggerMsgSb) {
        ArrayList<String> addressList = (ArrayList<String>) map.get(JobConstant.addressList);
        XxlJobInfo jobInfo = (XxlJobInfo) map.get(JobConstant.jobInfo);
        String flowLogId = (String) map.get(JobConstant.flowLogId);
        AtomicAlgorithm atomicAlgorithm = (AtomicAlgorithm) map.get(JobConstant.atomicAlgorithm);
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = (ExecutorRouteStrategyEnum) map.get(JobConstant.executorRouteStrategyEnum);
        String parentFlowlogId = (String) map.get(JobConstant.parentFlowlogId);
        /**=======1.保存并行日志===========**/
        parallelLog.setFlowId(flowLogId);
        parallelLog.setCreateTime(new Date());
        parallelLog.setParentFlowLogId(parentFlowlogId);
        parallelLog = dubboService.saveParallelLog(parallelLog);
        TriggerParam triggerParam = new TriggerParam();
        LinkedHashMap fixmap = schedulerUtilService.transformMap(atomicAlgorithm.getFixedParameter());
        LinkedHashMap dymap = schedulerUtilService.transformMap(parallelLog.getDynamicParameter());
        /*************入库流程参数  20180503 liyuan 添加  start*******************/
        if (null != jobInfo.getJsonString() && !"".equals(jobInfo.getJsonString())) {
            dymap.put("jsonString", jobInfo.getJsonString());
        }
        /*************入库流程参数  20180503 liyuan 添加      end *******************/
        /**=======2.调度信息封装===========**/
        schedulerUtilService.saveTriggerParam(triggerParam, jobInfo, atomicAlgorithm, jobLog, fixmap, dymap, parallelLog);
        /**=======3.调度执行器===========**/
        if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
            triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
            triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());
        }
        //调度失败重试
        if (triggerResult.getCode() != ReturnT.SUCCESS_CODE) {
            //重新查找存活节点
             schedulerUtilService.failRestrtFive(executorRouteStrategyEnum, triggerParam, triggerMsgSb, triggerResult, jobInfo, atomicAlgorithm);

        }
    }
    protected void batchExecute(ProcessSteps processSteps, List<ParallelLog> parallelLogList,Map paramMap) {
        XxlJobLog jobLog= (XxlJobLog) paramMap.get(JobConstant.jobLog);
        String flowChartId= (String) paramMap.get(JobConstant.flowChartId);
        String parentFlowlogId= (String) paramMap.get(JobConstant.parentFlowlogId);
        /**=======1.获取原子算法和地址列表===========**/
        AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(processSteps.getServiceId());
        ArrayList<String> addressList=(ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithm.getId(), atomicAlgorithm.getDealAmount());
        paramMap.put(JobConstant.atomicAlgorithm,atomicAlgorithm);
        paramMap.put(JobConstant.addressList,addressList);
        /**=======2.封装流程日志信息保存===========**/
        FlowLog flowLog = new FlowLog();
        flowLog.setIsPl(BooleanConstant.TRUE);
        handleFlowLogService.setFlowLog(processSteps, flowLog, parentFlowlogId, flowChartId, parallelLogList.size(),"1");
        String flowLogId = handleFlowLogService.saveFlowLog(flowLog, processSteps, jobLog.getId(), atomicAlgorithm);
        paramMap.put(JobConstant.flowLogId,flowLogId);

        for (int i = 0; i < parallelLogList.size(); i++) {
            /**=======3.注册方式，各种策略，地址列表===========**/
            ReturnT<String> triggerResult = new ReturnT(null);
            StringBuilder triggerMsgSb = new StringBuilder();
            /**=======4.发起调度===========**/
            this.startExecute(parallelLogList.get(i),paramMap,jobLog,triggerResult,triggerMsgSb);
            /**=======5.保存 trigger-info===========**/
            schedulerUtilService.updateHandleInfo(processSteps,jobLog,triggerResult);

        }

    }

    protected void noBatchExecute(ProcessSteps processSteps, List<ParallelLog> parallelLogList,Map paramMap) {
        XxlJobLog jobLog= (XxlJobLog) paramMap.get(JobConstant.jobLog);
        String flowChartId= (String) paramMap.get(JobConstant.flowChartId);
        String parentFlowlogId= (String) paramMap.get(JobConstant.parentFlowlogId);
        /**=======1.获取原子算法和地址列表===========**/
        AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(processSteps.getServiceId());
        ArrayList<String> addressList=(ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithm.getId(), atomicAlgorithm.getDealAmount());
        paramMap.put(JobConstant.atomicAlgorithm,atomicAlgorithm);
        paramMap.put(JobConstant.addressList,addressList);
        for (int i = 0; i < parallelLogList.size(); i++) {
            /**=======2.注册方式，各种策略，地址列表===========**/
            ReturnT<String> triggerResult = new ReturnT(null);
            StringBuilder triggerMsgSb = new StringBuilder();
            /**=======3.封装流程日志信息保存===========**/
            FlowLog flowLog = new FlowLog();
            flowLog.setIsPl(BooleanConstant.FALSE);
            handleFlowLogService.setFlowLog(processSteps, flowLog, parentFlowlogId, flowChartId, 1,"1");
            String flowLogId = handleFlowLogService.saveFlowLog(flowLog, processSteps, jobLog.getId(), atomicAlgorithm);
            paramMap.put(JobConstant.flowLogId,flowLogId);
            /**=======4.发起调度===========**/
            this.startExecute(parallelLogList.get(i),paramMap,jobLog,triggerResult,triggerMsgSb);
            /**=======5.保存 trigger-info===========**/
            schedulerUtilService.updateHandleInfo(processSteps,jobLog,triggerResult);
        }


    }
    public void depositNextStepMap(Map paramMap,XxlJobLog jobLog,XxlJobInfo jobInfo,String flowChartId,String parentFlowlogId){
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        paramMap.put(JobConstant.parentFlowlogId,parentFlowlogId);
        paramMap.put(JobConstant.jobInfo,jobInfo);
        paramMap.put(JobConstant.jobLog,jobLog);
        paramMap.put(JobConstant.flowChartId,flowChartId);
        paramMap.put(JobConstant.executorRouteStrategyEnum,executorRouteStrategyEnum);

    }
    public void depositHandSchedulerMap(Map map,XxlJobLog jobLog,XxlJobInfo jobInfo,ProcessSteps processSteps,String dynamicParameter,String dataId){
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        map.put(JobConstant.processSteps, processSteps);
        map.put(JobConstant.dynamicParameter, dynamicParameter);
        map.put(JobConstant.modelId, jobInfo.getModelId());
        map.put(JobConstant.executorRouteStrategyEnum, executorRouteStrategyEnum);
        map.put(JobConstant.jobInfo, jobInfo);
        map.put(JobConstant.jobLog, jobLog);
        map.put(JobConstant.dataId, dataId);
        map.put(JobConstant.isProcess,processSteps.getIsProcess());

    }
}


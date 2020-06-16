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
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowchart.FlowChartDTO;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
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
        ProcessStepsDTO processStepsDTO = (ProcessStepsDTO) paramMap.get(JobConstant.PROCESSSTEPS);
        String dynamicParameter = (String) paramMap.get(JobConstant.DYNAMICPARAMETER);
        List<ParallelLogDTO> parallelLogDTOList = new ArrayList<>();
        List<CommonParameter> taskParams = JSON.parseArray(dynamicParameter, CommonParameter.class);
        /**======1获取流程参数==========**/
        List<CommonParameter> flowParams = JSON.parseArray(processStepsDTO.getDynamicParameter(), CommonParameter.class);
        commonParameterService.repalceListValueByDataId(flowParams, taskParams);
        processStepsDTO.setDynamicParameter(JSON.toJSONString(flowParams));
        /**======2处理输入参数==========**/
        ParamClassifyBean paramClassifyBean = new ParamClassifyBean();
        paramMap.put(JobConstant.LABEL, processStepsDTO.getLabel());
        this.handleParam(paramMap, flowParams, paramClassifyBean, modelId, resultUtil);
        if (!resultUtil.isSuccess()) {
            XxlJobInfo jobInfo = (XxlJobInfo) paramMap.get(JobConstant.JOBINFO);
            schedulerUtilService.insertFailLogByFile("没有数据",jobInfo,200);
            return;
        }
        if (!paramClassifyBean.getOutFolder().isEmpty()) {
            this.setFolder(paramClassifyBean.getOutFolder(), flowParams, paramMap);
        }

        if (!paramClassifyBean.getFileList().isEmpty()) {
            //设置输入和输出
            this.setOutInputValue(paramMap, paramClassifyBean, parallelLogDTOList);

        } else {
            ParallelLogDTO parallelLogDTO = new ParallelLogDTO();
            parallelLogDTO.setDynamicParameter(JSON.toJSONString(flowParams));
            parallelLogDTOList.add(parallelLogDTO);
        }

        processStepsDTO.setParallelLogDTOS(parallelLogDTOList);
        paramMap.put(JobConstant.PROCESSSTEPS, processStepsDTO);
    }

    protected void handleParam(Map paramMap, List<CommonParameter> flowParams, ParamClassifyBean paramClassifyBean, String modelId, ResultUtil<String> resultUtil) {
        String outputDirectory = (String) paramMap.get(JobConstant.OUTPUTDIRECTORY);
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
        FlowChartDTO flowChartDTO = dubboService.getFlowById(modelId);
        paramMap.put(JobConstant.MAPDATAID, mapDataId);
        paramMap.put(JobConstant.FLOWCHART, flowChartDTO);
        paramMap.put(JobConstant.FLOWPARAMS, flowParams);
        paramMap.put(JobConstant.OUTPUTDIRECTORY, outputDirectory);

    }

    protected void handleFirstStep(Map map, FlowLogVo flowLogVo, FlowLogVo flowLogVo1, ResultUtil<String> resultUtil) {
        ProcessStepsDTO processStepsDTO = (ProcessStepsDTO) map.get(JobConstant.PROCESSSTEPS);
        XxlJobInfo jobInfo = (XxlJobInfo) map.get(JobConstant.JOBINFO);
        try {
            /**=======1.如果节点是一个流程，对节点进行处理===========**/
            if (BooleanConstant.TRUE.equals(processStepsDTO.getIsProcess())) {
                /**=======2.处理子流程参数===========**/
                handleFlowLogService.handleFistStepParam(processStepsDTO, flowLogVo, map, resultUtil);
                if (!resultUtil.isSuccess()) {
                    return;
                }
                /**=======3.进行递归处理===========**/
                handleFirstStep(map, flowLogVo.getFlowLogVo(), flowLogVo1, resultUtil);
            } else {
                /**=======4.处理第一步参数===========**/
                map.put(JobConstant.JOBID, jobInfo.getId());
                map.put(JobConstant.OPERATION, jobInfo.getOperation());
                this.firstStepParam(map, jobInfo.getModelId(), resultUtil);
                if (!resultUtil.isSuccess()) {
                    return;
                }
                ProcessStepsDTO dealProcessStepsDTO = (ProcessStepsDTO) map.get(JobConstant.PROCESSSTEPS);
                List<ParallelLogDTO> parallelLogDTOList = dealProcessStepsDTO.getParallelLogDTOS();
                /**=======5.递归完成发起调度===========**/
                this.execute(map, dealProcessStepsDTO, flowLogVo, flowLogVo1, parallelLogDTOList);

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    protected void batchExecute(Map map, List<ParallelLogDTO> parallelLogDTOList, FlowLogVo flowLogVo, FlowLogVo flowLogVo1) {
        ProcessStepsDTO processStepsDTO = (ProcessStepsDTO) map.get(JobConstant.PROCESSSTEPS);
        XxlJobInfo jobInfo = (XxlJobInfo) map.get(JobConstant.JOBINFO);
        /**=======1.获取原子算法===========**/
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(processStepsDTO.getServiceId());
        /**=======2.获取地址===========**/
        ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithmDTO.getId(), atomicAlgorithmDTO.getDealAmount());
        map.put(JobConstant.ATOMICALGORITHM, atomicAlgorithmDTO);
        map.put(JobConstant.ADDRESSLIST, addressList);
        /**=======3.保存调度日志===========**/
        XxlJobLog jobLog = (XxlJobLog) map.get(JobConstant.JOBLOG);
        if (null == jobLog) {
            jobLog = new XxlJobLog();
            schedulerUtilService.saveJobLog(jobInfo, jobLog);
        }
        /**=======4.处理流程日志===========**/
        handleFlowLogService.batchExecuteFlowlog(map, flowLogVo, flowLogVo1, jobLog, parallelLogDTOList.size(), atomicAlgorithmDTO);
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
        for (int i = 0; i < parallelLogDTOList.size(); i++) {
            this.startExecute(parallelLogDTOList.get(i), map, jobLog, triggerResult, triggerMsgSb);
        }
        /**=======8.保存 trigger-info===========**/
        schedulerUtilService.updateTriggerInfo(jobLog, triggerResult, triggerMsgSb);
    }


    protected void noBatchExecute(Map map, List<ParallelLogDTO> parallelLogDTOList, FlowLogVo flowLogVo, FlowLogVo flowLogVo1) {
        ProcessStepsDTO processStepsDTO = (ProcessStepsDTO) map.get(JobConstant.PROCESSSTEPS);
        XxlJobInfo jobInfo = (XxlJobInfo) map.get(JobConstant.JOBINFO);
        /**=======1.获取原子算法===========**/
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(processStepsDTO.getServiceId());
        /**=======2.获取地址===========**/
        ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithmDTO.getId(), atomicAlgorithmDTO.getDealAmount());
        map.put(JobConstant.ATOMICALGORITHM, atomicAlgorithmDTO);
        map.put(JobConstant.ADDRESSLIST, addressList);
        for (int i = 0; i < parallelLogDTOList.size(); i++) {
            /**=======3.保存调度日志===========**/
            XxlJobLog jobLog = (XxlJobLog) map.get(JobConstant.JOBLOG);
            if (null == jobLog) {
                jobLog = new XxlJobLog();
                schedulerUtilService.saveJobLog(jobInfo, jobLog);
            }
            /**=======4.处理流程日志===========**/
            handleFlowLogService.noBatchExecuteFlowlog(map, parallelLogDTOList.get(i), flowLogVo, flowLogVo1, jobLog, atomicAlgorithmDTO);
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
            this.startExecute(parallelLogDTOList.get(i), map, jobLog, triggerResult, triggerMsgSb);

            /**=======8.保存 trigger-info===========**/
            schedulerUtilService.updateTriggerInfo(jobLog, triggerResult, triggerMsgSb);
        }


    }


    protected void execute(Map map, ProcessStepsDTO processStepsDTO, FlowLogVo flowLogVo, FlowLogVo flowLogVo1, List<ParallelLogDTO> parallelLogDTOList) {
        if (BooleanConstant.TRUE.equals(processStepsDTO.getIsPl())) {

            this.batchExecute(map, parallelLogDTOList, flowLogVo, flowLogVo1);

        } else {
            this.noBatchExecute(map, parallelLogDTOList, flowLogVo, flowLogVo1);
        }

    }

    /**
     * @Description: 发起调度
     * @Param: [parallelLog, map, jobLog, triggerResult, triggerMsgSb]
     * @return: void
     * @Author: zzj
     * @Date: 2018/10/30
     */
    public void startExecute(ParallelLogDTO parallelLogDTO, Map map, XxlJobLog jobLog, ReturnT<String> triggerResult, StringBuilder triggerMsgSb) {
        ArrayList<String> addressList = (ArrayList<String>) map.get(JobConstant.ADDRESSLIST);
        XxlJobInfo jobInfo = (XxlJobInfo) map.get(JobConstant.JOBINFO);
        String flowLogId = (String) map.get(JobConstant.FLOWLOGID);
        AtomicAlgorithmDTO atomicAlgorithmDTO = (AtomicAlgorithmDTO) map.get(JobConstant.ATOMICALGORITHM);
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = (ExecutorRouteStrategyEnum) map.get(JobConstant.EXECUTORROUTESTRATEGYENUM);
        String parentFlowlogId = (String) map.get(JobConstant.PARENTFLOWLOGID);
        /**=======1.保存并行日志===========**/
        parallelLogDTO.setFlowId(flowLogId);
        parallelLogDTO.setCreateTime(new Date());
        parallelLogDTO.setParentFlowLogId(parentFlowlogId);
        parallelLogDTO = dubboService.saveParallelLog(parallelLogDTO);
        TriggerParam triggerParam = new TriggerParam();
        LinkedHashMap fixmap = (LinkedHashMap) schedulerUtilService.transformMap(atomicAlgorithmDTO.getFixedParameter());
        LinkedHashMap dymap = (LinkedHashMap) schedulerUtilService.transformMap(parallelLogDTO.getDynamicParameter());
        /*************入库流程参数  20180503 liyuan 添加  start*******************/
        if (null != jobInfo.getJsonString() && !"".equals(jobInfo.getJsonString())) {
            dymap.put("jsonString", jobInfo.getJsonString());
        }
        /*************入库流程参数  20180503 liyuan 添加      end *******************/
        /**=======2.调度信息封装===========**/
        schedulerUtilService.saveTriggerParam(triggerParam, jobInfo, atomicAlgorithmDTO, jobLog, fixmap, dymap, parallelLogDTO);
        /**=======3.调度执行器===========**/
        if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
            triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
            triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());
        }
        //调度失败重试
        if (triggerResult.getCode() != ReturnT.SUCCESS_CODE) {
            //重新查找存活节点
            schedulerUtilService.failRestrtFive(executorRouteStrategyEnum, triggerParam, triggerMsgSb, triggerResult, jobInfo, atomicAlgorithmDTO);

        }
    }

    protected void batchExecute(ProcessStepsDTO processStepsDTO, List<ParallelLogDTO> parallelLogDTOList, Map paramMap) {
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.JOBLOG);
        String flowChartId = (String) paramMap.get(JobConstant.FLOWCHARTID);
        String parentFlowlogId = (String) paramMap.get(JobConstant.PARENTFLOWLOGID);
        /**=======1.获取原子算法和地址列表===========**/
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(processStepsDTO.getServiceId());
        ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithmDTO.getId(), atomicAlgorithmDTO.getDealAmount());
        paramMap.put(JobConstant.ATOMICALGORITHM, atomicAlgorithmDTO);
        paramMap.put(JobConstant.ADDRESSLIST, addressList);
        /**=======2.封装流程日志信息保存===========**/
        FlowLogDTO flowLogDTO = new FlowLogDTO();
        flowLogDTO.setIsPl(BooleanConstant.TRUE);
        handleFlowLogService.setFlowLog(processStepsDTO, flowLogDTO, parentFlowlogId, flowChartId, parallelLogDTOList.size(), "1");
        String flowLogId = handleFlowLogService.saveFlowLog(flowLogDTO, processStepsDTO, jobLog.getId(), atomicAlgorithmDTO);
        paramMap.put(JobConstant.FLOWLOGID, flowLogId);

        for (int i = 0; i < parallelLogDTOList.size(); i++) {
            /**=======3.注册方式，各种策略，地址列表===========**/
            ReturnT<String> triggerResult = new ReturnT(null);
            StringBuilder triggerMsgSb = new StringBuilder();
            /**=======4.发起调度===========**/
            this.startExecute(parallelLogDTOList.get(i), paramMap, jobLog, triggerResult, triggerMsgSb);
            /**=======5.保存 trigger-info===========**/
            schedulerUtilService.updateHandleInfo(processStepsDTO, jobLog, triggerResult);

        }

    }

    protected void noBatchExecute(ProcessStepsDTO processStepsDTO, List<ParallelLogDTO> parallelLogDTOList, Map paramMap) {
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.JOBLOG);
        String flowChartId = (String) paramMap.get(JobConstant.FLOWCHARTID);
        String parentFlowlogId = (String) paramMap.get(JobConstant.PARENTFLOWLOGID);
        /**=======1.获取原子算法和地址列表===========**/
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(processStepsDTO.getServiceId());
        ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithmDTO.getId(), atomicAlgorithmDTO.getDealAmount());
        paramMap.put(JobConstant.ATOMICALGORITHM, atomicAlgorithmDTO);
        paramMap.put(JobConstant.ADDRESSLIST, addressList);
        for (int i = 0; i < parallelLogDTOList.size(); i++) {
            /**=======2.注册方式，各种策略，地址列表===========**/
            ReturnT<String> triggerResult = new ReturnT(null);
            StringBuilder triggerMsgSb = new StringBuilder();
            /**=======3.封装流程日志信息保存===========**/
            FlowLogDTO flowLogDTO = new FlowLogDTO();
            flowLogDTO.setIsPl(BooleanConstant.FALSE);
            handleFlowLogService.setFlowLog(processStepsDTO, flowLogDTO, parentFlowlogId, flowChartId, 1, "1");
            String flowLogId = handleFlowLogService.saveFlowLog(flowLogDTO, processStepsDTO, jobLog.getId(), atomicAlgorithmDTO);
            paramMap.put(JobConstant.FLOWLOGID, flowLogId);
            /**=======4.发起调度===========**/
            this.startExecute(parallelLogDTOList.get(i), paramMap, jobLog, triggerResult, triggerMsgSb);
            /**=======5.保存 trigger-info===========**/
            schedulerUtilService.updateHandleInfo(processStepsDTO, jobLog, triggerResult);
        }


    }

    @Override
    public void depositNextStepMap(Map paramMap, XxlJobLog jobLog, XxlJobInfo jobInfo, String flowChartId, String parentFlowlogId) {
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        paramMap.put(JobConstant.PARENTFLOWLOGID, parentFlowlogId);
        paramMap.put(JobConstant.JOBINFO, jobInfo);
        paramMap.put(JobConstant.JOBLOG, jobLog);
        paramMap.put(JobConstant.FLOWCHARTID, flowChartId);
        paramMap.put(JobConstant.EXECUTORROUTESTRATEGYENUM, executorRouteStrategyEnum);

    }

    public void depositHandSchedulerMap(Map map, XxlJobLog jobLog, XxlJobInfo jobInfo, ProcessStepsDTO processStepsDTO, String dynamicParameter, String dataId) {
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        map.put(JobConstant.PROCESSSTEPS, processStepsDTO);
        map.put(JobConstant.DYNAMICPARAMETER, dynamicParameter);
        map.put(JobConstant.MODELID, jobInfo.getModelId());
        map.put(JobConstant.EXECUTORROUTESTRATEGYENUM, executorRouteStrategyEnum);
        map.put(JobConstant.JOBINFO, jobInfo);
        map.put(JobConstant.JOBLOG, jobLog);
        map.put(JobConstant.DATAID, dataId);
        map.put(JobConstant.ISPROCESS, processStepsDTO.getIsProcess());

    }
}


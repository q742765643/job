package com.htht.job.admin.service.impl.scheduler;

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.jobbean.ParamClassifyBean;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.model.app.FlowLogVo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.service.FlowSchedulerNextService;
import com.htht.job.admin.service.FlowSchedulerService;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.admin.service.impl.SchedulerFlowServiceImpl;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.constant.BooleanConstant;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @program: htht-job
 * @description: 流程调度
 * @author: zzj
 * @create: 2018-05-15 13:18
 **/
@Service("flowSchedulerService")
public class FlowSchedulerServiceImpl extends SchedulerFlowServiceImpl implements SchedulerService, FlowSchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(FlowSchedulerServiceImpl.class);
    @Autowired
    private FlowSchedulerNextService flowSchedulerNextService;

    @Override
    /**
     * @Description: 流程调度
     * @Param: [jobInfo]
     * @return: void
     * @Author: zzj
     * @Date: 2018/10/30
     */
    public void scheduler(XxlJobInfo jobInfo) {
        ResultUtil<String> resultUtil = new ResultUtil<>();
        try {
            /**=======1.获取开始节点===========**/
            ProcessStepsDTO startProcessStepsDTO = this.findStartFlowCeaselessly(jobInfo.getModelId());
            /**=======2.获取下一个节点===========**/
            List<String> nextIdlist = this.getNextIds(startProcessStepsDTO.getNextId());
            List<ProcessStepsDTO> nextProcessStepDTOS = this.findNextFlowCeaselessly(nextIdlist, jobInfo.getModelId(), resultUtil);
            if (!resultUtil.isSuccess()) {
                return;
            }
            /**=======3.处理输入参数===========**/
            String dynamicParameter = this.depositDynamicParameter(jobInfo, resultUtil);
            if (!resultUtil.isSuccess()) {
                return;
            }
            //流程第一步不能多模块，如果存在两个输入文件无法组合取第一条
            ProcessStepsDTO processStepsDTO = nextProcessStepDTOS.get(0);
            this.checkBroad(processStepsDTO, dynamicParameter, resultUtil, jobInfo.getId());
            if (!resultUtil.isSuccess()) {
                return;
            }
            Map map = new HashMap();
            /**=======4.参数放入map进行传递===========**/
            this.depositMap(map, processStepsDTO, dynamicParameter, jobInfo, resultUtil);
            if (!resultUtil.isSuccess()) {
                return;
            }
            /**=======5.处理参数获取并行数量并调度===========**/
            FlowLogVo flowLogVo = new FlowLogVo();
            this.handleFirstStep(map, flowLogVo, flowLogVo, resultUtil);
        } catch (Exception e) {
            resultUtil.setErrorMessage(e.getMessage());
            logger.error(e.getMessage(), e);
        }


    }

    /**
     * @Description: 获取输入参数
     * @Param: [jobInfo, resultUtil]
     * @return: java.lang.String
     * @Author: zzj
     * @Date: 2018/10/30
     */
    public String depositDynamicParameter(XxlJobInfo jobInfo, ResultUtil<String> resultUtil) {
        String dynamicParameter = "";
        try {
            //opration为0为自动调用从库获取流程参数，非0接口调用或者页面传入参数
            if (0 == jobInfo.getOperation()) {
                TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
                dynamicParameter = taskParametersDTO.getDynamicParameter();
            } else {
                dynamicParameter = jobInfo.getDynamicParameter();
            }
        } catch (Exception e) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_305_ERROR);
            logger.error(e.getMessage(), e);
        }
        return dynamicParameter;
    }

    /**
     * @Description: 参数存入Map进行传递
     * @Param: [map, processSteps, dynamicParameter, jobInfo]
     * @return: void
     * @Author: zzj
     * @Date: 2018/10/30
     */
    public void depositMap(Map map, ProcessStepsDTO processStepsDTO, String dynamicParameter, XxlJobInfo jobInfo, ResultUtil<String> resultUtil) {
        try {
            ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
            map.put(JobConstant.PROCESSSTEPS, processStepsDTO);
            map.put(JobConstant.DYNAMICPARAMETER, dynamicParameter);
            map.put(JobConstant.MODELID, jobInfo.getModelId());
            map.put(JobConstant.EXECUTORROUTESTRATEGYENUM, executorRouteStrategyEnum);
            map.put(JobConstant.JOBINFO, jobInfo);
            map.put(JobConstant.ISPROCESS, processStepsDTO.getIsProcess());
            List<CommonParameter> startDynamicParameter = JSON.parseArray(dynamicParameter, CommonParameter.class);
            String outputDirectory = this.outputDirectory(startDynamicParameter);
            map.put(JobConstant.OUTPUTDIRECTORY, outputDirectory);
        } catch (Exception e) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_306_ERROR);
            logger.error(e.getMessage(), e);
        }
    }

    public void checkBroad(ProcessStepsDTO processStepsDTO, String dynamicParameter, ResultUtil<String> resultUtil, int jobId) {
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(processStepsDTO.getServiceId());
        if (atomicAlgorithmDTO.getModelIdentify().indexOf("Broad") != -1) {
            TriggerParam triggerParam = new TriggerParam();
            List<CommonParameter> taskParams = JSON.parseArray(dynamicParameter, CommonParameter.class);
            /**======1获取流程参数==========**/
            List<CommonParameter> flowParams = JSON.parseArray(processStepsDTO.getDynamicParameter(), CommonParameter.class);
            commonParameterService.repalceListValueByDataId(flowParams, taskParams);
            processStepsDTO.setDynamicParameter(JSON.toJSONString(flowParams));
            LinkedHashMap fixmap = (LinkedHashMap) schedulerUtilService.transformMap(atomicAlgorithmDTO.getFixedParameter());
            LinkedHashMap dymap = (LinkedHashMap) schedulerUtilService.transformMap(JSON.toJSONString(flowParams));
            triggerParam.setFixedParameter(fixmap);
            triggerParam.setDynamicParameter(dymap);
            triggerParam.setExecutorHandler(atomicAlgorithmDTO.getModelIdentify());
            ArrayList<String> addressList = (ArrayList<String>) schedulerUtilService.findAddressList(atomicAlgorithmDTO.getId(), atomicAlgorithmDTO.getDealAmount());

            ExecutorBiz executorBiz = RealReference.getExecutorBiz(addressList.get(0));
            executorBiz.finAllExeuteList(triggerParam, processStepsDTO, flowParams, jobId, atomicAlgorithmDTO, dynamicParameter);
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_311_ERROR);
        }

    }

    public void schedulerRpc(ProcessStepsDTO processStepsDTO, List<CommonParameter> flowParams,
                             List<String> outputList, int jobId, AtomicAlgorithmDTO atomicAlgorithmDTO, String dynamicParameter) {
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
        List<ParallelLogDTO> parallelLogDTOList = new ArrayList<>();
        if (null == outputList || outputList.isEmpty()) {
            return;
        }
        int k = 0;
        for (int i = 0; i < flowParams.size(); i++) {
            if ((FlowConstant.OUTFILE.equals(flowParams.get(i).getParameterType()) ||
                    FlowConstant.OUTSTRING.equals(flowParams.get(i).getParameterType()))
                    && k < outputList.size()) {
                flowParams.get(i).setValue(outputList.get(k));
                k++;
            }
        }

        Map mapDataId = new HashMap();
        ParamClassifyBean paramClassifyBean = new ParamClassifyBean();
        this.paramClassifyBean(paramClassifyBean, flowParams, mapDataId);
        Map<Integer, String> flowMap = paramClassifyBean.getFlowMap();
        if (flowMap.size() == 0) {
            ParallelLogDTO parallelLogDTO = new ParallelLogDTO();
            parallelLogDTO.setDynamicParameter(JSON.toJSONString(flowParams));
            parallelLogDTOList.add(parallelLogDTO);
        } else {
            for (int i = 0; i < paramClassifyBean.getParallel(); i++) {
                for (Map.Entry<Integer, String> entry : flowMap.entrySet()) {
                    flowParams.get(entry.getKey()).setValue(entry.getValue().split("#HT#")[i]);
                }
                ParallelLogDTO parallelLogDTO = new ParallelLogDTO();
                parallelLogDTO.setDynamicParameter(JSON.toJSONString(flowParams));
                parallelLogDTOList.add(parallelLogDTO);
            }
        }
        for (ParallelLogDTO parallelLogDTO : parallelLogDTOList) {
            FlowLogVo flowLogVo = new FlowLogVo();
            XxlJobLog jobLog = new XxlJobLog();
            schedulerUtilService.saveJobLog(jobInfo, jobLog);
            FlowLogDTO startFlowLogDTO = new FlowLogDTO();
            handleFlowLogService.setStartFlowLog(dynamicParameter, processStepsDTO, flowLogVo, jobInfo.getModelId());
            handleFlowLogService.recursiveSave(flowLogVo, jobLog.getId(), "1", startFlowLogDTO);
            FlowLogDTO flowLogDTO = new FlowLogDTO();
            flowLogDTO.setIsPl(BooleanConstant.FALSE);
            processStepsDTO.setDynamicParameter(parallelLogDTO.getDynamicParameter());
            handleFlowLogService.setFlowLog(processStepsDTO, flowLogDTO, "1", jobInfo.getModelId(), 1, "0");
            flowLogDTO.setCode(200);
            String flowLogId = handleFlowLogService.saveFlowLog(flowLogDTO, processStepsDTO, jobLog.getId(), atomicAlgorithmDTO);
            schedulerUtilService.updateJobLog(jobInfo, jobLog);
            parallelLogDTO.setFlowId(flowLogId);
            parallelLogDTO.setCreateTime(new Date());
            parallelLogDTO.setParentFlowLogId("1");
            parallelLogDTO.setCode(200);
            dubboService.saveParallelLog(parallelLogDTO);
            /**=======8.保存 trigger-info===========**/
            ReturnT<String> triggerResult = new ReturnT<>(null);
            StringBuilder triggerMsgSb = new StringBuilder();
            schedulerUtilService.updateTriggerInfo(jobLog, triggerResult, triggerMsgSb);
            Map paramMap = new HashMap();
            /***========2.拼装参数map========**/
            this.depositNextStepMap(paramMap, jobLog, jobInfo, flowLogDTO.getFlowChartId(), flowLogDTO.getParentFlowlogId());
            /***========3.执行下一步========**/
            flowSchedulerNextService.nextStep(processStepsDTO.getNextId().split(","), paramMap);
        }
    }
}


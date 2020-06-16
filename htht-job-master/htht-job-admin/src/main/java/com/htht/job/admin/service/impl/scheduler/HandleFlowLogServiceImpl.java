package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/10/30.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.model.app.FlowLogVo;
import com.htht.job.admin.core.util.RedisUtil;
import com.htht.job.admin.service.CommonParameterService;
import com.htht.job.admin.service.FindStepService;
import com.htht.job.admin.service.HandleFlowLogService;
import com.htht.job.admin.service.SchedulerUtilService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.constant.BooleanConstant;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-30 14:08
 **/
@Service
public class HandleFlowLogServiceImpl implements HandleFlowLogService {
    private static Logger logger = LoggerFactory.getLogger(HandleFlowLogServiceImpl.class);
    @Resource
    private DubboService dubboService;
    @Resource
    private CommonParameterService commonParameterService;
    @Resource
    private SchedulerUtilService schedulerUtilService;
    @Qualifier("findStepServiceImpl")
    @Autowired
    private FindStepService findStepService;
    @Autowired
    private TaskParametersService taskParametersService;

    /**
     * @Description: 递归保存flowlog值
     * @Param: [flowLogVo, jobLogId, parentFlowlogId, flowLog]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void recursiveSave(FlowLogVo flowLogVo, int jobLogId, String parentFlowlogId, FlowLogDTO flowLogDTO) {
        if (null != flowLogVo.getStartFlowLogDTO()) {
            flowLogVo.getStartFlowLogDTO().setJobLogId(jobLogId);
            flowLogVo.getStartFlowLogDTO().setCreateTime(new Date());
            flowLogVo.getStartFlowLogDTO().setParentFlowlogId(parentFlowlogId);
            dubboService.saveFlowLog(flowLogVo.getStartFlowLogDTO());
        }
        if (null != flowLogVo.getFlowLogDTO()) {
            flowLogVo.getFlowLogDTO().setJobLogId(jobLogId);
            flowLogVo.getFlowLogDTO().setCreateTime(new Date());
            flowLogVo.getFlowLogDTO().setParentFlowlogId(parentFlowlogId);
            FlowLogDTO flowLogDTO2 = dubboService.saveFlowLog(flowLogVo.getFlowLogDTO());
            flowLogDTO.setParentFlowlogId(flowLogDTO2.getId());
            if (null != flowLogVo.getFlowLogVo()) {
                this.recursiveSave(flowLogVo.getFlowLogVo(), jobLogId, flowLogDTO2.getId(), flowLogDTO);
            }

        }


    }

    /**
     * @Description: 设置flowlog值
     * @Param: [flowLogVo, flowLogVo1, paralleDynamicParameter]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void matchingFlowLog(FlowLogVo flowLogVo, FlowLogVo flowLogVo1, List<CommonParameter> paralleDynamicParameter) {
        if (null != flowLogVo.getFlowLogDTO()) {
            String dynamicParameter2 = flowLogVo.getFlowLogDTO().getDynamicParameter();
            List<CommonParameter> dynamicParameter2List = JSON.parseArray(dynamicParameter2, CommonParameter.class);
            commonParameterService.repalceInListValueByUuid(dynamicParameter2List, paralleDynamicParameter);
            flowLogVo.getFlowLogDTO().setDynamicParameter(JSON.toJSONString(dynamicParameter2List));

        }
        if (null != flowLogVo.getStartFlowLogDTO()) {
            String dynamicParameter1 = flowLogVo.getStartFlowLogDTO().getDynamicParameter();
            List<CommonParameter> dynamicParameter1List = JSON.parseArray(dynamicParameter1, CommonParameter.class);
            commonParameterService.repalceInListValueByUuid(dynamicParameter1List, paralleDynamicParameter);
            flowLogVo.getStartFlowLogDTO().setDynamicParameter(JSON.toJSONString(dynamicParameter1List));
        }

        if (null != flowLogVo.getFlowLogVo() && null != flowLogVo.getFlowLogDTO()) {

            matchingFlowLog(flowLogVo.getFlowLogVo(), flowLogVo1, paralleDynamicParameter);
        }


    }

    /**
     * @Description: 设置flowlog值
     * @Param: [dynamicParameter, processSteps, flowLogVo, modelId]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void setStartFlowLog(String dynamicParameter, ProcessStepsDTO processStepsDTO, FlowLogVo flowLogVo, String modelId) {
        flowLogVo.setStartFlowLogDTO(new FlowLogDTO());
        flowLogVo.getStartFlowLogDTO().setCode(200);
        flowLogVo.getStartFlowLogDTO().setDynamicParameter(dynamicParameter);
        flowLogVo.getStartFlowLogDTO().setParallel(1);
        flowLogVo.getStartFlowLogDTO().setIsStart("0");
        flowLogVo.getStartFlowLogDTO().setDataId(FlowConstant.STARTFIGURE);
        flowLogVo.getStartFlowLogDTO().setFlowChartId(modelId);
        flowLogVo.getStartFlowLogDTO().setNextId(processStepsDTO.getDataId());

    }

    /**
     * @Description: 设置flowlog值
     * @Param: [dynamicParameter, processSteps, flowLogVo, modelId]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void setFlowLog(String dynamicParameter, ProcessStepsDTO processStepsDTO, FlowLogVo flowLogVo, String modelId) {
        flowLogVo.setFlowLogDTO(new FlowLogDTO());
        flowLogVo.getFlowLogDTO().setIsStart("0");
        flowLogVo.getFlowLogDTO().setLabel(processStepsDTO.getLabel());
        flowLogVo.getFlowLogDTO().setSort(processStepsDTO.getSort());
        flowLogVo.getFlowLogDTO().setFlowChartId(processStepsDTO.getServiceId());
        flowLogVo.getFlowLogDTO().setIsProcess("true");
        flowLogVo.getFlowLogDTO().setParallel(1);
        flowLogVo.getFlowLogDTO().setNextId(processStepsDTO.getNextId());
        flowLogVo.getFlowLogDTO().setDataId(processStepsDTO.getDataId());
        flowLogVo.getFlowLogDTO().setFixedParameter("[]");
        flowLogVo.getFlowLogDTO().setFlowChartId(modelId);
        flowLogVo.getFlowLogDTO().setDynamicParameter(dynamicParameter);
        flowLogVo.setFlowLogVo(new FlowLogVo());
    }

    /**
     * @Description: 设置flowlog值
     * @Param: [dynamicParameter, processSteps, flowLog, paramMap]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void setNextFlowLogIsProcess(String dynamicParameter, ProcessStepsDTO processStepsDTO, FlowLogDTO flowLogDTO, Map paramMap) {
        String flowChartId = (String) paramMap.get(JobConstant.FLOWCHARTID);
        String parentFlowlogId = (String) paramMap.get(JobConstant.PARENTFLOWLOGID);
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.JOBLOG);
        flowLogDTO.setIsStart("1");
        flowLogDTO.setIsPl("true");
        flowLogDTO.setLabel(processStepsDTO.getLabel());
        flowLogDTO.setSort(processStepsDTO.getSort());
        flowLogDTO.setIsProcess("true");
        flowLogDTO.setParallel(1);
        flowLogDTO.setNextId(processStepsDTO.getNextId());
        flowLogDTO.setDataId(processStepsDTO.getDataId());
        flowLogDTO.setFixedParameter("[]");
        flowLogDTO.setFlowChartId(flowChartId);
        flowLogDTO.setDynamicParameter(dynamicParameter);
        flowLogDTO.setParentFlowlogId(parentFlowlogId);
        flowLogDTO.setJobLogId(jobLog.getId());
        flowLogDTO.setCreateTime(new Date());
    }

    /**
     * @Description: 设置flowlog值
     * @Param: [processSteps, flowLog, parentFlowlogId, modelId, parallel, isStart]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void setFlowLog(ProcessStepsDTO processStepsDTO, FlowLogDTO flowLogDTO, String parentFlowlogId, String modelId, int parallel, String isStart) {
        flowLogDTO.setIsStart(isStart);
        flowLogDTO.setLabel(processStepsDTO.getLabel());
        flowLogDTO.setSort(processStepsDTO.getSort());
        flowLogDTO.setDynamicParameter(processStepsDTO.getDynamicParameter());
        flowLogDTO.setParallel(parallel);
        if (null == parentFlowlogId) {
            flowLogDTO.setParentFlowlogId("1");
        } else {
            flowLogDTO.setParentFlowlogId(parentFlowlogId);
        }
        flowLogDTO.setFlowChartId(modelId);

    }

    /**
     * @Description: 保存日志
     * @Param: [flowLog, processSteps, jobLogId, atomicAlgorithm]
     * @return: java.lang.String
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public String saveFlowLog(FlowLogDTO flowLogDTO, ProcessStepsDTO processStepsDTO, int jobLogId, AtomicAlgorithmDTO atomicAlgorithmDTO) {
        flowLogDTO.setNextId(processStepsDTO.getNextId());
        flowLogDTO.setJobLogId(jobLogId);
        flowLogDTO.setDataId(processStepsDTO.getDataId());
        flowLogDTO.setCreateTime(new Date());
        flowLogDTO.setFixedParameter(atomicAlgorithmDTO.getFixedParameter());
        flowLogDTO = dubboService.saveFlowLog(flowLogDTO);
        return flowLogDTO.getId();
    }

    /**
     * @Description: 保存开始节点日志
     * @Param: [dynamicParameter, jobLogId, dataId, parentFlowLogId, flowChartId]
     * @return: com.htht.job.executor.model.flowlog.FlowLog
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public FlowLogDTO saveStartFlow(String dynamicParameter, int jobLogId, String dataId, String parentFlowLogId, String flowChartId) {
        FlowLogDTO startflowLogDTO = new FlowLogDTO();
        startflowLogDTO.setDynamicParameter(dynamicParameter);
        startflowLogDTO.setDataId(FlowConstant.STARTFIGURE);
        startflowLogDTO.setCode(200);
        startflowLogDTO.setIsStart("1");
        startflowLogDTO.setCreateTime(new Date());
        startflowLogDTO.setUpdateTime(new Date());
        startflowLogDTO.setJobLogId(jobLogId);
        startflowLogDTO.setNextId(dataId + ",");
        startflowLogDTO.setParentFlowlogId(parentFlowLogId);
        startflowLogDTO.setFlowChartId(flowChartId);
        startflowLogDTO = dubboService.saveFlowLog(startflowLogDTO);
        return startflowLogDTO;
    }

    /**
     * @Description: 修改输入输出
     * @Param: [newflowLog, handleCallbackParam, parallelLogList, dynamicParameter]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void updateOutValue(FlowLogDTO newflowLogDTO, HandleCallbackParam handleCallbackParam, List<ParallelLogDTO> parallelLogDTOList, List<CommonParameter> dynamicParameter) {
        List<CommonParameter> flowdynamicParameter = JSON.parseArray(newflowLogDTO.getDynamicParameter(), CommonParameter.class);
        newflowLogDTO.setCode(handleCallbackParam.getExecuteResult().getCode());
        Map<Integer, String> mapValue = new HashMap<>();
        parallelLogDTOList.forEach(parallelLog ->
                commonParameterService.replaceFlowDynamicParameter(dynamicParameter, newflowLogDTO, parallelLog, mapValue)
        );
        mapValue.forEach((integer, s) ->
                flowdynamicParameter.get(integer).setValue(s)

        );

        newflowLogDTO.setDynamicParameter(JSON.toJSONString(flowdynamicParameter));
        dubboService.saveFlowLog(newflowLogDTO);

    }

    /**
     * @Description: 修改流程日志
     * @Param: [handleCallbackParam, dynamicParameter, jobLog, parallelLog]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void updateFlowLog(HandleCallbackParam handleCallbackParam,
                              List<CommonParameter> dynamicParameter,
                              XxlJobLog jobLog,
                              ParallelLogDTO parallelLogDTO, FlowLogDTO nowflowLogDTO) {
        long result = RedisUtil.setIncr(jobLog.getId() + "_" + nowflowLogDTO.getId(), 0);
        if (result == nowflowLogDTO.getParallel()) {
            ParallelLogDTO parallelLogDTOSuccess = new ParallelLogDTO();
            parallelLogDTOSuccess.setFlowId(parallelLogDTO.getFlowId());
            parallelLogDTOSuccess.setCode(200);
            List<ParallelLogDTO> parallelLogDTOList = dubboService.findParallelLogList(parallelLogDTOSuccess);
            this.updateOutValue(nowflowLogDTO, handleCallbackParam, parallelLogDTOList, dynamicParameter);
        }
    }

    /**
     * @Description: 失败修改日志
     * @Param: [handleCallbackParam, parallelLog, jobLog, resultUtil]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void updateFailFlowLog(HandleCallbackParam handleCallbackParam, ParallelLogDTO parallelLogDTO, XxlJobLog jobLog, ResultUtil resultUtil) {
        if (ReturnT.SUCCESS_CODE != handleCallbackParam.getExecuteResult().getCode()) {
            StringBuilder handleMsg = new StringBuilder();
            ParallelLogDTO parallelLogDTOFail = new ParallelLogDTO();
            parallelLogDTOFail.setFlowId(parallelLogDTO.getFlowId());
            parallelLogDTOFail.setCode(500);
            List<ParallelLogDTO> parallelLogDTOListFail = dubboService.findParallelLogList(parallelLogDTOFail);
            parallelLogDTOListFail.forEach(parallelLogfail -> {
                if (!StringUtils.isEmpty(parallelLogfail.getHandleMsg())) {
                    handleMsg.append(parallelLogfail.getHandleMsg() + ";");
                }
            });
            schedulerUtilService.updateHandleInfo(handleCallbackParam, jobLog);
            FlowLogDTO flowLogDTO = dubboService.findByFlowLogId(parallelLogDTO.getFlowId());
            if (!StringUtils.isEmpty(handleMsg)) {
                flowLogDTO.setHandleMsg(handleMsg.toString());
                handleCallbackParam.getExecuteResult().setMsg(handleMsg.toString());
            }
            if (flowLogDTO.getCode() != handleCallbackParam.getExecuteResult().getCode()) {
                this.updateParentFlowLog(flowLogDTO, handleCallbackParam);
            }

            resultUtil.setErrorMessage("500");
        }

    }

    /**
     * 批量执行处理流程日志
     *
     * @Description:
     * @Param: [map, flowLogVo, flowLogVo1, jobLog, length, atomicAlgorithm]
     * @return: void
     * @Author: zzj
     * @Date: 2018/10/31
     */
    @Override
    public void batchExecuteFlowlog(Map map, FlowLogVo flowLogVo, FlowLogVo flowLogVo1, XxlJobLog jobLog, int length, AtomicAlgorithmDTO atomicAlgorithmDTO) {
        String dynamicParameter = (String) map.get(JobConstant.DYNAMICPARAMETER);
        String modelId = (String) map.get(JobConstant.MODELID);
        ProcessStepsDTO processStepsDTO = (ProcessStepsDTO) map.get(JobConstant.PROCESSSTEPS);
        String dataId = (String) map.get(JobConstant.DATAID);
        String isProcess = (String) map.get(JobConstant.ISPROCESS);

        /**=======1.保存流程开始日志===========**/
        if (null == dataId || FlowConstant.STARTFIGURE.equals(dataId) || BooleanConstant.TRUE.equals(isProcess)) {
            this.setStartFlowLog(dynamicParameter, processStepsDTO, flowLogVo, modelId);
        }
        FlowLogDTO startFlowLogDTO = new FlowLogDTO();
        this.recursiveSave(flowLogVo1, jobLog.getId(), "1", startFlowLogDTO);

        /**=======2.保存流程第一步日志===========**/
        FlowLogDTO flowLogDTO = new FlowLogDTO();
        flowLogDTO.setIsPl(BooleanConstant.TRUE);
        this.setFlowLog(processStepsDTO, flowLogDTO, startFlowLogDTO.getParentFlowlogId(), modelId, length, "0");
        String flowLogId = this.saveFlowLog(flowLogDTO, processStepsDTO, jobLog.getId(), atomicAlgorithmDTO);
        map.put(JobConstant.FLOWLOGID, flowLogId);
        map.put(JobConstant.PARENTFLOWLOGID, startFlowLogDTO.getParentFlowlogId());

    }

    /**
     * @Description: 不批量执行保存流程日志
     * @Param: [map, parallelLog, flowLogVo, flowLogVo1, jobLog, atomicAlgorithm]
     * @return: void
     * @Author: zzj
     * @Date: 2018/10/31
     */
    @Override
    public void noBatchExecuteFlowlog(Map map, ParallelLogDTO parallelLogDTO, FlowLogVo flowLogVo, FlowLogVo flowLogVo1, XxlJobLog jobLog, AtomicAlgorithmDTO atomicAlgorithmDTO) {
        String dynamicParameter = (String) map.get(JobConstant.DYNAMICPARAMETER);
        String modelId = (String) map.get(JobConstant.MODELID);
        String dataId = (String) map.get(JobConstant.DATAID);
        ProcessStepsDTO processStepsDTO = (ProcessStepsDTO) map.get(JobConstant.PROCESSSTEPS);
        String isProcess = (String) map.get(JobConstant.ISPROCESS);
        /**=======1.将子流程值传递给父流程===========**/
        List<CommonParameter> startDynamicParameter = JSON.parseArray(dynamicParameter, CommonParameter.class);
        List<CommonParameter> paralleDynamicParameter = JSON.parseArray(parallelLogDTO.getDynamicParameter(), CommonParameter.class);
        commonParameterService.repalceInListValueByDataId(startDynamicParameter, paralleDynamicParameter);
        if (null != flowLogVo1.getFlowLogVo()) {
            this.matchingFlowLog(flowLogVo1, flowLogVo1, paralleDynamicParameter);
        }
        /**=======2.保存流程开始节点日志===========**/
        if (null == dataId || FlowConstant.STARTFIGURE.equals(dataId) || BooleanConstant.TRUE.equals(isProcess)) {
            this.setStartFlowLog(JSON.toJSONString(startDynamicParameter), processStepsDTO, flowLogVo, modelId);
        }
        FlowLogDTO startFlowLogDTO = new FlowLogDTO();
        this.recursiveSave(flowLogVo1, jobLog.getId(), "1", startFlowLogDTO);
        /**=======3.保存流程日志===========**/
        FlowLogDTO flowLogDTO = new FlowLogDTO();
        flowLogDTO.setIsPl(BooleanConstant.FALSE);
        processStepsDTO.setDynamicParameter(JSON.toJSONString(paralleDynamicParameter));
        this.setFlowLog(processStepsDTO, flowLogDTO, startFlowLogDTO.getParentFlowlogId(), modelId, 1, "0");
        String flowLogId = this.saveFlowLog(flowLogDTO, processStepsDTO, jobLog.getId(), atomicAlgorithmDTO);
        map.put(JobConstant.FLOWLOGID, flowLogId);
        map.put(JobConstant.PARENTFLOWLOGID, startFlowLogDTO.getParentFlowlogId());
    }

    private void updateParentFlowLog(FlowLogDTO flowLogDTO, HandleCallbackParam handleCallbackParam) {
        flowLogDTO.setCode(handleCallbackParam.getExecuteResult().getCode());
        flowLogDTO.setHandleMsg(handleCallbackParam.getExecuteResult().getMsg());
        dubboService.saveFlowLog(flowLogDTO);
        if (!"1".equals(flowLogDTO.getParentFlowlogId())) {
            FlowLogDTO parentflowLogDTO = dubboService.findByFlowLogId(flowLogDTO.getParentFlowlogId());
            this.updateParentFlowLog(parentflowLogDTO, handleCallbackParam);
        }

    }

    /**
     * @Description: 处理第一步参数传递给子流程
     * @Param: [processSteps, flowLogVo, map, resultUtil]
     * @return: void
     * @Author: zzj
     * @Date: 2018/10/30
     */
    @Override
    public void handleFistStepParam(ProcessStepsDTO processStepsDTO, FlowLogVo flowLogVo, Map map, ResultUtil<String> resultUtil) {
        try {
            String dynamicParameter = (String) map.get(JobConstant.DYNAMICPARAMETER);
            String modelId = (String) map.get(JobConstant.MODELID);
            String dataId = (String) map.get(JobConstant.DATAID);
            /**=======1.获取子流程第一步===========**/
            ProcessStepsDTO startProcessStepsDTO = findStepService.findStartFlowCeaselessly(processStepsDTO.getServiceId());
            /**=======1.获取子流程下一步===========**/
            List<String> nextIdlist = findStepService.getNextIds(startProcessStepsDTO.getNextId());
            List<ProcessStepsDTO> nextProcessStepDTOS = findStepService.findNextFlowCeaselessly(nextIdlist, processStepsDTO.getServiceId(), resultUtil);
            /**=======3.组装子流程开始节点对象===========**/
            if (null == dataId || FlowConstant.STARTFIGURE.equals(dataId)) {

                this.setStartFlowLog(dynamicParameter, processStepsDTO, flowLogVo, processStepsDTO.getServiceId());
            }
            /**=======4.参数值进行传递===========**/
            List<CommonParameter> upParameter = JSON.parseArray(processStepsDTO.getDynamicParameter(), CommonParameter.class);
            List<CommonParameter> nowCommonParameter = JSON.parseArray(dynamicParameter, CommonParameter.class);
            List<CommonParameter> upParameter1 = JSON.parseArray(nextProcessStepDTOS.get(0).getDynamicParameter(), CommonParameter.class);
            List<CommonParameter> upParameter2 = dubboService.parseFlowXmlParameter(processStepsDTO.getServiceId());
            commonParameterService.repalceListValueByUuid(upParameter, nowCommonParameter);
            commonParameterService.repalceListValueByUuid(upParameter1, nowCommonParameter);
            commonParameterService.repalceListValueByUuid(upParameter2, nowCommonParameter);
            nextProcessStepDTOS.get(0).setDynamicParameter(JSON.toJSONString(upParameter1));
            /**=======5.组装流程第一步节点对象===========**/
            this.setFlowLog(JSON.toJSONString(upParameter), processStepsDTO, flowLogVo, modelId);
            /**=======6.替换原有值===========**/
            map.put(JobConstant.PROCESSSTEPS, nextProcessStepDTOS.get(0));
            map.put(JobConstant.DYNAMICPARAMETER, JSON.toJSONString(upParameter2));
            map.put(JobConstant.MODELID, processStepsDTO.getServiceId());
        } catch (Exception e) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_307_ERROR);
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String saveFlowLog(XxlJobLog jobLog, XxlJobInfo jobInfo) {
        FlowLogDTO flowLogDTO = new FlowLogDTO();
        flowLogDTO.setJobLogId(jobLog.getId());
        flowLogDTO.setCreateTime(new Date());
        flowLogDTO.setLabel(jobInfo.getJobDesc());
        FlowLogDTO saveFlowLogDTO = dubboService.saveFlowLog(flowLogDTO);
        return saveFlowLogDTO.getId();
    }

    @Override
    public String saveParallelLog(String flowId, Map dymap, XxlJobInfo jobInfo, String formatmodelParameters) {
        ParallelLogDTO parallelLogDTO = new ParallelLogDTO();
        parallelLogDTO.setFlowId(flowId);
        parallelLogDTO.setCreateTime(new Date());
        String logDynmic = taskParametersService.getLogDynamic(dymap, jobInfo.getModelId());
        parallelLogDTO.setDynamicParameter(logDynmic);
        parallelLogDTO.setModelParameters(formatmodelParameters);
        ParallelLogDTO saveParallelLogDTO = dubboService.saveParallelLog(parallelLogDTO);
        return saveParallelLogDTO.getId();
    }

}


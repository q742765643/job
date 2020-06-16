package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/4/16.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.jobbean.ParamClassifyBean;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.model.app.FlowLogVo;
import com.htht.job.admin.core.util.RedisUtil;
import com.htht.job.admin.service.FlowSchedulerNextService;
import com.htht.job.admin.service.impl.SchedulerFlowServiceImpl;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.constant.BooleanConstant;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowchart.FlowChartDTO;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @program: htht-job
 * @description:
 * @author: zzj
 * @create: 2018-04-16 21:25
 **/
@Service("flowSchedulerNextService")
public class FlowSchedulerNextServiceImpl extends SchedulerFlowServiceImpl implements FlowSchedulerNextService {
    private static Logger logger = LoggerFactory.getLogger(FlowSchedulerNextServiceImpl.class);

    @Override
    /**
     * @Description: 回调方法
     * @Param: [handleCallbackParam]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    public void callback(HandleCallbackParam handleCallbackParam) {
        try {
            ResultUtil<String> resultUtil = new ResultUtil();
            ParallelLogDTO parallelLogDTO = dubboService.findParallelLogById(handleCallbackParam.getParallelLogId());
            List<CommonParameter> dynamicParameter = JSON.parseArray(parallelLogDTO.getDynamicParameter(), CommonParameter.class);
            XxlJobLog jobLog = xxlJobLogDao.load(handleCallbackParam.getLogId());
            /***========1.修改并行表===============**/
            this.updateParallelLog(handleCallbackParam, dynamicParameter, parallelLogDTO);
            /***========2.失败修改flowlog表========**/
            handleFlowLogService.updateFailFlowLog(handleCallbackParam, parallelLogDTO, jobLog, resultUtil);
            if (!resultUtil.isSuccess()) {
                return;
            }
            /***========3.成功修改flowlog表========**/
            FlowLogDTO nowflowLogDTO = dubboService.findByFlowLogId(parallelLogDTO.getFlowId());
            handleFlowLogService.updateFlowLog(handleCallbackParam, dynamicParameter, jobLog, parallelLogDTO, nowflowLogDTO);
            String[] nextIds = nowflowLogDTO.getNextId().split(",");
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobLog.getJobId());

            /***========4.检查是否能进行下一步========**/
            this.checkLastStep(nowflowLogDTO, nextIds, jobLog, resultUtil);
            if (!resultUtil.isSuccess()) {
                return;
            }
            /***========5.递归添加结束flowlog表========**/
            this.addEndFlowLog(nowflowLogDTO, jobLog, handleCallbackParam, resultUtil, jobInfo);
        } catch (Exception e) {
            RedisUtil.delete(String.valueOf(handleCallbackParam.getLogId()));
            logger.error("callback回调异常", e);
        }

    }

    /**
     * @Description: 检查能否执行下一步
     * @Param: [nowflowLog, nextIds, jobLog, resultUtil]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    public void checkLastStep(FlowLogDTO nowflowLogDTO, String[] nextIds, XxlJobLog jobLog, ResultUtil<String> resultUtil) {
        /***========1.检查并行是否全部执行完成========**/
        for (String dataId : nextIds) {
            ProcessStepsDTO findVo = new ProcessStepsDTO();
            findVo.setFlowId(nowflowLogDTO.getFlowChartId());
            findVo.setDataId(dataId);
            List<ProcessStepsDTO> processStepsDTOList = dubboService.findFlowCeaselesslyList(findVo);
            for (ProcessStepsDTO processStepsDTO : processStepsDTOList) {
                FlowLogDTO lastFlowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(nowflowLogDTO.getJobLogId(), processStepsDTO.getDataId(), nowflowLogDTO.getParentFlowlogId());
                if (lastFlowLogDTO == null || ReturnT.SUCCESS_CODE != lastFlowLogDTO.getCode()) {
                    resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_308_ERROR);
                    return;
                }
            }
        }
        /***========2.检查是否有暂停========**/
        if (jobLog.getSuspend() == 1 && "1".equals(nowflowLogDTO.getParentFlowlogId())) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_309_ERROR);
            return;
        }
        /***========3.检查是否有并行锁========**/
        long result = RedisUtil.setIncr(jobLog.getId() + "_" + nowflowLogDTO.getNextId() + nowflowLogDTO.getParentFlowlogId(), 0);
        if (result > 1) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_310_ERROR);
        }
    }

    /**
     * @Description: 修改并行日志
     * @Param: [handleCallbackParam, dynamicParameter, parallelLog]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void updateParallelLog(HandleCallbackParam handleCallbackParam, List<CommonParameter> dynamicParameter, ParallelLogDTO parallelLogDTO) {
        if (null != handleCallbackParam.getOutput() && !handleCallbackParam.getOutput().isEmpty()) {
            List<String> outputList = handleCallbackParam.getOutput();
            int k = 0;
            for (int i = 0; i < dynamicParameter.size(); i++) {
                if ((FlowConstant.OUTFILE.equals(dynamicParameter.get(i).getParameterType()) ||
                        FlowConstant.OUTSTRING.equals(dynamicParameter.get(i).getParameterType()))
                        && k < outputList.size()) {
                    dynamicParameter.get(i).setValue(outputList.get(k));
                    k++;

                }
            }
            parallelLogDTO.setDynamicParameter(JSON.toJSONString(dynamicParameter));
        }
        parallelLogDTO.setCode(handleCallbackParam.getExecuteResult().getCode());
        parallelLogDTO.setHandleMsg(handleCallbackParam.getExecuteResult().getMsg());
        dubboService.saveParallelLog(parallelLogDTO);

    }

    /**
     * @Description: 修改结束日志
     * @Param: [nowflowLog, jobLog, handleCallbackParam, resultUtil, jobInfo]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void addEndFlowLog(FlowLogDTO nowflowLogDTO, XxlJobLog jobLog, HandleCallbackParam handleCallbackParam, ResultUtil<String> resultUtil, XxlJobInfo jobInfo) {
        if (nowflowLogDTO.getNextId().indexOf(FlowConstant.ENDFIGURE) != -1) {
            /***========1.ENDFIGURE修改结束状态========**/
            this.nextIdIsEnd(nowflowLogDTO, jobLog, handleCallbackParam, resultUtil, jobInfo);
        } else {
            if (!resultUtil.isSuccess()) {
                return;
            }
            resultUtil.setResult(nowflowLogDTO.getNextId());
            Map paramMap = new HashMap();
            /***========2.拼装参数map========**/
            this.depositNextStepMap(paramMap, jobLog, jobInfo, nowflowLogDTO.getFlowChartId(), nowflowLogDTO.getParentFlowlogId());
            /***========3.执行下一步========**/
            this.nextStep(resultUtil.getResult().split(","), paramMap);

        }
    }

    /**
     * @Description: 修改父节点结束日志
     * @Param: [nowflowLog, jobLog, handleCallbackParam, resultUtil, jobInfo]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void nextIdIsEnd(FlowLogDTO nowflowLogDTO, XxlJobLog jobLog, HandleCallbackParam handleCallbackParam, ResultUtil<String> resultUtil, XxlJobInfo jobInfo) {
        /***========1.改结束状态========**/
        FlowLogDTO endFlowLogDTO = new FlowLogDTO();
        endFlowLogDTO.setDataId(FlowConstant.ENDFIGURE);
        endFlowLogDTO.setCreateTime(new Date());
        endFlowLogDTO.setUpdateTime(new Date());
        List<CommonParameter> outputParameter = commonParameterService.findOutputParameter(jobLog.getId(), "");
        ProcessStepsDTO findVo = new ProcessStepsDTO();
        findVo.setDataId(FlowConstant.ENDFIGURE);
        findVo.setFlowId(nowflowLogDTO.getFlowChartId());
        List<ProcessStepsDTO> processStepsDTOList = dubboService.findStartOrEndFlowCeaselesslyList(findVo);
        List<CommonParameter> endParameter = JSON.parseArray(processStepsDTOList.get(0).getDynamicParameter(), CommonParameter.class);
        commonParameterService.repalceListValueByDataIdReply(endParameter, outputParameter);
        endFlowLogDTO.setDynamicParameter(JSON.toJSONString(endParameter));
        endFlowLogDTO.setCode(200);
        endFlowLogDTO.setJobLogId(jobLog.getId());
        endFlowLogDTO.setParentFlowlogId(nowflowLogDTO.getParentFlowlogId());
        endFlowLogDTO.setFlowChartId(nowflowLogDTO.getFlowChartId());
        dubboService.saveFlowLog(endFlowLogDTO);
        if (!"1".equals(nowflowLogDTO.getParentFlowlogId())) {
            /***========2.递归修改父节点========**/
            this.recursionEndFlowLog(nowflowLogDTO, jobLog, jobInfo, handleCallbackParam, resultUtil);
        } else {
            /***========3.修改调度日志========**/
            schedulerUtilService.updateHandleInfo(handleCallbackParam, jobLog);
            RedisUtil.delete(String.valueOf(jobLog.getId()));
            resultUtil.setErrorMessage("500");
        }

    }

    /**
     * @Description: 进行递归处理
     * @Param: [nowflowLog, jobLog, jobInfo, handleCallbackParam, resultUtil]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void recursionEndFlowLog(FlowLogDTO nowflowLogDTO, XxlJobLog jobLog, XxlJobInfo jobInfo, HandleCallbackParam handleCallbackParam, ResultUtil<String> resultUtil) {
        /***========1.保存日志========**/
        FlowLogDTO flowLogDTO = dubboService.findByFlowLogId(nowflowLogDTO.getParentFlowlogId());
        flowLogDTO.setCode(200);
        flowLogDTO.setUpdateTime(new Date());
        List<CommonParameter> commonParameters = JSON.parseArray(flowLogDTO.getDynamicParameter(), CommonParameter.class);
        List<CommonParameter> endParameters = commonParameterService.findOutputParameter(jobLog.getId(), flowLogDTO.getId());
        commonParameterService.repalceListValueByUuid(commonParameters, endParameters);
        flowLogDTO.setDynamicParameter(JSON.toJSONString(commonParameters));
        dubboService.saveFlowLog(flowLogDTO);
        resultUtil.setResult(flowLogDTO.getNextId());
        /***========2.递归修改父节点========**/
        addEndFlowLog(flowLogDTO, jobLog, handleCallbackParam, resultUtil, jobInfo);

    }

    /**
     * @Description: 自动执行下一步
     * @Param: [nextIds, paramMap]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void nextStep(String[] nextIds, Map paramMap) {
        try {
            ResultUtil<String> resultUtil = new ResultUtil<>();
            String flowChartId = (String) paramMap.get(JobConstant.FLOWCHARTID);
            List<String> nextIdlist = new ArrayList<>();
            for (int i = 0; i < nextIds.length; i++) {
                nextIdlist.add(nextIds[i]);
            }
            /***========1.获取下一步========**/
            List<ProcessStepsDTO> processStepsDTOList = this.findNextFlowCeaselessly(nextIdlist, flowChartId, resultUtil);
            /***========2.执行下一步========**/
            this.handleNextStep(processStepsDTOList, paramMap, resultUtil);

        } catch (Exception e) {
            logger.error("next下一步执行异常", e);
        }
    }

    /**
     * @Description: 执行下一步
     * @Param: [nextIds, paramMap]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void handleNextStep(List<ProcessStepsDTO> processStepsDTOList, Map paramMap, ResultUtil<String> resultUtil) {
        processStepsDTOList.forEach(processSteps -> {
            if (BooleanConstant.TRUE.equals(processSteps.getIsProcess())) {
                /***========1.如果流程处理========**/
                this.handleNextStepParam(paramMap, processSteps, resultUtil);
            } else {
                /***========2.递归完成参数处理========**/
                this.nextStepParam(paramMap, processSteps);
                List<ParallelLogDTO> parallelLogDTOList = processSteps.getParallelLogDTOS();
                if (BooleanConstant.TRUE.equals(processSteps.getIsPl())) {
                    /***========3.批量调度========**/
                    this.batchExecute(processSteps, parallelLogDTOList, paramMap);
                } else {
                    /***========4.不批量调度========**/
                    this.noBatchExecute(processSteps, parallelLogDTOList, paramMap);
                }
            }
        });
    }

    /**
     * @Description: 处理下一步参数
     * @Param: [paramMap, processSteps, resultUtil]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void handleNextStepParam(Map paramMap, ProcessStepsDTO processStepsDTO, ResultUtil<String> resultUtil) {
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.JOBLOG);
        XxlJobInfo jobInfo = (XxlJobInfo) paramMap.get(JobConstant.JOBINFO);
        /**=======1.获取开始节点===========**/
        ProcessStepsDTO startProcessStepsDTO = this.findStartFlowCeaselessly(processStepsDTO.getServiceId());
        /**=======2.获取下一个节点===========**/
        List<String> nextIdlist = this.getNextIds(startProcessStepsDTO.getNextId());
        FlowLogDTO flowLogDTO = new FlowLogDTO();
        List<CommonParameter> nextParameter = new ArrayList<>();
        List<CommonParameter> nowCommonParameter = new ArrayList<>();
        /***========3.处理参数========**/
        this.handleCommonParameter(paramMap, processStepsDTO, nextParameter, nowCommonParameter);
        List<CommonParameter> upParameter = dubboService.parseFlowXmlParameter(processStepsDTO.getServiceId());
        handleFlowLogService.setNextFlowLogIsProcess(JSON.toJSONString(nextParameter), processStepsDTO, flowLogDTO, paramMap);
        flowLogDTO = dubboService.saveFlowLog(flowLogDTO);
        commonParameterService.repalceListValueByUuid(upParameter, nowCommonParameter);
        handleFlowLogService.saveStartFlow(JSON.toJSONString(upParameter), jobLog.getId(), startProcessStepsDTO.getNextId(), flowLogDTO.getId(), processStepsDTO.getServiceId());
        List<ProcessStepsDTO> nextProcessStepDTOS = this.findNextFlowCeaselessly(nextIdlist, processStepsDTO.getServiceId(), resultUtil);
        if (0 != jobInfo.getOperation()) {
            for (int j = 0; j < nextProcessStepDTOS.size(); j++) {
                List<CommonParameter> parameters = JSON.parseArray(nextProcessStepDTOS.get(j).getDynamicParameter(), CommonParameter.class);
                commonParameterService.repalceListValueByDataId(parameters, upParameter);
                nextProcessStepDTOS.get(j).setDynamicParameter(JSON.toJSONString(parameters));

            }
        }
        paramMap.put(JobConstant.PARENTFLOWLOGID, flowLogDTO.getId());
        paramMap.put(JobConstant.FLOWCHARTID, processStepsDTO.getServiceId());
        /**=======4.执行下一步===========**/
        handleNextStep(nextProcessStepDTOS, paramMap, resultUtil);

    }

    /**
     * @Description: 处理下一步参数
     * @Param: [paramMap, processSteps, nextParameter, nowCommonParameter]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void handleCommonParameter(Map paramMap, ProcessStepsDTO processStepsDTO, List<CommonParameter> nextParameter, List<CommonParameter> nowCommonParameter) {
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.JOBLOG);
        XxlJobInfo jobInfo = (XxlJobInfo) paramMap.get(JobConstant.JOBINFO);
        String parentFlowlogId = (String) paramMap.get(JobConstant.PARENTFLOWLOGID);
        FlowLogDTO startFlowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLog.getId(), FlowConstant.STARTFIGURE, parentFlowlogId);
        List<CommonParameter> outputParameter = commonParameterService.findOutputParameter(jobLog.getId(), "");
        List<CommonParameter> next = JSON.parseArray(processStepsDTO.getDynamicParameter(), CommonParameter.class);
        List<CommonParameter> parameterStart = JSON.parseArray(startFlowLogDTO.getDynamicParameter(), CommonParameter.class);
        List<CommonParameter> now = JSON.parseArray(processStepsDTO.getDynamicParameter(), CommonParameter.class);
        if (jobInfo.getOperation() == 0) {
            commonParameterService.repalceListValueByDataId(next, parameterStart);
            commonParameterService.repalceListValueByDataIdReply(next, outputParameter);
            commonParameterService.repalceListValueByDataId(now, parameterStart);
            commonParameterService.repalceListValueByDataIdReply(now, outputParameter);
        }
        nextParameter.addAll(next);
        nowCommonParameter.addAll(now);
    }

    @Override
    /**
     * @Description: 手动执行下一步
     * @Param: [nextId, jobInfo, jobLog, dynamicParameter, parentFlowlogId, flowChartId]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    public void handNextStep(String nextId, XxlJobInfo jobInfo, XxlJobLog jobLog, String dynamicParameter, String parentFlowlogId, String flowChartId) {
        ResultUtil<String> resultUtil = new ResultUtil<>();
        List<String> nextIdlist = new ArrayList<>();
        nextIdlist.add(nextId);
        List<ProcessStepsDTO> processStepsDTOList = this.findNextFlowCeaselessly(nextIdlist, jobInfo.getModelId(), resultUtil);
        for (ProcessStepsDTO processStepsDTO : processStepsDTOList) {
            processStepsDTO.setDynamicParameter(dynamicParameter);
        }
        Map paramMap = new HashMap();
        /**=======1.流程参数拼装===========**/
        this.depositNextStepMap(paramMap, jobLog, jobInfo, flowChartId, parentFlowlogId);
        /**=======2.执行下一步===========**/
        this.handleNextStep(processStepsDTOList, paramMap, resultUtil);

    }

    /**
     * @Description: 下一步参数处理
     * @Param: [paramMap, processStepsList]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void nextStepParam(Map paramMap, ProcessStepsDTO processStepsDTO) {
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.JOBLOG);
        XxlJobInfo jobInfo = (XxlJobInfo) paramMap.get(JobConstant.JOBINFO);
        String parentFlowlogId = (String) paramMap.get(JobConstant.PARENTFLOWLOGID);
        List<CommonParameter> outputParameter = commonParameterService.findOutputParameter(jobLog.getId(), "");
        FlowLogDTO startFlowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLog.getId(), FlowConstant.STARTFIGURE, parentFlowlogId);
        List<CommonParameter> startDynamicParameter = JSON.parseArray(startFlowLogDTO.getDynamicParameter(), CommonParameter.class);
        /**======1获取输出目录==========**/
        FlowLogDTO startFlowLogDTOParent = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLog.getId(), FlowConstant.STARTFIGURE, "1");
        List<CommonParameter> startDynamicParameterParent = JSON.parseArray(startFlowLogDTOParent.getDynamicParameter(), CommonParameter.class);
        String outputDirectory = this.outputDirectory(startDynamicParameterParent);
        /**======2获取输出参数到输入==========**/
        List<ParallelLogDTO> parallelLogDTOList = new ArrayList();
        List<CommonParameter> flowParams = JSON.parseArray(processStepsDTO.getDynamicParameter(), CommonParameter.class);
        if (jobInfo.getOperation() == 0) {
            commonParameterService.repalceListValueByDataId(flowParams, startDynamicParameter);
            commonParameterService.repalceListValueByDataIdReply(flowParams, outputParameter);
        }
        processStepsDTO.setDynamicParameter(JSON.toJSONString(flowParams));
        /**======3封装替换参数值==========**/
        this.posttingParam(paramMap, flowParams, jobInfo, processStepsDTO, outputDirectory, parallelLogDTOList);
        processStepsDTO.setParallelLogDTOS(parallelLogDTOList);

    }

    /**
     * @Description: 组装参数
     * @Param: [paramMap, flowParams, jobInfo, processSteps, outputDirectory, parallelLogList]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    private void posttingParam(Map paramMap, List<CommonParameter> flowParams, XxlJobInfo jobInfo, ProcessStepsDTO processStepsDTO, String outputDirectory, List<ParallelLogDTO> parallelLogDTOList) {
        Map mapDataId = new HashMap(20);
        ParamClassifyBean paramClassifyBean = new ParamClassifyBean();
        this.paramClassifyBean(paramClassifyBean, flowParams, mapDataId);
        Map<Integer, String> flowMap = paramClassifyBean.getFlowMap();
        FlowChartDTO flowChartDTO = dubboService.getFlowById(jobInfo.getModelId());
        paramMap.put(JobConstant.MAPDATAID, mapDataId);
        paramMap.put(JobConstant.FLOWCHART, flowChartDTO);
        paramMap.put(JobConstant.LABEL, processStepsDTO.getLabel());
        paramMap.put(JobConstant.OUTPUTDIRECTORY, outputDirectory);
        int outFolderLength = paramClassifyBean.getOutFolder().size();
        if (outFolderLength > 0) {
            this.setFolder(paramClassifyBean.getOutFolder(), flowParams, paramMap);
        }
        if (flowMap.size() == 0) {
            int inFileLength = paramClassifyBean.getInFile().size();
            int outFileLength = paramClassifyBean.getOutFile().size();
            if (inFileLength == 1 && outFileLength >= 1) {
                String tempvalue = paramClassifyBean.getInFile().get(0).getValue();
                this.setFileName(tempvalue, paramClassifyBean.getOutFile(), flowParams, paramMap);
            }
            ParallelLogDTO parallelLogDTO = new ParallelLogDTO();
            parallelLogDTO.setDynamicParameter(JSON.toJSONString(flowParams));
            parallelLogDTOList.add(parallelLogDTO);
        } else {
            paramMap.put(JobConstant.PROCESSSTEPS, processStepsDTO);
            paramMap.put(JobConstant.FLOWMAP, flowMap);
            this.parallelByFlowMap(paramClassifyBean, flowParams, parallelLogDTOList, paramMap);
        }

    }

    /**
     * @Description: 手动执行第一步
     * @Param: [jobInfo, jobLog, dataId, dynamicParameter]
     * @return: void
     * @Author: zzj
     * @Date: 2018/11/1
     */
    @Override
    public void handScheduler(XxlJobInfo jobInfo, XxlJobLog jobLog, String dataId, String dynamicParameter) {
        ResultUtil resultUtil = new ResultUtil();
        //operation 0调度触发执行,1手动触发执行
        /**=======1.获取开始节点===========**/
        ProcessStepsDTO startProcessStepsDTO = this.findStartFlowCeaselessly(jobInfo.getModelId());
        /**=======2.获取下一个节点===========**/
        List<String> nextIdlist = this.getNextIds(startProcessStepsDTO.getNextId());
        List<ProcessStepsDTO> nextProcessStepDTOS = this.findNextFlowCeaselessly(nextIdlist, jobInfo.getModelId(), resultUtil);
        if (nextProcessStepDTOS.size() != 1) {
            return;
        }
        ProcessStepsDTO processStepsDTO = nextProcessStepDTOS.get(0);
        FlowLogVo flowLogVo = new FlowLogVo();
        Map map = new HashMap();
        /**=======3.参数放入map进行传递===========**/
        this.depositHandSchedulerMap(map, jobLog, jobInfo, processStepsDTO, dynamicParameter, dataId);
        FlowLogDTO startFlowLogDTO = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLog.getId(), FlowConstant.STARTFIGURE, "1");
        List<CommonParameter> startDynamicParameter;
        if (null == startFlowLogDTO) {
            startDynamicParameter = JSON.parseArray(dynamicParameter, CommonParameter.class);
        } else {
            startDynamicParameter = JSON.parseArray(startFlowLogDTO.getDynamicParameter(), CommonParameter.class);
        }
        String outputDirectory = this.outputDirectory(startDynamicParameter);
        map.put(JobConstant.OUTPUTDIRECTORY, outputDirectory);
        /**=======3.处理流程第一步===========**/
        this.handleFirstStep(map, flowLogVo, flowLogVo, resultUtil);
    }


}


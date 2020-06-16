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
import com.htht.job.executor.model.flowchart.FlowChartModel;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.processsteps.ProcessSteps;
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
    * @Description:  回调方法
    * @Param: [handleCallbackParam] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void callback(HandleCallbackParam handleCallbackParam) {
        try {
            ResultUtil<String> resultUtil = new ResultUtil();
            ParallelLog parallelLog = dubboService.findParallelLogById(handleCallbackParam.getParallelLogId());
            List<CommonParameter> dynamicParameter = JSON.parseArray(parallelLog.getDynamicParameter(), CommonParameter.class);
            XxlJobLog jobLog = xxlJobLogDao.load(handleCallbackParam.getLogId());
            /***========1.修改并行表===============**/
            this.updateParallelLog(handleCallbackParam, dynamicParameter, parallelLog);
            /***========2.失败修改flowlog表========**/
            handleFlowLogService.updateFailFlowLog(handleCallbackParam, parallelLog, jobLog, resultUtil);
            if (!resultUtil.isSuccess()) {
                return;
            }
            /***========3.成功修改flowlog表========**/
            FlowLog nowflowLog = dubboService.findByFlowLogId(parallelLog.getFlowId());
            handleFlowLogService.updateFlowLog(handleCallbackParam, dynamicParameter, jobLog, parallelLog,nowflowLog);
            String[] nextIds = nowflowLog.getNextId().split(",");
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobLog.getJobId());

            /***========4.检查是否能进行下一步========**/
            this.checkLastStep(nowflowLog,nextIds,jobLog,resultUtil);
            if(!resultUtil.isSuccess()){
                return;
            }
            /***========5.递归添加结束flowlog表========**/
            this.addEndFlowLog(nowflowLog, jobLog, handleCallbackParam, resultUtil, jobInfo);
            //RedisUtil.delete(String.valueOf(jobLog.getId()));
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
    public void  checkLastStep(FlowLog nowflowLog,String[] nextIds,XxlJobLog jobLog,ResultUtil<String> resultUtil){
        /***========1.检查并行是否全部执行完成========**/
        for (String dataId : nextIds) {
            ProcessSteps findVo = new ProcessSteps();
            findVo.setFlowId(nowflowLog.getFlowChartId());
            findVo.setDataId(dataId);
            List<ProcessSteps> processStepsList = dubboService.findFlowCeaselesslyList(findVo);
            for (ProcessSteps processSteps : processStepsList) {
                FlowLog lastFlowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(nowflowLog.getJobLogId(), processSteps.getDataId(), nowflowLog.getParentFlowlogId());
                if (lastFlowLog == null || ReturnT.SUCCESS_CODE != lastFlowLog.getCode()) {
                    resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_308_ERROR);
                    return;
                }
            }
        }
        /***========2.检查是否有暂停========**/
        if (jobLog.getSuspend() == 1 && "1".equals(nowflowLog.getParentFlowlogId())) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_309_ERROR);
            return;
        }
        /***========3.检查是否有并行锁========**/
        long result=RedisUtil.setIncr(jobLog.getId() + "_" + nowflowLog.getNextId() + nowflowLog.getParentFlowlogId(),0);
        if (result>1) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_310_ERROR);
            return;
        }
    }
    /** 
    * @Description: 修改并行日志 
    * @Param: [handleCallbackParam, dynamicParameter, parallelLog] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    private void updateParallelLog(HandleCallbackParam handleCallbackParam, List<CommonParameter> dynamicParameter, ParallelLog parallelLog) {
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
            parallelLog.setDynamicParameter(JSON.toJSONString(dynamicParameter));
        }
        parallelLog.setCode(handleCallbackParam.getExecuteResult().getCode());
        parallelLog.setHandleMsg(handleCallbackParam.getExecuteResult().getMsg());
        dubboService.saveParallelLog(parallelLog);

    }

    /** 
    * @Description: 修改结束日志 
    * @Param: [nowflowLog, jobLog, handleCallbackParam, resultUtil, jobInfo] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    private void addEndFlowLog(FlowLog nowflowLog, XxlJobLog jobLog, HandleCallbackParam handleCallbackParam,ResultUtil<String> resultUtil, XxlJobInfo jobInfo) {
        if (nowflowLog.getNextId().indexOf(FlowConstant.ENDFIGURE) != -1) {
            /***========1.ENDFIGURE修改结束状态========**/
            this.nextIdIsEnd(nowflowLog,jobLog,handleCallbackParam,resultUtil,jobInfo);
        } else {
            if (!resultUtil.isSuccess()) {
                return;
            }
            resultUtil.setResult(nowflowLog.getNextId());
            Map paramMap = new HashMap();
            /***========2.拼装参数map========**/
            this.depositNextStepMap(paramMap, jobLog, jobInfo, nowflowLog.getFlowChartId(), nowflowLog.getParentFlowlogId());
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
    private void nextIdIsEnd(FlowLog nowflowLog, XxlJobLog jobLog, HandleCallbackParam handleCallbackParam,ResultUtil<String> resultUtil, XxlJobInfo jobInfo){
        /***========1.改结束状态========**/
        FlowLog endFlowLog = new FlowLog();
        endFlowLog.setDataId(FlowConstant.ENDFIGURE);
        endFlowLog.setCreateTime(new Date());
        endFlowLog.setUpdateTime(new Date());
        List<CommonParameter> outputParameter = commonParameterService.findOutputParameter(jobLog.getId(), "");
        ProcessSteps findVo = new ProcessSteps();
        findVo.setDataId(FlowConstant.ENDFIGURE);
        findVo.setFlowId(nowflowLog.getFlowChartId());
        List<ProcessSteps> processStepsList = dubboService.findStartOrEndFlowCeaselesslyList(findVo);
        List<CommonParameter> endParameter = JSON.parseArray(processStepsList.get(0).getDynamicParameter(), CommonParameter.class);
        commonParameterService.repalceListValueByDataIdReply(endParameter, outputParameter);
        endFlowLog.setDynamicParameter(JSON.toJSONString(endParameter));
        endFlowLog.setCode(200);
        endFlowLog.setJobLogId(jobLog.getId());
        endFlowLog.setParentFlowlogId(nowflowLog.getParentFlowlogId());
        endFlowLog.setFlowChartId(nowflowLog.getFlowChartId());
        dubboService.saveFlowLog(endFlowLog);
        if (!"1".equals(nowflowLog.getParentFlowlogId())) {
            /***========2.递归修改父节点========**/
            this.recursionEndFlowLog(nowflowLog,jobLog,jobInfo,handleCallbackParam,resultUtil);
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
    private void recursionEndFlowLog(FlowLog nowflowLog,XxlJobLog jobLog,XxlJobInfo jobInfo,HandleCallbackParam handleCallbackParam,ResultUtil<String> resultUtil){
        /***========1.保存日志========**/
        FlowLog flowLog = dubboService.findByFlowLogId(nowflowLog.getParentFlowlogId());
        flowLog.setCode(200);
        flowLog.setUpdateTime(new Date());
        List<CommonParameter> commonParameters = JSON.parseArray(flowLog.getDynamicParameter(), CommonParameter.class);
        List<CommonParameter> endParameters = commonParameterService.findOutputParameter(jobLog.getId(), flowLog.getId());
        commonParameterService.repalceListValueByUuid(commonParameters, endParameters);
        flowLog.setDynamicParameter(JSON.toJSONString(commonParameters));
        dubboService.saveFlowLog(flowLog);
        resultUtil.setResult(flowLog.getNextId());
        /***========2.递归修改父节点========**/
        addEndFlowLog(flowLog, jobLog, handleCallbackParam, resultUtil, jobInfo);

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
            String flowChartId = (String) paramMap.get(JobConstant.flowChartId);
            List<String> nextIdlist = new ArrayList<>();
            for (int i = 0; i < nextIds.length; i++) {
                nextIdlist.add(nextIds[i]);
            }
            /***========1.获取下一步========**/
            List<ProcessSteps> processStepsList = this.findNextFlowCeaselessly(nextIdlist, flowChartId, resultUtil);
            /***========2.执行下一步========**/
            this.handleNextStep(processStepsList, paramMap, resultUtil);

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
    private void handleNextStep(List<ProcessSteps> processStepsList, Map paramMap, ResultUtil<String> resultUtil) {
        processStepsList.forEach(processSteps -> {
            if (BooleanConstant.TRUE.equals(processSteps.getIsProcess())) {
                /***========1.如果流程处理========**/
                this.handleNextStepParam(paramMap, processSteps, resultUtil);
            } else {
                /***========2.递归完成参数处理========**/
                this.nextStepParam(paramMap, processSteps);
                List<ParallelLog> parallelLogList = processSteps.getParallelLogs();
                if (BooleanConstant.TRUE.equals(processSteps.getIsPl())) {
                    /***========3.批量调度========**/
                    this.batchExecute(processSteps, parallelLogList, paramMap);
                } else {
                    /***========4.不批量调度========**/
                    this.noBatchExecute(processSteps, parallelLogList, paramMap);
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
    private void handleNextStepParam(Map paramMap, ProcessSteps processSteps, ResultUtil<String> resultUtil) {
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.jobLog);
        XxlJobInfo jobInfo = (XxlJobInfo) paramMap.get(JobConstant.jobInfo);
        /**=======1.获取开始节点===========**/
        ProcessSteps startProcessSteps = this.findStartFlowCeaselessly(processSteps.getServiceId());
        /**=======2.获取下一个节点===========**/
        List<String> nextIdlist = this.getNextIds(startProcessSteps.getNextId());
        FlowLog flowLog = new FlowLog();
        List<CommonParameter> nextParameter = new ArrayList<>();
        List<CommonParameter> nowCommonParameter = new ArrayList<>();
        /***========3.处理参数========**/
        this.handleCommonParameter(paramMap, processSteps, nextParameter, nowCommonParameter);
        List<CommonParameter> upParameter = dubboService.parseFlowXmlParameter(processSteps.getServiceId());
        handleFlowLogService.setNextFlowLogIsProcess(JSON.toJSONString(nextParameter), processSteps, flowLog, paramMap);
        flowLog = dubboService.saveFlowLog(flowLog);
        commonParameterService.repalceListValueByUuid(upParameter, nowCommonParameter);
        handleFlowLogService.saveStartFlow(JSON.toJSONString(upParameter), jobLog.getId(), startProcessSteps.getNextId(), flowLog.getId(), processSteps.getServiceId());
        List<ProcessSteps> nextProcessSteps = this.findNextFlowCeaselessly(nextIdlist, processSteps.getServiceId(), resultUtil);
        if (0 != jobInfo.getOperation()) {
            for (int j = 0; j < nextProcessSteps.size(); j++) {
                List<CommonParameter> parameters = JSON.parseArray(nextProcessSteps.get(j).getDynamicParameter(), CommonParameter.class);
                commonParameterService.repalceListValueByDataId(parameters, upParameter);
                nextProcessSteps.get(j).setDynamicParameter(JSON.toJSONString(parameters));

            }
        }
        paramMap.put(JobConstant.parentFlowlogId, flowLog.getId());
        paramMap.put(JobConstant.flowChartId, processSteps.getServiceId());
        /**=======4.执行下一步===========**/
        handleNextStep(nextProcessSteps, paramMap, resultUtil);

    }
    /** 
    * @Description: 处理下一步参数
    * @Param: [paramMap, processSteps, nextParameter, nowCommonParameter] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    private void handleCommonParameter(Map paramMap, ProcessSteps processSteps, List<CommonParameter> nextParameter, List<CommonParameter> nowCommonParameter) {
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.jobLog);
        XxlJobInfo jobInfo = (XxlJobInfo) paramMap.get(JobConstant.jobInfo);
        String parentFlowlogId = (String) paramMap.get(JobConstant.parentFlowlogId);
        FlowLog startFlowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLog.getId(), FlowConstant.STARTFIGURE, parentFlowlogId);
        List<CommonParameter> outputParameter = commonParameterService.findOutputParameter(jobLog.getId(), "");
        List<CommonParameter> next = JSON.parseArray(processSteps.getDynamicParameter(), CommonParameter.class);
        List<CommonParameter> parameterStart = JSON.parseArray(startFlowLog.getDynamicParameter(), CommonParameter.class);
        List<CommonParameter> now = JSON.parseArray(processSteps.getDynamicParameter(), CommonParameter.class);
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
        List<ProcessSteps> processStepsList = this.findNextFlowCeaselessly(nextIdlist, jobInfo.getModelId(), resultUtil);
        for (ProcessSteps processSteps : processStepsList) {
            processSteps.setDynamicParameter(dynamicParameter);
        }
        Map paramMap = new HashMap();
        /**=======1.流程参数拼装===========**/
        this.depositNextStepMap(paramMap, jobLog, jobInfo, flowChartId, parentFlowlogId);
        /**=======2.执行下一步===========**/
        this.handleNextStep(processStepsList, paramMap, resultUtil);

    }
    /** 
    * @Description: 下一步参数处理 
    * @Param: [paramMap, processStepsList] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    private void nextStepParam(Map paramMap, ProcessSteps processSteps) {
        XxlJobLog jobLog = (XxlJobLog) paramMap.get(JobConstant.jobLog);
        XxlJobInfo jobInfo = (XxlJobInfo) paramMap.get(JobConstant.jobInfo);
        String parentFlowlogId = (String) paramMap.get(JobConstant.parentFlowlogId);
        List<CommonParameter> outputParameter = commonParameterService.findOutputParameter(jobLog.getId(), "");
        FlowLog startFlowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLog.getId(), FlowConstant.STARTFIGURE, parentFlowlogId);
        List<CommonParameter> startDynamicParameter = JSON.parseArray(startFlowLog.getDynamicParameter(), CommonParameter.class);
        /**======1获取输出目录==========**/
        FlowLog startFlowLogParent = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLog.getId(), FlowConstant.STARTFIGURE, "1");
        List<CommonParameter> startDynamicParameterParent = JSON.parseArray(startFlowLogParent.getDynamicParameter(), CommonParameter.class);
        String outputDirectory = this.outputDirectory(startDynamicParameterParent);
        /**======2获取输出参数到输入==========**/
            List<ParallelLog> parallelLogList = new ArrayList();
            List<CommonParameter> flowParams = JSON.parseArray(processSteps.getDynamicParameter(), CommonParameter.class);
            if (jobInfo.getOperation() == 0) {
                commonParameterService.repalceListValueByDataId(flowParams, startDynamicParameter);
                commonParameterService.repalceListValueByDataIdReply(flowParams, outputParameter);
            }
            processSteps.setDynamicParameter(JSON.toJSONString(flowParams));
            /**======3封装替换参数值==========**/
            this.posttingParam(paramMap,flowParams,jobInfo,processSteps,outputDirectory,parallelLogList);
            processSteps.setParallelLogs(parallelLogList);

    }
    /** 
    * @Description: 组装参数 
    * @Param: [paramMap, flowParams, jobInfo, processSteps, outputDirectory, parallelLogList] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    private void posttingParam(Map paramMap,List<CommonParameter> flowParams,XxlJobInfo jobInfo,ProcessSteps processSteps,String outputDirectory,List<ParallelLog> parallelLogList){
        Map mapDataId = new HashMap(20);
        ParamClassifyBean paramClassifyBean = new ParamClassifyBean();
        this.paramClassifyBean(paramClassifyBean, flowParams, mapDataId);
        Map<Integer, String> flowMap = paramClassifyBean.getFlowMap();
        FlowChartModel flowChartModel = dubboService.getFlowById(jobInfo.getModelId());
        paramMap.put(JobConstant.mapDataId, mapDataId);
        paramMap.put(JobConstant.flowChart, flowChartModel);
        paramMap.put(JobConstant.label, processSteps.getLabel());
        paramMap.put(JobConstant.outputDirectory, outputDirectory);
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
            ParallelLog parallelLog = new ParallelLog();
            parallelLog.setDynamicParameter(JSON.toJSONString(flowParams));
            parallelLogList.add(parallelLog);
        } else {
            paramMap.put(JobConstant.processSteps, processSteps);
            paramMap.put(JobConstant.flowMap, flowMap);
            this.parallelByFlowMap(paramClassifyBean, flowParams, parallelLogList, paramMap);
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
        ProcessSteps startProcessSteps = this.findStartFlowCeaselessly(jobInfo.getModelId());
        /**=======2.获取下一个节点===========**/
        List<String> nextIdlist = this.getNextIds(startProcessSteps.getNextId());
        List<ProcessSteps> nextProcessSteps = this.findNextFlowCeaselessly(nextIdlist, jobInfo.getModelId(), resultUtil);
        if (nextProcessSteps.size() != 1) {
            return;
        }
        ProcessSteps processSteps = nextProcessSteps.get(0);
        FlowLogVo flowLogVo = new FlowLogVo();
        Map map = new HashMap();
        /**=======3.参数放入map进行传递===========**/
        this.depositHandSchedulerMap(map, jobLog, jobInfo, processSteps, dynamicParameter, dataId);
        FlowLog startFlowLog = dubboService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLog.getId(), FlowConstant.STARTFIGURE, "1");
        List<CommonParameter> startDynamicParameter;
        if (null == startFlowLog) {
            startDynamicParameter = JSON.parseArray(dynamicParameter, CommonParameter.class);
        } else {
            startDynamicParameter = JSON.parseArray(startFlowLog.getDynamicParameter(), CommonParameter.class);
        }
        String outputDirectory = this.outputDirectory(startDynamicParameter);
        map.put(JobConstant.outputDirectory, outputDirectory);
        /**=======3.处理流程第一步===========**/
        this.handleFirstStep(map, flowLogVo, flowLogVo, resultUtil);
    }


}


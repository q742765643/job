package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/10/30.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.model.app.FlowLogVo;
import com.htht.job.admin.core.util.RedisUtil;
import com.htht.job.admin.service.CommonParameterService;
import com.htht.job.admin.service.FindStepService;
import com.htht.job.admin.service.HandleFlowLogService;
import com.htht.job.admin.service.SchedulerUtilService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.constant.BooleanConstant;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.processsteps.ProcessSteps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.htht.job.core.constant.JobConstant.parentFlowlogId;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-30 14:08
 **/
@Service
public class HandleFlowLogServiceImpl implements HandleFlowLogService {
    @Resource
    private DubboService dubboService;
    @Resource
    private CommonParameterService commonParameterService;
    @Resource
    private SchedulerUtilService schedulerUtilService;
    @Qualifier("findStepServiceImpl")
    @Autowired
    private FindStepService findStepService;
    private static Logger logger = LoggerFactory.getLogger(HandleFlowLogServiceImpl.class);


    /**
    * @Description:  递归保存flowlog值
    * @Param: [flowLogVo, jobLogId, parentFlowlogId, flowLog]
    * @return: void
    * @Author: zzj
    * @Date: 2018/11/1
    */
    public void recursiveSave(FlowLogVo flowLogVo, int jobLogId, String parentFlowlogId, FlowLog flowLog){
        if(null!=flowLogVo.getStartFlowLog()) {
            flowLogVo.getStartFlowLog().setJobLogId(jobLogId);
            flowLogVo.getStartFlowLog().setCreateTime(new Date());
            flowLogVo.getStartFlowLog().setParentFlowlogId(parentFlowlogId);
            dubboService.saveFlowLog(flowLogVo.getStartFlowLog());
        }
        if(null!=flowLogVo.getFlowLog()) {
            flowLogVo.getFlowLog().setJobLogId(jobLogId);
            flowLogVo.getFlowLog().setCreateTime(new Date());
            flowLogVo.getFlowLog().setParentFlowlogId(parentFlowlogId);
            FlowLog flowLog2=dubboService.saveFlowLog(flowLogVo.getFlowLog());
            flowLog.setParentFlowlogId(flowLog2.getId());
            if(null!=flowLogVo.getFlowLogVo()) {
                this.recursiveSave(flowLogVo.getFlowLogVo(),jobLogId,flowLog2.getId(),flowLog);
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
    public void matchingFlowLog(FlowLogVo flowLogVo,FlowLogVo flowLogVo1,List<CommonParameter> paralleDynamicParameter){
        if(null!=flowLogVo.getFlowLog()){
            String dynamicParameter2=flowLogVo.getFlowLog().getDynamicParameter();
            List<CommonParameter> dynamicParameter2List= JSON.parseArray(dynamicParameter2,CommonParameter.class);
            commonParameterService.repalceInListValueByUuid(dynamicParameter2List,paralleDynamicParameter);
            flowLogVo.getFlowLog().setDynamicParameter(JSON.toJSONString(dynamicParameter2List));

        }
        if(null!=flowLogVo.getStartFlowLog()) {
            String dynamicParameter1 = flowLogVo.getStartFlowLog().getDynamicParameter();
            List<CommonParameter> dynamicParameter1List = JSON.parseArray(dynamicParameter1, CommonParameter.class);
            commonParameterService.repalceInListValueByUuid(dynamicParameter1List,paralleDynamicParameter);
            flowLogVo.getStartFlowLog().setDynamicParameter(JSON.toJSONString(dynamicParameter1List));
        }

        if(null!=flowLogVo.getFlowLogVo()&&null!=flowLogVo.getFlowLog()) {

            matchingFlowLog(flowLogVo.getFlowLogVo(),flowLogVo1,paralleDynamicParameter);
        }


    }
    /** 
    * @Description: 设置flowlog值 
    * @Param: [dynamicParameter, processSteps, flowLogVo, modelId] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void setStartFlowLog(String dynamicParameter, ProcessSteps processSteps, FlowLogVo flowLogVo, String modelId){
        flowLogVo.setStartFlowLog(new FlowLog());
        flowLogVo.getStartFlowLog().setCode(200);
        flowLogVo.getStartFlowLog().setDynamicParameter(dynamicParameter);
        flowLogVo.getStartFlowLog().setParallel(1);
        flowLogVo.getStartFlowLog().setIsStart("0");
        flowLogVo.getStartFlowLog().setDataId(FlowConstant.STARTFIGURE);
        flowLogVo.getStartFlowLog().setFlowChartId(modelId);
        flowLogVo.getStartFlowLog().setNextId(processSteps.getDataId());

    }
    /** 
    * @Description: 设置flowlog值 
    * @Param: [dynamicParameter, processSteps, flowLogVo, modelId] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void setFlowLog(String dynamicParameter,ProcessSteps processSteps,FlowLogVo flowLogVo,String modelId){
        flowLogVo.setFlowLog(new FlowLog());
        flowLogVo.getFlowLog().setIsStart("0");
        flowLogVo.getFlowLog().setLabel(processSteps.getLabel());
        flowLogVo.getFlowLog().setSort(processSteps.getSort());
        flowLogVo.getFlowLog().setFlowChartId(processSteps.getServiceId());
        flowLogVo.getFlowLog().setIsProcess("true");
        flowLogVo.getFlowLog().setParallel(1);
        flowLogVo.getFlowLog().setNextId(processSteps.getNextId());
        flowLogVo.getFlowLog().setDataId(processSteps.getDataId());
        flowLogVo.getFlowLog().setFixedParameter("[]");
        flowLogVo.getFlowLog().setFlowChartId(modelId);
        flowLogVo.getFlowLog().setDynamicParameter(dynamicParameter);
        flowLogVo.setFlowLogVo(new FlowLogVo());
    }
    /** 
    * @Description:  设置flowlog值
    * @Param: [dynamicParameter, processSteps, flowLog, paramMap] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void setNextFlowLogIsProcess(String dynamicParameter,ProcessSteps processSteps,FlowLog flowLog,Map paramMap){
        String flowChartId= (String) paramMap.get(JobConstant.flowChartId);
        String parentFlowlogId= (String) paramMap.get(JobConstant.parentFlowlogId);
        XxlJobLog jobLog= (XxlJobLog) paramMap.get(JobConstant.jobLog);
        flowLog.setIsStart("1");
        flowLog.setIsPl("true");
        flowLog.setLabel(processSteps.getLabel());
        flowLog.setSort(processSteps.getSort());
        flowLog.setIsProcess("true");
        flowLog.setParallel(1);
        flowLog.setNextId(processSteps.getNextId());
        flowLog.setDataId(processSteps.getDataId());
        flowLog.setFixedParameter("[]");
        flowLog.setFlowChartId(flowChartId);
        flowLog.setDynamicParameter(dynamicParameter);
        flowLog.setParentFlowlogId(parentFlowlogId);
        flowLog.setJobLogId(jobLog.getId());
        flowLog.setCreateTime(new Date());
    }
    /** 
    * @Description: 设置flowlog值 
    * @Param: [processSteps, flowLog, parentFlowlogId, modelId, parallel, isStart] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void setFlowLog(ProcessSteps processSteps,FlowLog flowLog,String parentFlowlogId,String modelId,int parallel,String isStart){
        flowLog.setIsStart(isStart);
        flowLog.setLabel(processSteps.getLabel());
        flowLog.setSort(processSteps.getSort());
        flowLog.setDynamicParameter(processSteps.getDynamicParameter());
        flowLog.setParallel(parallel);
        if(null==parentFlowlogId) {
            flowLog.setParentFlowlogId("1");
        }else{
            flowLog.setParentFlowlogId(parentFlowlogId);
        }
        flowLog.setFlowChartId(modelId);

    }
    /** 
    * @Description: 保存日志 
    * @Param: [flowLog, processSteps, jobLogId, atomicAlgorithm] 
    * @return: java.lang.String 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public String  saveFlowLog(FlowLog flowLog, ProcessSteps processSteps, int jobLogId, AtomicAlgorithm atomicAlgorithm){
        flowLog.setNextId(processSteps.getNextId());
        flowLog.setJobLogId(jobLogId);
        flowLog.setDataId(processSteps.getDataId());
        flowLog.setCreateTime(new Date());
        flowLog.setFixedParameter(atomicAlgorithm.getFixedParameter());
        flowLog = dubboService.saveFlowLog(flowLog);
        return flowLog.getId();
    }
    /** 
    * @Description: 保存开始节点日志 
    * @Param: [dynamicParameter, jobLogId, dataId, parentFlowLogId, flowChartId] 
    * @return: com.htht.job.executor.model.flowlog.FlowLog 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public FlowLog saveStartFlow(String dynamicParameter,int jobLogId,String dataId,String parentFlowLogId,String flowChartId){
        FlowLog startflowLog = new FlowLog();
        startflowLog.setDynamicParameter(dynamicParameter);
        startflowLog.setDataId(FlowConstant.STARTFIGURE);
        startflowLog.setCode(200);
        startflowLog.setIsStart("1");
        startflowLog.setCreateTime(new Date());
        startflowLog.setUpdateTime(new Date());
        startflowLog.setJobLogId(jobLogId);
        startflowLog.setNextId(dataId + ",");
        startflowLog.setParentFlowlogId(parentFlowLogId);
        startflowLog.setFlowChartId(flowChartId);
        startflowLog = dubboService.saveFlowLog(startflowLog);
        return startflowLog;
    }
    /** 
    * @Description: 修改输入输出 
    * @Param: [newflowLog, handleCallbackParam, parallelLogList, dynamicParameter] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void updateOutValue(FlowLog newflowLog, HandleCallbackParam handleCallbackParam, List<ParallelLog> parallelLogList, List<CommonParameter> dynamicParameter) {
        List<CommonParameter> flowdynamicParameter = JSON.parseArray(newflowLog.getDynamicParameter(), CommonParameter.class);
        newflowLog.setCode(handleCallbackParam.getExecuteResult().getCode());
        Map<Integer, String> mapValue = new HashMap<>();
        parallelLogList.forEach(parallelLog ->
                commonParameterService.replaceFlowDynamicParameter(dynamicParameter, newflowLog, parallelLog, mapValue)
        );
        mapValue.forEach((integer, s) ->
                flowdynamicParameter.get(integer).setValue(s)

        );

        newflowLog.setDynamicParameter(JSON.toJSONString(flowdynamicParameter));
        dubboService.saveFlowLog(newflowLog);

    }
    /** 
    * @Description: 修改流程日志 
    * @Param: [handleCallbackParam, dynamicParameter, jobLog, parallelLog] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void updateFlowLog(HandleCallbackParam handleCallbackParam,
                               List<CommonParameter> dynamicParameter,
                               XxlJobLog jobLog,
                               ParallelLog parallelLog,FlowLog nowflowLog) {
        long result=RedisUtil.setIncr(jobLog.getId() + "_" + nowflowLog.getId(),0);
        if (result == nowflowLog.getParallel()) {
            ParallelLog parallelLogSuccess = new ParallelLog();
            parallelLogSuccess.setFlowId(parallelLog.getFlowId());
            parallelLogSuccess.setCode(200);
            List<ParallelLog> parallelLogList = dubboService.findParallelLogList(parallelLogSuccess);
            this.updateOutValue(nowflowLog, handleCallbackParam, parallelLogList, dynamicParameter);
        }
    } 
    /** 
    * @Description: 失败修改日志 
    * @Param: [handleCallbackParam, parallelLog, jobLog, resultUtil] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void updateFailFlowLog(HandleCallbackParam handleCallbackParam, ParallelLog parallelLog, XxlJobLog jobLog, ResultUtil resultUtil) {
        if (ReturnT.SUCCESS_CODE != handleCallbackParam.getExecuteResult().getCode()) {
            StringBuilder handleMsg = new StringBuilder();
            ParallelLog parallelLogFail = new ParallelLog();
            parallelLogFail.setFlowId(parallelLog.getFlowId());
            parallelLogFail.setCode(500);
            List<ParallelLog> parallelLogListFail = dubboService.findParallelLogList(parallelLogFail);
            parallelLogListFail.forEach(parallelLogfail -> {
                if (!StringUtils.isEmpty(parallelLogfail.getHandleMsg())) {
                    handleMsg.append(parallelLogfail.getHandleMsg() + ";");
                }
            });
            schedulerUtilService.updateHandleInfo(handleCallbackParam, jobLog);
            FlowLog flowLog = dubboService.findByFlowLogId(parallelLog.getFlowId());
            if (!StringUtils.isEmpty(handleMsg)) {
                flowLog.setHandleMsg(handleMsg.toString());
                handleCallbackParam.getExecuteResult().setMsg(handleMsg.toString());
            }
            if (flowLog.getCode() != handleCallbackParam.getExecuteResult().getCode()) {
                this.updateParentFlowLog(flowLog,handleCallbackParam);
                //flowLog.setCode(handleCallbackParam.getExecuteResult().getCode());
                //dubboService.saveFlowLog(flowLog);
            }

            resultUtil.setErrorMessage("500");
        }

    }
    /** 批量执行处理流程日志
    * @Description:
    * @Param: [map, flowLogVo, flowLogVo1, jobLog, length, atomicAlgorithm]
    * @return: void
    * @Author: zzj
    * @Date: 2018/10/31
    */
    public void batchExecuteFlowlog(Map map,FlowLogVo flowLogVo,FlowLogVo flowLogVo1,XxlJobLog jobLog,int length, AtomicAlgorithm atomicAlgorithm ){
        String dynamicParameter = (String) map.get(JobConstant.dynamicParameter);
        String modelId = (String) map.get(JobConstant.modelId);
        ProcessSteps processSteps = (ProcessSteps) map.get(JobConstant.processSteps);
        String dataId = (String) map.get(JobConstant.dataId);
        String isProcess = (String) map.get(JobConstant.isProcess);

        /**=======1.保存流程开始日志===========**/
        if (null == dataId || FlowConstant.STARTFIGURE.equals(dataId)||BooleanConstant.TRUE.equals(isProcess)) {
            this.setStartFlowLog(dynamicParameter, processSteps, flowLogVo, modelId);
        }
        FlowLog startFlowLog = new FlowLog();
            this.recursiveSave(flowLogVo1, jobLog.getId(), "1", startFlowLog);

        /**=======2.保存流程第一步日志===========**/
        FlowLog flowLog = new FlowLog();
        flowLog.setIsPl(BooleanConstant.TRUE);
        this.setFlowLog(processSteps, flowLog, startFlowLog.getParentFlowlogId(), modelId, length,"0");
        String flowLogId = this.saveFlowLog(flowLog, processSteps, jobLog.getId(), atomicAlgorithm);
        map.put(JobConstant.flowLogId,flowLogId);
        map.put(parentFlowlogId,startFlowLog.getParentFlowlogId());

    }
    /** 
    * @Description: 不批量执行保存流程日志
    * @Param: [map, parallelLog, flowLogVo, flowLogVo1, jobLog, atomicAlgorithm] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/31 
    */ 
    public void noBatchExecuteFlowlog(Map map,ParallelLog parallelLog,FlowLogVo flowLogVo,FlowLogVo flowLogVo1,XxlJobLog jobLog,AtomicAlgorithm atomicAlgorithm){
        String dynamicParameter = (String) map.get(JobConstant.dynamicParameter);
        String modelId = (String) map.get(JobConstant.modelId);
        String dataId = (String) map.get(JobConstant.dataId);
        ProcessSteps processSteps = (ProcessSteps) map.get(JobConstant.processSteps);
        String isProcess = (String) map.get(JobConstant.isProcess);
        /**=======1.将子流程值传递给父流程===========**/
        List<CommonParameter> startDynamicParameter = JSON.parseArray(dynamicParameter, CommonParameter.class);
        List<CommonParameter> paralleDynamicParameter = JSON.parseArray(parallelLog.getDynamicParameter(), CommonParameter.class);
        commonParameterService.repalceInListValueByDataId(startDynamicParameter, paralleDynamicParameter);
        if (null != flowLogVo1.getFlowLogVo()) {
            this.matchingFlowLog(flowLogVo1, flowLogVo1, paralleDynamicParameter);
        }
        /**=======2.保存流程开始节点日志===========**/
        if (null == dataId || FlowConstant.STARTFIGURE.equals(dataId)||BooleanConstant.TRUE.equals(isProcess)) {
            this.setStartFlowLog(JSON.toJSONString(startDynamicParameter), processSteps, flowLogVo, modelId);
        }
        FlowLog startFlowLog = new FlowLog();
        this.recursiveSave(flowLogVo1, jobLog.getId(), "1", startFlowLog);
        /**=======3.保存流程日志===========**/
        FlowLog flowLog = new FlowLog();
        flowLog.setIsPl(BooleanConstant.FALSE);
        processSteps.setDynamicParameter(JSON.toJSONString(paralleDynamicParameter));
        this.setFlowLog(processSteps, flowLog, startFlowLog.getParentFlowlogId(), modelId, 1,"0");
        String flowLogId = this.saveFlowLog(flowLog, processSteps, jobLog.getId(), atomicAlgorithm);
        map.put(JobConstant.flowLogId, flowLogId);
        map.put(parentFlowlogId, startFlowLog.getParentFlowlogId());
    }

    private void updateParentFlowLog(FlowLog flowLog,HandleCallbackParam handleCallbackParam){
        flowLog.setCode(handleCallbackParam.getExecuteResult().getCode());
        flowLog.setHandleMsg(handleCallbackParam.getExecuteResult().getMsg());
        dubboService.saveFlowLog(flowLog);
        if(!"1".equals(flowLog.getParentFlowlogId())){
            FlowLog parentflowLog = dubboService.findByFlowLogId(flowLog.getParentFlowlogId());
            this.updateParentFlowLog(parentflowLog,handleCallbackParam);
        }

    }

    /**
     * @Description: 处理第一步参数传递给子流程
     * @Param: [processSteps, flowLogVo, map, resultUtil]
     * @return: void
     * @Author: zzj
     * @Date: 2018/10/30
     */
    public void handleFistStepParam(ProcessSteps processSteps, FlowLogVo flowLogVo, Map map, ResultUtil<String> resultUtil){
        try {
            String dynamicParameter = (String) map.get(JobConstant.dynamicParameter);
            String modelId = (String) map.get(JobConstant.modelId);
            String dataId = (String) map.get(JobConstant.dataId);
            /**=======1.获取子流程第一步===========**/
            ProcessSteps startProcessSteps = findStepService.findStartFlowCeaselessly(processSteps.getServiceId());
            /**=======1.获取子流程下一步===========**/
            List<String> nextIdlist = findStepService.getNextIds(startProcessSteps.getNextId());
            List<ProcessSteps> nextProcessSteps = findStepService.findNextFlowCeaselessly(nextIdlist, processSteps.getServiceId(),resultUtil);
            /**=======3.组装子流程开始节点对象===========**/
            if (null == dataId || FlowConstant.STARTFIGURE.equals(dataId)) {

                this.setStartFlowLog(dynamicParameter, processSteps, flowLogVo, processSteps.getServiceId());
            }
            /**=======4.参数值进行传递===========**/
            List<CommonParameter> upParameter = JSON.parseArray(processSteps.getDynamicParameter(), CommonParameter.class);
            List<CommonParameter> nowCommonParameter = JSON.parseArray(dynamicParameter, CommonParameter.class);
            List<CommonParameter> upParameter1 = JSON.parseArray(nextProcessSteps.get(0).getDynamicParameter(), CommonParameter.class);
            List<CommonParameter> upParameter2 = dubboService.parseFlowXmlParameter(processSteps.getServiceId());
            commonParameterService.repalceListValueByUuid(upParameter, nowCommonParameter);
            commonParameterService.repalceListValueByUuid(upParameter1, nowCommonParameter);
            commonParameterService.repalceListValueByUuid(upParameter2, nowCommonParameter);
            nextProcessSteps.get(0).setDynamicParameter(JSON.toJSONString(upParameter1));
            /**=======5.组装流程第一步节点对象===========**/
            this.setFlowLog(JSON.toJSONString(upParameter), processSteps, flowLogVo, modelId);
            /**=======6.替换原有值===========**/
            map.put(JobConstant.processSteps, nextProcessSteps.get(0));
            map.put(JobConstant.dynamicParameter, JSON.toJSONString(upParameter2));
            map.put(JobConstant.modelId, processSteps.getServiceId());
        } catch (Exception e) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_307_ERROR);
            logger.error(e.getMessage(),e);
        }
    }

}


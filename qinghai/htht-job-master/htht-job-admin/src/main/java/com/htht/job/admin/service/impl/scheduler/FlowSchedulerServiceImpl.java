package com.htht.job.admin.service.impl.scheduler;

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.app.FlowLogVo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.admin.service.impl.SchedulerFlowServiceImpl;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.processsteps.ProcessSteps;
import com.htht.job.executor.model.algorithm.TaskParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @program: htht-job
 * @description: 流程调度
 * @author: zzj
 * @create: 2018-05-15 13:18
 **/
@Service("flowSchedulerService")
public class FlowSchedulerServiceImpl extends SchedulerFlowServiceImpl implements SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(FlowSchedulerServiceImpl.class);

    @Override
    /** 
    * @Description: 流程调度
    * @Param: [jobInfo] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public void scheduler(XxlJobInfo jobInfo) {
        ResultUtil<String> resultUtil=new ResultUtil<>();
        try {
            /**=======1.获取开始节点===========**/
            ProcessSteps startProcessSteps = this.findStartFlowCeaselessly(jobInfo.getModelId());
            /**=======2.获取下一个节点===========**/
            List<String> nextIdlist = this.getNextIds(startProcessSteps.getNextId());
            List<ProcessSteps> nextProcessSteps = this.findNextFlowCeaselessly(nextIdlist, jobInfo.getModelId(),resultUtil);
            if(!resultUtil.isSuccess()){
                return;
            }
            /**=======3.处理输入参数===========**/
            String dynamicParameter =this.depositDynamicParameter(jobInfo,resultUtil);
            if(!resultUtil.isSuccess()){
                return;
            }
            //流程第一步不能多模块，如果存在两个输入文件无法组合取第一条
            ProcessSteps processSteps = nextProcessSteps.get(0);
            Map map=new HashMap();
            /**=======4.参数放入map进行传递===========**/
            this.depositMap(map,processSteps,dynamicParameter,jobInfo,resultUtil);
            if(!resultUtil.isSuccess()){
                return;
            }
            /**=======5.处理参数获取并行数量并调度===========**/
            FlowLogVo flowLogVo=new FlowLogVo();
            this.handleFirstStep(map,flowLogVo,flowLogVo,resultUtil);
        } catch (Exception e) {
            resultUtil.setErrorMessage(e.getMessage());
            logger.error(e.getMessage(),e);
        }


    }

    /** 
    * @Description: 获取输入参数  
    * @Param: [jobInfo, resultUtil] 
    * @return: java.lang.String 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public String depositDynamicParameter(XxlJobInfo jobInfo,ResultUtil<String> resultUtil){
        String dynamicParameter = "";
        try {
            //opration为0为自动调用从库获取流程参数，非0接口调用或者页面传入参数
            if (0 == jobInfo.getOperation()) {
                TaskParameters taskParameters = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
                dynamicParameter = taskParameters.getDynamicParameter();
            } else {
                dynamicParameter = jobInfo.getDynamicParameter();
            }
        } catch (Exception e) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_305_ERROR);
            logger.error(e.getMessage(),e);
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
    public void depositMap(Map map,ProcessSteps processSteps,String dynamicParameter,XxlJobInfo jobInfo,ResultUtil<String> resultUtil){
        try {
            ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
            map.put(JobConstant.processSteps,processSteps);
            map.put(JobConstant.dynamicParameter,dynamicParameter);
            map.put(JobConstant.modelId,jobInfo.getModelId());
            map.put(JobConstant.executorRouteStrategyEnum,executorRouteStrategyEnum);
            map.put(JobConstant.jobInfo,jobInfo);
            map.put(JobConstant.isProcess,processSteps.getIsProcess());
            List<CommonParameter> startDynamicParameter = JSON.parseArray(dynamicParameter, CommonParameter.class);
            String outputDirectory = this.outputDirectory(startDynamicParameter);
            map.put(JobConstant.outputDirectory,outputDirectory);
        } catch (Exception e) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_306_ERROR);
            logger.error(e.getMessage(),e);        }
    }


}


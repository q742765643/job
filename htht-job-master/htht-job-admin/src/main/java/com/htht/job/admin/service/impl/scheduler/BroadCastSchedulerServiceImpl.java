package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/4/16.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.service.BroadCastSchedulerService;
import com.htht.job.admin.service.HandleFlowLogService;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.admin.service.impl.SchedulerServiceImpl;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: htht-job
 * @description: 广播调度
 * @author: zzj
 * @create: 2018-04-16 21:07
 **/
@Service("broadCastSchedulerService")
public class BroadCastSchedulerServiceImpl extends SchedulerServiceImpl implements SchedulerService, BroadCastSchedulerService {

    @Resource
    private HandleFlowLogService handleFlowLogService;

    @Override
    public void scheduler(XxlJobInfo jobInfo) {
        Map methodMap = new HashMap();
        /**======1.组装参数==========**/
        this.depositMap(jobInfo, methodMap);

        this.getAllExecuteList(jobInfo, methodMap);


    }


    public void getAllExecuteList(XxlJobInfo jobInfo, Map methodMap) {
        AtomicAlgorithmDTO atomicAlgorithmDTO= (AtomicAlgorithmDTO) methodMap.get(JobConstant.ATOMICALGORITHM);
        methodMap.put(JobConstant.JOB_HANDLER, atomicAlgorithmDTO.getModelIdentify());
        dubboService.execute(JSON.toJSONString(methodMap));

    }

    @Override
    public void broadScheduler(List<String> list, String methodMap, Map fixLinkMap, Map dyLinkMap) {
        Map map = JSONObject.parseObject(methodMap, Map.class);
        map.put(JobConstant.FIX_MAP, fixLinkMap);
        map.put(JobConstant.DY_MAP, dyLinkMap);
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match((String) map.get(JobConstant.EXECUTORROUTESTRATEGYENUM), null);
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match((String) map.get(JobConstant.BLOCK_STRATEGY), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        ExecutorFailStrategyEnum failStrategy = ExecutorFailStrategyEnum.match((String) map.get(JobConstant.FAIL_STRATEGY), ExecutorFailStrategyEnum.FAIL_ALARM);

        ArrayList<String> addressList = (ArrayList<String>) JSON.parseArray(JSON.toJSONString(map.get(JobConstant.ADDRESSLIST)), String.class);
        AtomicAlgorithmDTO atomicAlgorithmDTO = JSON.parseObject(JSON.toJSONString(map.get(JobConstant.ATOMICALGORITHM)), AtomicAlgorithmDTO.class);
        TaskParametersDTO taskParametersDTO = JSON.parseObject(JSON.toJSONString(map.get(JobConstant.TASK_PARAMETERS)), TaskParametersDTO.class);
        XxlJobInfo jobInfo = JSON.parseObject(JSON.toJSONString(map.get(JobConstant.JOBINFO)), XxlJobInfo.class);
        map.put(JobConstant.TASK_PARAMETERS, taskParametersDTO);
        map.put(JobConstant.BLOCK_STRATEGY, blockStrategy);
        map.put(JobConstant.FAIL_STRATEGY, failStrategy);
        map.put(JobConstant.EXECUTORROUTESTRATEGYENUM, executorRouteStrategyEnum);
        map.put(JobConstant.JOBINFO, jobInfo);
        map.put(JobConstant.ATOMICALGORITHM, atomicAlgorithmDTO);
        map.put(JobConstant.ADDRESSLIST, addressList);

        list.forEach(executorParams ->
                this.prepareParameters(map, executorParams)
        );


    }

    public void prepareParameters(Map methodMap, String executorParams) {
        String formatmodelParameters = (String) methodMap.get(JobConstant.FORMAT_MODEL_PARAMETERS);
        LinkedHashMap dymap = (LinkedHashMap) methodMap.get(JobConstant.DY_MAP);
        XxlJobInfo jobInfo = (XxlJobInfo) methodMap.get(JobConstant.JOBINFO);
        XxlJobLog jobLog = new XxlJobLog();
        List<CommonParameter> commonParameters = JSON.parseArray(formatmodelParameters, CommonParameter.class);
        CommonParameter commonParameter = new CommonParameter();
        commonParameter.setValue(executorParams);
        commonParameter.setParameterName("executorParams");
        commonParameter.setParameterDesc("广播参数");
        commonParameter.setParameterType("string");
        commonParameters.add(commonParameter);
        schedulerUtilService.saveJobLog(jobInfo, jobLog);
        String flowId = handleFlowLogService.saveFlowLog(jobLog, jobInfo);
        String parallelLogId = handleFlowLogService.saveParallelLog(flowId, dymap, jobInfo, JSON.toJSONString(commonParameters));
        this.excute(methodMap, jobLog, executorParams, parallelLogId, dymap);

    }

}

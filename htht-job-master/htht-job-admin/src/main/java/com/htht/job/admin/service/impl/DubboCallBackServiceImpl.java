package com.htht.job.admin.service.impl;/**
 * Created by zzj on 2018/7/20.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.service.BroadCastSchedulerService;
import com.htht.job.admin.service.FlowSchedulerService;
import com.htht.job.admin.service.SchedulerUtilService;
import com.htht.job.core.api.DubboCallBackService;
import com.htht.job.core.biz.AdminBiz;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.RegistryParam;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-07-20 13:00
 **/
@Service("dubboCallBackService")
public class DubboCallBackServiceImpl implements DubboCallBackService {
    @Autowired
    private AdminBiz adminBiz;
    @Autowired
    private BroadCastSchedulerService broadCastSchedulerService;
    @Autowired
    private FlowSchedulerService flowSchedulerService;
    @Autowired
    private SchedulerUtilService schedulerUtilService;
    @Autowired
    private XxlJobInfoDao xxlJobInfoDao;
    @Autowired
    private XxlJobLogDao xxlJobLogDao;


    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        return adminBiz.callback(callbackParamList);
    }

    public ReturnT<String> registry(RegistryParam registryParam) {
        return adminBiz.registry(registryParam);
    }

    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return adminBiz.registryRemove(registryParam);
    }

    @Override
    public void broadScheduler(List<String> list, String methodMap, Map fixLinkMap, Map dyLinkMap) {
        broadCastSchedulerService.broadScheduler(list, methodMap, fixLinkMap, dyLinkMap);
    }

    public void schedulerRpc(ProcessStepsDTO processStepsDTO, List<CommonParameter> flowParams,
                             List<String> outputList, int jobId, AtomicAlgorithmDTO atomicAlgorithmDTO, String dynamicParameter) {
        flowSchedulerService.schedulerRpc(processStepsDTO, flowParams, outputList, jobId, atomicAlgorithmDTO, dynamicParameter);

    }
    public void insertFlowFailLog(String msg,int jobId,int code){
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
        XxlJobLog jobLog = new XxlJobLog();
        schedulerUtilService.saveJobLog(jobInfo, jobLog);
        jobLog.setHandleMsg(msg);
        jobLog.setTriggerCode(200);
        jobLog.setHandleCode(code);
        jobLog.setHandleTime(new Date());
        schedulerUtilService.updateJobLog(jobInfo,jobLog);
        xxlJobLogDao.updateTriggerInfo(jobLog);
        xxlJobLogDao.updateHandleInfo(jobLog);


    }
    public void insertFailLog(String msg,String methodMap,int code){
        Map map = JSONObject.parseObject(methodMap, Map.class);
        XxlJobInfo jobInfo = JSON.parseObject(JSON.toJSONString(map.get(JobConstant.JOBINFO)), XxlJobInfo.class);
        XxlJobLog jobLog = new XxlJobLog();
        schedulerUtilService.saveJobLog(jobInfo, jobLog);
        jobLog.setHandleMsg(msg);
        jobLog.setTriggerCode(200);
        jobLog.setHandleCode(code);
        jobLog.setHandleTime(new Date());
        schedulerUtilService.updateJobLog(jobInfo,jobLog);
        xxlJobLogDao.updateTriggerInfo(jobLog);
        xxlJobLogDao.updateHandleInfo(jobLog);


    }



}


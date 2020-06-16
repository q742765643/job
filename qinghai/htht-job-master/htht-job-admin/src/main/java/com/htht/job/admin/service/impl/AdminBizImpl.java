package com.htht.job.admin.service.impl;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.dao.XxlJobRegistryDao;
import com.htht.job.admin.service.FlowSchedulerNextService;
import com.htht.job.admin.service.XxlJobService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.AdminBiz;
import com.htht.job.core.biz.model.HandleCallbackParam;
import com.htht.job.core.biz.model.RegistryParam;
import com.htht.job.core.biz.model.ReturnT;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {
    private static Logger logger = LoggerFactory.getLogger(AdminBizImpl.class);

    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    @Resource
    private XxlJobService xxlJobService;
    @Resource
    private FlowSchedulerNextService flowSchedulerNextSevice;
    @Resource
    private DubboService dubboService;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();




    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam : callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            logger.info(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode() == ReturnT.SUCCESS_CODE ? "success" : "fail"), handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        if ( handleCallbackParam.isFlow()) {
            cachedThreadPool.execute(
                    () -> flowSchedulerNextSevice.callback(handleCallbackParam)
            );
            return ReturnT.SUCCESS;
        } else {
            // valid log item
            XxlJobLog log = xxlJobLogDao.load(handleCallbackParam.getLogId());
            if (log == null) {
                return new ReturnT(ReturnT.FAIL_CODE, "log item not found.");
            }

            // trigger success, to trigger child job, and avoid repeat trigger child job
            StringBuilder childTriggerMsg = new StringBuilder();
            if (ReturnT.SUCCESS_CODE == handleCallbackParam.getExecuteResult().getCode() && ReturnT.SUCCESS_CODE != log.getHandleCode()) {
                this.childTriggerMsg(log,childTriggerMsg);
            }

            // handle msg
            StringBuilder handleMsg = new StringBuilder();

            this.handleMsg(log,childTriggerMsg,handleMsg,handleCallbackParam);
            // success, save log
            log.setHandleTime(new Date());
            log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
            log.setHandleMsg(handleMsg.toString());
            log.setLogFileName(handleCallbackParam.getLogFileName());
            xxlJobLogDao.updateHandleInfo(log);
            XxlJobInfo xxlJobInfo= xxlJobInfoDao.loadById(log.getJobId());
            if("ARCHIVE".equals(xxlJobInfo.getGlueSource())){
                dubboService.deleteParallelLogAndFlowLog(log.getId());
            }

        }

        return ReturnT.SUCCESS;
    }
    public void childTriggerMsg(XxlJobLog log, StringBuilder childTriggerMsg ){
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if (xxlJobInfo != null && StringUtils.isNotBlank(xxlJobInfo.getChildJobKey())) {
            childTriggerMsg.append( "<hr>");
            String[] childJobKeys = xxlJobInfo.getChildJobKey().split(",");
            for (int i = 0; i < childJobKeys.length; i++) {
                String[] jobKeyArr = childJobKeys[i].split("_");
                if (jobKeyArr != null && jobKeyArr.length == 2) {
                    ReturnT<String> triggerChildResult = xxlJobService.triggerJob(Integer.valueOf(jobKeyArr[1]));
                    // add msg
                    childTriggerMsg.append(MessageFormat.format("<br> {0}/{1} 触发子任务{2}, 子任务Key: {3}, 子任务触发备注: {4}",
                            (i + 1), childJobKeys.length, (triggerChildResult.getCode() == ReturnT.SUCCESS_CODE ? "成功" : "失败"), childJobKeys[i], triggerChildResult.getMsg()));
                } else {
                    childTriggerMsg.append(MessageFormat.format("<br> {0}/{1} 触发子任务失败, 子任务Key格式错误, 子任务Key: {2}",
                            (i + 1), childJobKeys.length, childJobKeys[i]));
                }
            }

        }
    }
    public void handleMsg(XxlJobLog log , StringBuilder childTriggerMsg,StringBuilder handleMsg,HandleCallbackParam handleCallbackParam ){
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        if (childTriggerMsg != null&&childTriggerMsg.length()>0) {
            handleMsg.append("<br>子任务触发备注：").append(childTriggerMsg);
        }
    }
    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        int ret = xxlJobRegistryDao.registryUpdate(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        if (ret < 1) {
            xxlJobRegistryDao.registrySave(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        xxlJobRegistryDao.registryDelete(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> triggerJob(int jobId) {
        return xxlJobService.triggerJob(jobId);
    }

}

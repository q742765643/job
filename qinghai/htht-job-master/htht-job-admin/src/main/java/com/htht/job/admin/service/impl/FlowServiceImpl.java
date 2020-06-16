package com.htht.job.admin.service.impl;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.service.FlowService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.flowchart.FlowChartModel;
import com.htht.job.executor.model.algorithm.TaskParameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;

/**
 * @program: htht-job
 * @description: 流程任务保存逻辑层
 * @author: zzj
 * @create: 2018-03-27 15:55
 **/
@Service
public class FlowServiceImpl implements FlowService {
    private static Logger logger = LoggerFactory.getLogger(FlowServiceImpl.class);

    @Resource
    private DubboService dubboService;
    @Resource
    private TaskParametersService taskParametersService;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;

    public static boolean isValidExpression(String cronExpression) {
        try {
            new CronExpression(cronExpression);
            return true;
        } catch (ParseException var2) {
            return false;
        }
    }

    public ResultUtil<String> add(XxlJobInfo jobInfo) {
        ResultUtil<String> resultUtil = new ResultUtil<>();
        /**========1.校验参数==============**/
        this.checkJobInfo(jobInfo, resultUtil);
        if (!resultUtil.isSuccess()) {
            return resultUtil;
        }
        /**========2.拼装参数==============**/
        this.paddingParameter(jobInfo);

        /**========3.保存任务==============**/
        xxlJobInfoDao.save(jobInfo);
        if (jobInfo.getId() < 1) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_210_ERROR);
            return resultUtil;
        }
        /**========4.添加定时==============**/
        this.addJob(jobInfo, resultUtil);
        if (!resultUtil.isSuccess()) {
            return resultUtil;
        }

        return resultUtil;

    }

    public void checkJobInfo(XxlJobInfo jobInfo, ResultUtil<String> resultUtil) {
        if (StringUtils.isEmpty(jobInfo.getJobDesc())) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_211_ERROR);
            return;
        }
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_203_ERROR);
            return;
        }
        if (StringUtils.isEmpty(jobInfo.getModelId())) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_212_ERROR);
            return;
        }
        if (!StringUtils.isEmpty(jobInfo.getModelId())) {
            FlowChartModel flowChartModel = dubboService.getFlowById(jobInfo.getModelId());
            if (null == flowChartModel) {
                resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_213_ERROR);
                return;
            }

        }

    }

    public void paddingParameter(XxlJobInfo jobInfo) {
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        jobInfo.setAuthor(username);
        jobInfo.setExecutorBlockStrategy("CONCURRENT_EXECUTION");
        jobInfo.setExecutorFailStrategy("FAIL_ALARM");
        jobInfo.setGlueType("BEAN");
        jobInfo.setJobGroup(-1);
//        jobInfo.setTasktype(5);

        TaskParameters taskParameters = new TaskParameters();
        taskParameters.setParameterId(jobInfo.getModelId());
        taskParameters.setCreateTime(new Date());
        taskParameters.setDynamicParameter(jobInfo.getDynamicParameter());

        TaskParameters saveParameterModel = taskParametersService.saveJobParameter(taskParameters);

        jobInfo.setExecutorParam(saveParameterModel.getId());

        //dubboService.saveEachInput(jobInfo.getDynamicParameter(), jobInfo.getModelId());


    }

    public void addJob(XxlJobInfo jobInfo, ResultUtil<String> resultUtil) {
        String qz_group = String.valueOf(jobInfo.getJobGroup());
        String qz_name = String.valueOf(jobInfo.getId());
        try {
            XxlJobDynamicScheduler.addJob(qz_name, qz_group, jobInfo.getJobCron());
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            try {
                xxlJobInfoDao.delete(jobInfo.getId());
                XxlJobDynamicScheduler.removeJob(qz_name, qz_group);
            } catch (SchedulerException e1) {
                logger.error(e.getMessage(), e1);
            }
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_214_ERROR);
        }
    }

    public ResultUtil<String> updateSave(XxlJobInfo jobInfo) {
        ResultUtil<String> resultUtil = new ResultUtil<>();
        /**========1.校验参数==============**/
        this.checkJobInfo(jobInfo, resultUtil);
        if (!resultUtil.isSuccess()) {
            return resultUtil;
        }

        TaskParameters saveParameterModel = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
        saveParameterModel.setDynamicParameter(jobInfo.getDynamicParameter());
        saveParameterModel.setParameterId(jobInfo.getModelId());
        saveParameterModel.setUpdateTime(new Date());

        taskParametersService.saveJobParameter(saveParameterModel);
        //dubboService.deleteByParameterId(saveParameterModel.getId());
       // dubboService.saveEachInput(jobInfo.getDynamicParameter(), jobInfo.getModelId());

        XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
        exists_jobInfo.setModelId(jobInfo.getModelId());
        exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
        exists_jobInfo.setJobCron(jobInfo.getJobCron());
        exists_jobInfo.setPriority(jobInfo.getPriority());
        exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
        exists_jobInfo.setJobCronName(jobInfo.getJobCronName());

        xxlJobInfoDao.update(exists_jobInfo);
        String qz_group = String.valueOf(exists_jobInfo.getJobGroup());
        String qz_name = String.valueOf(exists_jobInfo.getId());
        try {
            boolean ret = XxlJobDynamicScheduler.rescheduleJob(qz_group, qz_name, exists_jobInfo.getJobCron());
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }


        return resultUtil;

    }

}


package com.htht.job.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobGroup;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.app.AppXxlJobInfo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.dao.XxlJobLogGlueDao;
import com.htht.job.admin.service.AppJobInfoService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by zzj on 2018/3/14.
 */
@Service
public class AppJobInfoServiceImpl implements AppJobInfoService {
    private static Logger logger = LoggerFactory.getLogger(AppJobInfoServiceImpl.class);


    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobLogGlueDao xxlJobLogGlueDao;
    @Resource
    private TaskParametersService taskParametersService;
    @Resource
    private AtomicAlgorithmService atomicAlgorithmService;

    public ResultUtil<String> add(AppXxlJobInfo jobInfo, List<CommonParameter> fixedParameter, List<CommonParameter> dynamicParameter) {
        // valid
        ResultUtil<String> result = new ResultUtil<>();
        XxlJobGroup group = xxlJobGroupDao.load(jobInfo.getJobGroup());
        if (group == null) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_202_ERROR);
        }
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_203_ERROR);
        }
        if (StringUtils.isBlank(jobInfo.getJobDesc())) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_204_ERROR);
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_205_ERROR);
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_206_ERROR);
        }
        if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_207_ERROR);
        }
        if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_208_ERROR);
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_209_ERROR);
        }
        if (!result.isSuccess()) {
            return result;
        }
        TaskParametersDTO taskParametersDTO = new TaskParametersDTO();
        taskParametersDTO.setParameterId(jobInfo.getModelId());
        taskParametersDTO.setCreateTime(new Date());
        taskParametersDTO.setJobId(String.valueOf(jobInfo.getId()));
        taskParametersDTO.setFixedParameter(JSON.toJSONString(fixedParameter));
        taskParametersDTO.setDynamicParameter(JSON.toJSONString(dynamicParameter));
        taskParametersDTO.setModelParameters(jobInfo.getModelParameters());
        TaskParametersDTO saveParameterModel = taskParametersService.saveJobParameter(taskParametersDTO);
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(jobInfo.getModelId());
        jobInfo.setExecutorParam(saveParameterModel.getId());
        jobInfo.setExecutorHandler(atomicAlgorithmDTO.getModelIdentify());
        // add in db
        XxlJobInfo xxlJobInfo = new XxlJobInfo();

        //复制对象
        BeanUtils.copyProperties(xxlJobInfo, jobInfo);

        xxlJobInfoDao.save(xxlJobInfo);
        if (jobInfo.getId() < 1) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_210_ERROR);
        }
        if (!result.isSuccess()) {
            return result;
        }
        // add in quartz
        String qzGroup = String.valueOf(jobInfo.getJobGroup());
        String qzName = String.valueOf(jobInfo.getId());
        try {
            XxlJobDynamicScheduler.addJob(qzName, qzGroup, jobInfo.getJobCron());
            return result;
        } catch (SchedulerException e) {
            try {
                xxlJobInfoDao.delete(jobInfo.getId());
                XxlJobDynamicScheduler.removeJob(qzName, qzGroup);
            } catch (SchedulerException e1) {
                logger.error(e.getMessage(),e);
            }
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_210_ERROR);
            return result;
        }
    }
}

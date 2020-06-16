package com.htht.job.admin.service.impl;

import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.dao.XxlJobLogGlueDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.admin.service.XxlJobService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import com.htht.job.executor.model.registry.RegistryDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 * core job action for xxl-job
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlJobServiceImpl implements XxlJobService {
    private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);
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
    @Resource
    private DubboService dubboService;
    @Resource
    private CheckAliveService checkAliveService;

    @Override
    public Map<String, Object> pageList(int start, int length, int jobGroup, String executorHandler, String filterTime, String tasktype) {

        // page list
        List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobGroup, executorHandler, tasktype);
        int listCount = xxlJobInfoDao.pageListCount(start, length, jobGroup, executorHandler, tasktype);

        // fill job info
        if (null != list && !list.isEmpty()) {
            for (XxlJobInfo jobInfo : list) {
                XxlJobDynamicScheduler.fillJobInfo(jobInfo);
            }
        }

        // package result
        Map<String, Object> maps = new HashMap<>();
        maps.put("recordsTotal", listCount);        // 总记录数
        maps.put("recordsFiltered", listCount);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @Override
    public ReturnT<String> add(XxlJobInfo jobInfo) {
        if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "运行模式非法非法");
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "请输入算法");
        }
        if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }
        jobInfo.setJobGroup(-1);
        ReturnT<String> returnParam=this.checkParam(jobInfo);
        if(ReturnT.SUCCESS_CODE!=returnParam.getCode()){
            return returnParam;
        }

        TaskParametersDTO taskParametersDTO = new TaskParametersDTO();
        taskParametersDTO.setParameterId(jobInfo.getModelId());
        taskParametersDTO.setCreateTime(new Date());
        taskParametersDTO.setJobId(String.valueOf(jobInfo.getId()));
        taskParametersDTO.setFixedParameter(jobInfo.getFixedParameter());
        taskParametersDTO.setDynamicParameter(jobInfo.getDynamicParameter());
        taskParametersDTO.setModelParameters(jobInfo.getModelParameters());
        TaskParametersDTO saveParameterModel = taskParametersService.saveJobParameter(taskParametersDTO);
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(jobInfo.getModelId());
        jobInfo.setExecutorParam(saveParameterModel.getId());
        jobInfo.setExecutorHandler(atomicAlgorithmDTO.getModelIdentify());
        xxlJobInfoDao.save(jobInfo);
        if (jobInfo.getId() < 1) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "新增任务失败");
        }
        String qzGroup = String.valueOf(jobInfo.getJobGroup());
        String qzName = String.valueOf(jobInfo.getId());
        try {
            XxlJobDynamicScheduler.addJob(qzName, qzGroup, jobInfo.getJobCron());
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            try {
                xxlJobInfoDao.delete(jobInfo.getId());
                XxlJobDynamicScheduler.removeJob(qzName, qzGroup);
            } catch (SchedulerException e1) {
                logger.error(e.getMessage(), e1);
            }
            return new ReturnT<>(ReturnT.FAIL_CODE, "新增任务失败:" + e.getMessage());
        }
    }
    public ReturnT<String> checkParam(XxlJobInfo jobInfo){
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "请输入格式正确的“Cron”");
        }
        if (StringUtils.isBlank(jobInfo.getJobDesc())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "请输入“任务描述”");
        }
        if (StringUtils.isBlank(jobInfo.getAuthor())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "请输入“负责人”");
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "路由策略非法");
        }
        if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "调度异常处理非法");
        }
        if (jobInfo.getExecutorFailStrategy().equals("FAIL_RETRY")) {
            jobInfo.setFailRetryTimes(Integer.parseInt(jobInfo.getAlarmEmail()));
            jobInfo.setAlarmEmail(null);
        }
        if (StringUtils.isNotBlank(jobInfo.getChildJobKey())) {
            String[] childJobKeys = jobInfo.getChildJobKey().split(",");
            for (String childJobKeyItem : childJobKeys) {
                String[] childJobKeyArr = childJobKeyItem.split("_");
                if (childJobKeyArr.length != 2) {
                    return new ReturnT<>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
                }
                XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.valueOf(childJobKeyArr[1]));
                if (childJobInfo == null) {
                    return new ReturnT<>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
                }
            }
        }
        return ReturnT.SUCCESS;
    }
    @Override
    public ReturnT<String> reschedule(XxlJobInfo jobInfo) {

        ReturnT<String> returnParam=this.checkParam(jobInfo);
        if(ReturnT.SUCCESS_CODE!=returnParam.getCode()){
            return returnParam;
        }

        // stage job info
        XxlJobInfo existsJobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
        if (existsJobInfo == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "参数异常");
        }
        TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
        if (null == taskParametersDTO) {
            taskParametersDTO = new TaskParametersDTO();
            taskParametersDTO.setCreateTime(new Date());

        } else {
            taskParametersDTO.setUpdateTime(new Date());

        }
        taskParametersDTO.setParameterId(jobInfo.getModelId());
        taskParametersDTO.setJobId(String.valueOf(jobInfo.getId()));
        taskParametersDTO.setFixedParameter(jobInfo.getFixedParameter());
        taskParametersDTO.setDynamicParameter(jobInfo.getDynamicParameter());
        taskParametersDTO.setModelParameters(jobInfo.getModelParameters());
        TaskParametersDTO saveParameterModel = taskParametersService.saveJobParameter(taskParametersDTO);
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(jobInfo.getModelId());
        existsJobInfo.setJobCron(jobInfo.getJobCron());
        existsJobInfo.setJobCronName(jobInfo.getJobCronName());
        existsJobInfo.setJobDesc(jobInfo.getJobDesc());
        existsJobInfo.setAuthor(jobInfo.getAuthor());
        existsJobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
        existsJobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        existsJobInfo.setFailRetryTimes(jobInfo.getFailRetryTimes());
        existsJobInfo.setExecutorHandler(atomicAlgorithmDTO.getModelIdentify());
        existsJobInfo.setExecutorParam(saveParameterModel.getId());
        existsJobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        existsJobInfo.setExecutorFailStrategy(jobInfo.getExecutorFailStrategy());
        existsJobInfo.setChildJobKey(jobInfo.getChildJobKey());
        existsJobInfo.setModelId(jobInfo.getModelId());
        existsJobInfo.setProductId(jobInfo.getProductId());
        existsJobInfo.setPriority(jobInfo.getPriority());
        xxlJobInfoDao.update(existsJobInfo);

        // fresh quartz
        String qzGroup = String.valueOf(existsJobInfo.getJobGroup());
        String qzName = String.valueOf(existsJobInfo.getId());
        try {
            boolean ret = XxlJobDynamicScheduler.rescheduleJob(qzGroup, qzName, existsJobInfo.getJobCron());
            return ret ? ReturnT.SUCCESS : ReturnT.FAIL;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }

        return ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> remove(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            XxlJobDynamicScheduler.removeJob(name, group);
            xxlJobInfoDao.delete(id);
            xxlJobLogDao.delete(id);
            xxlJobLogGlueDao.deleteByJobId(id);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
        return ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> pause(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            boolean ret = XxlJobDynamicScheduler.pauseJob(name, group);    // jobStatus do not store
            return ret ? ReturnT.SUCCESS : ReturnT.FAIL;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }

    @Override
    public ReturnT<String> resume(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            boolean ret = XxlJobDynamicScheduler.resumeJob(name, group);
            return ret ? ReturnT.SUCCESS : ReturnT.FAIL;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }

    @Override
    public ReturnT<String> triggerJob(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
        if (xxlJobInfo == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "任务ID非法");
        }

        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            XxlJobDynamicScheduler.triggerJob(name, group);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @Override
    public Map<String, Object> dashboardInfo() throws InterruptedException {

        int jobInfoCount = xxlJobInfoDao.findAllCount();
        int jobLogCount = xxlJobLogDao.triggerCountByHandleCode(-1);
        int jobLogSuccessCount = xxlJobLogDao.triggerCountByHandleCode(ReturnT.SUCCESS_CODE);
        List<RegistryDTO> registryDTOList = dubboService.findAllRegistry();
        ArrayList<String> adressList = new ArrayList<>();
        for (RegistryDTO registryDTO : registryDTOList) {
            adressList.add(registryDTO.getRegistryIp());
        }
        List<String> executerAddressSet = checkAliveService.checkAliveByAddressList(adressList);
        int executorCount = executerAddressSet.size();
        Map<String, Object> dashboardMap = new HashMap<>();
        dashboardMap.put("jobInfoCount", jobInfoCount);
        dashboardMap.put("jobLogCount", jobLogCount);
        dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
        dashboardMap.put("executorCount", executorCount);
        return dashboardMap;
    }

    @Override
    public ReturnT<Map<String, Object>> triggerChartDate() {
        Date from = DateUtils.addDays(new Date(), -30);
        Date to = new Date();

        List<String> triggerDayList = new ArrayList<>();
        List<Integer> triggerDayCountSucList = new ArrayList<>();
        List<Integer> triggerDayCountFailList = new ArrayList<>();
        int triggerCountSucTotal = 0;
        int triggerCountFailTotal = 0;

        List<Map<String, Object>> triggerCountMapAll = xxlJobLogDao.triggerCountByDay(from, to, -1);
        List<Map<String, Object>> triggerCountMapSuc = xxlJobLogDao.triggerCountByDay(from, to, ReturnT.SUCCESS_CODE);
        if (CollectionUtils.isNotEmpty(triggerCountMapAll)) {
            for (Map<String, Object> item : triggerCountMapAll) {
                String day = String.valueOf(item.get("triggerDay"));
                String triggerCount=String.valueOf(item.get("triggerCount"));
                int dayAllCount = Integer.valueOf(triggerCount);
                int daySucCount = 0;
                int dayFailCount = dayAllCount - daySucCount;

                if (CollectionUtils.isNotEmpty(triggerCountMapSuc)) {
                    for (Map<String, Object> sucItem : triggerCountMapSuc) {
                        String daySuc = String.valueOf(sucItem.get("triggerDay"));
                        if (day.equals(daySuc)) {
                            daySucCount = Integer.valueOf(String.valueOf(sucItem.get("triggerCount")));
                            dayFailCount = dayAllCount - daySucCount;
                        }
                    }
                }

                triggerDayList.add(day);
                triggerDayCountSucList.add(daySucCount);
                triggerDayCountFailList.add(dayFailCount);
                triggerCountSucTotal += daySucCount;
                triggerCountFailTotal += dayFailCount;
            }
        } else {
            for (int i = 4; i > -1; i--) {
                triggerDayList.add(FastDateFormat.getInstance("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -i)));
                triggerDayCountSucList.add(0);
                triggerDayCountFailList.add(0);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("triggerDayList", triggerDayList);
        result.put("triggerDayCountSucList", triggerDayCountSucList);
        result.put("triggerDayCountFailList", triggerDayCountFailList);
        result.put("triggerCountSucTotal", triggerCountSucTotal);
        result.put("triggerCountFailTotal", triggerCountFailTotal);
        return new ReturnT<>(result);
    }

    /* 
     * 查询与数管相关的流程任务
	 */
    @Override
    public List<XxlJobInfo> findDataFlow() {
        List<XxlJobInfo> findAll = xxlJobInfoDao.findAll();
        ArrayList<XxlJobInfo> dataFlowList = new ArrayList<>();
        for (XxlJobInfo xxlJobInfo : findAll) {
            if (xxlJobInfo.getJobDesc().contains("数管") && xxlJobInfo.getJobDesc().contains("入库")) {
                dataFlowList.add(xxlJobInfo);
            }
        }

        return dataFlowList;
    }

}

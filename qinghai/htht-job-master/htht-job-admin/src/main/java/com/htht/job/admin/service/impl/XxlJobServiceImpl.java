package com.htht.job.admin.service.impl;

import com.alibaba.fastjson.JSON;
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
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.executor.model.algorithm.TaskParameters;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.registry.Registry;

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
        int list_count = xxlJobInfoDao.pageListCount(start, length, jobGroup, executorHandler, tasktype);

        // fill job info
        if (list != null && list.size() > 0) {
            for (XxlJobInfo jobInfo : list) {
                XxlJobDynamicScheduler.fillJobInfo(jobInfo);
            }
        }

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);        // 总记录数
        maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @Override
    public ReturnT<String> add(XxlJobInfo jobInfo) {
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入格式正确的“Cron”");
        }
        if (StringUtils.isBlank(jobInfo.getJobDesc())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“任务描述”");
        }
        if (StringUtils.isBlank(jobInfo.getAuthor())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“负责人”");
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "路由策略非法");
        }
       /* if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "阻塞处理策略非法");
        }*/
        if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "调度异常处理非法");
        }
        if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "运行模式非法非法");
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入算法");
        }

        // fix "\r" in shell
        if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }
        jobInfo.setJobGroup(-1);

        // childJobKey valid
        if (StringUtils.isNotBlank(jobInfo.getChildJobKey())) {
            String[] childJobKeys = jobInfo.getChildJobKey().split(",");
            for (String childJobKeyItem : childJobKeys) {
                String[] childJobKeyArr = childJobKeyItem.split("_");
                if (childJobKeyArr.length != 2) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
                }
                XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.valueOf(childJobKeyArr[1]));
                if (childJobInfo == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
                }
            }
        }
        //数据类型为"实时数据"，即"dataType"为1的情况下，判端 天数"productRangeDay"，为空则设为1
        verifyProductRangeDay(jobInfo);
        
        TaskParameters taskParameters = new TaskParameters();
        taskParameters.setParameterId(jobInfo.getModelId());
        taskParameters.setCreateTime(new Date());
        taskParameters.setJobId(String.valueOf(jobInfo.getId()));
        taskParameters.setFixedParameter(jobInfo.getFixedParameter());
        taskParameters.setDynamicParameter(jobInfo.getDynamicParameter());
        taskParameters.setModelParameters(jobInfo.getModelParameters());
        TaskParameters saveParameterModel = taskParametersService.saveJobParameter(taskParameters);
        AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(jobInfo.getModelId());
        jobInfo.setExecutorParam(saveParameterModel.getId());
        jobInfo.setExecutorHandler(atomicAlgorithm.getModelIdentify());
        // add in db
        xxlJobInfoDao.save(jobInfo);
        if (jobInfo.getId() < 1) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "新增任务失败");
        }

        // add in quartz
        String qz_group = String.valueOf(jobInfo.getJobGroup());
        String qz_name = String.valueOf(jobInfo.getId());
        try {
            XxlJobDynamicScheduler.addJob(qz_name, qz_group, jobInfo.getJobCron());
            XxlJobDynamicScheduler.pauseJob(qz_name, qz_group);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            try {
                xxlJobInfoDao.delete(jobInfo.getId());
                XxlJobDynamicScheduler.removeJob(qz_name, qz_group);
            } catch (SchedulerException e1) {
                logger.error(e.getMessage(), e1);
            }
            return new ReturnT<String>(ReturnT.FAIL_CODE, "新增任务失败:" + e.getMessage());
        }
    }

	private void verifyProductRangeDay(XxlJobInfo jobInfo) {
		if(StringUtils.isNotBlank(jobInfo.getModelParameters())) {
        	@SuppressWarnings("unchecked")
			Map<String,String> modelParamMap = (Map<String, String>) JSON.parse(jobInfo.getModelParameters());
        	if(null!=modelParamMap && "1".equals(modelParamMap.get("dateType")) && StringUtils.isBlank(modelParamMap.get("productRangeDay"))){
        		modelParamMap.put("productRangeDay", "1");
        		jobInfo.setModelParameters(JSON.toJSONString(modelParamMap));
        	}
        }
	}

    @Override
    public ReturnT<String> reschedule(XxlJobInfo jobInfo) {

        // valid
        if (!CronExpression.isValidExpression(jobInfo.getJobCron())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入格式正确的“Cron”");
        }
        if (StringUtils.isBlank(jobInfo.getJobDesc())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“任务描述”");
        }
        if (StringUtils.isBlank(jobInfo.getAuthor())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入“负责人”");
        }
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "路由策略非法");
        }
        /*if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "阻塞处理策略非法");
        }*/
        if (ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), null) == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "调度异常处理非法");
        }

        // childJobKey valid
        if (StringUtils.isNotBlank(jobInfo.getChildJobKey())) {
            String[] childJobKeys = jobInfo.getChildJobKey().split(",");
            for (String childJobKeyItem : childJobKeys) {
                String[] childJobKeyArr = childJobKeyItem.split("_");
                if (childJobKeyArr.length != 2) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})格式错误", childJobKeyItem));
                }
                XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.valueOf(childJobKeyArr[1]));
                if (childJobInfo == null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, MessageFormat.format("子任务Key({0})无效", childJobKeyItem));
                }
            }
        }

        // stage job info
        XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
        if (exists_jobInfo == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "参数异常");
        }
        //数据类型为"实时数据"，即"dataType"为1的情况下，判端 天数"productRangeDay"，为空则设为1
        verifyProductRangeDay(jobInfo);
        
        //String old_cron = exists_jobInfo.getJobCron();
        TaskParameters taskParameters = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
        if (null == taskParameters) {
            taskParameters = new TaskParameters();
            taskParameters.setCreateTime(new Date());

        } else {
            taskParameters.setUpdateTime(new Date());

        }
        taskParameters.setParameterId(jobInfo.getModelId());
        taskParameters.setJobId(String.valueOf(jobInfo.getId()));
        taskParameters.setFixedParameter(jobInfo.getFixedParameter());
        taskParameters.setDynamicParameter(jobInfo.getDynamicParameter());
        taskParameters.setModelParameters(jobInfo.getModelParameters());
        TaskParameters saveParameterModel = taskParametersService.saveJobParameter(taskParameters);
        AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(jobInfo.getModelId());
        exists_jobInfo.setJobCron(jobInfo.getJobCron());
        exists_jobInfo.setJobCronName(jobInfo.getJobCronName());
        exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
        exists_jobInfo.setAuthor(jobInfo.getAuthor());
        exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
        exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        exists_jobInfo.setExecutorHandler(atomicAlgorithm.getModelIdentify());
        exists_jobInfo.setExecutorParam(saveParameterModel.getId());
        exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        exists_jobInfo.setExecutorFailStrategy(jobInfo.getExecutorFailStrategy());
        exists_jobInfo.setChildJobKey(jobInfo.getChildJobKey());
        exists_jobInfo.setModelId(jobInfo.getModelId());
        exists_jobInfo.setProductId(jobInfo.getProductId());
        exists_jobInfo.setPriority(jobInfo.getPriority());
        xxlJobInfoDao.update(exists_jobInfo);

        // fresh quartz
        String qz_group = String.valueOf(exists_jobInfo.getJobGroup());
        String qz_name = String.valueOf(exists_jobInfo.getId());
        try {
            boolean ret = XxlJobDynamicScheduler.rescheduleJob(qz_group, qz_name, exists_jobInfo.getJobCron());
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
            return new ReturnT<String>(ReturnT.FAIL_CODE, "任务ID非法");
        }

        String group = String.valueOf(xxlJobInfo.getJobGroup());
        String name = String.valueOf(xxlJobInfo.getId());

        try {
            XxlJobDynamicScheduler.triggerJob(name, group);
            return ReturnT.SUCCESS;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @Override
    public Map<String, Object> dashboardInfo() throws InterruptedException {

        int jobInfoCount = xxlJobInfoDao.findAllCount();
        int jobLogCount = xxlJobLogDao.triggerCountByHandleCode(-1);
        int jobLogSuccessCount = xxlJobLogDao.triggerCountByHandleCode(ReturnT.SUCCESS_CODE);
        List<Registry> registryList=dubboService.findAllRegistry();
        ArrayList<String> adressList=new ArrayList<String>();
        for(Registry registry:registryList){
            adressList.add(registry.getRegistryIp());
        }
        List<String> executerAddressSet=checkAliveService.checkAliveByAddressList(adressList);
        /*// executor count
        Set<String> executerAddressSet = new HashSet<String>();
        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();

        if (CollectionUtils.isNotEmpty(groupList)) {
            for (XxlJobGroup group : groupList) {
                if (CollectionUtils.isNotEmpty(group.getRegistryList())) {
                    executerAddressSet.addAll(group.getRegistryList());
                }
            }
        }*/

        //int executorCount = executerAddressSet.size();
        int executorCount = executerAddressSet.size();
        Map<String, Object> dashboardMap = new HashMap<String, Object>();
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

        List<String> triggerDayList = new ArrayList<String>();
        List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
        List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
        int triggerCountSucTotal = 0;
        int triggerCountFailTotal = 0;

        List<Map<String, Object>> triggerCountMapAll = xxlJobLogDao.triggerCountByDay(from, to, -1);
        List<Map<String, Object>> triggerCountMapSuc = xxlJobLogDao.triggerCountByDay(from, to, ReturnT.SUCCESS_CODE);
        if (CollectionUtils.isNotEmpty(triggerCountMapAll)) {
            for (Map<String, Object> item : triggerCountMapAll) {
                String day = String.valueOf(item.get("triggerDay"));
                int dayAllCount = Integer.valueOf(String.valueOf(item.get("triggerCount")));
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

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("triggerDayList", triggerDayList);
        result.put("triggerDayCountSucList", triggerDayCountSucList);
        result.put("triggerDayCountFailList", triggerDayCountFailList);
        result.put("triggerCountSucTotal", triggerCountSucTotal);
        result.put("triggerCountFailTotal", triggerCountFailTotal);
        return new ReturnT<Map<String, Object>>(result);
    }
    
    /* 
	 * 查询与数管相关的流程任务
	 */
	@Override
	public List<XxlJobInfo> findDataFlow() {
		List<XxlJobInfo> findAll = xxlJobInfoDao.findAll();
		ArrayList<XxlJobInfo> dataFlowList = new ArrayList<XxlJobInfo>();
		for (XxlJobInfo xxlJobInfo : findAll) {
			if(xxlJobInfo.getJobDesc().contains("数管")&&xxlJobInfo.getJobDesc().contains("入库")) {
				dataFlowList.add(xxlJobInfo);
			}
		}
		
		return dataFlowList;
	}

	@Override
	public ReturnT<String> copyJob(XxlJobInfo jobInfo) {
		// TODO Auto-generated method stub
				if (jobInfo.getId() < 1) {
					return new ReturnT<>(ReturnT.FAIL_CODE, "复制任务失败");
				}
				XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
				if (xxlJobInfo == null) {
					return new ReturnT<>(ReturnT.FAIL_CODE, "复制任务不存在");
				}
				TaskParameters taskParametersDTO = taskParametersService.findJobParameterById(xxlJobInfo.getExecutorParam());
				taskParametersDTO.setId(null);
				taskParametersDTO.setCreateTime(new Date());
				xxlJobInfo.setJobDesc(xxlJobInfo.getJobDesc()+"-副本");
				xxlJobInfoDao.save(xxlJobInfo);
				taskParametersDTO.setJobId(String.valueOf(xxlJobInfo.getId()));
				TaskParameters saveParameterModel = taskParametersService.saveJobParameter(taskParametersDTO);
				xxlJobInfo.setExecutorParam(saveParameterModel.getId());
				xxlJobInfoDao.update(xxlJobInfo);
				if (xxlJobInfo.getId() < 1) {
					return new ReturnT<>(ReturnT.FAIL_CODE, "复制任务失败");
				}
				String qzGroup = String.valueOf(xxlJobInfo.getJobGroup());
				String qzName = String.valueOf(xxlJobInfo.getId());
				try {
					XxlJobDynamicScheduler.addJob(qzName, qzGroup, xxlJobInfo.getJobCron());
					XxlJobDynamicScheduler.pauseJob(qzName, qzGroup);    // jobStatus do not store
					return ReturnT.SUCCESS;
				} catch (SchedulerException e) {
					logger.error(e.getMessage(), e);
					try {
						xxlJobInfoDao.delete(jobInfo.getId());
						XxlJobDynamicScheduler.removeJob(qzName, qzGroup);
					} catch (SchedulerException e1) {
						logger.error(e.getMessage(), e1);
					}
					return new ReturnT<>(ReturnT.FAIL_CODE, "复制任务失败:" + e.getMessage());
				}
	}

}

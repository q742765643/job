package com.htht.job.admin.service.impl.scheduler;/**
													* Created by zzj on 2018/4/16.
													*/

import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.route.strategy.ExecutorRouteRandom;
import com.htht.job.admin.core.thread.JobFailMonitorHelper;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.admin.service.impl.SchedulerServiceImpl;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.algorithm.TaskParameters;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: htht-job
 * @description: 广播调度
 * @author: zzj
 * @create: 2018-04-16 21:07
 **/
@Service("broadCastSchedulerService")
public class BroadCastSchedulerServiceImpl extends SchedulerServiceImpl implements SchedulerService {
	private static Logger logger = LoggerFactory.getLogger(BroadCastSchedulerServiceImpl.class);

	public void scheduler(XxlJobInfo jobInfo) {
		AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(jobInfo.getModelId());

		ArrayList<String> addressList = new ArrayList<>();
		addressList = this.findAddressList(jobInfo.getModelId(), atomicAlgorithm.getDealAmount());
		LinkedHashMap fixmap = new LinkedHashMap(20);
		LinkedHashMap dymap = new LinkedHashMap(20);
		String modelParameters = "";
		fixmap = taskParametersService.getJobParameter(jobInfo.getExecutorParam(), jobInfo.getModelId(), "1");
		dymap = taskParametersService.getJobParameter(jobInfo.getExecutorParam(), jobInfo.getModelId(), "2");
		TaskParameters taskParameters = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
		modelParameters = taskParameters.getModelParameters(); // block strategy
		String formatmodelParameters = taskParametersService.formatJobModelParameters(modelParameters);
		ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(),
				ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
		// fail strategy
		ExecutorFailStrategyEnum failStrategy = ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(),
				ExecutorFailStrategyEnum.FAIL_ALARM);
		// route strategy
		ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum
				.match(jobInfo.getExecutorRouteStrategy(), null);
		List<String> list = new ArrayList<String>();
		ResultUtil<List<String>> result = dubboService.execute(modelParameters, jobInfo.getExecutorHandler(), fixmap,
				dymap);
		list = result.getResult();
		for (int i = 0; i < list.size(); i++) {
			/** =======1.保存日志=========== **/
			XxlJobLog jobLog = new XxlJobLog();
			Integer saveJobLogId = this.saveJobLog(jobInfo, jobLog);
			// 保存flow_log
			FlowLog flowLog = new FlowLog();
			flowLog.setJobLogId(jobLog.getId());
			flowLog.setCreateTime(new Date());
			flowLog.setLabel(jobInfo.getJobDesc());
			FlowLog saveFlowLog = dubboService.saveFlowLog(flowLog);
			// 保存parallelLog
			ParallelLog parallelLog = new ParallelLog();
			parallelLog.setFlowId(saveFlowLog.getId());
			parallelLog.setCreateTime(new Date());
			String logDynmic = taskParametersService.getLogDynamic(dymap, jobInfo.getModelId());
			parallelLog.setDynamicParameter(logDynmic);
			parallelLog.setModelParameters(formatmodelParameters);
			ParallelLog saveParallelLog = dubboService.saveParallelLog(parallelLog);
			/** =======2.注册方式，各种策略，地址列表=========== **/
			ReturnT<String> triggerResult = new ReturnT<String>(null);
			StringBuffer triggerMsgSb = new StringBuffer();
			this.acquireTriggerResult(triggerResult, blockStrategy, failStrategy, executorRouteStrategyEnum,
					addressList, triggerMsgSb);
			jobLog.setGlueType(jobInfo.getGlueType());
			jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
			jobLog.setExecutorParam(jobInfo.getExecutorParam());
			jobLog.setTriggerTime(new Date());
			if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
				TriggerParam triggerParam = new TriggerParam();
				/** =======3.获取调度参数=========== **/
				this.acquireTriggerParamBroadCast(triggerParam, jobInfo, jobLog, fixmap, dymap, modelParameters,
						list.get(i), atomicAlgorithm, saveParallelLog);

				/** =======4.调度执行器=========== **/
				triggerResult = new ExecutorRouteRandom().routeRun(triggerParam, addressList);
				triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>")
						.append(triggerResult.getMsg());
				if (triggerResult.getCode() != ReturnT.SUCCESS_CODE
						&& failStrategy == ExecutorFailStrategyEnum.FAIL_RETRY) {
					triggerResult = new ExecutorRouteRandom().routeRun(triggerParam, addressList);
					triggerMsgSb
							.append("<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试<<<<<<<<<<< </span><br>")
							.append(triggerResult.getMsg());
				}
				// 执行失败重试5次
				if (triggerResult.getCode() != ReturnT.SUCCESS_CODE
						&& failStrategy == ExecutorFailStrategyEnum.FAIL_RETRY_FIVE) {
					// 重新查找存活节点
					addressList = this.findAddressList(jobInfo.getModelId(), atomicAlgorithm.getDealAmount());
					HashMap<String, Object> failRestrtFive = failRestrtFive(executorRouteStrategyEnum, triggerParam,
							triggerMsgSb, triggerResult, jobInfo, atomicAlgorithm);
					triggerResult = (ReturnT<String>) failRestrtFive.get("triggerResult");
					triggerMsgSb = (StringBuffer) failRestrtFive.get("triggerMsgSb");

				}

			}

			/** =======5.保存 trigger-info=========== **/
			this.updateTriggerInfo(jobInfo, jobLog, triggerResult, triggerMsgSb);

			/** =======6.发送警告=========== **/
			JobFailMonitorHelper.monitor(jobLog.getId());
			logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
		}
	}

	private HashMap<String, Object> failRestrtFive(ExecutorRouteStrategyEnum executorRouteStrategyEnum,
			TriggerParam triggerParam, StringBuffer triggerMsgSb, ReturnT<String> triggerResult, XxlJobInfo jobInfo,
			AtomicAlgorithm atomicAlgorithm) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		int maxRetryTime = 5;
		int time = 0;
		do {
			time++;
			try {
				// 检查存活节点重新获取
				ArrayList<String> addressList = this.findAddressList(jobInfo.getModelId(),
						atomicAlgorithm.getDealAmount());
				triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
				triggerMsgSb.append("<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试<<<<<<<<<<< </span><br>")
						.append(triggerResult.getMsg());
				if(triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
					map.put("triggerResult", triggerResult);
					map.put("triggerMsgSb", triggerMsgSb);
					return map;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (time < maxRetryTime);
		map.put("triggerResult", triggerResult);
		map.put("triggerMsgSb", triggerMsgSb);
		return map;
	}

	private void acquireTriggerParamBroadCast(TriggerParam triggerParam, XxlJobInfo jobInfo, XxlJobLog jobLog,
                                              LinkedHashMap fixmap, LinkedHashMap dymap, String modelParameters, String executorParams,
                                              AtomicAlgorithm atomicAlgorithm, ParallelLog parallelLog) {
		triggerParam.setJobId(jobInfo.getId());
		triggerParam.setExecutorHandler(atomicAlgorithm.getModelIdentify());
		triggerParam.setExecutorParams(executorParams);
		triggerParam.setExecutorBlockStrategy(atomicAlgorithm.getExecutorBlockStrategy());
		triggerParam.setLogId(jobLog.getId());
		triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
		triggerParam.setGlueType(jobInfo.getGlueType());
		triggerParam.setGlueSource(jobInfo.getGlueSource());
		triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
		triggerParam.setBroadcastIndex(0);
		triggerParam.setBroadcastTotal(1);
		triggerParam.setModelParameters(modelParameters);
		triggerParam.setFixedParameter(fixmap);
		triggerParam.setDynamicParameter(dymap);
		triggerParam.setModelId(jobInfo.getModelId());
		triggerParam.setProductId(jobInfo.getProductId());
		triggerParam.setAlgorId(atomicAlgorithm.getId());
		triggerParam.setPriority(jobInfo.getPriority());
		triggerParam.setParallelLogId(parallelLog.getId());
		triggerParam.setFlow(false);
	}
}

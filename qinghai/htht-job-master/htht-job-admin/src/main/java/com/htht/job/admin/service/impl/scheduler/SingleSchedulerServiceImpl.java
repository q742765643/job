package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/4/16.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.core.thread.JobFailMonitorHelper;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.admin.service.impl.SchedulerServiceImpl;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParameters;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @program: htht-job
 * @description: 单个调度
 * @author: zzj
 * @create: 2018-04-16 21:05
 **/
@Service("singleSchedulerService")
public class SingleSchedulerServiceImpl extends SchedulerServiceImpl implements SchedulerService {
    private static Logger logger = LoggerFactory.getLogger(SingleSchedulerServiceImpl.class);

    public void scheduler(XxlJobInfo jobInfo) {
        AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(jobInfo.getModelId());

        ArrayList<String> addressList = new ArrayList<>();
        addressList = this.findAddressList(jobInfo.getModelId(), atomicAlgorithm.getDealAmount());
        LinkedHashMap fixmap = new LinkedHashMap(20);
        LinkedHashMap dymap = new LinkedHashMap(20);
        String modelParameters = "";
        fixmap = taskParametersService.getJobParameter(jobInfo.getExecutorParam(), jobInfo.getModelId(), "1");
        if(jobInfo.getOperation()==1) {
            List<CommonParameter> params = JSON.parseArray(jobInfo.getDynamicParameter(), CommonParameter.class);
            for(CommonParameter commonParameter:params){
                dymap.put(commonParameter.getParameterName(),commonParameter.getValue());
            }

        }else{
            dymap = taskParametersService.getJobParameter(jobInfo.getExecutorParam(), jobInfo.getModelId(), "2");

        }
        TaskParameters taskParameters = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
        List<CommonParameter> dynamicParameter = JSON.parseArray(taskParameters.getDynamicParameter(), CommonParameter.class);
        /**======1获取输出目录==========**/
        String outputDirectory = "";
        for (CommonParameter commonParameter : dynamicParameter) {
            if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType())) {
                outputDirectory = commonParameter.getValue();
                break;
            }
        }
        modelParameters = taskParameters.getModelParameters();        // block strategy
        String formatmodelParameters = taskParametersService.formatJobModelParameters(modelParameters);
        List<LinkedHashMap> dymaps = new ArrayList<LinkedHashMap>();
        this.dealFlowCeaselessly(dynamicParameter, outputDirectory, dymap, dymaps, jobInfo);
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        // fail strategy
        ExecutorFailStrategyEnum failStrategy = ExecutorFailStrategyEnum.match(jobInfo.getExecutorFailStrategy(), ExecutorFailStrategyEnum.FAIL_ALARM);
        // route strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        /**=======1.保存日志===========**/
        for (LinkedHashMap mapdy : dymaps) {

            XxlJobLog jobLog = new XxlJobLog();
            Integer saveJobLogId = this.saveJobLog(jobInfo, jobLog);
            //保存flow_log
            FlowLog flowLog = new FlowLog();
            flowLog.setJobLogId(jobLog.getId());
            flowLog.setCreateTime(new Date());
            flowLog.setLabel(jobInfo.getJobDesc());
            FlowLog saveFlowLog = dubboService.saveFlowLog(flowLog);
            //保存parallelLog
            ParallelLog parallelLog = new ParallelLog();
            parallelLog.setFlowId(saveFlowLog.getId());
            parallelLog.setCreateTime(new Date());
            String logDynmic = taskParametersService.getLogDynamic(mapdy,jobInfo.getModelId());
            parallelLog.setDynamicParameter(logDynmic);
            parallelLog.setModelParameters(formatmodelParameters);
            ParallelLog saveParallelLog = dubboService.saveParallelLog(parallelLog);
            /**=======2.注册方式，各种策略，地址列表===========**/
            ReturnT<String> triggerResult = new ReturnT<String>(null);
            StringBuffer triggerMsgSb = new StringBuffer();
            this.acquireTriggerResult(triggerResult, blockStrategy, failStrategy, executorRouteStrategyEnum, addressList, triggerMsgSb);
            jobLog.setGlueType(jobInfo.getGlueType());
            jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
            jobLog.setExecutorParam(jobInfo.getExecutorParam());
            jobLog.setTriggerTime(new Date());
            if (triggerResult.getCode() == ReturnT.SUCCESS_CODE) {
                TriggerParam triggerParam = new TriggerParam();
                /**=======3.获取调度参数===========**/
                this.acquireTriggerParam(triggerParam, jobInfo, jobLog, fixmap, mapdy, modelParameters, atomicAlgorithm,saveParallelLog);

                /**=======4.调度执行器===========**/
                triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
                triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());
                if (triggerResult.getCode() != ReturnT.SUCCESS_CODE && failStrategy == ExecutorFailStrategyEnum.FAIL_RETRY) {
                    triggerResult = executorRouteStrategyEnum.getRouter().routeRun(triggerParam, addressList);
                    triggerMsgSb.append("<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>失败重试<<<<<<<<<<< </span><br>").append(triggerResult.getMsg());
                }
                //执行失败重试5次
                if (triggerResult.getCode() != ReturnT.SUCCESS_CODE && failStrategy == ExecutorFailStrategyEnum.FAIL_RETRY_FIVE) {
                	//重新查找存活节点
                	addressList = this.findAddressList(jobInfo.getModelId(), atomicAlgorithm.getDealAmount());
                	HashMap<String,Object> failRestrtFive = failRestrtFive(executorRouteStrategyEnum, triggerParam, triggerMsgSb, triggerResult,jobInfo, atomicAlgorithm);
                	triggerResult =  (ReturnT<String>) failRestrtFive.get("triggerResult");
                	triggerMsgSb = (StringBuffer) failRestrtFive.get("triggerMsgSb");
                	
                }

            }

            /**=======5.保存 trigger-info===========**/
            this.updateTriggerInfo(jobInfo, jobLog, triggerResult, triggerMsgSb);

            /**=======6.发送警告===========**/
            JobFailMonitorHelper.monitor(jobLog.getId());
            logger.debug(">>>>>>>>>>> xxl-job trigger end, jobId:{}", jobLog.getId());
        }

    }
    
    
	public HashMap<String, Object> failRestrtFive(ExecutorRouteStrategyEnum executorRouteStrategyEnum,
			TriggerParam triggerParam, StringBuffer triggerMsgSb, ReturnT<String> triggerResult, XxlJobInfo jobInfo,
			AtomicAlgorithm atomicAlgorithm) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		int maxRetryTime = 5;
		int time = 0;
		do {
			time++;
			try {
				//检查存活节点重新获取
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
		// arrayList.add(triggerResult);
		// arrayList.add(triggerMsgSb);
		map.put("triggerResult", triggerResult);
		map.put("triggerMsgSb", triggerMsgSb);
		return map;
	}

	private void acquireTriggerParam(TriggerParam triggerParam, XxlJobInfo jobInfo, XxlJobLog jobLog, LinkedHashMap fixmap, LinkedHashMap dymap, String modelParameters, AtomicAlgorithm atomicAlgorithm, ParallelLog parallelLog) {
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(atomicAlgorithm.getModelIdentify());
        triggerParam.setExecutorParams("");
        triggerParam.setExecutorBlockStrategy(atomicAlgorithm.getExecutorBlockStrategy());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTim(jobLog.getTriggerTime().getTime());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdatetime(jobInfo.getGlueUpdatetime().getTime());
        triggerParam.setBroadcastIndex(0);
        triggerParam.setBroadcastTotal(1);
        triggerParam.setFixedParameter(fixmap);
        triggerParam.setDynamicParameter(dymap);
        triggerParam.setModelParameters(modelParameters);
        triggerParam.setModelId(jobInfo.getModelId());
        triggerParam.setProductId(jobInfo.getProductId());
        triggerParam.setAlgorId(atomicAlgorithm.getId());
        triggerParam.setPriority(jobInfo.getPriority());
        triggerParam.setParallelLogId(parallelLog.getId());
        triggerParam.setFlow(false);

    }


    public void dealFlowCeaselessly(List<CommonParameter> dynamicParameter, String outputDirectory, LinkedHashMap<String, String> dymap, List<LinkedHashMap> dymaps, XxlJobInfo jobInfo) {

        /**======2获取流程参数==========**/
        List<CommonParameter> inFile = new ArrayList<CommonParameter>();
        List<CommonParameter> inFolder = new ArrayList<CommonParameter>();
        List<CommonParameter> outFile = new ArrayList<CommonParameter>();
        List<CommonParameter> paramFile = new ArrayList<CommonParameter>();
        List<CommonParameter> fileList = new ArrayList<CommonParameter>();
        List<CommonParameter> outFolder = new ArrayList<CommonParameter>();

        for (int i = 0; i < dynamicParameter.size(); i++) {
            if (FlowConstant.INFILE.equals(dynamicParameter.get(i).getParameterType())) {
                inFile.add(dynamicParameter.get(i));
            } else if (FlowConstant.INFOLDER.equals(dynamicParameter.get(i).getParameterType())) {
                inFolder.add(dynamicParameter.get(i));
            } else if (FlowConstant.OUTFILE.equals(dynamicParameter.get(i).getParameterType())) {
                outFile.add(dynamicParameter.get(i));
            } else if (FlowConstant.OUTFOLDER.equals(dynamicParameter.get(i).getParameterType())) {
                outFolder.add(dynamicParameter.get(i));
            }
        }
        if (inFile.size() > 0 && fileList.size() == 0) {
            CommonParameter inFileCommonParameter = inFile.get(0);
            String expandedname = "*";
            if (!StringUtils.isEmpty(inFileCommonParameter.getExpandedname())) {
                expandedname = inFileCommonParameter.getExpandedname();
            }
            List<File> files   = FileUtil.getAllFiles(inFileCommonParameter.getValue(), "*" + expandedname);
            for (int i = 0; i < files.size(); i++) {
                inFileCommonParameter.setValue(files.get(i).getPath().replaceAll("\\\\", "/"));
                CommonParameter commonParameter = JSON.parseObject(JSON.toJSONString(inFileCommonParameter), CommonParameter.class);
                fileList.add(commonParameter);
            }
        }
        if(inFile.size() > 0 && fileList.size() == 0){
            CommonParameter inFileCommonParameter = inFile.get(0);
            fileList.add(inFileCommonParameter);
        }
        if (inFolder.size() > 0 && fileList.size() == 0) {
            CommonParameter inFolderCommonParameter = inFolder.get(0);
            File file = new File(inFolderCommonParameter.getValue());
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File fileIn = files[i];
                if (fileIn.isDirectory()) {
                    inFolderCommonParameter.setValue(fileIn.getPath().replaceAll("\\\\", "/"));
                    CommonParameter commonParameter = JSON.parseObject(JSON.toJSONString(inFolderCommonParameter), CommonParameter.class);
                    fileList.add(commonParameter);
                }
            }
        }
        if (paramFile.size() > 0 && fileList.size() == 0) {
            inFile.add(paramFile.get(0));
            fileList.add(paramFile.get(0));
        }
        if (outFile.size()>0) {
            for(int j=0;j<outFolder.size();j++){
                if(outFolder.get(j).getValue().indexOf( "/" +jobInfo.getJobDesc())==-1){
                    File file = new File(outFolder.get(j).getValue() + "/" +jobInfo.getJobDesc());
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdirs();
                    }
                    dymap.put(outFolder.get(j).getParameterName(), outFolder.get(j).getValue() + "/" +jobInfo.getJobDesc()+ "/");
                }

            }
        }
        if (fileList.size() > 0) {
            //设置输入和输出
            for (int i = 0; i < fileList.size(); i++) {
                dymap.put(fileList.get(i).getParameterName(), fileList.get(i).getValue());
                if (inFile.size() == 1 && outFile.size()>=1) {
                    //设置输入和输出
                    String tempvalue = fileList.get(i).getValue();
                    File file_input=new File(tempvalue);
                    String fileName = "";
                    if(file_input.exists()){
                       if(!file_input.isDirectory()){
                           fileName=file_input.getName().substring(0, file_input.getName().lastIndexOf("."));
                       }
                    }
                    File file = new File(outputDirectory + "/" + jobInfo.getJobDesc() + "/" + fileName);
                    if (!file.exists() && !file.isDirectory()) {
                        file.mkdirs();
                    }
                    for(int j=0;j<outFile.size();j++){
                        dymap.put(outFile.get(j).getParameterName(), outputDirectory + "/" + jobInfo.getJobDesc() + "/" + fileName + "/" + fileName+outFile.get(j).getExpandedname());
                    }

                }

                LinkedHashMap newDymap = new LinkedHashMap();
                for (Map.Entry<String, String> entry : dymap.entrySet()) {
                    newDymap.put(entry.getKey(), entry.getValue());
                }
                dymaps.add(newDymap);

            }

        } else {
            dymaps.add(dymap);
        }

    }

}


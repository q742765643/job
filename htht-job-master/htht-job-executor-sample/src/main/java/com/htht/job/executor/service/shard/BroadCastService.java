package com.htht.job.executor.service.shard;/**
 * Created by zzj on 2018/11/21.
 */

import com.htht.job.core.api.DubboCallBackService;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.executor.XxlJobExecutor;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @program: htht-job
 * @description:
 * @author: zzj
 * @create: 2018-11-21 10:38
 **/
@Service
public class BroadCastService {
    @Value("${cluster.job.executor.logpath}")
    private String logpath;
    @Autowired
    private DubboCallBackService dubboCallBackService;

    public void execute(TriggerParam triggerParam, ProcessStepsDTO processStepsDTO, List<CommonParameter> flowParams,
                        int jobId, AtomicAlgorithmDTO atomicAlgorithmDTO, String dynamicParameter) {
        Runnable race = () -> {
            String handler = triggerParam.getExecutorHandler();
            IJobHandler newJobHandler = XxlJobExecutor.loadJobHandler(handler);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowFormat = sdf.format(triggerParam.getLogDateTim());
            String outputLog = logpath + nowFormat + "/" + "prj.log";
            XxlJobFileAppender.makeLogFileNameByPath(outputLog);
            triggerParam.setLogFileName(outputLog);
            /**===========3执行器执行业务代码=========*/
            try {
                newJobHandler.execute(triggerParam);
                if(null==triggerParam.getOutput()||triggerParam.getOutput().isEmpty()){
                    dubboCallBackService.insertFlowFailLog("没有数据",jobId,200);
                    return;
                }
                dubboCallBackService.schedulerRpc(processStepsDTO, flowParams, triggerParam.getOutput(), jobId, atomicAlgorithmDTO, dynamicParameter);


            } catch (Exception e) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                String errorMsg = stringWriter.toString();
                dubboCallBackService.insertFlowFailLog(errorMsg,jobId,500);
            }
        };
        race.run();
    }
}


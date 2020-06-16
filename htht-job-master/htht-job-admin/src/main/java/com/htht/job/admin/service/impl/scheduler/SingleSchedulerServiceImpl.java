package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/4/16.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.jobbean.ParamClassifyBean;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.service.HandleFlowLogService;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.admin.service.SettleDocumentsService;
import com.htht.job.admin.service.impl.SchedulerServiceImpl;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Qualifier("settleDocumentsServiceImpl")
    @Resource
    private SettleDocumentsService settleDocumentsService;
    @Resource
    private HandleFlowLogService handleFlowLogService;

    @Override
    public void scheduler(XxlJobInfo jobInfo) {
        ResultUtil<String> resultUtil = new ResultUtil<>();
        Map methodMap = new HashMap();
        this.depositMap(jobInfo, methodMap);
        LinkedHashMap<String, String> dymap = (LinkedHashMap<String, String>) methodMap.get(JobConstant.DY_MAP);
        List<LinkedHashMap> dymaps = new ArrayList<>();
        this.getAllExecuteList(jobInfo, resultUtil, dymap, dymaps);
        String formatmodelParameters = (String) methodMap.get(JobConstant.FORMAT_MODEL_PARAMETERS);
        dymaps.forEach(linkedHashMap -> {
            XxlJobLog jobLog = new XxlJobLog();
            schedulerUtilService.saveJobLog(jobInfo, jobLog);
            String flowId = handleFlowLogService.saveFlowLog(jobLog, jobInfo);
            String parallelLogId = handleFlowLogService.saveParallelLog(flowId, linkedHashMap, jobInfo, formatmodelParameters);
            this.excute(methodMap, jobLog, "", parallelLogId, linkedHashMap);
        });


    }


    public void getAllExecuteList(XxlJobInfo jobInfo, ResultUtil<String> resultUtil,Map<String, String> dymap, List<LinkedHashMap> dymaps) {
        ParamClassifyBean paramClassifyBean = new ParamClassifyBean();
        TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
        List<CommonParameter> commonParameterList = JSON.parseArray(taskParametersDTO.getDynamicParameter(), CommonParameter.class);
        String outputDirectory = settleDocumentsService.outputDirectory(commonParameterList);
        this.getParamClassifyBean(commonParameterList,paramClassifyBean);
        /**======1扫描文件列表变换值==========**/
        if (!paramClassifyBean.getInFile().isEmpty()) {
            settleDocumentsService.addFileParam(paramClassifyBean.getInFile().get(0), paramClassifyBean, resultUtil);
            if (!resultUtil.isSuccess()) {
                schedulerUtilService.insertFailLogByFile("没有数据",jobInfo,200);
                return;
            }
        }

        /**======2扫描子文件夹变换值==========**/
        if (!paramClassifyBean.getInFolder().isEmpty() && paramClassifyBean.getFileList().isEmpty()) {
            settleDocumentsService.addFolderParam(paramClassifyBean.getInFolder().get(0), paramClassifyBean, resultUtil);
            if (!resultUtil.isSuccess()) {
                schedulerUtilService.insertFailLogByFile("没有数据",jobInfo,200);
                return;
            }
        }
        if (!paramClassifyBean.getOutFolder().isEmpty()) {
            this.setFolder(paramClassifyBean.getOutFolder(), dymap, jobInfo.getJobDesc());
        }
        if (!paramClassifyBean.getFileList().isEmpty()) {
            this.setOutInputValue(paramClassifyBean, outputDirectory, jobInfo.getJobDesc(), dymap, dymaps);
        }
        if (paramClassifyBean.getFileList().isEmpty()) {
            dymaps.add((LinkedHashMap) dymap);
        }
    }
    public void getParamClassifyBean(List<CommonParameter> commonParameterList,ParamClassifyBean paramClassifyBean){
        for (int i = 0; i < commonParameterList.size(); i++) {
            CommonParameter commonParameter = commonParameterList.get(i);
            if (FlowConstant.INFILE.equals(commonParameter.getParameterType())) {
                paramClassifyBean.getInFile().add(commonParameter);
            } else if (FlowConstant.INFOLDER.equals(commonParameter.getParameterType())) {
                paramClassifyBean.getInFolder().add(commonParameter);
            } else if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType())) {
                paramClassifyBean.getOutFile().add(commonParameter);
            } else if (FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())) {
                paramClassifyBean.getOutFolder().add(commonParameter);
            }

        }
    }
    public void setFolder(List<CommonParameter> outFolder, Map<String, String> dymap, String jobDesc) {
        outFolder.forEach(commonParameter -> {
            File file = new File(commonParameter.getValue() + File.separator + jobDesc);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }
            dymap.put(commonParameter.getParameterName(), commonParameter.getValue() + File.separator + jobDesc + File.separator);
        });

    }

    public void setOutInputValue(ParamClassifyBean paramClassifyBean, String outputDirectory, String jobDesc,Map<String, String> dymap, List<LinkedHashMap> dymaps) {
        List<CommonParameter> fileList = paramClassifyBean.getFileList();
        List<CommonParameter> inFile = paramClassifyBean.getInFile();
        List<CommonParameter> outFile = paramClassifyBean.getOutFile();
        fileList.forEach(commonParameter -> {
            int infileLength = inFile.size();
            int outfileLength = outFile.size();
            dymap.put(commonParameter.getParameterName(), commonParameter.getValue());
            if (infileLength == 1 && outfileLength >= 1) {
                String tempvalue = commonParameter.getValue();
                this.setFileName(tempvalue, outFile, outputDirectory, jobDesc, dymap);
            }
            LinkedHashMap newDymap = new LinkedHashMap();
            for (Map.Entry<String, String> entry : dymap.entrySet()) {
                newDymap.put(entry.getKey(), entry.getValue());
            }
            dymaps.add(newDymap);

        });
    }

    public void setFileName(String tempvalue, List<CommonParameter> outFile, String outputDirectory, String jobDesc, Map<String, String> dymap) {
        tempvalue = tempvalue.replaceAll("\\\\", "/");
        File fileInput = new File(tempvalue);
        String fileName = "";
        if (fileInput.exists() && !fileInput.isDirectory()) {
            fileName = fileInput.getName().substring(0, fileInput.getName().lastIndexOf('.'));

        }
        String filePath = outputDirectory + File.separator + jobDesc + File.separator + fileName;
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        final String outfilename = fileName;
        outFile.forEach(commonParameter ->
                dymap.put(commonParameter.getParameterName(), outputDirectory + File.separator + jobDesc + File.separator + outfilename + commonParameter.getExpandedname())
        );

    }

}


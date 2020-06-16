package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/10/30.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.jobbean.ParamClassifyBean;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.dao.XxlJobRegistryDao;
import com.htht.job.admin.service.CommonParameterService;
import com.htht.job.admin.service.HandleFlowLogService;
import com.htht.job.admin.service.SchedulerUtilService;
import com.htht.job.admin.service.SettleDocumentsService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.constant.BooleanConstant;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowchart.FlowChartModel;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.processsteps.ProcessSteps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-30 14:20
 **/
@Service
public class SettleDocumentsServiceImpl extends FindStepServiceImpl implements SettleDocumentsService {
    @Resource
    protected XxlJobInfoDao xxlJobInfoDao;
    @Resource
    protected XxlJobLogDao xxlJobLogDao;
    @Resource
    protected AtomicAlgorithmService atomicAlgorithmService;
    @Resource
    protected XxlJobRegistryDao xxlJobRegistryDao;
    @Resource
    protected CommonParameterService commonParameterService;
    @Resource
    protected SchedulerUtilService schedulerUtilService;
    @Resource
    protected HandleFlowLogService handleFlowLogService;
    /** 
    * @Description: 并行获取flowMap 
    * @Param: [paramClassifyBean, flowParams, parallelLogList, methodMap] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void parallelByFlowMap(ParamClassifyBean paramClassifyBean, List<CommonParameter> flowParams, List<ParallelLog> parallelLogList, Map methodMap) {

        ProcessSteps processSteps = (ProcessSteps) methodMap.get(JobConstant.processSteps);
        if (BooleanConstant.FALSE.equals(processSteps.getIsPl())) {
            int inFileLength = paramClassifyBean.getInFile().size();
            int outFileLength = paramClassifyBean.getOutFile().size();
            if (inFileLength == 1 && outFileLength >= 1) {
                String[] value = paramClassifyBean.getInFile().get(0).getValue().split("#HT#");
                String tempvalue = value[0];
                this.setFileName(tempvalue, paramClassifyBean.getOutFile(), flowParams, methodMap);
            }
            ParallelLog parallelLog = new ParallelLog();
            parallelLog.setDynamicParameter(JSON.toJSONString(flowParams));
            parallelLogList.add(parallelLog);

        } else {
            this.parallelByFlowMapPl(paramClassifyBean, flowParams, parallelLogList, methodMap);

        }


    }
    /** 
    * @Description: 并行获取批量下flowmap 
    * @Param: [paramClassifyBean, flowParams, parallelLogList, methodMap] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void parallelByFlowMapPl(ParamClassifyBean paramClassifyBean, List<CommonParameter> flowParams, List<ParallelLog> parallelLogList, Map methodMap) {
        int inFileLength = paramClassifyBean.getInFile().size();
        int outFileLength = paramClassifyBean.getOutFile().size();
        Map<Integer, String> flowMap = (Map) methodMap.get(JobConstant.flowMap);
        String infileValue = "";
        if (inFileLength > 0 && outFileLength > 0) {
            infileValue = paramClassifyBean.getInFile().get(0).getValue();
        }
        String[] value = infileValue.split("#HT#");
        for (int i = 0; i < paramClassifyBean.getParallel(); i++) {
            for (Map.Entry<Integer, String> entry : flowMap.entrySet()) {
                flowParams.get(entry.getKey()).setValue(entry.getValue().split("#HT#")[i]);
            }
            if (inFileLength == 1 && outFileLength >= 1) {
                String tempvalue = value[i];
                this.setFileName(tempvalue, paramClassifyBean.getOutFile(), flowParams, methodMap);
            }
            ParallelLog parallelLog = new ParallelLog();
            parallelLog.setDynamicParameter(JSON.toJSONString(flowParams));
            parallelLogList.add(parallelLog);

        }
    }
    
    public void setOutInputValue(Map methodMap, ParamClassifyBean paramClassifyBean, List<ParallelLog> parallelLogList) {
        Map mapDataId = (Map) methodMap.get(JobConstant.mapDataId);
        List<CommonParameter> flowParams = (List<CommonParameter>) methodMap.get(JobConstant.flowParams);
        List<CommonParameter> fileList = paramClassifyBean.getFileList();
        List<CommonParameter> inFile = paramClassifyBean.getInFile();
        List<CommonParameter> outFile = paramClassifyBean.getOutFile();
        fileList.forEach(commonParameter -> {
            int intIndex = (int) mapDataId.get(commonParameter.getDataID());
            flowParams.set(intIndex, commonParameter);
            int infileLength = inFile.size();
            int outfileLength = outFile.size();
            if (infileLength == 1 && outfileLength >= 1) {
                String tempvalue = commonParameter.getValue();
                this.setFileName(tempvalue, outFile, flowParams, methodMap);
            }
            ParallelLog parallelLog = new ParallelLog();
            parallelLog.setDynamicParameter(JSON.toJSONString(flowParams));
            parallelLogList.add(parallelLog);
        });
        /** for (int i = 0; i < fileList.size(); i++) {
         int intIndex = (int) mapDataId.get(fileList.get(i).getDataID());
         flowParams.set(intIndex, fileList.get(i));
         if (inFile.size() ==1 && outFile.size() >=1) {
         //设置输入和输出
         String tempvalue = fileList.get(i).getValue();
         this.setFileName(tempvalue,outFile,flowParams,methodMap);

         }
         if(inFile.size() >1 && outFile.size() >=1){
         Map dataIdMap=new HashMap();
         File fileInput=new File(fileList.get(i). getValue());
         dataIdMap.put(fileList.get(i).getDataID(),fileInput.getName());
         List<Mapping> mapping=JSON.parseArray(matchRelation.getMatchData(),Mapping.class);
         for(int j=0;j<mapping.size();j++){
         if (mapping.get(j).getIsOut()==1) {
         String fileName= (String) dataIdMap.get(mapping.get(j).getValue());
         int indexOut= (int) mapDataId.get( mapping.get(j).getSfdataId());
         flowParams.get(indexOut).setValue(outputDirectory + "/" + flowChart.getProcessCHName() + "/" + label + "/" + fileName + "/" + fileName+ flowParams.get(indexOut).getExpandedname()) ;
         }else{
         String fileName = CompareFileUtil.getGoalFileName(
         mapping.get(j).getMatchBefore(), mapping.get(j).getMatchAfter(), (String) dataIdMap.get(mapping.get(j).getValue()));
         int indexIn= (int) mapDataId.get(mapping.get(j).getSfdataId());
         dataIdMap.put( mapping.get(j).getSfdataId(),fileName);
         flowParams.get(indexIn).setValue( flowParams.get(indexIn).getValue()+"/"+fileName+flowParams.get(indexIn).getExpandedname()) ;
         }
         }

         }*/


    }
    /** 
    * @Description: 设置输出文件夹 
    * @Param: [outFolder, flowParams, methodMap] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void setFolder(List<CommonParameter> outFolder, List<CommonParameter> flowParams, Map methodMap) {
        Map mapDataId = (Map) methodMap.get(JobConstant.mapDataId);
        FlowChartModel flowChartModel = (FlowChartModel) methodMap.get(JobConstant.flowChart);
        String label = (String) methodMap.get(JobConstant.label);
        outFolder.forEach(commonParameter -> {
            if (commonParameter.getValue().indexOf(flowChartModel.getProcessCHName() + "/" + label) == -1) {
                String folder = commonParameter.getValue() + "/" + flowChartModel.getProcessCHName() + "/" + label + "/";
                File file = new File(folder);
                if (!file.exists() && !file.isDirectory()) {
                    file.mkdirs();
                }
                int outIndex = (int) mapDataId.get(commonParameter.getDataID());
                flowParams.get(outIndex).setValue(folder);
            }

        });

    }
    /** 
    * @Description: 设置文件名 
    * @Param: [tempvalue, outFile, flowParams, methodMap] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void setFileName(String tempvalue, List<CommonParameter> outFile, List<CommonParameter> flowParams, Map methodMap) {
        Map mapDataId = (Map) methodMap.get(JobConstant.mapDataId);
        FlowChartModel flowChartModel = (FlowChartModel) methodMap.get(JobConstant.flowChart);
        String outputDirectory = (String) methodMap.get(JobConstant.outputDirectory);
        String label = (String) methodMap.get(JobConstant.label);
        tempvalue = tempvalue.replaceAll("\\\\", "/");
        File fileInput = new File(tempvalue);
        String fileName = "";
        if (fileInput.exists() && !fileInput.isDirectory()) {
            fileName = fileInput.getName().substring(0, fileInput.getName().lastIndexOf('.'));

        }

        String filePath = outputDirectory + File.separator + flowChartModel.getProcessCHName() + File.separator + label + File.separator + fileName;
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        final String outfilename = fileName;
        outFile.forEach(commonParameter -> {
            int outIndex = (int) mapDataId.get(commonParameter.getDataID());
            flowParams.get(outIndex).setValue(outputDirectory + "/" + flowChartModel.getProcessCHName() + "/" + label + "/" + outfilename + "/" + outfilename + flowParams.get(outIndex).getExpandedname());

        });

    }
    /** 
    * @Description: 各自参数类型保存 
    * @Param: [paramClassifyBean, commonParameterList, map] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void paramClassifyBean(ParamClassifyBean paramClassifyBean, List<CommonParameter> commonParameterList, Map map) {
        for (int i = 0; i < commonParameterList.size(); i++) {
            CommonParameter commonParameter = commonParameterList.get(i);
            map.put(commonParameter.getDataID(), i);
            if (FlowConstant.INFILE.equals(commonParameter.getParameterType())) {
                paramClassifyBean.getInFile().add(commonParameter);
            } else if (FlowConstant.INFOLDER.equals(commonParameter.getParameterType())) {
                paramClassifyBean.getInFolder().add(commonParameter);
            } else if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType())) {
                paramClassifyBean.getOutFile().add(commonParameter);
            } else if (FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())) {
                paramClassifyBean.getOutFolder().add(commonParameter);
            }
            if (commonParameter.getValue().indexOf("#HT#") != -1) {
                paramClassifyBean.getFlowMap().put(i, commonParameter.getValue());
                String[] value = commonParameter.getValue().split("#HT#");
                paramClassifyBean.setParallel(value.length);

            }

        }
    }
    /** 
    * @Description: 添加fileparam
    * @Param: [inFilecommonParameter, paramClassifyBean, resultUtil] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public void addFileParam(CommonParameter inFilecommonParameter, ParamClassifyBean paramClassifyBean, ResultUtil resultUtil) {
        String expandedname = "*";
        if (!StringUtils.isEmpty(inFilecommonParameter.getExpandedname())) {
            expandedname = inFilecommonParameter.getExpandedname();
        }
        List<File> fileList = FileUtil.getAllFiles(inFilecommonParameter.getValue().replaceAll("\\\\", "/"), "*" + expandedname);
        fileList.forEach(file -> {
            inFilecommonParameter.setValue(file.getPath().replace("\\\\", "/"));
            CommonParameter commonParameter = JSON.parseObject(JSON.toJSONString(inFilecommonParameter), CommonParameter.class);
            paramClassifyBean.getFileList().add(commonParameter);
        });
        if (fileList.isEmpty()) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_302_ERROR);
        }
    }
    /**
    * @Description:添加infolder
    * @Param: [inFolderCommonParameter, paramClassifyBean, resultUtil]
    * @return: void
    * @Author: zzj
    * @Date: 2018/11/1
    */
    public void addFolderParam(CommonParameter inFolderCommonParameter, ParamClassifyBean paramClassifyBean, ResultUtil resultUtil) {
        File file = new File(inFolderCommonParameter.getValue());
        File[] fileList = file.listFiles();
        for (File fileIn : fileList) {
            if (fileIn.isDirectory()) {
                inFolderCommonParameter.setValue(fileIn.getPath().replaceAll("\\\\", "/"));
                CommonParameter commonParameter = JSON.parseObject(JSON.toJSONString(inFolderCommonParameter), CommonParameter.class);
                paramClassifyBean.getFileList().add(commonParameter);
            }
        }
        if (paramClassifyBean.getFileList().isEmpty()) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_303_ERROR);
        }
    }
    /** 
    * @Description: 获取输出路径 
    * @Param: [taskParams] 
    * @return: java.lang.String 
    * @Author: zzj
    * @Date: 2018/11/1 
    */ 
    public String outputDirectory(List<CommonParameter> taskParams) {
        String outputDirectory = "";
        for (CommonParameter commonParameter : taskParams) {
            if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType())) {
                outputDirectory = commonParameter.getValue();
                break;
            }
        }
        return outputDirectory;
    }

}


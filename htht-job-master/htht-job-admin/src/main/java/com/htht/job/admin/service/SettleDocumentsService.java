package com.htht.job.admin.service;

import com.htht.job.admin.core.jobbean.ParamClassifyBean;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/11/16.
 */
public interface SettleDocumentsService {
    public void parallelByFlowMap(ParamClassifyBean paramClassifyBean, List<CommonParameter> flowParams, List<ParallelLogDTO> parallelLogDTOList, Map methodMap);

    public void parallelByFlowMapPl(ParamClassifyBean paramClassifyBean, List<CommonParameter> flowParams, List<ParallelLogDTO> parallelLogDTOList, Map methodMap);

    public void setOutInputValue(Map methodMap, ParamClassifyBean paramClassifyBean, List<ParallelLogDTO> parallelLogDTOList);

    public void setFolder(List<CommonParameter> outFolder, List<CommonParameter> flowParams, Map methodMap);

    public void setFileName(String tempvalue, List<CommonParameter> outFile, List<CommonParameter> flowParams, Map methodMap);

    public void paramClassifyBean(ParamClassifyBean paramClassifyBean, List<CommonParameter> commonParameterList, Map map);

    public void addFolderParam(CommonParameter inFolderCommonParameter, ParamClassifyBean paramClassifyBean, ResultUtil resultUtil);

    public String outputDirectory(List<CommonParameter> taskParams);

    public void addFileParam(CommonParameter inFilecommonParameter, ParamClassifyBean paramClassifyBean, ResultUtil resultUtil);


}

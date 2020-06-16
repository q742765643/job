package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/10/30.
 */

import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.dao.XxlJobRegistryDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.admin.service.FindStepService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.processsteps.ProcessSteps;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-30 14:33
 **/
@Service
public class FindStepServiceImpl implements FindStepService {

    @Resource
    protected TaskParametersService taskParametersService;
    @Resource
    protected DubboService dubboService;

    @Resource
    private CheckAliveService checkAliveService;
    public List<String> getNextIds(String nextIdString) {
        String[] nextIds = nextIdString.split(",");
        List<String> nextIdlist = new ArrayList<>();
        for (int i = 0; i < nextIds.length; i++) {
            nextIdlist.add(nextIds[i]);
        }
        return nextIdlist;
    }
    public List<ProcessSteps> findNextFlowCeaselessly(List<String> nextIdlist, String modelId,ResultUtil<String> resultUtil) {
        ProcessSteps findVo = new ProcessSteps();
        findVo.setNextIds(nextIdlist);
        findVo.setFlowId(modelId);
        List<ProcessSteps> processSteps=dubboService.findFlowCeaselesslyList(findVo);
        if(null==processSteps||processSteps.isEmpty()){
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_304_ERROR);
            return null;
        }
        return processSteps;
    }
    public ProcessSteps findStartFlowCeaselessly(String modelId) {
        ProcessSteps findVo = new ProcessSteps();
        findVo.setDataId(FlowConstant.STARTFIGURE);
        findVo.setFlowId(modelId);
        List<ProcessSteps> processStepsList = dubboService.findStartOrEndFlowCeaselesslyList(findVo);
        return processStepsList.get(0);
    }

}


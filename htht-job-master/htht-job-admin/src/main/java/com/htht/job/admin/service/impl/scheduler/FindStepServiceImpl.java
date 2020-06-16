package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/10/30.
 */

import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.admin.service.FindStepService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
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

    @Override
    public List<String> getNextIds(String nextIdString) {
        String[] nextIds = nextIdString.split(",");
        List<String> nextIdlist = new ArrayList<>();
        for (int i = 0; i < nextIds.length; i++) {
            nextIdlist.add(nextIds[i]);
        }
        return nextIdlist;
    }

    @Override
    public List<ProcessStepsDTO> findNextFlowCeaselessly(List<String> nextIdlist, String modelId, ResultUtil<String> resultUtil) {
        ProcessStepsDTO findVo = new ProcessStepsDTO();
        findVo.setNextIds(nextIdlist);
        findVo.setFlowId(modelId);
        List<ProcessStepsDTO> processStepDTOS = dubboService.findFlowCeaselesslyList(findVo);
        if (null == processStepDTOS || processStepDTOS.isEmpty()) {
            resultUtil.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_304_ERROR);
            return new ArrayList<>();
        }
        return processStepDTOS;
    }

    @Override
    public ProcessStepsDTO findStartFlowCeaselessly(String modelId) {
        ProcessStepsDTO findVo = new ProcessStepsDTO();
        findVo.setDataId(FlowConstant.STARTFIGURE);
        findVo.setFlowId(modelId);
        List<ProcessStepsDTO> processStepsDTOList = dubboService.findStartOrEndFlowCeaselesslyList(findVo);
        return processStepsDTOList.get(0);
    }

}


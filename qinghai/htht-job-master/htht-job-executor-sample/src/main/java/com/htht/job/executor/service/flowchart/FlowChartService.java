package com.htht.job.executor.service.flowchart;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.executor.dao.flowchart.FlowChartDao;
import com.htht.job.executor.model.flowchart.FlowChartModel;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.processsteps.ProcessSteps;
import com.htht.job.executor.service.processsteps.ProcessStepsService;
import com.htht.job.executor.util.ProcessXmlParse;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @program: htht-job
 * @description: 流程任务逻辑层
 * @author: zzj
 * @create: 2018-03-25 11:13
 **/
@Transactional
@Service("flowChartService")
public class FlowChartService extends BaseService<FlowChartModel> {
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;
    @Autowired
    private ProcessXmlParse processXmlParse;
    @Autowired
    private FlowChartDao flowChartDao;
    @Autowired
    private ProcessStepsService processStepsService;

    public FlowChartModel saveOrUpdate(FlowChartModel flowChartModel){
        if(StringUtils.isEmpty(flowChartModel.getId())){
            flowChartModel.setCreateTime(new Date());
            flowChartModel = flowChartDao.save(flowChartModel);
            processStepsService.saveEachInput(flowChartModel.getId());
            return flowChartModel;

        }else{
            FlowChartModel yFlowChartModel =this.getById(flowChartModel.getId());
            yFlowChartModel.setUpdateTime(new Date());
            yFlowChartModel.setProcessFigure(flowChartModel.getProcessFigure());
            yFlowChartModel.setProcessPicture(flowChartModel.getProcessPicture());
            yFlowChartModel.setProcessCHName(flowChartModel.getProcessCHName());
            yFlowChartModel.setProcessDescribe(flowChartModel.getProcessDescribe());
            yFlowChartModel =flowChartDao.save(yFlowChartModel);
            processStepsService.deleteByParameterId(yFlowChartModel.getId());
            processStepsService.saveEachInput(yFlowChartModel.getId());

            return yFlowChartModel;
        }

    }

    @Override
    public BaseDao<FlowChartModel> getBaseDao() {
        return flowChartDao;

    }
    /**
    * @Description: 解析流程xml为list
    * @Param: [id]
    * @return: java.util.List<com.htht.job.vo.FlowXmlVo>
    * @Author: zzj
    * @Date: 2018/3/21
    */
    public List<CommonParameter> parseFlowXmlParameter(String id) {
        FlowChartModel flowChartModel = flowChartDao.findOne(id);
        String xmlStr = flowChartModel.getProcessFigure();
        List<List<CommonParameter>> commonParameters = new ArrayList<List<CommonParameter>>();
        Set<String> set = new HashSet<String>();
        List<String> cellIds = new ArrayList<>();
        /**========1.获取输入参数list===============**/
        processXmlParse.getReceive(xmlStr, commonParameters, set, cellIds);

        /**========2.获取参数对应的vlue=============**/
        Map map = new HashMap(10);
        for (String cellId : set) {
            if (!StringUtils.isEmpty(cellId)) {
                AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(cellId);
                if(null!=atomicAlgorithm) {
                    List<CommonParameter> parameterlist = JSON.parseArray(atomicAlgorithm.getDynamicParameter(), CommonParameter.class);
                    if ("jar".equals(atomicAlgorithm.getAlgoType())) {
                        List<CommonParameter> output = new ArrayList<CommonParameter>();
                        for (CommonParameter commonParameter : parameterlist) {
                            if ("outputstring".equals(commonParameter.getParameterType()) || "outfile".equals(commonParameter.getParameterType())) {
                                output.add(commonParameter);
                            }

                        }
                        parameterlist.removeAll(output);
                    }

                    map.put(cellId, parameterlist);
                }
            }
        }
        /**========3.合并=========================**/
       /* for(int i=0;i<commonParameters.size();i++){
            List<CommonParameter> parameterlistCellId = (List<CommonParameter>) map.get(commonParameters.get(i).getCellId());
            for(CommonParameter commonParameter:parameterlistCellId){
                if(commonParameters.get(i).getParameterName().equals(commonParameter.getParameterName())){
                    commonParameters.get(i).setValue(commonParameter.getValue());
                    commonParameters.get(i).setExpandedname(commonParameter.getExpandedname());
                    commonParameters.get(i).setUrl(commonParameter.getUrl());
                    break;
                }
            }
        }*/
        List<CommonParameter> input = new ArrayList<>();
        List<String> dataIds=new ArrayList<String>();
        boolean outflag = true;
        for (int i = 0; i < commonParameters.size(); i++) {
            String cellId = cellIds.get(i);
            List<CommonParameter> commonParametersFlow = commonParameters.get(i);
            List<CommonParameter> parameterlistCellId = (List<CommonParameter>) map.get(cellId);
            if(null==parameterlistCellId){
                ProcessSteps processSteps = processStepsService.findBySort(id,i+1);
                List<CommonParameter> inputFlow=JSON.parseArray(processSteps.getDynamicParameter(),CommonParameter.class);
                for (CommonParameter commonParameter : inputFlow) {
                    if("false".equals(commonParameter.getDisplay())){
                        continue;
                    }
                    if (outflag && commonParameter.getParameterType().equals("outfile")) {
                        input.add(commonParameter);
                        outflag = false;
                    }
                    if ("outputstring".equals(commonParameter.getParameterType())||
                            FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())) {
                        if (commonParameter.getDataID().indexOf("Reply_") >= 0) {
                                input.add(commonParameter);
                        }
                    }
                    if (!"outputstring".equals(commonParameter.getParameterType()) &&
                            !"outfile".equals(commonParameter.getParameterType())&&
                            !FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())) {
                        if(i==0){
                            input.add(commonParameter);
                            dataIds.add(commonParameter.getDataID());
                        }else {
                            if (commonParameter.getDataID().indexOf("Receive_") >= 0) {
                                if (dataIds.contains(commonParameter.getDataID())) {
                                    continue;
                                }
                                input.add(commonParameter);
                                dataIds.add(commonParameter.getDataID());
                            }
                        }
                    }
                }

            }else{
                for (int j = 0; j < parameterlistCellId.size(); j++) {
                    for (CommonParameter commonParameter : commonParametersFlow) {
                        if (parameterlistCellId.get(j).getParameterName().equals(commonParameter.getParameterName())) {
                            CommonParameter commonParameterNew = new CommonParameter();
                            commonParameterNew.setCellId(commonParameter.getCellId());
                            commonParameterNew.setDataID(commonParameter.getDataID());
                            commonParameterNew.setGroup(commonParameter.getGroup());
                            commonParameterNew.setParameterType(parameterlistCellId.get(j).getParameterType());
                            commonParameterNew.setParameterName(parameterlistCellId.get(j).getParameterName());
                            commonParameterNew.setParameterDesc(parameterlistCellId.get(j).getParameterDesc());
                            commonParameterNew.setValue(parameterlistCellId.get(j).getValue());
                            commonParameterNew.setUrl(parameterlistCellId.get(j).getUrl());
                            commonParameterNew.setExpandedname(parameterlistCellId.get(j).getExpandedname());
                            commonParameterNew.setIsNull(parameterlistCellId.get(j).getIsNull());
                            commonParameterNew.setDialogType(parameterlistCellId.get(j).getDialogType());
                            commonParameterNew.setDisplay(parameterlistCellId.get(j).getDisplay());
                            if("undefined".equals(commonParameter.getUuid())){
                                commonParameterNew.setUuid(commonParameter.getGroup()+parameterlistCellId.get(j).getParameterName());
                            }
                            else {
                                commonParameterNew.setUuid(commonParameter.getUuid());
                            }
                            if("false".equals(commonParameterNew.getDisplay())){
                                continue;
                            }
                            if (outflag && commonParameterNew.getParameterType().equals("outfile")) {
                                input.add(commonParameterNew);
                                outflag = false;
                            }
                            if ("outputstring".equals(commonParameterNew.getParameterType())||
                                    FlowConstant.OUTFOLDER.equals(commonParameterNew.getParameterType())) {
                                if (commonParameterNew.getDataID().indexOf("Reply_") >= 0) {
                                        input.add(commonParameterNew);
                                }
                            }
                            if (!"outputstring".equals(commonParameterNew.getParameterType()) &&
                                    !"outfile".equals(commonParameterNew.getParameterType())&&
                                    !FlowConstant.OUTFOLDER.equals(commonParameterNew.getParameterType())) {
                                if(i==0){
                                    input.add(commonParameterNew);
                                    dataIds.add(commonParameterNew.getDataID());
                                }else {
                                    if (commonParameterNew.getDataID().indexOf("Receive_") >= 0) {
                                        if (dataIds.contains(commonParameterNew.getDataID())) {
                                            continue;
                                        }
                                        input.add(commonParameterNew);
                                        dataIds.add(commonParameterNew.getDataID());
                                    }
                                }
                            }
                        }

                    }
                }
            }
            }


        return input;
    }
    /** 
    * @Description: 获取流程list
    * @Param: [] 
    * @return: java.util.List<com.htht.job.executor.model.flow.FlowModel> 
    * @Author: zzj
    * @Date: 2018/3/27 
    */ 
    public List<FlowChartModel>  findFlowList(){
        List<FlowChartModel> flowChartModels =this.getAll();
        return flowChartModels;
    }
    public FlowChartModel getFlowById(String id){
        return this.getById(id);
    }

    public Map<String, Object> pageListFlow(int start, int length,
                                            FlowChartModel flowChartModel) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        SimpleSpecificationBuilder<FlowChartModel> specification=new SimpleSpecificationBuilder();
        if(!StringUtils.isEmpty(flowChartModel.getProcessCHName())){
            specification.add("processCHName","likeAll", flowChartModel.getProcessCHName());
        }
        Page<FlowChartModel> page = this.getPage(specification.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements());
        maps.put("recordsFiltered", page.getTotalElements());
        maps.put("data", page.getContent());
        return maps;
    }

    public ReturnT<String> deleteFlow(String id) {
        try {
            this.delete(id);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnT.FAIL;
    }

}

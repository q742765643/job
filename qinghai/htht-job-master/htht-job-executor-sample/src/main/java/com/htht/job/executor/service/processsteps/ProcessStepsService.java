package com.htht.job.executor.service.processsteps;/**
 * Created by zzj on 2018/3/29.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.executor.dao.processsteps.ProcessStepsDao;
import com.htht.job.executor.model.flowchart.FlowChartModel;
import com.htht.job.executor.model.processsteps.ProcessSteps;
import com.htht.job.executor.model.mapping.Mapping;
import com.htht.job.executor.model.mapping.Select;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.service.flowchart.FlowChartService;
import com.htht.job.executor.util.ProcessConstant;
import com.htht.job.executor.util.ProcessXmlParse;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import com.htht.job.vo.LinkVo;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * @program: htht-job
 * @description: 流程步骤逻辑层
 * @author: zzj
 * @create: 2018-03-29 10:43
 **/
@Transactional
@Service("flowCeaselesslyService")
public class ProcessStepsService extends BaseService<ProcessSteps> {
    @Autowired
    private ProcessStepsDao processStepsDao;
    @Autowired
    private ProcessXmlParse processXmlParse;
    @Autowired
    private FlowChartService flowChartService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;
    @Override
    public BaseDao<ProcessSteps> getBaseDao() {
        return processStepsDao;
    }
    public void saveEachInput(String id){
        FlowChartModel flowChartModel =flowChartService.getById(id);
        List<LinkVo> linkVos=new ArrayList<LinkVo>();
        StringBuffer startFigureBuffer=new StringBuffer();
        List<ProcessSteps> processStepsList =processXmlParse.parseToList(flowChartModel.getProcessFigure(),startFigureBuffer);
        for(int i = 0; i< processStepsList.size(); i++){
            processStepsList.get(i).setSort(i);
            if(ProcessConstant.STRATFIGURE.equals(processStepsList.get(i).getDataId())){
                processStepsList.get(i).setCreateTime(new Date());
                processStepsList.get(i).setNextId(startFigureBuffer.toString());
                processStepsList.get(i).setFlowId(id);

                continue;
            }
            if ( ProcessConstant.ENDFIGURE.equals(processStepsList.get(i).getDataId())){
                processStepsList.get(i).setCreateTime(new Date());
                processStepsList.get(i).setFlowId(id);
                List<CommonParameter>  endParameter= JSON.parseArray(processStepsList.get(i).getDynamicParameter(), CommonParameter.class);
                for (CommonParameter commonParameter : endParameter) {
                    if("undefined".equals(commonParameter.getUuid())) {
                        commonParameter.setUuid(commonParameter.getGroup() + commonParameter.getParameterName());
                    }
                }
                processStepsList.get(i).setDynamicParameter(JSON.toJSONString(endParameter));

                continue;


            }
            if(!"true".equals(processStepsList.get(i).getIsProcess())) {
                List<CommonParameter> input = JSON.parseArray(processStepsList.get(i).getDynamicParameter(), CommonParameter.class);
                AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(processStepsList.get(i).getServiceId());
                List<CommonParameter> inputParameter = JSON.parseArray(atomicAlgorithm.getDynamicParameter(), CommonParameter.class);

                for (int j = 0; j < inputParameter.size(); j++) {
                    String parameterName = inputParameter.get(j).getParameterName();
                    for (CommonParameter commonParameter : input) {
                        if ((commonParameter.getParameterName()).equals(parameterName)) {
                            inputParameter.get(j).setDataID(commonParameter.getDataID());
                            if("undefined".equals(commonParameter.getUuid())){
                                inputParameter.get(j).setUuid(processStepsList.get(i).getLabel()+commonParameter.getParameterName());
                            }else{
                                inputParameter.get(j).setUuid(commonParameter.getUuid());

                            }
                        }
                    }
                }
                processStepsList.get(i).setDynamicParameter(JSON.toJSONString(inputParameter));

            }else{
                List<CommonParameter> input = JSON.parseArray(processStepsList.get(i).getDynamicParameter(), CommonParameter.class);
                List<CommonParameter> inputParameter=flowChartService.parseFlowXmlParameter(processStepsList.get(i).getServiceId());
                for (int j = 0; j < inputParameter.size(); j++) {
                    inputParameter.get(j).setDataID(input.get(j).getDataID());
                    inputParameter.get(j).setGroup(processStepsList.get(i).getLabel());
                }
                if(input.size()>inputParameter.size()) {
                    inputParameter.addAll(input.subList(inputParameter.size(), input.size() - 1));
                }
                processStepsList.get(i).setDynamicParameter(JSON.toJSONString(inputParameter));
            }
            if(startFigureBuffer.indexOf(processStepsList.get(i).getDataId())!=-1){
                processStepsList.get(i).setIsStart("0");
            }else{
                processStepsList.get(i).setIsStart("1");

            }
            processStepsList.get(i).setFlowId(id);
            processStepsList.get(i).setCreateTime(new Date());
        }

        this.save(processStepsList);
    }

    public  List<Mapping>  mapping(String id){
        FlowChartModel flowChartModel =flowChartService.getById(id);
        List<LinkVo> linkVos=new ArrayList<LinkVo>();
        List<Mapping> allMaping=new ArrayList<Mapping>();
        StringBuffer startFigureBuffer=new StringBuffer();
        List<ProcessSteps> processStepsList =processXmlParse.parseToList(flowChartModel.getProcessFigure(),startFigureBuffer);
        for(int i = 0; i< processStepsList.size(); i++) {
            if(!ProcessConstant.STRATFIGURE.equals(processStepsList.get(i).getDataId())
             &&!ProcessConstant.ENDFIGURE.equals(processStepsList.get(i).getDataId())){
                AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService.findParameterById(processStepsList.get(i).getServiceId());
                List<CommonParameter> commonParameterList= JSON.parseArray(atomicAlgorithm.getDynamicParameter(),CommonParameter.class);
                List<Select> selectList=new ArrayList<Select>();
                List<Mapping> mappings_infile=new ArrayList<Mapping>();
                List<Mapping> mappings_outfile=new ArrayList<Mapping>();
                List<CommonParameter> inputParameter= JSON.parseArray(processStepsList.get(i).getDynamicParameter(),CommonParameter.class);

                for(CommonParameter commonParameter:commonParameterList){
                    for(int j=0;j<inputParameter.size();j++){
                            String parameterName=inputParameter.get(j).getParameterName();
                            if((commonParameter.getParameterName()).equals(parameterName)){
                                commonParameter.setDataID(inputParameter.get(j).getDataID());
                            }
                    }
                    if(FlowConstant.INFILE.equals(commonParameter.getParameterType())){
                        Select select=new Select();
                        select.setId(commonParameter.getDataID());
                        select.setText(processStepsList.get(i).getLabel()+"_"+commonParameter.getParameterDesc());
                        selectList.add(select);
                        if(i==1){
                            Mapping mapping=new Mapping();
                            mapping.setSfdataId(commonParameter.getDataID());
                            mapping.setParameterName(commonParameter.getParameterName());
                            mapping.setParameterDesc(commonParameter.getParameterDesc());
                            mapping.setParameterType("select");
                            mapping.setDataId(processStepsList.get(i).getDataId());
                            mapping.setLabel(processStepsList.get(i).getLabel());
                            mapping.setIsOut(0);
                            mappings_infile.add(mapping);
                        }
                    }
                    if(FlowConstant.OUTFILE.equals(commonParameter.getParameterType())){
                        Mapping mapping=new Mapping();
                        mapping.setSfdataId(commonParameter.getDataID());
                        mapping.setParameterName(commonParameter.getParameterName());
                        mapping.setParameterDesc(commonParameter.getParameterDesc());
                        mapping.setParameterType("select");
                        mapping.setDataId(processStepsList.get(i).getDataId());
                        mapping.setLabel(processStepsList.get(i).getLabel());
                        mapping.setIsOut(1);
                        mappings_outfile.add(mapping);
                    }

                }
                if((selectList.size()>1&&mappings_outfile.size()>1)||mappings_infile.size()>1){
                    for(int j=0;j<mappings_outfile.size();j++){
                        mappings_outfile.get(j).setUrl(JSON.toJSONString(selectList));
                    }
                    if(mappings_infile.size()>1){
                        for(int j=0;j<mappings_infile.size();j++){
                            mappings_infile.get(j).setUrl(JSON.toJSONString(selectList));
                        }
                        mappings_infile.remove(0);
                        allMaping.addAll(mappings_infile);

                    }
                    if(mappings_outfile.size()>1) {
                        allMaping.addAll(mappings_outfile);
                    }

                }

            }
        }
        return allMaping;
    }

    public List<ProcessSteps> findFlowCeaselesslyList(ProcessSteps processSteps){
        SimpleSpecificationBuilder<ProcessSteps> specification=new SimpleSpecificationBuilder();
        Sort sort = new Sort(Sort.Direction.ASC, "sort");

        if(!StringUtils.isEmpty(processSteps.getIsStart())) {
            specification.add("isStart", "eq", processSteps.getIsStart());
        }
        if(!StringUtils.isEmpty(processSteps.getFlowId())) {
            specification.add("flowId", "eq", processSteps.getFlowId());
        }
        if(!StringUtils.isEmpty(processSteps.getDataId())) {
            specification.add("nextId", "likeAll", processSteps.getDataId());
        }
        if(null!= processSteps.getNextIds()) {
            specification.add("dataId", "in", processSteps.getNextIds());
        }
        List<ProcessSteps> list= processStepsDao.findAll(specification.generateSpecification(),sort);
        return list;

    }

    public List<ProcessSteps> findStartOrEndFlowCeaselesslyList(ProcessSteps processSteps){
        SimpleSpecificationBuilder<ProcessSteps> specification=new SimpleSpecificationBuilder();
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        if(!StringUtils.isEmpty(processSteps.getFlowId())) {
            specification.add("flowId", "eq", processSteps.getFlowId());
        }
        if(!StringUtils.isEmpty(processSteps.getDataId())) {
            specification.add("dataId", "eq", processSteps.getDataId());
        }

        List<ProcessSteps> list= processStepsDao.findAll(specification.generateSpecification(),sort);
        return list;

    }
    public ProcessSteps findBySort(String flowId,int sort){
        SimpleSpecificationBuilder<ProcessSteps> specification=new SimpleSpecificationBuilder();
        if(!StringUtils.isEmpty(flowId)) {
            specification.add("flowId", "eq", flowId);
        }
            specification.add("sort", "eq", sort);


        List<ProcessSteps> list= processStepsDao.findAll(specification.generateSpecification());
        return list.get(0);

    }
    public void deleteByParameterId(String flowId){
        processStepsDao.deleteByParameterId(flowId);
    }


}


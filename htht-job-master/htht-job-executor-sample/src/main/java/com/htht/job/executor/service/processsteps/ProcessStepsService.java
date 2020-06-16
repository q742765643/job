package com.htht.job.executor.service.processsteps;/**
 * Created by zzj on 2018/3/29.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.executor.dao.processsteps.ProcessStepsDao;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowchart.FlowChartDTO;
import com.htht.job.executor.model.mapping.Mapping;
import com.htht.job.executor.model.mapping.Select;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: htht-job
 * @description: 流程步骤逻辑层
 * @author: zzj
 * @create: 2018-03-29 10:43
 **/
@Transactional
@Service("flowCeaselesslyService")
public class ProcessStepsService extends BaseService<ProcessStepsDTO> {
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
    public BaseDao<ProcessStepsDTO> getBaseDao() {
        return processStepsDao;
    }

    public void saveEachInput(String id) {
        FlowChartDTO flowChartDTO = flowChartService.getById(id);
        List<LinkVo> linkVos = new ArrayList<LinkVo>();
        StringBuffer startFigureBuffer = new StringBuffer();
        List<ProcessStepsDTO> processStepsDTOList = processXmlParse.parseToList(flowChartDTO.getProcessFigure(), startFigureBuffer);
        for (int i = 0; i < processStepsDTOList.size(); i++) {
            processStepsDTOList.get(i).setSort(i);
            if (ProcessConstant.STRATFIGURE.equals(processStepsDTOList.get(i).getDataId())) {
                processStepsDTOList.get(i).setCreateTime(new Date());
                processStepsDTOList.get(i).setNextId(startFigureBuffer.toString());
                processStepsDTOList.get(i).setFlowId(id);

                continue;
            }
            if (ProcessConstant.ENDFIGURE.equals(processStepsDTOList.get(i).getDataId())) {
                processStepsDTOList.get(i).setCreateTime(new Date());
                processStepsDTOList.get(i).setFlowId(id);
                List<CommonParameter> endParameter = JSON.parseArray(processStepsDTOList.get(i).getDynamicParameter(), CommonParameter.class);
                for (CommonParameter commonParameter : endParameter) {
                    if ("undefined".equals(commonParameter.getUuid())) {
                        commonParameter.setUuid(commonParameter.getGroup() + commonParameter.getParameterName());
                    }
                }
                processStepsDTOList.get(i).setDynamicParameter(JSON.toJSONString(endParameter));

                continue;


            }
            if (!"true".equals(processStepsDTOList.get(i).getIsProcess())) {
                List<CommonParameter> input = JSON.parseArray(processStepsDTOList.get(i).getDynamicParameter(), CommonParameter.class);
                AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(processStepsDTOList.get(i).getServiceId());
                List<CommonParameter> inputParameter = JSON.parseArray(atomicAlgorithmDTO.getDynamicParameter(), CommonParameter.class);

                for (int j = 0; j < inputParameter.size(); j++) {
                    String parameterName = inputParameter.get(j).getParameterName();
                    for (CommonParameter commonParameter : input) {
                        if ((commonParameter.getParameterName()).equals(parameterName)) {
                            inputParameter.get(j).setDataID(commonParameter.getDataID());
                            if ("undefined".equals(commonParameter.getUuid())) {
                                inputParameter.get(j).setUuid(processStepsDTOList.get(i).getLabel() + commonParameter.getParameterName());
                            } else {
                                inputParameter.get(j).setUuid(commonParameter.getUuid());

                            }
                        }
                    }
                }
                processStepsDTOList.get(i).setDynamicParameter(JSON.toJSONString(inputParameter));

            } else {
                List<CommonParameter> input = JSON.parseArray(processStepsDTOList.get(i).getDynamicParameter(), CommonParameter.class);
                List<CommonParameter> inputParameter = flowChartService.parseFlowXmlParameter(processStepsDTOList.get(i).getServiceId());
                for (int j = 0; j < inputParameter.size(); j++) {
                    inputParameter.get(j).setDataID(input.get(j).getDataID());
                    inputParameter.get(j).setGroup(processStepsDTOList.get(i).getLabel());
                }
                if (input.size() > inputParameter.size()) {
                    inputParameter.addAll(input.subList(inputParameter.size(), input.size() - 1));
                }
                processStepsDTOList.get(i).setDynamicParameter(JSON.toJSONString(inputParameter));
            }
            if (startFigureBuffer.indexOf(processStepsDTOList.get(i).getDataId()) != -1) {
                processStepsDTOList.get(i).setIsStart("0");
            } else {
                processStepsDTOList.get(i).setIsStart("1");

            }
            processStepsDTOList.get(i).setFlowId(id);
            processStepsDTOList.get(i).setCreateTime(new Date());
        }

        this.save(processStepsDTOList);
    }

    public List<Mapping> mapping(String id) {
        FlowChartDTO flowChartDTO = flowChartService.getById(id);
        List<LinkVo> linkVos = new ArrayList<LinkVo>();
        List<Mapping> allMaping = new ArrayList<Mapping>();
        StringBuffer startFigureBuffer = new StringBuffer();
        List<ProcessStepsDTO> processStepsDTOList = processXmlParse.parseToList(flowChartDTO.getProcessFigure(), startFigureBuffer);
        for (int i = 0; i < processStepsDTOList.size(); i++) {
            if (!ProcessConstant.STRATFIGURE.equals(processStepsDTOList.get(i).getDataId())
                    && !ProcessConstant.ENDFIGURE.equals(processStepsDTOList.get(i).getDataId())) {
                AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(processStepsDTOList.get(i).getServiceId());
                List<CommonParameter> commonParameterList = JSON.parseArray(atomicAlgorithmDTO.getDynamicParameter(), CommonParameter.class);
                List<Select> selectList = new ArrayList<Select>();
                List<Mapping> mappings_infile = new ArrayList<Mapping>();
                List<Mapping> mappings_outfile = new ArrayList<Mapping>();
                List<CommonParameter> inputParameter = JSON.parseArray(processStepsDTOList.get(i).getDynamicParameter(), CommonParameter.class);

                for (CommonParameter commonParameter : commonParameterList) {
                    for (int j = 0; j < inputParameter.size(); j++) {
                        String parameterName = inputParameter.get(j).getParameterName();
                        if ((commonParameter.getParameterName()).equals(parameterName)) {
                            commonParameter.setDataID(inputParameter.get(j).getDataID());
                        }
                    }
                    if (FlowConstant.INFILE.equals(commonParameter.getParameterType())) {
                        Select select = new Select();
                        select.setId(commonParameter.getDataID());
                        select.setText(processStepsDTOList.get(i).getLabel() + "_" + commonParameter.getParameterDesc());
                        selectList.add(select);
                        if (i == 1) {
                            Mapping mapping = new Mapping();
                            mapping.setSfdataId(commonParameter.getDataID());
                            mapping.setParameterName(commonParameter.getParameterName());
                            mapping.setParameterDesc(commonParameter.getParameterDesc());
                            mapping.setParameterType("select");
                            mapping.setDataId(processStepsDTOList.get(i).getDataId());
                            mapping.setLabel(processStepsDTOList.get(i).getLabel());
                            mapping.setIsOut(0);
                            mappings_infile.add(mapping);
                        }
                    }
                    if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType())) {
                        Mapping mapping = new Mapping();
                        mapping.setSfdataId(commonParameter.getDataID());
                        mapping.setParameterName(commonParameter.getParameterName());
                        mapping.setParameterDesc(commonParameter.getParameterDesc());
                        mapping.setParameterType("select");
                        mapping.setDataId(processStepsDTOList.get(i).getDataId());
                        mapping.setLabel(processStepsDTOList.get(i).getLabel());
                        mapping.setIsOut(1);
                        mappings_outfile.add(mapping);
                    }

                }
                if ((selectList.size() > 1 && mappings_outfile.size() > 1) || mappings_infile.size() > 1) {
                    for (int j = 0; j < mappings_outfile.size(); j++) {
                        mappings_outfile.get(j).setUrl(JSON.toJSONString(selectList));
                    }
                    if (mappings_infile.size() > 1) {
                        for (int j = 0; j < mappings_infile.size(); j++) {
                            mappings_infile.get(j).setUrl(JSON.toJSONString(selectList));
                        }
                        mappings_infile.remove(0);
                        allMaping.addAll(mappings_infile);

                    }
                    if (mappings_outfile.size() > 1) {
                        allMaping.addAll(mappings_outfile);
                    }

                }

            }
        }
        return allMaping;
    }

    public List<ProcessStepsDTO> findFlowCeaselesslyList(ProcessStepsDTO processStepsDTO) {
        SimpleSpecificationBuilder<ProcessStepsDTO> specification = new SimpleSpecificationBuilder();
        Sort sort = new Sort(Sort.Direction.ASC, "sort");

        if (!StringUtils.isEmpty(processStepsDTO.getIsStart())) {
            specification.add("isStart", "eq", processStepsDTO.getIsStart());
        }
        if (!StringUtils.isEmpty(processStepsDTO.getFlowId())) {
            specification.add("flowId", "eq", processStepsDTO.getFlowId());
        }
        if (!StringUtils.isEmpty(processStepsDTO.getDataId())) {
            specification.add("nextId", "likeAll", processStepsDTO.getDataId());
        }
        if (null != processStepsDTO.getNextIds()) {
            specification.add("dataId", "in", processStepsDTO.getNextIds());
        }
        List<ProcessStepsDTO> list = processStepsDao.findAll(specification.generateSpecification(), sort);
        return list;

    }

    public List<ProcessStepsDTO> findStartOrEndFlowCeaselesslyList(ProcessStepsDTO processStepsDTO) {
        SimpleSpecificationBuilder<ProcessStepsDTO> specification = new SimpleSpecificationBuilder();
        Sort sort = new Sort(Sort.Direction.ASC, "sort");
        if (!StringUtils.isEmpty(processStepsDTO.getFlowId())) {
            specification.add("flowId", "eq", processStepsDTO.getFlowId());
        }
        if (!StringUtils.isEmpty(processStepsDTO.getDataId())) {
            specification.add("dataId", "eq", processStepsDTO.getDataId());
        }

        List<ProcessStepsDTO> list = processStepsDao.findAll(specification.generateSpecification(), sort);
        return list;

    }

    public ProcessStepsDTO findBySort(String flowId, int sort) {
        SimpleSpecificationBuilder<ProcessStepsDTO> specification = new SimpleSpecificationBuilder();
        if (!StringUtils.isEmpty(flowId)) {
            specification.add("flowId", "eq", flowId);
        }
        specification.add("sort", "eq", sort);


        List<ProcessStepsDTO> list = processStepsDao.findAll(specification.generateSpecification());
        return list.get(0);

    }

    public void deleteByParameterId(String flowId) {
        processStepsDao.deleteByParameterId(flowId);
    }


}


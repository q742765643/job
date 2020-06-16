package com.htht.job.executor.service.algorithm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.executor.dao.algorithm.AtomicAlgorithmDao;
import com.htht.job.executor.dao.algorithm.TaskParametersDao;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import com.htht.job.executor.util.ParameterConstant;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;

/**
 * @Description:
 * @Param:
 * @return:
 * @Author: zzj
 * @Date: 2018/3/26
 */
@Transactional
@Service("taskParametersService")
public class TaskParametersServiceImpl extends BaseService<TaskParametersDTO>
        implements TaskParametersService {
    @Autowired
    private TaskParametersDao taskParametersDao;
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;
    @Autowired
    private AtomicAlgorithmDao atomicAlgorithmDao;

    @Override
    public BaseDao<TaskParametersDTO> getBaseDao() {
        return taskParametersDao;
    }

    /**
     * @Description: 根据id获取任务传递参数
     * @Param: [id]
     * @return: com.htht.job.executor.model.parameter.JobParameterModel
     * @Author: zzj
     * @Date: 2018/3/26
     */
    @Override
    public TaskParametersDTO findJobParameterById(String id) {
        return  this.getById(id);
    }

    /**
     * @Description: 根据id获取模型参数
     * @Param: [id]
     * @return: com.htht.job.executor.model.parameter.ParameterModel
     * @Author: zzj
     * @Date: 2018/3/26
     */
    public AtomicAlgorithmDTO findParameterById(String id) {
        return  atomicAlgorithmService.findParameterById(id);
    }

    /**
     * @Description: 根据表示获取输出和输入参数
     * @Param: [jobId, parameterId, mark]
     * @return: java.util.Map
     * @Author: zzj
     * @Date: 2018/3/26
     */
    @Override
    public LinkedHashMap getJobParameter(String jobId, String parameterId,
                                         String mark) {
        List<CommonParameter> jobparameterlist = new ArrayList<>();
        TaskParametersDTO taskParametersDTO = this.findJobParameterById(jobId);

        if (null != taskParametersDTO) {
            if (!ParameterConstant.EMPTY.equals(taskParametersDTO
                    .getFixedParameter())
                    && !StringUtils.isEmpty(taskParametersDTO.getFixedParameter())
                    && ParameterConstant.MARK1.equals(mark)) {
                jobparameterlist = JSON.parseArray(
                        taskParametersDTO.getFixedParameter(),
                        CommonParameter.class);
            }

            if (!ParameterConstant.EMPTY.equals(taskParametersDTO
                    .getDynamicParameter())
                    && !StringUtils.isEmpty(taskParametersDTO
                    .getDynamicParameter())
                    && ParameterConstant.MARK2.equals(mark)) {
                jobparameterlist = JSON.parseArray(
                        taskParametersDTO.getDynamicParameter(),
                        CommonParameter.class);
            }
        }

        List<CommonParameter> parameterlist = new ArrayList<>();
        AtomicAlgorithmDTO atomicAlgorithmDTO = this.findParameterById(parameterId);

        if (null != atomicAlgorithmDTO) {
            if (!ParameterConstant.EMPTY.equals(atomicAlgorithmDTO
                    .getFixedParameter())
                    && !StringUtils
                    .isEmpty(atomicAlgorithmDTO.getFixedParameter())
                    && ParameterConstant.MARK1.equals(mark)) {
                parameterlist = JSON.parseArray(
                        atomicAlgorithmDTO.getFixedParameter(),
                        CommonParameter.class);
            }

            if (!ParameterConstant.EMPTY.equals(atomicAlgorithmDTO
                    .getDynamicParameter())
                    && !StringUtils.isEmpty(atomicAlgorithmDTO
                    .getDynamicParameter())
                    && ParameterConstant.MARK2.equals(mark)) {
                parameterlist = JSON.parseArray(
                        atomicAlgorithmDTO.getDynamicParameter(),
                        CommonParameter.class);
            }
        }

        List<CommonParameter> list = new ArrayList<>();

        for (int i = 0; i < parameterlist.size(); i++) {
            String parameterName = parameterlist.get(i).getParameterName();
            for (int j = 0; j < jobparameterlist.size(); j++) {
                if (jobparameterlist.get(j).getParameterName()
                        .equals(parameterName)
                        && !"parameterType".equals(parameterName)
                        && !"url".equals(parameterName)) {
                    parameterlist.get(i).setValue(
                            jobparameterlist.get(j).getValue());
                }
            }
        }

        list.addAll(parameterlist);

        LinkedHashMap map = new LinkedHashMap();

        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getParameterName(), list.get(i).getValue());
        }

        return map;
    }

    /**
     * @Description: 保存参数
     * @Param: [jobParameterModel]
     * @return: com.htht.job.executor.model.parameter.JobParameterModel
     * @Author: zzj
     * @Date: 2018/3/26
     */
    @Override
    public TaskParametersDTO saveJobParameter(TaskParametersDTO taskParametersDTO) {
        return this.save(taskParametersDTO);
    }

    /**
     * @Description: 获取任务参数
     * @Param: [jobId, parameterId, mark]
     * @return: java.lang.String
     * @Author: zzj
     * @Date: 2018/3/26
     */
    @Override
    public String getJobParameterMap(String jobId, String parameterId,
                                     String mark) {
        List<CommonParameter> jobparameterlist = new ArrayList<>();
        TaskParametersDTO taskParametersDTO = this.findJobParameterById(jobId);
        Map map = new HashMap(20);

        if (null != taskParametersDTO) {
            if (!ParameterConstant.EMPTY.equals(taskParametersDTO
                    .getFixedParameter())
                    && !StringUtils.isEmpty(taskParametersDTO.getFixedParameter())
                    && ParameterConstant.MARK1.equals(mark)) {
                jobparameterlist = JSON.parseArray(
                        taskParametersDTO.getFixedParameter(),
                        CommonParameter.class);
            }

            if (!ParameterConstant.EMPTY.equals(taskParametersDTO
                    .getDynamicParameter())
                    && !StringUtils.isEmpty(taskParametersDTO
                    .getDynamicParameter())
                    && ParameterConstant.MARK2.equals(mark)) {
                jobparameterlist = JSON.parseArray(
                        taskParametersDTO.getDynamicParameter(),
                        CommonParameter.class);
            }
        }

        List<CommonParameter> parameterlist = new ArrayList<>();
        AtomicAlgorithmDTO atomicAlgorithmDTO = this.findParameterById(parameterId);

        // mark为1标示输入参数为模版传入type
        if (ParameterConstant.MARK1.equals(mark)) {
            map.put("parameterType", atomicAlgorithmDTO.getType());
            map.put("url", atomicAlgorithmDTO.getUrl());
        } else {
            map.put("parameterType", "0");
        }

        if (null != atomicAlgorithmDTO) {
            if (!ParameterConstant.EMPTY.equals(atomicAlgorithmDTO
                    .getFixedParameter())
                    && !StringUtils
                    .isEmpty(atomicAlgorithmDTO.getFixedParameter())
                    && ParameterConstant.MARK1.equals(mark)) {
                parameterlist = JSON.parseArray(
                        atomicAlgorithmDTO.getFixedParameter(),
                        CommonParameter.class);
            }

            if (!ParameterConstant.EMPTY.equals(atomicAlgorithmDTO
                    .getDynamicParameter())
                    && !StringUtils.isEmpty(atomicAlgorithmDTO
                    .getDynamicParameter())
                    && ParameterConstant.MARK2.equals(mark)) {
                parameterlist = JSON.parseArray(
                        atomicAlgorithmDTO.getDynamicParameter(),
                        CommonParameter.class);
            }

        }

        List<CommonParameter> list = new ArrayList<>();

        for (int i = 0; i < parameterlist.size(); i++) {
            String parameterName = parameterlist.get(i).getParameterName();
            for (int j = 0; j < jobparameterlist.size(); j++) {
                if (jobparameterlist.get(j).getParameterName()
                        .equals(parameterName)
                        && !"parameterType".equals(parameterName)
                        && !"url".equals(parameterName)) {
                    parameterlist.get(i).setValue(
                            jobparameterlist.get(j).getValue());
                    parameterlist.get(i).setExpandedname(
                            jobparameterlist.get(j).getExpandedname());
                }
            }
        }

        list.addAll(parameterlist);
        map.put("list", list);

        return JSON.toJSONString(map);
    }

    @Override
    public String getLogDynamic(Map dymap, String modelId) {
        AtomicAlgorithmDTO model = this.findParameterById(modelId);
        List<CommonParameter> parseArray = JSON.parseArray(
                model.getDynamicParameter(), CommonParameter.class);
        for (int i = 0; i < parseArray.size(); i++) {
            Iterator iter = dymap.entrySet().iterator(); // 获得map的Iterator
            while (iter.hasNext()) {
                Entry entry = (Entry) iter.next();
                if (parseArray.get(i).getParameterName().equals(entry.getKey())) {
                    parseArray.get(i).setValue((String) (entry.getValue()));
                }
            }
        }
        return JSON.toJSONString(parseArray);
    }

    @Override
    public String formatJobModelParameters(String modelParameters) {

        JSONObject obj = JSON.parseObject(modelParameters);
        List<CommonParameter> parseArray = new ArrayList<>();
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            CommonParameter cp = new CommonParameter();
            cp.setParameterDesc(entry.getKey());
            cp.setParameterName(entry.getKey());
            cp.setParameterType("string");
            if (StringUtils.isEmpty(entry.getValue().toString())) {
                continue;
            }
            cp.setValue(entry.getValue().toString());
            parseArray.add(cp);
        }

        return JSON.toJSONString(parseArray);
    }
}

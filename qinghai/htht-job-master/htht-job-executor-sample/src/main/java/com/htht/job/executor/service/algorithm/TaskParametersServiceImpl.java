package com.htht.job.executor.service.algorithm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.algorithm.AtomicAlgorithmDao;
import com.htht.job.executor.dao.algorithm.TaskParametersDao;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParameters;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
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
public class TaskParametersServiceImpl extends BaseService<TaskParameters>
		implements TaskParametersService {
	@Autowired
	private TaskParametersDao taskParametersDao;
	@Autowired
	private AtomicAlgorithmService atomicAlgorithmService;
	@Autowired
	private AtomicAlgorithmDao atomicAlgorithmDao;

	@Override
	public BaseDao<TaskParameters> getBaseDao() {
		// TODO Auto-generated method stub
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
	public TaskParameters findJobParameterById(String id) {
		TaskParameters taskParameters = this.getById(id);

		return taskParameters;
	}

	/**
	 * @Description: 根据id获取模型参数
	 * @Param: [id]
	 * @return: com.htht.job.executor.model.parameter.ParameterModel
	 * @Author: zzj
	 * @Date: 2018/3/26
	 */
	public AtomicAlgorithm findParameterById(String id) {
		AtomicAlgorithm atomicAlgorithm = atomicAlgorithmService
				.findParameterById(id);

		return atomicAlgorithm;
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
		List<CommonParameter> jobparameterlist = new ArrayList<CommonParameter>();
		TaskParameters taskParameters = this.findJobParameterById(jobId);

		if (null != taskParameters) {
			if (!ParameterConstant.EMPTY.equals(taskParameters
					.getFixedParameter())
					&& !StringUtils.isEmpty(taskParameters.getFixedParameter())
					&& ParameterConstant.MARK1.equals(mark)) {
				jobparameterlist = JSON.parseArray(
						taskParameters.getFixedParameter(),
						CommonParameter.class);
			}

			if (!ParameterConstant.EMPTY.equals(taskParameters
					.getDynamicParameter())
					&& !StringUtils.isEmpty(taskParameters
							.getDynamicParameter())
					&& ParameterConstant.MARK2.equals(mark)) {
				jobparameterlist = JSON.parseArray(
						taskParameters.getDynamicParameter(),
						CommonParameter.class);
			}
		}

		List<CommonParameter> parameterlist = new ArrayList<CommonParameter>();
		AtomicAlgorithm atomicAlgorithm = this.findParameterById(parameterId);

		if (null != atomicAlgorithm) {
			if (!ParameterConstant.EMPTY.equals(atomicAlgorithm
					.getFixedParameter())
					&& !StringUtils
							.isEmpty(atomicAlgorithm.getFixedParameter())
					&& ParameterConstant.MARK1.equals(mark)) {
				parameterlist = JSON.parseArray(
						atomicAlgorithm.getFixedParameter(),
						CommonParameter.class);
			}

			if (!ParameterConstant.EMPTY.equals(atomicAlgorithm
					.getDynamicParameter())
					&& !StringUtils.isEmpty(atomicAlgorithm
							.getDynamicParameter())
					&& ParameterConstant.MARK2.equals(mark)) {
				parameterlist = JSON.parseArray(
						atomicAlgorithm.getDynamicParameter(),
						CommonParameter.class);
			}
		}

		List<CommonParameter> list = new ArrayList<CommonParameter>();

		for (int i = 0; i < parameterlist.size(); i++) {
			String parameterName = parameterlist.get(i).getParameterName();
			boolean flag = true;

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
	public TaskParameters saveJobParameter(TaskParameters taskParameters) {
		return this.save(taskParameters);
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
		List<CommonParameter> jobparameterlist = new ArrayList<CommonParameter>();
		TaskParameters taskParameters = this.findJobParameterById(jobId);
		Map map = new HashMap(20);

		if (null != taskParameters) {
			if (!ParameterConstant.EMPTY.equals(taskParameters
					.getFixedParameter())
					&& !StringUtils.isEmpty(taskParameters.getFixedParameter())
					&& ParameterConstant.MARK1.equals(mark)) {
				jobparameterlist = JSON.parseArray(
						taskParameters.getFixedParameter(),
						CommonParameter.class);
			}

			if (!ParameterConstant.EMPTY.equals(taskParameters
					.getDynamicParameter())
					&& !StringUtils.isEmpty(taskParameters
							.getDynamicParameter())
					&& ParameterConstant.MARK2.equals(mark)) {
				jobparameterlist = JSON.parseArray(
						taskParameters.getDynamicParameter(),
						CommonParameter.class);
			}
		}

		List<CommonParameter> parameterlist = new ArrayList<CommonParameter>();
		AtomicAlgorithm atomicAlgorithm = this.findParameterById(parameterId);

		// mark为1标示输入参数为模版传入type
		if (ParameterConstant.MARK1.equals(mark)) {
			map.put("parameterType", atomicAlgorithm.getType());
			map.put("url", atomicAlgorithm.getUrl());
		} else {
			map.put("parameterType", "0");
		}

		if (null != atomicAlgorithm) {
			if (!ParameterConstant.EMPTY.equals(atomicAlgorithm
					.getFixedParameter())
					&& !StringUtils
							.isEmpty(atomicAlgorithm.getFixedParameter())
					&& ParameterConstant.MARK1.equals(mark)) {
				parameterlist = JSON.parseArray(
						atomicAlgorithm.getFixedParameter(),
						CommonParameter.class);
			}

			if (!ParameterConstant.EMPTY.equals(atomicAlgorithm
					.getDynamicParameter())
					&& !StringUtils.isEmpty(atomicAlgorithm
							.getDynamicParameter())
					&& ParameterConstant.MARK2.equals(mark)) {
				parameterlist = JSON.parseArray(
						atomicAlgorithm.getDynamicParameter(),
						CommonParameter.class);
			}

		}

		List<CommonParameter> list = new ArrayList<CommonParameter>();

		for (int i = 0; i < parameterlist.size(); i++) {
			String parameterName = parameterlist.get(i).getParameterName();
			boolean flag = true;

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
		AtomicAlgorithm model = this.findParameterById(modelId);
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
		String jsonString = JSON.toJSONString(parseArray);
		return jsonString;
	}

	@Override
	public String formatJobModelParameters(String modelParameters) {
		
		JSONObject obj=JSON.parseObject(modelParameters);
		List<CommonParameter> parseArray = new ArrayList<CommonParameter>();
		for (Map.Entry<String, Object> entry : obj.entrySet()) {
			CommonParameter cp = new CommonParameter();
			cp.setParameterDesc(entry.getKey());
			cp.setParameterName(entry.getKey());
			cp.setParameterType("string");
			if(StringUtils.isEmpty(entry.getValue().toString())){
				continue;
			}
			cp.setValue(entry.getValue().toString());
			parseArray.add(cp);
		}

		return JSON.toJSONString(parseArray);
	}
}

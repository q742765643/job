package com.htht.job.admin.controller.jobflow;/**
 * Created by zzj on 2018/3/26.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobGroup;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.service.FlowService;
import com.htht.job.admin.service.XxlJobService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.flowchart.FlowChartDTO;
import com.htht.job.executor.model.mapping.Mapping;
import com.htht.job.executor.model.mapping.MatchRelation;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: htht-job
 * @description: 流程任务控制层
 * @author: zzj
 * @create: 2018-03-26 13:46
 **/
@Controller
@RequestMapping("/jobflow")
public class JobFlowController {
    @Resource
    private XxlJobService xxlJobService;

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @Resource
    private DubboService dubboService;

    @Resource
    private FlowService flowService;

    @Resource
    private XxlJobInfoDao xxlJobInfoDao;

    @Resource
    private TaskParametersService taskParametersService;

    /**
     * @Description: 流程任务index
     * @Param: []
     * @return: java.lang.String
     * @Author: zzj
     * @Date: 2018/3/26
     */
    @RequestMapping
    public String index(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);

        /*任务类型*/
        model.addAttribute("tasktype", "5");
        return "jobflow/jobflow.index";
    }

    /**
     * @Description: 流程任务添加
     * @Param: [model]
     * @return: java.lang.String
     * @Author: zzj
     * @Date: 2018/3/26
     */
    @RequestMapping("/add")
    public String add(Model model, String tasktype) {
        // 路由策略-列表
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());
        // Glue类型-字典
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());
        // 阻塞处理策略-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());
        model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());
        List<FlowChartDTO> flowChartDTOList = dubboService.findFlowList();
        model.addAttribute("flowModelList", flowChartDTOList);

        //执行策略下拉框
        List<DictCodeDTO> executionStrategyList = dubboService.findChildrenDictCode("执行策略");
        // List<DictCode> executionStrategyList= dubboService.findALlExecutionStrategy();
        model.addAttribute("executionStrategyList", executionStrategyList);

        //添加的流程类型tasktype 5：代表流程任务 6：代表产品生产流程任务  7：代表气象卫星预处理流程  8：代表高分预处理流程
        if (StringUtils.isEmpty(tasktype)) {
            model.addAttribute("tasktype", "5");
        } else {
            model.addAttribute("tasktype", tasktype);
        }

        return "jobflow/jobflowAdd";
    }

    /**
     * @Description: 获取流程所需输入参数
     * @Param: [id]
     * @return: java.lang.String
     * @Author: zzj
     * @Date: 2018/3/27
     */
    @RequestMapping("/getFlowParameter")
    @ResponseBody
    public String getFlowParameter(String id) {
        List<CommonParameter> list = dubboService.parseFlowXmlParameter(id);
        return JSON.toJSONString(list);
    }

    @RequestMapping("/addSave")
    @ResponseBody
    public ResultUtil<String> addSave(XxlJobInfo jobInfo) {
        ResultUtil<String> resultUtil = flowService.add(jobInfo);
        return resultUtil;
    }

    @RequestMapping("/update")
    public String update(Model model, int id) {
        // 路由策略-列表
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());
        // Glue类型-字典
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());
        // 阻塞处理策略-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());
        model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());
        List<FlowChartDTO> flowChartDTOList = dubboService.findFlowList();
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(id);

        model.addAttribute("flowModelList", flowChartDTOList);
        model.addAttribute("jobInfo", jobInfo);

        //执行策略下拉框
        List<DictCodeDTO> executionStrategyList = dubboService.findChildrenDictCode("执行策略");
        //List<DictCode> executionStrategyList= dubboService.findALlExecutionStrategy();
        model.addAttribute("executionStrategyList", executionStrategyList);

        return "jobflow/jobflowUpdate";
    }

    @RequestMapping("/getUpdateParameters")
    @ResponseBody
    public String getUpdateParameters(String id, String modelId) {
        TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(id);
        List<CommonParameter> commonParameters = JSON.parseArray(taskParametersDTO.getDynamicParameter(), CommonParameter.class);
        List<CommonParameter> list = dubboService.parseFlowXmlParameter(modelId);
        for (int i = 0; i < list.size(); i++) {
            for (CommonParameter commonParameter : commonParameters) {
                if (list.get(i).getDataID().equals(commonParameter.getDataID())) {
                    list.get(i).setValue(commonParameter.getValue());
                    list.get(i).setExpandedname(commonParameter.getExpandedname());
                }

            }
        }

        return JSON.toJSONString(list);
    }

    @RequestMapping("/updateSave")
    @ResponseBody
    public ResultUtil<String> updateSave(XxlJobInfo jobInfo) {
        ResultUtil<String> resultUtil = flowService.updateSave(jobInfo);
        return resultUtil;
    }

    @RequestMapping("/mapping")
    @ResponseBody
    public String mapping(String id) {
        List<Mapping> mappings = dubboService.mapping(id);
        return JSON.toJSONString(mappings);
    }

    @RequestMapping("/saveMatchRelation")
    @ResponseBody
    public ResultUtil<String> saveMatchRelation(String mappingJson, int jobId) {
        ResultUtil<String> result = new ResultUtil<String>();
        List<Mapping> mappings = JSON.parseArray(mappingJson, Mapping.class);
        Map<String, List<Mapping>> resultMap = new HashMap<String, List<Mapping>>();
        for (int i = 0; i < mappings.size(); i++) {
            Mapping mapping = mappings.get(i);
            if (resultMap.containsKey(mapping.getDataId())) {
                resultMap.get(mapping.getDataId()).add(mapping);
            } else {
                List<Mapping> list = new ArrayList<Mapping>();
                list.add(mapping);
                resultMap.put(mapping.getDataId(), list);
            }
        }
        List<MatchRelation> matchRelations = new ArrayList<MatchRelation>();
        for (Map.Entry<String, List<Mapping>> entry : resultMap.entrySet()) {
            MatchRelation matchRelation = new MatchRelation();
            matchRelation.setDataId(entry.getKey());
            matchRelation.setCreateTime(new Date());
            matchRelation.setJobId(jobId);
            matchRelation.setMatchData(JSON.toJSONString(entry.getValue()));
            matchRelations.add(matchRelation);
        }
        dubboService.saveMatchRelation(matchRelations);
        return result;
    }
}


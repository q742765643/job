package com.htht.job.admin.controller;

import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobGroup;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.product.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: htht-job-api
 * @description: 数据预处理管理
 * @author: dingjiancheng
 * @create: 2018-08-27 15:49
 */
@Controller
@RequestMapping("/preData")
public class PreDataController {
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private AtomicAlgorithmService atomicAlgorithmService;
    @Resource
    private DubboService dubboService;

    @RequestMapping("/weatherPreData")
    public String cimissDataCollect(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());    // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());    // 阻塞处理策略-字典
        model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());        // 失败处理策略-字典

        /**============获取模型下拉框============**/
        List<AtomicAlgorithm> executorHandlerlist = atomicAlgorithmService.findListParameter();
        List<Product> productList = dubboService.findALlProduct();
        model.addAttribute("executorHandlerlist", executorHandlerlist);
        model.addAttribute("productList", productList);

        //模态下拉框执行策略
        List<DictCode> executionStrategyList= dubboService.findChildrenDictCode("执行策略");
        //  List<DictCode> executionStrategyList= dubboService.findALlExecutionStrategy();
        model.addAttribute("executionStrategyList", executionStrategyList);
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);
        model.addAttribute("tasktype", 6);
        return "predata/weatherpredata.index";
    }

    @RequestMapping("/gfPreData")
    public String gfPreData(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);

        /*任务类型*/
        model.addAttribute("tasktype", 7);
        return "predata/gfpredata.index";
    }
    
    //数管调度
    @RequestMapping("/dataJobInfo")
    public String dataJobInfo(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());    // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());    // 阻塞处理策略-字典
        model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());        // 失败处理策略-字典

        /**============获取模型下拉框============**/
        List<AtomicAlgorithm> executorHandlerlist = atomicAlgorithmService.findListParameter();
        List<Product> productList = dubboService.findALlProduct();
        model.addAttribute("executorHandlerlist", executorHandlerlist);
        model.addAttribute("productList", productList);

        //模态下拉框执行策略
        List<DictCode> executionStrategyList= dubboService.findChildrenDictCode("执行策略");
        //  List<DictCode> executionStrategyList= dubboService.findALlExecutionStrategy();
        model.addAttribute("executionStrategyList", executionStrategyList);
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);
        model.addAttribute("tasktype", 8);
        return "dataOperation/dataJobInfo.index";
    }
    //数管流程
    @RequestMapping("/dataJobFlow")
    public String dataJobFlow(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);

        /*任务类型*/
        model.addAttribute("tasktype", 9);
        return "dataOperation/dataJobFlow.index";
    }
}

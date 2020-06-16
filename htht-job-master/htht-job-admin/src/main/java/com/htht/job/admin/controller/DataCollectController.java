package com.htht.job.admin.controller;

import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobGroup;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.downupload.CimissDataInfoDTO;
import com.htht.job.executor.model.paramtemplate.CimissDownParam;
import com.htht.job.executor.model.product.ProductDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: htht-job-api
 * @description: 数据汇集
 * @author: dingjiancheng
 * @create: 2018-08-27 15:49
 */
@Controller
@RequestMapping("/dataCollect")
public class DataCollectController {
    @Resource
    private AtomicAlgorithmService atomicAlgorithmService;
    @Resource
    private DubboService dubboService;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @RequestMapping("/remotedatacollect")
    public String remotedataCollect(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());    // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());    // 阻塞处理策略-字典
        model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());        // 失败处理策略-字典

        /**============获取模型下拉框============**/
        List<AtomicAlgorithmDTO> executorHandlerlist = atomicAlgorithmService.findListParameter();
        List<ProductDTO> productDTOList = dubboService.findALlProduct();
        model.addAttribute("executorHandlerlist", executorHandlerlist);
        model.addAttribute("productList", productDTOList);

        //模态下拉框执行策略
        List<DictCodeDTO> executionStrategyList = dubboService.findChildrenDictCode("执行策略");
        model.addAttribute("executionStrategyList", executionStrategyList);
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);
        model.addAttribute("tasktype", 2);

        return "datacollerct/remotedatacollect.index";
    }

    @RequestMapping("/cimissdatacollect")
    public String cimissDataCollect(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());    // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());    // 阻塞处理策略-字典
        model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());        // 失败处理策略-字典
        /**============获取模型下拉框============**/
        List<AtomicAlgorithmDTO> executorHandlerlist = atomicAlgorithmService.findListParameter();
        List<ProductDTO> productDTOList = dubboService.findALlProduct();
        model.addAttribute("executorHandlerlist", executorHandlerlist);
        model.addAttribute("productList", productDTOList);

        //模态下拉框执行策略
        List<DictCodeDTO> executionStrategyList = dubboService.findChildrenDictCode("执行策略");
        model.addAttribute("executionStrategyList", executionStrategyList);
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);
        model.addAttribute("model", new CimissDownParam());
        model.addAttribute("tasktype", 3);
        return "datacollerct/cimissdatacollect.index";
    }

    @RequestMapping("/cimissQuery")
    @ResponseBody
    public List<CimissDataInfoDTO> cimissDataCollect(@RequestParam(required = true, defaultValue = "SATE") String dataType) {
        return dubboService.getCimissData(dataType);
    }
}

package com.htht.job.admin.controller;

import com.htht.job.admin.controllerLog.SystemControllerLog;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobGroup;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.admin.service.XxlJobService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfo;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobService xxlJobService;
    @Resource
    private AtomicAlgorithmService atomicAlgorithmService;
    @Resource
    private DubboService dubboService;
    @Resource
    private TaskParametersService taskParametersService;

    @RequestMapping("/{tasktype}")
    public String index(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup, @PathVariable("tasktype") String tasktype) {

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
        model.addAttribute("tasktype", tasktype);
        return "jobinfo/jobinfo.index";
    }

    @SystemControllerLog(description = "查看产品列表", type = SystemLog.OPERATELOG)
    @RequestMapping("/product")
    public String remotedataCollect(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

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
        model.addAttribute("tasktype", 4);

        return "jobinfo/product.index";
    }

    @RequestMapping(value = "getAtomicAlgorithms")
    @ResponseBody
    public List<AtomicAlgorithm> getAtomicAlgorithms(HttpServletRequest request, HttpServletResponse response, String productId) {
        List<AlgorithmRelationInfo>  algoId = dubboService.queryAlgo(productId);
        List<AtomicAlgorithm>  list = new ArrayList();
        for (AlgorithmRelationInfo algoid : algoId){
            list.add(atomicAlgorithmService.queryAogoInfo(algoid.getAlgoId()));
        }
        return list;
    }

    @RequestMapping("/pageList/{tasktype}")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        @RequestParam(required = false, defaultValue = "-1") int jobGroup, String executorHandler, String filterTime, @PathVariable("tasktype") String tasktype) {

        return xxlJobService.pageList(start, length, jobGroup, executorHandler, filterTime, tasktype);
    }

    @SystemControllerLog(description = "新建了一个任务", type = SystemLog.OPERATELOG)
    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(XxlJobInfo jobInfo) {
        return xxlJobService.add(jobInfo);
    }

    @SystemControllerLog(description = "复制了一个任务", type = SystemLog.OPERATELOG)
    @RequestMapping("/copy")
    @ResponseBody
    public ReturnT<String> copy(XxlJobInfo jobInfo) {
        return xxlJobService.copyJob(jobInfo);
    }
    //修改
    @SystemControllerLog(description = "修改了一个任务", type = SystemLog.OPERATELOG)
    @RequestMapping("/reschedule")
    @ResponseBody
    public ReturnT<String> reschedule(XxlJobInfo jobInfo) {
        return xxlJobService.reschedule(jobInfo);
    }

    @SystemControllerLog(description = "删除了一个任务", type = SystemLog.OPERATELOG)
    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {
        return xxlJobService.remove(id);
    }

    @SystemControllerLog(description = "暂停了一个任务", type = SystemLog.OPERATELOG)
    @RequestMapping("/pause")
    @ResponseBody
    public ReturnT<String> pause(int id) {
        return xxlJobService.pause(id);
    }

    @SystemControllerLog(description = "恢复了一个任务", type = SystemLog.OPERATELOG)
    @RequestMapping("/resume")
    @ResponseBody
    public ReturnT<String> resume(int id) {
        return xxlJobService.resume(id);
    }

    @SystemControllerLog(description = "启动了一次任务", type = SystemLog.OPERATELOG)
    @RequestMapping("/trigger")
    @ResponseBody
    public ReturnT<String> triggerJob(int id) {
        return xxlJobService.triggerJob(id);
    }

    @RequestMapping("/getJobParameter")
    @ResponseBody
    public String getJobParameter(String jobId, String parameterId, String mark) {
        String data = taskParametersService.getJobParameterMap(jobId, parameterId, mark);
        return data;
    }

}

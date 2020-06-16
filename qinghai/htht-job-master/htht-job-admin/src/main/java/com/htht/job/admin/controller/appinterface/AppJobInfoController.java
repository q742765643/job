package com.htht.job.admin.controller.appinterface;

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobGroup;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.app.AppXxlJobInfo;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.service.AppJobInfoService;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.admin.service.XxlJobService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.TaskParameters;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.product.Product;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/3/14.
 */
@RestController
@RequestMapping("/api/jobinfo")
@Api(value = "/api/jobinfo", description = "任务列表相关接口")
public class AppJobInfoController {

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
    @Resource
    private AppJobInfoService appJobInfoService;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Autowired
    @Qualifier("flowSchedulerService")
    private SchedulerService flowSchedulerService;
    @Autowired
    @Qualifier("singleSchedulerService")
    private SchedulerService singleSchedulerService;

    @RequestMapping(value = "/{taskType}", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "根据任务类型获取添加任务下拉框值", notes = "获取添加任务下拉框值", httpMethod = "POST")
    public ResultUtil<Map> index(@ApiParam(name = "jobGroup", required = false, value = "任务组") @RequestParam(required = false, defaultValue = "-1") int jobGroup,
                                 @ApiParam(name = "taskType", required = true, value = "任务类型") @PathVariable String taskType) {
        ResultUtil<Map> result = new ResultUtil<Map>();
        Map map = new HashMap();

        // 枚举-字典
        map.put("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());    // 路由策略-列表
        map.put("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        map.put("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());    // 阻塞处理策略-字典
        map.put("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());        // 失败处理策略-字典

        /**============获取模型下拉框============**/
        List<AtomicAlgorithm> executorHandlerlist = atomicAlgorithmService.findListParameter();
        List<Product> productList = dubboService.findALlProduct();
        map.put("executorHandlerlist", executorHandlerlist);
        map.put("productList", productList);

        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        map.put("JobGroupList", jobGroupList);
        map.put("jobGroup", jobGroup);
        map.put("taskType", taskType);
        result.setResult(map);
        return result;
    }

    @RequestMapping(value = "/pageList/{taskType}", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "根据任务类型查询任务列表", notes = "任务列表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "start", required = false, value = "起始页", defaultValue = "0"),
            @ApiImplicitParam(paramType = "query", name = "length", required = false, value = "条数", defaultValue = "10"),
            @ApiImplicitParam(paramType = "query", name = "jobGroup", required = false, value = "任务组", defaultValue = "-1"),
            @ApiImplicitParam(paramType = "query", name = "executorHandler", required = false, value = "执行器"),
            @ApiImplicitParam(paramType = "query", name = "filterTime", required = false, value = "时间"),
            @ApiImplicitParam(paramType = "path", name = "taskType", required = true, value = "任务类型")
    })
    public ResultUtil<Map> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                    @RequestParam(required = false, defaultValue = "10") int length,
                                    @RequestParam(required = true, defaultValue = "-1") int jobGroup,
                                    @RequestParam(required = false, defaultValue = "") String executorHandler,
                                    @RequestParam(required = false, defaultValue = "") String filterTime,
                                    @PathVariable("taskType") String taskType) {
        ResultUtil<Map> result = new ResultUtil<Map>();
        if (StringUtils.isEmpty(taskType)) {
            result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_201_ERROR);
        }
        if (!result.isSuccess()) {
            return result;
        }
        Map<String, Object> map = xxlJobService.pageList(start, length, jobGroup, executorHandler, filterTime, taskType);
        result.setResult(map);
        return result;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "添加任务", notes = "不传国家码时返回5大国所有的活动列表,每个国家最多返回8条数据")
    public ResultUtil<String> add(AppXxlJobInfo jobInfo,@RequestBody List<CommonParameter> fixedParameter,@RequestBody List<CommonParameter> dynamicParameter) {
        ResultUtil<String> result = new ResultUtil<String>();
        result = appJobInfoService.add(jobInfo,fixedParameter,dynamicParameter);
        return result;
    }
    @RequestMapping(value = "/findParameterByJobId", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "根据任务ID获取参数", notes = "调度任务所需要的参数，改变list 中value的值即可")
    public ResultUtil<List> findParameterByJobId(int jobId)  {
        ResultUtil<List> result = new ResultUtil<List>();
        XxlJobInfo jobInfo=xxlJobInfoDao.loadById(jobId);
        if(5 == jobInfo.getTasktype() || 7 == jobInfo.getTasktype()){

            TaskParameters taskParameters = taskParametersService.findJobParameterById(jobInfo.getExecutorParam());
            List<CommonParameter> commonParameters = com.alibaba.fastjson.JSON.parseArray(taskParameters.getDynamicParameter(), CommonParameter.class);
            result.setResult(commonParameters);
        }else{
            String parameter= taskParametersService.getJobParameterMap(jobInfo.getExecutorParam(),jobInfo.getModelId(),"2");
            Map map= JSON.parseObject(parameter,Map.class);
            List list= (List) map.get("list");
            result.setResult(list);

        }

        return result;
    }
    @RequestMapping(value = "/triggerJob", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "触发任务", notes = "参数为json")
    public ResultUtil<String> triggerJob(@RequestBody List<CommonParameter> parameterlist,int jobId){
        ResultUtil<String> result = new ResultUtil<String>();
        XxlJobInfo jobInfo=xxlJobInfoDao.loadById(jobId);
        jobInfo.setDynamicParameter(JSON.toJSONString(parameterlist));
        jobInfo.setOperation(1);
        if(5 == jobInfo.getTasktype() || 7 == jobInfo.getTasktype()){
            flowSchedulerService.scheduler(jobInfo);
        }else{
            singleSchedulerService.scheduler(jobInfo);
        }
        return result;

    }
}

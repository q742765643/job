package com.htht.job.admin.controller.appinterface;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.htht.job.admin.service.SchedulerService;
import com.htht.job.core.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liyuan on 2018/5/2.
 */
@RestController
@RequestMapping("/api/achive")
@Api(value = "/api/achive", description = "数据入库接口")
public class AppAchiveController {
    @Autowired
    @Qualifier("flowSchedulerService")
    private SchedulerService flowSchedulerService;
	private static Logger logger = LoggerFactory.getLogger(AppAchiveController.class);
    @RequestMapping(value = "/cp", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(value = "执行入库流程", notes = "输入流程ID和Json参数字符串")
    public ResultUtil<String> cp(int jobId , String json) {
        ResultUtil<String> result = new ResultUtil<String>();
        // load data
        XxlJobInfo jobInfo = XxlJobDynamicScheduler.xxlJobInfoDao.loadById(jobId);
        // job info
        if (jobInfo == null) {
            logger.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return null;
        }
        // 执行器参数
        jobInfo.setJsonString(json);

        flowSchedulerService.scheduler(jobInfo);
        return result;
    }
}

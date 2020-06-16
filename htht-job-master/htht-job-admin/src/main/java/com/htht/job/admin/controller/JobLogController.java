package com.htht.job.admin.controller;

import com.htht.job.admin.core.model.XxlJobGroup;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.dao.XxlJobLogDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.LogResult;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.vo.NodeMonitor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);
    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Autowired
    public DubboService dubboService;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    //判断哪个节点是活着
    @Resource
    private CheckAliveService checkAliveService;

    @RequestMapping
    public String index(Model model, @RequestParam(required = false, defaultValue = "0") Integer jobId) {

        // 执行器列表
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);

        // 任务
        if (jobId > 0) {
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
            model.addAttribute("jobInfo", jobInfo);
        }

        return "joblog/joblog.index";
    }

    @RequestMapping("/detaillog")
    public String detaillog(Model model, Integer jobId) {
        // 任务
        if (jobId > 0) {
            XxlJobLog load = xxlJobLogDao.load(jobId);
            model.addAttribute("jobInfo", load);
        }

        return "joblog/jobflowlog.detail.index";
    }

    @RequestMapping("/detaillogById")
    @ResponseBody
    public XxlJobLog detaillogById(Model model, Integer jobId) {
        // 任务
        XxlJobLog jobLog;
        if (jobId > 0) {
             jobLog = xxlJobLogDao.load(jobId);
            return jobLog;
        } else {
            return null;
        }
    }

    @RequestMapping("/detailPageList")
    @ResponseBody
    public Map<String, Object> detailPageList(@RequestParam(required = false, defaultValue = "0") int start,
                                              @RequestParam(required = false, defaultValue = "10") int length,
                                              int logId, int logStatus) {
        Map pageMap = dubboService.findParallelLogPage(logId, start, length, logStatus);
        Map<String, Object> maps = new HashMap<>();
        maps.put("data", pageMap.get("list"));
        maps.put("recordsTotal", pageMap.get("total"));
        maps.put("recordsFiltered", pageMap.get("total"));
        return maps;
    }

    @RequestMapping("/getJobsByGroup")
    @ResponseBody
    public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup) {
        List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
        return new ReturnT<>(list);
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int jobGroup, int jobId, int logStatus, String filterTime) {

        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (StringUtils.isNotBlank(filterTime)) {
            String[] temp = filterTime.split(" - ");
            if (temp != null && temp.length == 2) {
                try {
                    triggerTimeStart = DateUtils.parseDate(temp[0], new String[]{"yyyy-MM-dd HH:mm:ss"});
                    triggerTimeEnd = DateUtils.parseDate(temp[1], new String[]{"yyyy-MM-dd HH:mm:ss"});
                } catch (ParseException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }

        // page query
        List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        int listCount = xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

        // package result
        Map<String, Object> maps = new HashMap<>();
        // 总记录数
        maps.put("recordsTotal", listCount);
        // 过滤后的总记录数
        maps.put("recordsFiltered", listCount);
        // jobName 2018/2/11
        List<XxlJobLog> list1 = new ArrayList<>(list);
        List<XxlJobInfo> jobInfoList = this.xxlJobInfoDao.findAll();
        for (XxlJobLog log : list1) {
            for (XxlJobInfo info : jobInfoList) {
                if (info.getId() == log.getJobId() && info.getJobGroup() == log.getJobGroup()) {
                    log.setExecutorHandler(info.getJobDesc());
                    log.setModelId(info.getModelId());
                    log.setTasktype(info.getTasktype());
                }
            }
        }

        maps.put("data", list1);                    // 分页列表

        return maps;
    }

    @RequestMapping("/logDetailPage")
    public String logDetailPage(int id, Model model) {

        // base check
        XxlJobLog jobLog = xxlJobLogDao.load(id);
        if (jobLog == null) {
            return  "抱歉，日志ID非法.";
        }

        model.addAttribute("triggerCode", jobLog.getTriggerCode());
        model.addAttribute("handleCode", jobLog.getHandleCode());
        model.addAttribute("executorAddress", jobLog.getExecutorAddress());
        model.addAttribute("triggerTime", jobLog.getTriggerTime().getTime());
        model.addAttribute("logId", jobLog.getId());
        model.addAttribute("logFileName", jobLog.getLogFileName());

        return "joblog/joblog.detail";
    }

    @RequestMapping("/logDetailCat")
    @ResponseBody
    public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, int logId, int fromLineNum, String logFileName) {
        try {
            ExecutorBiz executorBiz = RealReference.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.logbypath(triggerTime, logId, fromLineNum, logFileName);
            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                XxlJobLog jobLog = xxlJobLogDao.load(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @RequestMapping("/parallelLogDetailPage")
    public String parallelLogDetailPage(int jobLogId, String id, Model model) {

        // base check
        XxlJobLog jobLog = xxlJobLogDao.load(jobLogId);
        ParallelLogDTO parallelLogDTO = dubboService.findParallelLogById(id);
        model.addAttribute("executorAddress", parallelLogDTO.getIp());
        model.addAttribute("parallelLogId", id);
        model.addAttribute("triggerTime", jobLog.getTriggerTime().getTime());


        return "joblog/parallellog.detail";
    }

    @RequestMapping("/parallelLogDetailCat")
    @ResponseBody
    public ReturnT<LogResult> parallelLogDetailCat(String executorAddress, String parallelLogId, int fromLineNum, long triggerTime) {
        try {
            ExecutorBiz executorBiz = RealReference.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.logbypath(triggerTime, parallelLogId, fromLineNum);
            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                ParallelLogDTO parallelLogDTO = dubboService.findParallelLogById(parallelLogId);
                if (parallelLogDTO.getCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @RequestMapping("/parallelLogDetailCat1")
    @ResponseBody
    public ReturnT<LogResult> parallelLogDetailCat1(int fromLineNum, String logFileName) {
        try {
            //获取一个活着的节点,一定有一个活着的节点，这个逻辑是基于日志是放在了公共的位置
            List<NodeMonitor> list = dubboService.findAllMonitor();
            List<String> adressList = checkAliveService.checkAliveByMonitors(list);
            ExecutorBiz executorBiz = RealReference.getExecutorBiz(adressList.get(0));
            ReturnT<LogResult> logResult = executorBiz.logbypath(logFileName, fromLineNum);
            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                logResult.getContent().setEnd(true);
            }

            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @RequestMapping("/logKill")
    @ResponseBody
    public ReturnT<String> logKill(int id) {
        // base check
        XxlJobLog log = xxlJobLogDao.load(id);
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if (jobInfo == null) {
            return new ReturnT<>(500, "参数异常");
        }
        if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
            return new ReturnT<>(500, "调度失败，无法终止日志");
        }

        // request of kill
        ReturnT<String> runResult = new ReturnT<>("");
        log.setHandleCode(ReturnT.FAIL_CODE);
        log.setHandleMsg("人为操作主动终止:" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
        log.setHandleTime(new Date());
        xxlJobLogDao.updateHandleInfo(log);
        return new ReturnT<>(runResult.getMsg());

    }

    @RequestMapping("/clearLog")
    @ResponseBody
    public ReturnT<String> clearLog(int jobGroup, int jobId, int type) {

        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -1);    // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -3);    // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -6);    // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtils.addYears(new Date(), -1);    // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;        // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;        // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;        // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;    // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;            // 清理所有日志数据
        } else {
            return new ReturnT<>(ReturnT.FAIL_CODE, "清理类型参数异常");
        }
        List<XxlJobLog> logList = xxlJobLogDao.selectClearLoglist(jobGroup, jobId, clearBeforeTime, clearBeforeNum);
        for (XxlJobLog xxlJobLog : logList) {
            dubboService.deleteParallelLogAndFlowLog(xxlJobLog.getId());
        }
        xxlJobLogDao.clearLog(jobGroup, jobId, clearBeforeTime, clearBeforeNum);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/clearOneLog")
    @ResponseBody
    public ReturnT<String> clearOneLog(int id) {
        if (StringUtils.isBlank(id + "")) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "清理日志id参数异常");
        }
        XxlJobLog xxlJobLog = xxlJobLogDao.load(id);
        dubboService.deleteParallelLogAndFlowLog(xxlJobLog.getId());
        xxlJobLogDao.clearOneLog(xxlJobLog.getId());
        return ReturnT.SUCCESS;
    }

}

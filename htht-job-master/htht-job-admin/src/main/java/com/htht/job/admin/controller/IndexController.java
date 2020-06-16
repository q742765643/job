package com.htht.job.admin.controller;

import com.htht.job.admin.controllerLog.SystemControllerLog;
import com.htht.job.admin.service.XxlJobService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

    @Resource
    private XxlJobService xxlJobService;
    @Resource
    private DubboService dubboService;

    @RequestMapping("/")
    public String index(Model model) {

        return "index";
    }

    @RequestMapping("/main")
    public String main(Model model) throws InterruptedException {

        Map<String, Object> dashboardMap = xxlJobService.dashboardInfo();
        model.addAllAttributes(dashboardMap);

        return "main";
    }

    @RequestMapping("/triggerChartDate")
    @ResponseBody
    public ReturnT<Map<String, Object>> triggerChartDate() {
        ReturnT<Map<String, Object>> triggerChartDate;
        triggerChartDate = xxlJobService.triggerChartDate();
        return triggerChartDate;
    }


    @SystemControllerLog(description = "退出了系统", type = SystemLog.SYSTEMLOG)
    @PostMapping(value = "logout")
    @ResponseBody
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/help")
    public String help() {


        return "help";
    }

}

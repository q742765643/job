package com.htht.job.admin.controller;

import com.htht.job.admin.controllerLog.SystemControllerLog;
import com.htht.job.admin.service.XxlJobService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.shiro.User;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
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
        ReturnT<Map<String, Object>> triggerChartDate = xxlJobService.triggerChartDate();
        return triggerChartDate;
    }

    //@RequestMapping("/toLogin")
    public String toLogin(Model model, HttpServletRequest request) {
        Subject currentUser = SecurityUtils.getSubject();
        //查找出所有标题
    	List<DictCode> tileList = dubboService.findChildrenDictCode("标题管理");
    	request.getSession().setAttribute("tileList",tileList);   	
        if (currentUser.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    //@SystemControllerLog(description = "登录了系统", type = SystemLog.SYSTEMLOG)
    //@RequestMapping(value = "login", method = RequestMethod.POST)
    //@ResponseBody
    public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember) {
        try {
        	
            UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
            Subject currentUser = SecurityUtils.getSubject();
            //if (!currentUser.isAuthenticated()){
            //使用shiro来验证
            //token.setRememberMe(true);
            currentUser.login(token);//验证角色和权限
            request.getSession().setAttribute("userName", token.getUsername());
        /*}else {
			return new ReturnT<String>(500, "账号或密码错误");
		}*/
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return new ReturnT<String>(500, "账号或密码错误");
        } 
        return ReturnT.SUCCESS;
    }

    @SystemControllerLog(description = "退出了系统", type = SystemLog.SYSTEMLOG)
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/help")
    public String help() {

		/*if (!PermissionInterceptor.ifLogin(request)) {
			return "redirect:/toLogin";
		}*/

        return "help";
    }

}

package com.htht.job.admin.controller;


import com.htht.job.admin.core.shiro.common.UpmsResult;
import com.htht.job.admin.core.shiro.common.UpmsResultConstant;
import com.htht.job.admin.core.util.RedisUtil;

import com.htht.job.core.api.DubboService;
import com.htht.job.executor.model.dictionary.DictCode;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

/**
 * Created by admin on 2017/6/29.
 */
@Controller
@RequestMapping("/sso")
public class SSOController {

    private final static Logger _log = LoggerFactory.getLogger(SSOController.class);
    // global session key
    private final static String MASTER_UPMS_SERVER_SESSION_ID = "master-upms-server-session-id";
    // global session list key 会话Id列表
    private final static String MASTER_UPMS_SERVER_SESSION_IDS = "master-upms-server-session-ids";
    // code key
    private final static String MASTER_UPMS_SERVER_CODE = "master-upms-server-code";
    @Autowired
    private DubboService dubboService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(HttpServletRequest request) throws Exception {
        String appId = request.getParameter("appid");  //sysname
        //返回的Url
        String backUrl = request.getParameter("backurl");
        if (StringUtils.isBlank(appId)) {
            throw new RuntimeException("无效访问");
        }
        return "redirect:/sso/login?backurl=" + URLEncoder.encode(backUrl, "utf-8");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String serverSessionId = session.getId().toString();
        //判断是否登陆，已经登陆则回跳
        String code = RedisUtil.get(MASTER_UPMS_SERVER_SESSION_ID + "_" + serverSessionId);

        if (StringUtils.isNotBlank(code)) {
            String backUrl = request.getParameter("backurl");
            String username = (String) subject.getPrincipal();
            if (StringUtils.isBlank(backUrl)) {
                backUrl = "/";
            } else {
                if (backUrl.contains("?")) {
                    backUrl += "&upms_code=" + code + "&upms_username=" + username;
                } else {
                    backUrl += "?upms_code=" + code + "&upms_username=" + username;
                }
            }
            _log.debug("认证中心账号通过，带code回跳:{}", backUrl);
            return "redirect:" + backUrl;
        }
        List<DictCode> tileList = dubboService.findChildrenDictCode("标题管理");
        request.getSession().setAttribute("tileList",tileList);
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String username = request.getParameter("userName");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        if (StringUtils.isBlank(username)) {
            return new UpmsResult(UpmsResultConstant.EMPTY_PASSWORD, "账号不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return new UpmsResult(UpmsResultConstant.EMPTY_PASSWORD, "密码不能为空");
        }
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String sessionId = session.getId().toString();
        // 判断是否登陆，如果已经登陆，则回跳，防止重复登陆
        String hasCode = RedisUtil.get(MASTER_UPMS_SERVER_SESSION_ID + "_" + sessionId);
        if (StringUtils.isBlank(hasCode)) {
            //shiro 认证
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
            try {
                if (BooleanUtils.toBoolean(rememberMe)) {
                    usernamePasswordToken.setRememberMe(true);
                } else {
                    usernamePasswordToken.setRememberMe(false);
                }
                subject.login(usernamePasswordToken);
            } catch (UnknownAccountException e) {
                return new UpmsResult(UpmsResultConstant.INVALID_USERNAME, "账号不存在!");
            } catch (IncorrectCredentialsException e) {
                return new UpmsResult(UpmsResultConstant.INVALID_PASSWORD, "密码错误!");
            } catch (LockedAccountException e) {
                return new UpmsResult(UpmsResultConstant.INVILID_ACCOUNT, "账号已经锁定");
            }
            //更新session状态
            //Global sessions list
            RedisUtil.lpush(MASTER_UPMS_SERVER_SESSION_IDS, sessionId.toString());
            //默认验证账号密码正确 创建code
            String code = UUID.randomUUID().toString();
            //Global session code
            RedisUtil.set(MASTER_UPMS_SERVER_SESSION_ID + "_" + sessionId, code, (int)subject.getSession().getTimeout() / 1000);
            //code
            RedisUtil.set(MASTER_UPMS_SERVER_CODE + "_" + code, code, (int) subject.getSession().getTimeout() / 1000);
        }
        String backUrl = request.getParameter("backurl");
        if (StringUtils.isBlank(backUrl)) {
            return new UpmsResult(UpmsResultConstant.SUCCESS, "/");
        } else {
            return new UpmsResult(UpmsResultConstant.SUCCESS, backUrl);
        }
    }

    @RequestMapping(value = "/code", method = RequestMethod.POST)
    @ResponseBody
    public Object code(HttpServletRequest request) {
        String codeParam = request.getParameter("code");
        String code = RedisUtil.get(MASTER_UPMS_SERVER_CODE + "_" + codeParam);
        if (StringUtils.isBlank(codeParam) || !codeParam.equals(code)) {
            new UpmsResult(UpmsResultConstant.FAILED, "无效code");
        }
        return new UpmsResult(UpmsResultConstant.SUCCESS, code);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) throws UnsupportedEncodingException {
        //shiro退出
        SecurityUtils.getSubject().logout();
        //redirect back
        String redirectUrl = request.getHeader("Referer");
        if (null == redirectUrl) {
            redirectUrl = "/";
        }
        return "redirect:"+redirectUrl;

    }
}

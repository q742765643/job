package com.htht.job.admin.controllerLog;

import com.htht.job.admin.core.shiro.UpmsRealm;
import com.htht.job.core.api.DubboService;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * 切点类
 */
@Aspect
@Component
public class SystemLogAspect {

    // 本地异常日志记录对象
    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);
    @Autowired
    DubboService dubboService;

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static String getControllerMethodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(
                            SystemControllerLog.class).description();
                    break;
                }
            }
        }
        return description;
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static String getControllerMethodType(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String type = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    type = method.getAnnotation(
                            SystemControllerLog.class).type();
                    break;
                }
            }
        }
        return type;
    }

    // Controller层切点
    //@Pointcut("execution(public * com.htht.job.admin.controller.*.*(..)) ")
    @Pointcut("@annotation(com.htht.job.admin.controllerLog.SystemControllerLog)")
    public void controllerAspect() {
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     */
    // @AfterReturning("controllerAspect()")
    @Around("@annotation(systemControllerLog)")
    public Object dobefore(ProceedingJoinPoint pjp, SystemControllerLog systemControllerLog) {

        Object[] args = pjp.getArgs();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //读取session中的用户
        String username = null;
        try {
            username = (String) SecurityUtils.getSubject().getSession().getAttribute(UpmsRealm.CURRENT_USERNAME);
            Object res = pjp.proceed();
            if (StringUtils.isEmpty(username)) {
                username = (String) SecurityUtils.getSubject().getSession().getAttribute(UpmsRealm.CURRENT_USERNAME);
            }
            if (StringUtils.hasLength(username)) {
                getControllerMethodLog(systemControllerLog, username, getRemoteHost(request));
            }
            return res;
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 异常通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @AfterThrowing(pointcut = "controllerAspect()")
    public void doafter(JoinPoint joinPoint) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        // 读取session中的用户
        try {
            // *========控制台输出=========*//
            System.out.println("=====异常通知开始=====");
            System.out.println("请求方法:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            System.out.println("方法描述:" + getControllerMethodDescription(joinPoint));

        } catch (Exception e) {
            // 记录本地异常日志
            logger.error("==异常通知异常==");
            logger.error("异常信息:{}", e.getMessage());
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public void getControllerMethodLog(SystemControllerLog systemControllerLog, String username, String ip)
            throws Exception {
        SystemLog systemLog = new SystemLog();
        systemLog.setCategory(systemControllerLog.type());
        systemLog.setContent(systemControllerLog.description());
        systemLog.setIp(ip);
        systemLog.setUsername(username);
        systemLog.setCreateTime(new Date());
        systemLog.setUpdateTime(new Date());
        systemLog.setVersion(1);
        dubboService.saveSystemLog(systemLog);
    }

    public String getRemoteHost(javax.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

}
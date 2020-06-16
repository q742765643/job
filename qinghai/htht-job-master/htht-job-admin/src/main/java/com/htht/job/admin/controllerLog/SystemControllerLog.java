package com.htht.job.admin.controllerLog;

import com.htht.job.executor.model.systemlog.SystemLog;

import java.lang.annotation.*;

/**  
 *自定义注解 拦截Controller  
 */    
    
@Target({ElementType.PARAMETER, ElementType.METHOD})    
@Retention(RetentionPolicy.RUNTIME)    
@Documented    
public  @interface SystemControllerLog {

    String description()  default "";
    String type() default SystemLog.SYSTEMLOG;
    
    
}  
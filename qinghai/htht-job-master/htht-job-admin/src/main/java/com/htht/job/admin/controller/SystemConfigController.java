package com.htht.job.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
*@Author: dingjiancheng
*@Description:  系统配置项
*@date: 2018/9/6
*/
@Controller
@RequestMapping("/systemConfig")
public class SystemConfigController {

    @RequestMapping("/ftpsConfig")
    public String ConfigItem() {
        return "systemConfig/ftpsConfig";
    }
}

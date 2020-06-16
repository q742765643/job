package com.htht.job.admin.controller.brace;

import com.htht.job.core.api.DubboShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @program: htht-job-api
 * @description: 资源管理
 * @author: fuyanchao
 * @create: 2018-09-03 11:22
 */
@Controller
@RequestMapping("/brace")
public class BResourceInfoController {

    @Autowired
    private DubboShiroService dubboShiroService;

    @RequestMapping("/resource")
    public String UserInfo() {
        return "brace/resource";
    }

    @RequestMapping("/resource/pageList")
    @ResponseBody
    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
                           @RequestParam(required = false, defaultValue = "10") int length,
                           String searchText, String id) {
        if (start != 0) {
            start = start / length;
        }
        return dubboShiroService.resourceList(start, length, searchText, id);
    }
}

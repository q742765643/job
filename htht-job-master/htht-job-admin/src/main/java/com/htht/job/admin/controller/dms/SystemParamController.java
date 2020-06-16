package com.htht.job.admin.controller.dms;

import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.dms.module.SystemParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: yss
 * @time:2018年10月18日
 */

@Controller
@RequestMapping("/system_param")
public class SystemParamController {
    @Autowired
    private DubboService dubboService;

    @RequestMapping
    public String index() {
        return "dms/system_param.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
                           @RequestParam(required = false, defaultValue = "10") int length,
                           String searchText, @RequestParam(required = false, defaultValue = "") String id) {
        if (start != 0) {
            start = start / length;
        }
        return dubboService.systemParamList(start, length, searchText, id);
    }

    @RequestMapping("/savesystemParam")
    @ResponseBody
    public ReturnT<String> saveUser(SystemParam systemParam) {
        try {

            dubboService.saveOrUpdateSystemParam(systemParam);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @RequestMapping("/deleteSystemParam/{id}")
    @ResponseBody
    public ReturnT<String> delSystemParam(@PathVariable String id) {
        try {
            dubboService.deleteSystemParam(id);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

}

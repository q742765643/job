package com.htht.job.admin.controller;

import com.htht.job.admin.controllerLog.SystemControllerLog;
import com.htht.job.core.api.DubboService;
import com.htht.job.executor.model.ftp.Ftp;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job-api
 * @description: 日志管理
 * @author: fuyanchao
 * @create: 2018-09-03 11:22
 */
@Controller
@RequestMapping("/logInfo")
public class LogInfoController {

    @Autowired
    private DubboService dubboService;

    @RequestMapping("/logInfo")
    public String LogInfoController() {

        return "logInfo/logManage";
    }

    @RequestMapping("/logs")
    @ResponseBody
    public Map<String,Object> logs(@RequestParam(required = false, defaultValue = "0") int start,
                                            @RequestParam(required = false, defaultValue = "10") int length, String type) {
        if (start != 0) {
            start = start / length;
        }
        Map<String, Object> maps = new HashMap<>();
        Pageable pageable = new PageRequest(start, length, new Sort(Sort.Direction.DESC, "createTime"));

        Page<SystemLog> page = dubboService.getSystemLogsByCategory(type, pageable);

        maps.put("recordsTotal", page.getTotalElements());        // 总记录数
        maps.put("recordsFiltered", page.getTotalElements());    // 过滤后的总记录数
        maps.put("data", page.getContent());
        return maps;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public int delete(String id) {
        return dubboService.deleteSystemLog(id);
    }
}

package com.htht.job.admin.controller.appinterface.dbms;

import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dbms.DbmsModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * 功能管理
 *
 * @author Administrator
 *         2018年9月13日
 */
@Controller
@RequestMapping("/dbms_module")
public class ModuleInfoController {
    @Autowired
    private DubboShiroService dubboShiroService;

    @RequestMapping
    public String index() {
        return "dbms/dbms_module.index";
    }

    @RequestMapping("/tree/{resourceId}")
    @ResponseBody
    public List<ZtreeView> tree(@PathVariable String resourceId) {
        List<ZtreeView> list = dubboShiroService.tree(resourceId, SystemType.SYSTEM_TYPE_DMS);
        return list;
    }

    @RequestMapping("/allTree")
    @ResponseBody
    public List<ZtreeView> allTree() {
        List<ZtreeView> list = dubboShiroService.allTree(SystemType.SYSTEM_TYPE_DMS);
        return list;
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
                           @RequestParam(required = false, defaultValue = "10") int length,
                           String searchText, String id) {
        if (start != 0) {
            start = start / length;
        }
        return dubboShiroService.moduleList(start, length, searchText, id);
    }

    @RequestMapping("/saveModule")
    @ResponseBody
    public ReturnT<String> saveResource(DbmsModule module) {
        try {
            module.setCreateTime(new Date());
            dubboShiroService.saveOrUpdateModule(module);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            return ReturnT.FAIL;

        }
    }

    @RequestMapping("/delModule/{id}")
    @ResponseBody
    public ReturnT<String> delResource(@PathVariable String id) {
        try {
            dubboShiroService.deleteModule(id);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            return ReturnT.FAIL;
        }
    }
}

package com.htht.job.admin.controller.shiro;


import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.shiro.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    private DubboShiroService dubboShiroService;

    @RequestMapping
    public String index(Model model) {

        return "/shiro/resource.index";
    }

    @RequestMapping("/tree/{resourceId}")
    @ResponseBody
    public List<ZtreeView> tree(@PathVariable String resourceId) {
        List<ZtreeView> list = dubboShiroService.tree(resourceId, SystemType.SYSTEM_TYPE_CLUSTER);
        return list;
    }

    @RequestMapping("/allTree")
    @ResponseBody
    public List<ZtreeView> allTree() {
        List<ZtreeView> list = dubboShiroService.allTree(SystemType.SYSTEM_TYPE_CLUSTER);
        return list;
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
                           @RequestParam(required = false, defaultValue = "10") int length,
                           String searchText,String id) {
        if (start != 0) {
            start = start / length;
        }
        return dubboShiroService.resourceList(start, length, searchText,id);
    }

    @RequestMapping("/saveResource")
    @ResponseBody
    public ReturnT<String> saveResource(Resource resource) {
        try {
            dubboShiroService.saveOrUpdateResource(resource);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            return ReturnT.FAIL;

        }
    }

    @RequestMapping("/delResource/{id}")
    @ResponseBody
    public ReturnT<String> delResource(@PathVariable String id) {
        try {
            dubboShiroService.deleteResource(id);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            return ReturnT.FAIL;
        }
    }
}

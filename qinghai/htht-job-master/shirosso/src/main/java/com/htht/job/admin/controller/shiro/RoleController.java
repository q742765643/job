package com.htht.job.admin.controller.shiro;

import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.shiro.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/role")
public class RoleController {


    @Autowired
    private DubboShiroService dubboShiroService;

    @RequestMapping
    public String index() {
        return "/shiro/role.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
                           @RequestParam(required = false, defaultValue = "10") int length,
                           String searchText) {
        if (start != 0) {
            start = start / length;
        }
        return dubboShiroService.roleList(start, length, searchText, SystemType.SYSTEM_TYPE_CLUSTER);
    }

    @RequestMapping("/saveRole")
    @ResponseBody
    public ReturnT<String> saveRole(Role role) {
        try {
            dubboShiroService.saveOrUpdateRole(role, SystemType.SYSTEM_TYPE_CLUSTER);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @RequestMapping("/delRole/{id}")
    @ResponseBody
    public ReturnT<String> delRole(@PathVariable String id) {
        try {
            dubboShiroService.deleteRole(id, SystemType.SYSTEM_TYPE_CLUSTER);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @RequestMapping("/grant/{id}")
    @ResponseBody
    public ReturnT<String> grant(@PathVariable String id, @RequestParam(required = false) String[] resourceIds) {
        try {
            dubboShiroService.grantResoure(id, resourceIds, SystemType.SYSTEM_TYPE_CLUSTER);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }


}

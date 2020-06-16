package com.htht.job.admin.controller.shiro;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.shiro.Role;
import com.htht.job.executor.model.shiro.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private DubboShiroService dubboShiroService;

    @RequestMapping
    public String index() {
        return "/shiro/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
                           @RequestParam(required = false, defaultValue = "10") int length,
                           String searchText) {
        if (start != 0) {
            start = start / length;
        }
        return dubboShiroService.userList(start, length, searchText, SystemType.SYSTEM_TYPE_CLUSTER);
    }

    @RequestMapping("/saveUser")
    @ResponseBody
    public ReturnT<String> saveUser(User user) {
        try {
            dubboShiroService.saveOrUpdateUser(user, SystemType.SYSTEM_TYPE_CLUSTER);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @RequestMapping("/delUser/{id}")
    @ResponseBody
    public ReturnT<String> delUser(@PathVariable String id) {
        try {
            dubboShiroService.deleteUser(id, SystemType.SYSTEM_TYPE_CLUSTER);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String grant(@PathVariable String id) {
        String userString = dubboShiroService.findById(id, SystemType.SYSTEM_TYPE_CLUSTER);
        User user = JSON.parseObject(userString, User.class);
        Set<Role> set = user.getRoles();
        List<String> roleIds = new ArrayList<String>();
        for (Role role : set) {
            roleIds.add(role.getId());
        }
        String rolesString = dubboShiroService.findAllRole(SystemType.SYSTEM_TYPE_CLUSTER);
        List<Role> roles = JSON.parseArray(rolesString, Role.class);
        Map map = new HashMap();
        map.put("roleIds", roleIds);
        map.put("roles", roles);
        return JSON.toJSONString(map);
    }

    @RequestMapping("/grant/{id}")
    @ResponseBody
    public ReturnT<String> grant(@PathVariable String id, @RequestParam(required = false) String[] roleIds) {
        try {
            dubboShiroService.grantUser(id, roleIds, SystemType.SYSTEM_TYPE_CLUSTER);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
}

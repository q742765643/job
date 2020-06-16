package com.htht.job.admin.controller.appinterface.dbms;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.dbms.DbmsRole;
import com.htht.job.executor.model.dbms.DbmsUser;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

/**
 * @program: htht-job-api
 * @description: 角色管理
 * @author: fuyanchao
 * @create: 2018-09-03 11:22
 */
@Controller
@RequestMapping("/dbms_user")
@Api(value = "/dbms_user")
public class UserInfoController {
    @Autowired
    private DubboShiroService dubboShiroService;

    @ApiIgnore
    @RequestMapping
    public String index() {
        return "dbms/dbms_user.index";
    }

    @RequestMapping(value = "/pageList", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
                           @RequestParam(required = false, defaultValue = "10") int length,
                           String searchText) {
        if (start != 0) {
            start = start / length;
        }
        return dubboShiroService.userList(start, length, searchText, SystemType.SYSTEM_TYPE_DMS);
    }

    @RequestMapping(value = "/saveUser", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ReturnT<String> saveUser(DbmsUser user) {
        try {
            dubboShiroService.saveOrUpdateUser(user, SystemType.SYSTEM_TYPE_DMS);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @RequestMapping(value = "/delUser/{id}", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ReturnT<String> delUser(@PathVariable String id) {
        try {
            dubboShiroService.deleteUser(id, SystemType.SYSTEM_TYPE_DMS);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String grant(@PathVariable String id) {
        String userString = dubboShiroService.findById(id, SystemType.SYSTEM_TYPE_DMS);
        DbmsUser user = JSON.parseObject(userString, DbmsUser.class);
        Set<DbmsRole> set = user.getRoles();
        List<String> roleIds = new ArrayList<String>();
        for (DbmsRole role : set) {
            roleIds.add(role.getId());
        }
        String rolesString = dubboShiroService.findAllRole(SystemType.SYSTEM_TYPE_DMS);
        List<DbmsRole> roles = JSON.parseArray(rolesString, DbmsRole.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("roleIds", roleIds);
        map.put("roles", roles);
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/grant/{id}", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ReturnT<String> grant(@PathVariable String id, @RequestParam(required = false) String[] roleIds) {
        try {
            dubboShiroService.grantUser(id, roleIds, SystemType.SYSTEM_TYPE_DMS);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
}

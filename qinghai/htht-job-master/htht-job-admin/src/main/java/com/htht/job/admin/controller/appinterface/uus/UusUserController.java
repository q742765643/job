package com.htht.job.admin.controller.appinterface.uus;

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.util.TreeBuilder;
import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.shiro.Role;
import com.htht.job.executor.model.shiro.User;
import com.htht.job.executor.model.uus.RegionInfo;
import com.htht.job.executor.model.uus.UusUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/uus_user")
public class UusUserController {

    @Autowired
    private DubboShiroService dubboShiroService;

    @RequestMapping
    public String index() {
        return "/uus/user";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
                           @RequestParam(required = false, defaultValue = "10") int length,
                           String searchText) {
        if (start != 0) {
            start = start / length;
        }
        return dubboShiroService.userList(start, length, searchText, SystemType.SYSTEM_TYPE_UUS);
    }
    
    /**
     * 获取所有的区划代码目录树
     * @return
     */
    @RequestMapping("/getAllRegionInfo")
    @ResponseBody
    public List<RegionInfo> getAllRegionInfo(){
    	List<RegionInfo> regionInfoList = dubboShiroService.findAllRegionInfo();
    	if ( null ==regionInfoList || 0 == regionInfoList.size()) {
    		return null;
    	}
    	regionInfoList = TreeBuilder.buildByRecursive(regionInfoList);
    	return regionInfoList;
    }

    @RequestMapping("/saveUser")
    @ResponseBody
    public ReturnT<String> saveUser(UusUser user) {
        try {
            dubboShiroService.saveOrUpdateUser(user, SystemType.SYSTEM_TYPE_UUS);
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
            dubboShiroService.deleteUser(id, SystemType.SYSTEM_TYPE_UUS);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String grant(@PathVariable String id) {
        String userString = dubboShiroService.findById(id, SystemType.SYSTEM_TYPE_UUS);
        User user = JSON.parseObject(userString, User.class);
        Set<Role> set = user.getRoles();
        List<String> roleIds = new ArrayList<String>();
        for (Role role : set) {
            roleIds.add(role.getId());
        }
        String rolesString = dubboShiroService.findAllRole(SystemType.SYSTEM_TYPE_UUS);
        List<Role> roles = JSON.parseArray(rolesString, Role.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("roleIds", roleIds);
        map.put("roles", roles);
        return JSON.toJSONString(map);
    }

    @RequestMapping("/grant/{id}")
    @ResponseBody
    public ReturnT<String> grant(@PathVariable String id, @RequestParam(required = false) String[] roleIds) {
        try {
            dubboShiroService.grantUser(id, roleIds, SystemType.SYSTEM_TYPE_UUS);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
    }
}

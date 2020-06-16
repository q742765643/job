package com.htht.job.admin.controller.appinterface.dbms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.dbms.DbmsRole;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @program: htht-job-api
 * @description: 角色管理
 * @author: fuyanchao
 * @create: 2018-09-03 11:22
 */
@Controller
@RequestMapping("/dbms_role")
@Api(value = "/dbms_role")
public class RoleInfoController {
	@Autowired
    private DubboShiroService dubboShiroService;
	
	@ApiIgnore
	@RequestMapping
	public String index() {

		return "/dbms/dbms_role.index";
	}

	@RequestMapping(value="/pageList",method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length, String searchText) {
		if (start != 0) {
			start = start / length;
		}
		return dubboShiroService.roleList(start, length, searchText, SystemType.SYSTEM_TYPE_DMS);
	}

	@RequestMapping(value="/saveRole",method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	@ApiOperation(value = "添加角色")
	public ReturnT<String> saveRole(DbmsRole role) {
		try {
			dubboShiroService.saveOrUpdateRole(role, SystemType.SYSTEM_TYPE_DMS);
			return ReturnT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@RequestMapping(value="/delRole/{id}",method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public ReturnT<String> delRole(@PathVariable String id) {
		try {
			dubboShiroService.deleteRole(id, SystemType.SYSTEM_TYPE_DMS);
			return ReturnT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@RequestMapping(value="/grant/{id}",method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public ReturnT<String> grant(@PathVariable String id, @RequestParam(required = false) String[] resourceIds) {
		try {
			dubboShiroService.grantResoure(id, resourceIds, SystemType.SYSTEM_TYPE_DMS);
			return ReturnT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
}

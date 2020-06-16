package com.htht.job.admin.controller.dms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.dms.module.Disk;

/**
 * @author: yss
 * @time:2018年10月31日 上午10:36:33
 */
@Controller
@RequestMapping("/disk_information")
public class DiskInformationController {
	@Autowired
	private DubboService dubboService;

	@RequestMapping
	public String index() {
		return "dms/disk_information.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length, String searchText) {
		if (start != 0) {
			start = start / length;
		}
		return dubboService.diskList(start, length, searchText);
	}

	@RequestMapping("saveDisk")
	@ResponseBody
	public ReturnT<String> saveDisk(Disk disk) {
		try {
			dubboService.saveOrUpdateDisk(disk);
			return ReturnT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}

	@RequestMapping("/delDisk/{id}")
	@ResponseBody
	public ReturnT<String> delUser(@PathVariable String id) {
		try {
			dubboService.delDisk(id);
			return ReturnT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	/*
	 * @RequestMapping(value = "/find/{id}", method = RequestMethod.GET, produces =
	 * {"application/json;charset=UTF-8"})
	 * 
	 * @ResponseBody public String grant(@PathVariable String id) { String
	 * userString = dubboShiroService.findById(id, SystemType.SYSTEM_TYPE_DMS);
	 * DbmsUser user = JSON.parseObject(userString, DbmsUser.class); Set<DbmsRole>
	 * set = user.getRoles(); List<String> roleIds = new ArrayList<String>(); for
	 * (DbmsRole role : set) { roleIds.add(role.getId()); } String rolesString =
	 * dubboShiroService.findAllRole(SystemType.SYSTEM_TYPE_DMS); List<DbmsRole>
	 * roles = JSON.parseArray(rolesString, DbmsRole.class); Map<String, Object> map
	 * = new HashMap<String, Object>(); map.put("roleIds", roleIds);
	 * map.put("roles", roles); return JSON.toJSONString(map); }
	 * 
	 * @RequestMapping(value="/grant/{id}",method = RequestMethod.POST, produces =
	 * {"application/json;charset=UTF-8"})
	 * 
	 * @ResponseBody public ReturnT<String> grant(@PathVariable String
	 * id, @RequestParam(required = false) String[] roleIds) { try {
	 * dubboShiroService.grantUser(id, roleIds, SystemType.SYSTEM_TYPE_DMS); return
	 * ReturnT.SUCCESS; } catch (Exception e) { e.printStackTrace(); return
	 * ReturnT.FAIL; } }
	 */
}

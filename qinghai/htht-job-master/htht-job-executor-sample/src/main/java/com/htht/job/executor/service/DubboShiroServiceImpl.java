package com.htht.job.executor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.api.datacategory.DataCategoryService;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dbms.DbmsModule;
import com.htht.job.executor.model.dbms.DbmsRole;
import com.htht.job.executor.model.dbms.DbmsUser;
import com.htht.job.executor.model.shiro.Resource;
import com.htht.job.executor.model.shiro.Role;
import com.htht.job.executor.model.shiro.User;
import com.htht.job.executor.model.uus.RegionInfo;
import com.htht.job.executor.model.uus.UusRole;
import com.htht.job.executor.model.uus.UusUser;
import com.htht.job.executor.service.dbms.DbmsModuleService;
import com.htht.job.executor.service.dbms.DbmsRoleService;
import com.htht.job.executor.service.dbms.DbmsUserService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.shiro.ResourceService;
import com.htht.job.executor.service.shiro.RoleService;
import com.htht.job.executor.service.shiro.UserService;
import com.htht.job.executor.service.uus.RegionInfoService;
import com.htht.job.executor.service.uus.UusRoleService;
import com.htht.job.executor.service.uus.UusUserService;

/**
 * Created by zzj on 2018/1/24.
 */
@Transactional
@Service("dubboShiroService")
public class DubboShiroServiceImpl implements DubboShiroService {
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService userService;
	@Autowired
	private UusRoleService uusRoleService;
	@Autowired
	private UusUserService uusUserService;
	@Autowired
	private DbmsRoleService dbmsRoleService;
	@Autowired
	private DbmsUserService dbmsUserService;
	@Autowired
	private ProductService productService;
	@Autowired
	private DataCategoryService catetoryService;
	@Autowired
	private DbmsModuleService dbmsModuleService;
	@Autowired
	private RegionInfoService regionInfoService;

	@Override
	public String findByUserName(String username, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			return userService.findByUserName(username);
		case SYSTEM_TYPE_UUS:
			return uusUserService.findByUserName(username);
		case SYSTEM_TYPE_DMS:
			return dbmsUserService.findByUserName(username);
		default:
			return userService.findByUserName(username);
		}
	}

	@Override
	public void saveOrUpdateUser(Object user, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			userService.saveOrUpdate((User) user);
			break;
		case SYSTEM_TYPE_UUS:
			uusUserService.saveOrUpdate((UusUser) user);
			break;
		case SYSTEM_TYPE_DMS:
			dbmsUserService.saveOrUpdate((DbmsUser) user);
			break;
		default:
			break;
		}
	}

	@Override
	public void deleteUser(String id, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			userService.delete(id);
			break;
		case SYSTEM_TYPE_UUS:
			uusUserService.delete(id);
			break;
		case SYSTEM_TYPE_DMS:
			dbmsUserService.delete(id);
			break;
		default:
			break;
		}
	}

	@Override
	public void grantUser(String id, String[] roleIds, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			userService.grant(id, roleIds);
			break;
		case SYSTEM_TYPE_UUS:
			uusUserService.grant(id, roleIds);
			break;
		case SYSTEM_TYPE_DMS:
			dbmsUserService.grant(id, roleIds);
			break;
		default:
			break;
		}
	}

	@Override
	public void saveOrUpdateRole(Object role, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			roleService.saveOrUpdate((Role) role);
			break;
		case SYSTEM_TYPE_UUS:
			uusRoleService.saveOrUpdate((UusRole) role);
			break;
		case SYSTEM_TYPE_DMS:
			dbmsRoleService.saveOrUpdate((DbmsRole) role);
			break;
		default:
			break;
		}
	}

	@Override
	public void deleteRole(String id, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			roleService.delete(id);
			break;
		case SYSTEM_TYPE_UUS:
			uusRoleService.delete(id);
			break;
		case SYSTEM_TYPE_DMS:
			dbmsRoleService.delete(id);
			break;
		default:
			break;
		}
	}

	@Override
	public void grantResoure(String id, String[] resourceIds, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			roleService.grant(id, resourceIds);
			break;
		case SYSTEM_TYPE_UUS:
			uusRoleService.grant(id, resourceIds);
			break;
		case SYSTEM_TYPE_DMS:
			dbmsRoleService.grant(id, resourceIds);
			break;
		default:
			break;
		}
	}

	@Override
	public List<ZtreeView> tree(String roleId, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			return resourceService.tree(roleId);
		case SYSTEM_TYPE_UUS:
			return catetoryService.tree(roleId);
		case SYSTEM_TYPE_DMS:
			return dbmsModuleService.tree(roleId);
		default:
			return resourceService.tree(roleId);
		}
	}

	@Override
	public void saveOrUpdateResource(Resource resource) {
		resourceService.saveOrUpdate(resource);
	}

	@Override
	public void deleteResource(String id) {
		resourceService.delete(id);
	}

	@Override
	public String userList(int start, int length, String searchText, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			return userService.list(start, length, searchText);
		case SYSTEM_TYPE_UUS:
			return uusUserService.list(start, length, searchText);
		case SYSTEM_TYPE_DMS:
			return dbmsUserService.list(start, length, searchText);
		default:
			return userService.list(start, length, searchText);
		}
	}

	@Override
	public String roleList(int start, int length, String searchText, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			return roleService.list(start, length, searchText);
		case SYSTEM_TYPE_UUS:
			return uusRoleService.list(start, length, searchText);
		case SYSTEM_TYPE_DMS:
			return dbmsRoleService.list(start, length, searchText);
		default:
			return roleService.list(start, length, searchText);
		}
	}

	@Override
	public String resourceList(int start, int length, String searchText, String id) {
		return resourceService.list(start, length, searchText, id);
	}

	@Override
	public String findAllResouce() {
		return resourceService.findAllResouce();
	}

	@Override
	public List<ZtreeView> allTree(SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			return resourceService.allTree();
		case SYSTEM_TYPE_DMS:
			return dbmsModuleService.allTree();
		default:
			return resourceService.allTree();
		}
	}

	@Override
	public String findById(String id, SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			return userService.findById(id);
		case SYSTEM_TYPE_UUS:
			return uusUserService.findById(id);
		case SYSTEM_TYPE_DMS:
			return dbmsUserService.findById(id);
		default:
			return userService.findById(id);
		}
	}

	@Override
	public String findAllRole(SystemType type) {
		switch (type) {
		case SYSTEM_TYPE_CLUSTER:
			return roleService.findAllRole();
		case SYSTEM_TYPE_UUS:
			return uusRoleService.findAllRole();
		case SYSTEM_TYPE_DMS:
			return dbmsRoleService.findAllRole();
		default:
			return roleService.findAllRole();
		}
	}

	@Override
	public String moduleList(int start, int length, String searchText, String id) {
		return dbmsModuleService.list(start,length,searchText,id);
	}

	@Override
	public void saveOrUpdateModule(DbmsModule module) {
		dbmsModuleService.save(module);
	}

	@Override
	public void deleteModule(String id) {
		dbmsModuleService.delete(id);
	}

	@Override
	public List<RegionInfo> findAllRegionInfo() {		
		return regionInfoService.findAllRegionInfo();
	}
}

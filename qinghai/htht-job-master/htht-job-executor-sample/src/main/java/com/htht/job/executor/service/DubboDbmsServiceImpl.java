//package com.htht.job.executor.service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.htht.job.core.api.DubboDbmsService;
//import com.htht.job.executor.model.datacategory.ZtreeView;
//import com.htht.job.executor.model.dbms.DbmsModule;
//import com.htht.job.executor.model.dbms.DbmsRole;
//import com.htht.job.executor.model.dbms.DbmsUser;
//import com.htht.job.executor.service.dbms.DbmsModuleService;
//import com.htht.job.executor.service.dbms.DbmsRoleService;
//import com.htht.job.executor.service.dbms.DbmsUserService;
//
//
///**
// * 
// * @author Administrator
// *	2018年9月10日
// */
//@Transactional
//@Service("dubboDbmsService")
//public class DubboDbmsServiceImpl implements DubboDbmsService {
//    @Autowired
//    private DbmsModuleService dbmsModuleService;
//    @Autowired
//    private DbmsRoleService roleService;
//    @Autowired
//    private DbmsUserService userService;
//
//    public String findByUserName(String username){
//        return userService.findByUserName(username);
//    }
//    public void saveOrUpdateUser(DbmsUser user){
//         userService.saveOrUpdate(user);
//    }
//    public void deleteUser(String id){
//         userService.delete(id);
//    }
//    public void grantUser(String id, String[] roleIds){
//         userService.grant(id,roleIds);
//    }
//    public void saveOrUpdateRole(DbmsRole role){
//         roleService.saveOrUpdate(role);
//    }
//    public void deleteRole(String id){
//         roleService.delete(id);
//    }
//    public void grantModule(String id, String[] modulesIds){
//         roleService.grant(id,modulesIds);
//    }
//    public List<ZtreeView> tree(String roleId){
//        return  dbmsModuleService.tree(roleId);
//    }
//    public void saveOrUpdateResource(DbmsModule module){
//    	dbmsModuleService.saveOrUpdate(module);
//    }
//    public String userList(int start, int length, String searchText){
//        return  userService.list(start,length,searchText);
//    }
//    public String roleList(int start, int length, String searchText){
//        return  roleService.list(start,length,searchText);
//    }
//    public String resourceList(int start, int length, String searchText,String id){
//        return  dbmsModuleService.list(start,length,searchText,id);
//    }
//
//    public List<ZtreeView> allTree(){
//        return dbmsModuleService.allTree();
//    }
//    public String findById(String id){
//        return userService.findById(id);
//    }
//    public String findAllRole(){
//        return roleService.findAllRole();
//    }
//	@Override
//	public void saveOrUpdateModule(DbmsModule module) {
//		dbmsModuleService.saveOrUpdate(module);
//	}
//	@Override
//	public void deleteModule(String id) {
//		dbmsModuleService.delete(id);
//	}
//	@Override
//	public String findAllModule() {
//		return dbmsModuleService.findAllModule();
//	}
//	@Override
//	public String moduleList(int start, int length, String searchText, String id) {
//		return  dbmsModuleService.list(start,length,searchText,id);
//	}
//}

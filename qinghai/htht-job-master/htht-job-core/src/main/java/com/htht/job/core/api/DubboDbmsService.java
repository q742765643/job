//package com.htht.job.core.api;
//
//import com.htht.job.executor.model.datacategory.ZtreeView;
//import com.htht.job.executor.model.dbms.DbmsModule;
//import com.htht.job.executor.model.dbms.DbmsRole;
//import com.htht.job.executor.model.dbms.DbmsUser;
//
//import java.util.List;
//
///**
// * Created by zzj on 2018/1/24.
// */
//public interface DubboDbmsService {
//
//
//    public String findByUserName(String username);
//    public void saveOrUpdateUser(DbmsUser user);
//    public void deleteUser(String id);
//    public void grantUser(String id, String[] roleIds);
//    public void saveOrUpdateRole(DbmsRole role);
//    public void deleteRole(String id);
//    public void grantModule(String id, String[] moduleIds);
//    public List<ZtreeView> tree(String roleId);
//    public void saveOrUpdateModule(DbmsModule module);
//    public void deleteModule(String id);
//    public String userList(int start, int length, String searchText);
//    public String roleList(int start, int length, String searchText);
//    public String moduleList(int start, int length, String searchText, String id);
//    public String findAllModule();
//    public List<ZtreeView> allTree();
//    public String findById(String id);
//    public String findAllRole();
//}

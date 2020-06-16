package com.htht.job.core.api;

import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dbms.DbmsModule;
import com.htht.job.executor.model.shiro.Resource;
import com.htht.job.executor.model.uus.RegionInfo;

import java.util.List;

/**
 * Created by zzj on 2018/1/24.
 */
public interface DubboShiroService {


    public String findByUserName(String username, SystemType type);

    public void saveOrUpdateUser(Object user, SystemType type);

    public void deleteUser(String id, SystemType type);

    public void grantUser(String id, String[] roleIds, SystemType type);

    public void saveOrUpdateRole(Object role, SystemType type);

    public void deleteRole(String id, SystemType type);

    public void grantResoure(String id, String[] resourceIds, SystemType type);

    public List<ZtreeView> tree(String roleId, SystemType type);

    public void saveOrUpdateResource(Resource resource);

    public void deleteResource(String id);

    public String userList(int start, int length, String searchText, SystemType type);

    public String roleList(int start, int length, String searchText, SystemType type);

    public String resourceList(int start, int length, String searchText, String id);

    public String findAllResouce();

    public List<ZtreeView> allTree(SystemType type);

    public String findById(String id, SystemType type);

    public String findAllRole(SystemType type);

    public String moduleList(int start, int length, String searchText, String id);

    public void saveOrUpdateModule(DbmsModule module);

    public void deleteModule(String id);

    public List<RegionInfo> findAllRegionInfo();
}

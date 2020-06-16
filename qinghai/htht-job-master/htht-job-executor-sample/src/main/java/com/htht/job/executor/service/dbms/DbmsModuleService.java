package com.htht.job.executor.service.dbms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.dbms.DbmsModuleDao;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dbms.DbmsModule;
import com.htht.job.executor.model.dbms.DbmsRole;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;

/**
 * 
 * @author Administrator
 *	2018年9月6日
 */
@Transactional
@Service("dbmsModuleService")
public class DbmsModuleService extends BaseService<DbmsModule>{
    @Autowired
    private DbmsModuleDao dbmsModuleDao;
    @Autowired
    private DbmsRoleService dbmsRoleService;
    @Override
    public BaseDao<DbmsModule> getBaseDao() {
        return dbmsModuleDao;
    }
    @Cacheable(value = "moduleCache", key = "'tree' + #roleId")
    public List<ZtreeView> tree(String roleId) {
        List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        DbmsRole role = dbmsRoleService.getById(roleId);
        Set<DbmsModule> roleModules = role.getModules();
        resulTreeNodes.add(new ZtreeView("0", null, "系统菜单", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id");
        List<DbmsModule> all = dbmsModuleDao.findAll(sort);
        for (DbmsModule module : all) {
            node = new ZtreeView();
            node.setId(module.getId());
            if (module.getParentId() == null) {
                node.setpId("0");
            } else {
                node.setpId(module.getParentId());
            }
            node.setName(module.getName());
            if (roleModules != null && roleModules.contains(module)) {
                node.setChecked(true);
            }
            resulTreeNodes.add(node);
        }
        return resulTreeNodes;
    }
    @Cacheable(value = "moduleCache")
    public List<ZtreeView> allTree(){
        List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        resulTreeNodes.add(new ZtreeView("0", null, "系统菜单", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id");
        List<DbmsModule> all = dbmsModuleDao.findAll(sort);
        for (DbmsModule Module : all) {
            node = new ZtreeView();
            node.setId(Module.getId());
            if (Module.getParentId() == null) {
                node.setpId("0");
            } else {
                node.setpId(Module.getParentId());
            }
            node.setName(Module.getName());
            resulTreeNodes.add(node);
        }
        return resulTreeNodes;

    }
    @CacheEvict(value = "moduleCache")
    public void saveOrUpdate(DbmsModule module) {
    	if(module.getId() != null){
    		DbmsModule dbModule = getById(module.getId());
            dbModule.setName(module.getName());
            dbModule.setSourceKey(module.getSourceKey());
            dbModule.setSourceUrl(module.getSourceUrl());
            dbModule.setIsHide(module.getIsHide());
            dbModule.setIcon(module.getIcon());
            dbModule.setDescription(module.getDescription());
            dbModule.setUpdateTime(new Date());
            dbModule.setParentId(module.getParentId());
            save(dbModule);
        }else{
        	module.setCreateTime(new Date());
        	module.setUpdateTime(new Date());

        	dbmsModuleDao.save(module);
        }
    }
    @CacheEvict(value = "moduleCache")
    public void delete(String id) {
    	dbmsModuleDao.deleteGrant(id);
        super.delete(id);
    }

    public String list(int start, int length, String searchText, String id) {
        SimpleSpecificationBuilder<DbmsModule> builder = new SimpleSpecificationBuilder<DbmsModule>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if(StringUtils.isNotBlank(searchText)){
            builder.add("name", "likeAll", searchText);
        }
        if (!StringUtils.isEmpty(id)) {
        	builder.addOr("parentId","eq",id);
        }
        Page<DbmsModule> page = this.getPage(builder.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONString(maps,SerializerFeature.WriteMapNullValue);
    }
    public String findAllModule(){
        List<DbmsModule> list=super.getAll();
        return JSON.toJSONString(list);
    }
}

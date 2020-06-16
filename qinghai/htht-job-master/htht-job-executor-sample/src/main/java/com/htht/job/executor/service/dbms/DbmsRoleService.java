package com.htht.job.executor.service.dbms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.dbms.DbmsRoleDao;
import com.htht.job.executor.model.dbms.DbmsModule;
import com.htht.job.executor.model.dbms.DbmsRole;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 
 * @author Administrator
 *	2018年9月6日
 */
@Transactional
@Service("dbmsRoleService")
public class DbmsRoleService extends BaseService<DbmsRole> {
    @Autowired
    private DbmsRoleDao dbmsRoleDao;
    @Autowired
    private DbmsModuleService dbmsModuleService;
    @Override
    public BaseDao<DbmsRole> getBaseDao() {
        return dbmsRoleDao;
    }
    public void saveOrUpdate(DbmsRole role) {
    	dbmsRoleDao.save(role);
    }

    public void delete(String id) {
    	DbmsRole role = getById(id);
        Assert.state(!"administrator".equals(role.getRoleKey()),"超级管理员角色不能删除");
        super.delete(id);
    }
    @CacheEvict(value = "resourceCache", key = "'tree' + #id")
    public void grant(String id, String[] modulesIds) {
    	DbmsRole role = getById(id);
        Assert.notNull(role, "角色不存在");

        //Assert.state(!"administrator".equals(role.getRoleKey()),"超级管理员角色不能进行资源分配");
        DbmsModule module;
        Set<DbmsModule> modules = new HashSet<DbmsModule>();
        if(modulesIds != null){
            for (int i = 0; i < modulesIds.length; i++) {
                if(StringUtils.isBlank(modulesIds[i]) || "0".equals(modulesIds[i])){
                    continue;
                }
                String rid = modulesIds[i];
                module = dbmsModuleService.getById(rid);
                modules.add(module);
            }
        }
        role.setModules(modules);
        save(role);
    }

    public String list(int start, int length, String searchText) {
        SimpleSpecificationBuilder<DbmsRole> builder = new SimpleSpecificationBuilder<DbmsRole>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if(StringUtils.isNotBlank(searchText)){
            builder.add("name", "likeAll", searchText);
        }
        Page<DbmsRole> page = this.getPage(builder.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONString(maps);
    }
    public String findAllRole(){
        List<DbmsRole> roles=dbmsRoleDao.findAll();
        return JSON.toJSONStringWithDateFormat(roles,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);

    }
}

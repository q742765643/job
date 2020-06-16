package com.htht.job.executor.service.shiro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.shiro.RoleDao;
import com.htht.job.executor.model.shiro.Resource;
import com.htht.job.executor.model.shiro.Role;
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
 * Created by zzj on 2018/1/24.
 */
@Transactional
@Service("roleService")
public class RoleService extends BaseService<Role> {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private ResourceService resourceService;

    @Override
    public BaseDao<Role> getBaseDao() {
        return roleDao;
    }

    public void saveOrUpdate(Role role) {
        if (role.getId() != null) {
            Role dbRole = getById(role.getId());
            dbRole.setUpdateTime(new Date());
            dbRole.setName(role.getName());
            dbRole.setDescription(role.getDescription());
            dbRole.setUpdateTime(new Date());
            dbRole.setStatus(role.getStatus());
            save(dbRole);
        } else {
            role.setCreateTime(new Date());
            role.setUpdateTime(new Date());
            save(role);
        }
    }

    public void delete(String id) {
        Role role = getById(id);
        Assert.state(!"administrator".equals(role.getRoleKey()), "超级管理员角色不能删除");
        super.delete(id);
    }

    @CacheEvict(value = "resourceCache", key = "'tree' + #id")
    public void grant(String id, String[] resourceIds) {
        Role role = getById(id);
        Assert.notNull(role, "角色不存在");

        //Assert.state(!"administrator".equals(role.getRoleKey()),"超级管理员角色不能进行资源分配");
        Resource resource;
        Set<Resource> resources = new HashSet<Resource>();
        if (resourceIds != null) {
            for (int i = 0; i < resourceIds.length; i++) {
                if (StringUtils.isBlank(resourceIds[i]) || "0".equals(resourceIds[i])) {
                    continue;
                }
                String rid = resourceIds[i];
                resource = resourceService.getById(rid);
                resources.add(resource);
            }
        }
        role.setResources(resources);
        save(role);
    }

    public String list(int start, int length, String searchText) {
        SimpleSpecificationBuilder<Role> builder = new SimpleSpecificationBuilder<Role>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if (StringUtils.isNotBlank(searchText)) {
            builder.add("name", "likeAll", searchText);
        }
        Page<Role> page = this.getPage(builder.generateSpecification(), d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONString(maps);
    }

    public String findAllRole() {
        List<Role> roles = roleDao.findAll();
        return JSON.toJSONStringWithDateFormat(roles, "yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);

    }
}

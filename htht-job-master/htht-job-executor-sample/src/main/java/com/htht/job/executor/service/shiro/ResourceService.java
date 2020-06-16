package com.htht.job.executor.service.shiro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.shiro.ResourceDao;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.shiro.Resource;
import com.htht.job.executor.model.shiro.Role;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
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

import java.util.*;

/**
 * Created by zzj on 2018/1/24.
 */
@Transactional
@Service("resourceService")
public class ResourceService extends BaseService<Resource> {
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private RoleService roleService;

    @Override
    public BaseDao<Resource> getBaseDao() {
        return resourceDao;
    }

    @Cacheable(value = "resourceCache", key = "'tree' + #roleId")
    public List<ZtreeView> tree(String roleId) {
        List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        Role role = roleService.getById(roleId);
        Set<Resource> roleResources = role.getResources();
        resulTreeNodes.add(new ZtreeView("0", null, "系统菜单", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id", "sort");
        List<Resource> all = resourceDao.findAll(sort);
        for (Resource resource : all) {
            node = new ZtreeView();
            node.setId(resource.getId());
            if (resource.getParentId() == null) {
                node.setpId("0");
            } else {
                node.setpId(resource.getParentId());
            }
            node.setName(resource.getName());
            if (roleResources != null && roleResources.contains(resource)) {
                node.setChecked(true);
            }
            resulTreeNodes.add(node);
        }
        return resulTreeNodes;
    }

    @Cacheable(value = "resourceCache")
    public List<ZtreeView> allTree() {
        List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        resulTreeNodes.add(new ZtreeView("0", null, "系统菜单", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id", "sort");
        List<Resource> all = resourceDao.findAll(sort);
        for (Resource resource : all) {
            node = new ZtreeView();
            node.setId(resource.getId());
            if (resource.getParentId() == null) {
                node.setpId("0");
            } else {
                node.setpId(resource.getParentId());
            }
            node.setName(resource.getName());
            resulTreeNodes.add(node);
        }
        return resulTreeNodes;

    }

    @CacheEvict(value = "resourceCache")
    public void saveOrUpdate(Resource resource) {
        if (resource.getId() != null) {
            Resource dbResource = getById(resource.getId());
            dbResource.setUpdateTime(new Date());
            dbResource.setName(resource.getName());
            dbResource.setSourceKey(resource.getSourceKey());
            dbResource.setType(resource.getType());
            dbResource.setSourceUrl(resource.getSourceUrl());
            dbResource.setLevel(resource.getLevel());
            dbResource.setSort(resource.getSort());
            dbResource.setIsHide(resource.getIsHide());
            dbResource.setIcon(resource.getIcon());
            dbResource.setDescription(resource.getDescription());
            dbResource.setUpdateTime(new Date());
            //dbResource.setParent(resource.getParent());
            save(dbResource);
        } else {
            resource.setCreateTime(new Date());
            resource.setUpdateTime(new Date());

            resourceDao.save(resource);
        }
    }

    @CacheEvict(value = "resourceCache")
    public void delete(String id) {
        resourceDao.deleteGrant(id);
        super.delete(id);
    }

    public String list(int start, int length, String searchText, String id) {
        SimpleSpecificationBuilder<Resource> builder = new SimpleSpecificationBuilder<Resource>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if (StringUtils.isNotBlank(searchText)) {
            builder.add("name", "likeAll", searchText);
        }
        if (!StringUtils.isEmpty(id)) {
            builder.addOr("parentId", "eq", id);
        }
        Page<Resource> page = this.getPage(builder.generateSpecification(), d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONString(maps, SerializerFeature.WriteMapNullValue);
    }

    public String findAllResouce() {
        List<Resource> list = super.getAll();
        return JSON.toJSONString(list);
    }
}

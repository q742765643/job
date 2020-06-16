package com.htht.job.executor.service.uus;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.core.api.datacategory.DataCategoryService;
import com.htht.job.executor.dao.uus.UusRoleDao;
import com.htht.job.executor.model.datacategory.DataCategory;
import com.htht.job.executor.model.uus.UusRole;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;

/**
 * Created by zzj on 2018/1/24.
 */
@Transactional
@Service("uusRoleService")
public class UusRoleService extends BaseService<UusRole> {
    @Autowired
    private UusRoleDao roleDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private DataCategoryService categoryService;
    @Override
    public BaseDao<UusRole> getBaseDao() {
        return roleDao;
    }
    public void saveOrUpdate(UusRole role) {
        if(role.getId() != null){
        	UusRole dbRole = getById(role.getId());
            dbRole.setUpdateTime(new Date());
            dbRole.setName(role.getName());
            dbRole.setDescription(role.getDescription());
            dbRole.setUpdateTime(new Date());
            dbRole.setStatus(role.getStatus());
            save(dbRole);
        }else{
            role.setCreateTime(new Date());
            role.setUpdateTime(new Date());
            save(role);
        }
    }

    public void delete(String id) {
    	UusRole role = getById(id);
        Assert.state(!"administrator".equals(role.getRoleKey()),"超级管理员角色不能删除");
        super.delete(id);
    }
    @CacheEvict(value = "resourceCache", key = "'tree' + #id")
    public void grant(String id, String[] categoryIds) {
        UusRole role = getById(id);
        Assert.notNull(role, "角色不存在");

        //Assert.state(!"administrator".equals(role.getRoleKey()),"超级管理员角色不能进行资源分配");
        DataCategory category;
        Set<DataCategory> categorys = new HashSet<DataCategory>();
        if(categoryIds != null){
            for (int i = 0; i < categoryIds.length; i++) {
                if(StringUtils.isBlank(categoryIds[i]) || "0".equals(categoryIds[i])){
                    continue;
                }
                String rid = categoryIds[i];
                category = categoryService.getById(rid);
                categorys.add(category);
            }
        }
        role.setCategory(categorys);
        save(role);
    }

    public String list(int start, int length, String searchText) {
        SimpleSpecificationBuilder<UusRole> builder = new SimpleSpecificationBuilder<UusRole>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if(StringUtils.isNotBlank(searchText)){
            builder.add("name", "likeAll", searchText);
        }
        Page<UusRole> page = this.getPage(builder.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONString(maps);
    }
    public String findAllRole(){
        List<UusRole> roles=roleDao.findAll();
        return JSON.toJSONStringWithDateFormat(roles,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);

    }
}

package com.htht.job.executor.service.uus;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.uus.UusUserDao;
import com.htht.job.executor.model.shiro.User;
import com.htht.job.executor.model.uus.UusRole;
import com.htht.job.executor.model.uus.UusUser;
import com.htht.job.executor.util.MD5Utils;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;

/**
 * Created by zzj on 2018/1/24.
 */
@Transactional
@Service("uusUserService")
public class UusUserService  extends BaseService<UusUser>{
    @Autowired
    private UusUserDao userDao;
    @Autowired
    private UusRoleService roleService;
    @Override
    public BaseDao<UusUser> getBaseDao() {
        return userDao;
    }

    public String findByUserName(String username) {
        User user= userDao.findByUserName(username);
        return JSON.toJSONStringWithDateFormat(user,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);
    }
    public String findById(String id) {
    	UusUser user=userDao.getOne(id);
        return JSON.toJSONString(user);
    }

    public void saveOrUpdate(UusUser user) {
        if(user.getId() != null){
        	UusUser dbUser = this.getById(user.getId());
            dbUser.setNickName(user.getNickName());
            dbUser.setSex(user.getSex());
            dbUser.setBirthday(user.getBirthday());
            dbUser.setTelephone(user.getTelephone());
            dbUser.setEmail(user.getEmail());
            dbUser.setAddress(user.getAddress());
            if(!userDao.getOne(user.getId()).getPassword().equals(user.getPassword())){
                dbUser.setPassword(MD5Utils.md5(user.getPassword()==null?"111111":user.getPassword()));
            }else {
                dbUser.setPassword(userDao.getOne(user.getId()).getPassword());
            }
            dbUser.setLocked(user.getLocked());
            dbUser.setDescription(user.getDescription());
            dbUser.setRegion(user.getRegion());
            dbUser.setUpdateTime(new Date());
            this.save(dbUser);
        }else{
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setDeleteStatus(0);
            user.setPassword(MD5Utils.md5(user.getPassword()==null?"111111":user.getPassword()));
            save(user);
        }
    }
    public void delete(String id) {
    	UusUser user = getById(id);
        Assert.state(!"admin".equals(user.getUserName()),"超级管理员用户不能删除");
        super.delete(id);
    }
    public void grant(String id, String[] roleIds) {
        UusUser user = getById(id);
        Assert.notNull(user, "用户不存在");
        Assert.state(!"admin".equals(user.getUserName()),"超级管理员用户不能修改管理角色");
        UusRole role;
        Set<UusRole> roles = new HashSet<UusRole>();
        if(roleIds != null){
            for (int i = 0; i < roleIds.length; i++) {
                String rid = roleIds[i];
                role = roleService.getById(rid);
                roles.add(role);
            }
        }
        user.setRoles(roles);
        save(user);
    }

    public String list(int start, int length, String searchText) {
        SimpleSpecificationBuilder<UusUser> builder = new SimpleSpecificationBuilder<UusUser>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if(StringUtils.isNotBlank(searchText)){
            builder.add("nickName", "likeAll", searchText);
        }
        Page<UusUser> page = this.getPage(builder.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONStringWithDateFormat(maps,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);
    }
}

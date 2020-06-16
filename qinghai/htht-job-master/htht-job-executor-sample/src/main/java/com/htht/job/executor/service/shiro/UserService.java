package com.htht.job.executor.service.shiro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.shiro.UserDao;
import com.htht.job.executor.model.shiro.Role;
import com.htht.job.executor.model.shiro.User;
import com.htht.job.executor.util.MD5Utils;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
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

import java.util.*;

/**
 * Created by zzj on 2018/1/24.
 */
@Transactional
@Service("userService")
public class UserService  extends BaseService<User>{
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleService roleService;
    @Override
    public BaseDao<User> getBaseDao() {
        return userDao;
    }

    public String findByUserName(String username) {
        User user= userDao.findByUserName(username);
        return JSON.toJSONStringWithDateFormat(user,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);
    }
    public String findById(String id) {
        User user=userDao.getOne(id);
        return JSON.toJSONString(user);
    }

    public void saveOrUpdate(User user) {
        if(user.getId() != null){
            User dbUser = this.getById(user.getId());
            dbUser.setNickName(user.getNickName());
            dbUser.setSex(user.getSex());
            dbUser.setBirthday(user.getBirthday());
            dbUser.setTelephone(user.getTelephone());
            dbUser.setEmail(user.getEmail());
            dbUser.setAddress(user.getAddress());
            user.setPassword(MD5Utils.md5(user.getPassword()==null?"111111":user.getPassword()));
            dbUser.setLocked(user.getLocked());
            dbUser.setDescription(user.getDescription());
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
        User user = getById(id);
        Assert.state(!"admin".equals(user.getUserName()),"超级管理员用户不能删除");
        super.delete(id);
    }
    public void grant(String id, String[] roleIds) {
        User user = getById(id);
        Assert.notNull(user, "用户不存在");
        Assert.state(!"admin".equals(user.getUserName()),"超级管理员用户不能修改管理角色");
        Role role;
        Set<Role> roles = new HashSet<Role>();
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
        SimpleSpecificationBuilder<User> builder = new SimpleSpecificationBuilder<User>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if(StringUtils.isNotBlank(searchText)){
            builder.add("nickName", "likeAll", searchText);
        }
        Page<User> page = this.getPage(builder.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONStringWithDateFormat(maps,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);
    }
}

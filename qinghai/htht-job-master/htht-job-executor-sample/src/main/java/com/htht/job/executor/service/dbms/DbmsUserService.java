package com.htht.job.executor.service.dbms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.dbms.DbmsUserDao;
import com.htht.job.executor.model.dbms.DbmsRole;
import com.htht.job.executor.model.dbms.DbmsUser;
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
 * 
 * @author Administrator
 *	2018年9月6日
 */
@Transactional
@Service("dbmsUserService")
public class DbmsUserService  extends BaseService<DbmsUser>{
    @Autowired
    private DbmsUserDao dbmsUserDao;
    @Autowired
    private DbmsRoleService roleService;
    @Override
    public BaseDao<DbmsUser> getBaseDao() {
        return dbmsUserDao;
    }

    public String findByUserName(String username) {
    	DbmsUser user= dbmsUserDao.findByUserName(username);
        return JSON.toJSONStringWithDateFormat(user,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);
    }
    public String findById(String id) {
    	DbmsUser user=dbmsUserDao.getOne(id);
        return JSON.toJSONString(user);
    }

    public void saveOrUpdate(DbmsUser user) {
        if(user.getId() != null){
        	DbmsUser dbUser = this.getById(user.getId());
            dbUser.setApplytype(user.getApplytype());
            dbUser.setBirthday(user.getBirthday());
            dbUser.setCareer(user.getCareer());
            dbUser.setEmail(user.getEmail());
            dbUser.setFax(user.getFax());
            dbUser.setLocked(user.getLocked());
            dbUser.setPhone(user.getPhone());
            dbUser.setSex(user.getSex());
            dbUser.setSsdw(user.getSsdw());
            dbUser.setStatus(user.getStatus());
            dbUser.setNickName(user.getNickName());
            dbUser.setUserName(user.getUserName());
            if(!dbmsUserDao.getOne(user.getId()).getPassword().equals(user.getPassword())){
                dbUser.setPassword(MD5Utils.md5(user.getPassword()==null?"111111":user.getPassword()));
            }else {
                dbUser.setPassword(dbmsUserDao.getOne(user.getId()).getPassword());
            }
            dbUser.setWorkunit(user.getWorkunit());
            dbUser.setDescription(user.getDescription());
            dbUser.setCreateTime(new Date());
            //dbUser.setPassword(MD5Utils.md5(user.getPassword()));
            this.save(dbUser);
        }else{
            user.setStatus("0");
            user.setPassword(MD5Utils.md5(user.getPassword()==null?"111111":user.getPassword()));
            save(user);
        }
    }
    public void delete(String id) {
    	DbmsUser user = getById(id);
        Assert.state(!"admin".equals(user.getUserName()),"超级管理员用户不能删除");
        super.delete(id);
    }
    public void grant(String id, String[] roleIds) {
    	DbmsUser user = getById(id);
        Assert.notNull(user, "用户不存在");
        Assert.state(!"admin".equals(user.getUserName()),"超级管理员用户不能修改管理角色");
        DbmsRole role;
        Set<DbmsRole> roles = new HashSet<DbmsRole>();
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
        SimpleSpecificationBuilder<DbmsUser> builder = new SimpleSpecificationBuilder<DbmsUser>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if(StringUtils.isNotBlank(searchText)){
            builder.add("nickName", "likeAll", searchText);
        }
        Page<DbmsUser> page = this.getPage(builder.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONStringWithDateFormat(maps,"yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);
    }
}

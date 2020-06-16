package com.htht.job.admin.core.shiro;


import com.alibaba.fastjson.JSON;
import com.htht.job.admin.core.shiro.common.UpmsConstant;
import com.htht.job.admin.core.util.MD5Utils;
import com.htht.job.admin.core.util.PropertiesFileUtil;
import com.htht.job.core.api.DubboShiroService;
import com.htht.job.core.enums.SystemType;
import com.htht.job.executor.model.shiro.Resource;
import com.htht.job.executor.model.shiro.Role;
import com.htht.job.executor.model.shiro.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UpmsRealm extends AuthorizingRealm {

    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "123456";
    public static final String CURRENT_USERNAME = "username";

    @Autowired
    private DubboShiroService dubboShiroService;

    /*
       * 授权
       */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        String dbUserString = dubboShiroService.findByUserName(username, SystemType.SYSTEM_TYPE_CLUSTER);
        User dbUser = JSON.parseObject(dbUserString, User.class);
        Set<String> roleNames = new HashSet<String>();
        Set<String> permissions = new HashSet<String>();
        Set<Role> roles = dbUser.getRoles();
        for (Role role : roles) {
            Set<Resource> resources = role.getResources();
            for (Resource resource : resources) {
                permissions.add(resource.getSourceKey());

            }
            roleNames.add(role.getRoleKey());
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roleNames);
        info.setStringPermissions(permissions);
        return info;
    }

    /*
     * 登录验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) throws AuthenticationException {

        String username = (String)authcToken.getPrincipal();
        String password = new String((char[])authcToken.getCredentials());

        //单点登陆认证 client无密认证
        String upmsType = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).get("master.upms.type");
        if ("client".equals(upmsType)) {
            return new SimpleAuthenticationInfo(username, password, getName());
        }

        String userString = dubboShiroService.findByUserName(username, SystemType.SYSTEM_TYPE_CLUSTER);
        User user = JSON.parseObject(userString, User.class);
        if (null == user) {
            throw new UnknownAccountException();
        }
        if (!user.getPassword().equals(MD5Utils.md5(password))) {
            throw new IncorrectCredentialsException();
        }
        if (user.getLocked() == 1) {
            throw new LockedAccountException();
        }
        SecurityUtils.getSubject().getSession().setAttribute(CURRENT_USERNAME, username);

        return new SimpleAuthenticationInfo(username, password, getName());
    }
}

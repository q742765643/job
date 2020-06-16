package com.htht.job.uus.common;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.htht.job.uus.model.User;
import com.htht.job.uus.service.UserService;
import com.htht.job.uus.util.MD5Utils;

public class UserRealm extends AuthorizingRealm
{
	@Resource
	private UserService userService;
	/*
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
	{

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		return info;
	}

	/*
	 * 登录验证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException
	{
		
		String username = (String) authcToken.getPrincipal();
		String password = new String((char[]) authcToken.getCredentials());
		
		User user = userService.findUserInfoByUsername(username);

		// 账号不存在
		if (null == user)
		{
			throw new UnknownAccountException("账号或密码不正确");
		}
		// 密码错误
		if (!MD5Utils.md5(password).equals(user.getPassword()))
		{
			throw new IncorrectCredentialsException("账号或密码不正确");
		}
		// 账号锁定
		if (user.getLocked() == 1)
		{
			throw new LockedAccountException("账号已被锁定,请联系管理员");
		}

		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());

		return info;
	}
}
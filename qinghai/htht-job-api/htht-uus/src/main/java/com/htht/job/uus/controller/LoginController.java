package com.htht.job.uus.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.htht.job.uus.common.Consts;
import com.htht.job.uus.model.RegionInfo;
import com.htht.job.uus.model.DictCode;
import com.htht.job.uus.model.User;
import com.htht.job.uus.model.viewModel.UserView;
import com.htht.job.uus.service.RegionInfoService;
import com.htht.job.uus.service.DictCodeService;
import com.htht.job.uus.service.UserService;
import com.htht.job.uus.util.ResponseModel;

/**
 * 
 * @author yuguoqing
 * @Date 2018年5月9日 上午11:31:12
 *
 *
 */
@Controller
@RequestMapping("/sysUser")
public class LoginController
{
	@Autowired
	private UserService userService;
	
	@Autowired
	private RegionInfoService regionInfoService;
	
	@Autowired
	private DictCodeService dictCodeService;
	
	/**
	 * 跳转登录
	 * 
	 * @return
	 */
	@RequestMapping("/toLogin")
	@ResponseBody
	public ResponseModel toLogin()
	{
		ResponseModel response = new ResponseModel();
		response.setStatus(Consts.ResposeStatus.STATUS_NOAUTHENTION);
		response.setCode(Consts.ResponseCode.CODE_NOAUTHENTION);
		return response;
	}
	//用户退出
	@RequestMapping("/logout")
	public String logout()throws Exception{

	//重定向到商品查询页面
	return "redirect:http://10.181.23.78/";

	}

	/**
	 * 用户登录
	 * 
	 * @return
	 */
	@RequestMapping("/login")
	@ResponseBody
	public ResponseModel login(@RequestParam(value="userName") String userName, @RequestParam(value="password") String password)
	{
		ResponseModel response = new ResponseModel();
		response.setStatus(Consts.ResposeStatus.STATUS_NOAUTHENTION);
		response.setCode(Consts.ResponseCode.CODE_NOAUTHENTION);
		UserView userView = new UserView();
		try
		{
			UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
			Subject currentUser = SecurityUtils.getSubject();
			currentUser.login(token);// 验证角色和权限
			User user = userService.findUserInfoByUsername(userName);
			RegionInfo regionInfo = null;
			if (null != user.getRegionId() && !"".equals(user.getRegionId())) {
				regionInfo = regionInfoService.findRegionInfoByRegionId(user.getRegionId());
			}
			userView.setId(user.getId());
			userView.setUserName(userName);
			userView.setNickName(user.getNickName());
			userView.setRegionId(user.getRegionId());
			userView.setRegionName(regionInfo.getAreaName());
			userView.setRegionLevel(regionInfo.getRegionLevel());
			userView.setLatitudeCenter(regionInfo.getLatitudeCenter());
			userView.setLatitudeMax(regionInfo.getLatitudeMax());
			userView.setLatitudeMin(regionInfo.getLatitudeMin());
			userView.setLongitudeCenter(regionInfo.getLongitudeCenter());
			userView.setLongitudeMax(regionInfo.getLongitudeMax());
			userView.setLongitudeMin(regionInfo.getLongitudeMin());
		} catch (AuthenticationException e)
		{
			System.out.println(e);
			response.setData("用户名或密码错误！");
			return response;
		}
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setCode(Consts.ResponseCode.CODE_OK);
		response.setData(userView);
		return response;
	}
	
	/**
	 * 获取uus标题
	 * 
	 * @return
	 */
	@RequestMapping("/getTitle")
	@ResponseBody
	public ResponseModel getUUSTitle(@RequestParam("dictCode") String dictCode)
	{
		
		ResponseModel response = new ResponseModel();
		DictCode title = dictCodeService.getNameByDictCode(dictCode);
		response.setData(title);
		response.setStatus(Consts.ResposeStatus.STATUS_NOAUTHENTION);
		response.setCode(Consts.ResponseCode.CODE_NOAUTHENTION);
		
		return response;
	}


}

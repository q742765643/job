package com.htht.job.uus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.htht.job.uus.common.Consts;
import com.htht.job.uus.model.AchTabOrderEnum;
import com.htht.job.uus.model.AchTableInfo;
import com.htht.job.uus.model.AchTableInfoEnum;
import com.htht.job.uus.model.AchievementInfo;
import com.htht.job.uus.model.AchievementMenu;
import com.htht.job.uus.model.ProductTree;
import com.htht.job.uus.model.RegionInfo;
import com.htht.job.uus.service.AchievementInfoService;
import com.htht.job.uus.service.AchievementMenuService;
import com.htht.job.uus.service.AchievementUserService;
import com.htht.job.uus.util.ResponseModel;


@Controller
@RequestMapping("/achievement")
public class AchievementController {
	
	private final static String UserInfo = "UserInfo";
	private final static String SmartAgri = "SmartAgri";
	private final static String GSDServer = "GSDServer";
	
	private static Map<String, String> specialMenu;
	
	static {
		specialMenu = new HashMap<String, String>();
		specialMenu.put("GSDServer-errt4sdead2fv343", "雪深监测_Terra");
		specialMenu.put("GSDServer-gbhh4sdead2fv343", "雪深监测_F3B");
		specialMenu.put("GSDServer-mkjuu4sdead2fv343", "雪深监测_F3C");
		specialMenu.put("GSDServer-vf212ddasad2fv343", "雪深监测_Aqua");
		specialMenu.put("GSDServer-bnhh6dead2fv343", "雪盖监测_F3C");
		specialMenu.put("GSDServer-gvv55ead2fv343", "雪盖监测_F3B");
		specialMenu.put("GSDServer-yhui88dead2fv343", "H8雪盖监测");
		specialMenu.put("GSDServer-aa434555ead2fv343", "农区干土层产品");
		specialMenu.put("GSDServer-loppkk6dead2fv343", "土壤重量含水率");
		specialMenu.put("GSDServer-556dfdd2fv343", "牧草1年距平");
		specialMenu.put("GSDServer-annjkkead2fv343", "牧草5年距平");
		specialMenu.put("GSDServer-lplilu6734dss", "牧草10年距平");
		specialMenu.put("GSDServer-y667jjad2fv343", "牧草产量");
	}
	
	@Autowired
	private AchievementMenuService achievementMenuService;
	
	@Autowired
	private AchievementInfoService achievementInfoService;
	
	@Autowired
	private AchievementUserService achievementUserService;

	@RequestMapping("/menuTree")
	@ResponseBody
	public ResponseModel menuTree() {
		List<AchievementMenu> menuList = achievementMenuService.queryMenus(null);
		menuList = buildByRecursive(menuList);
		ResponseModel response = new ResponseModel();
		response.setData(menuList);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setCode(Consts.ResponseCode.CODE_OK);
		return response;
	}
	
	@RequestMapping("/queryImg")
	@ResponseBody
	public ResponseModel queryImg(String menuId,String regionId) {
		List menuList = new ArrayList();
		ResponseModel response = new ResponseModel();
		if(menuId.startsWith(UserInfo)){
			//用户信息菜单
			menuList = achievementUserService.queryAchUser();
			
		}else if(menuId.startsWith(SmartAgri)){
			//智慧农业数据库菜单
			menuList = achievementInfoService.queryAchTab(AchTableInfoEnum.getName(menuId),AchTabOrderEnum.getName(menuId));
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("tabData", menuList);
			map.put("tabInfo", AchTableInfo.titleMap.get(menuId));
			response.setData(map);
			response.setStatus(Consts.ResposeStatus.STATUS_OK);
			response.setCode(Consts.ResponseCode.CODE_OK);
			return response;
		}else if(menuId.startsWith(GSDServer)){
			//草地积雪干旱菜单
			for(String m:specialMenu.keySet()){
				if(m.equals(menuId)){
					menuList = achievementInfoService.queryNewest(specialMenu.get(m), regionId);
				}
			}
		}else{
			menuList = achievementInfoService.queryImgInfo(menuId, regionId);
		}
		
		response.setData(menuList);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setCode(Consts.ResponseCode.CODE_OK);
		return response;
	}
	
	@RequestMapping("/queryAllImg")
	@ResponseBody
	public ResponseModel queryAllImg(String menuId) {
		List<AchievementInfo> menuList = achievementInfoService.queryImgInfo(menuId,null);
		ResponseModel response = new ResponseModel();
		response.setData(menuList);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setCode(Consts.ResponseCode.CODE_OK);
		return response;
	}
	
	
	@RequestMapping("/getRegionByMenuId")
	@ResponseBody
	public ResponseModel findRegionInfoByRegionLevelAndParentId(String menuId) {
		ResponseModel response = new ResponseModel();
		List<RegionInfo> regionInfoList = new ArrayList<RegionInfo>();
		
		for(String m:specialMenu.keySet()){
			if(m.equals(menuId)){
				menuId = "QHS";
			}
		}
		regionInfoList = achievementInfoService.findRegionInfosByMenuId(menuId);
		response.setCode(Consts.ResponseCode.CODE_OK);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setData(regionInfoList);
		return response;
	}
	
	
	public static List<AchievementMenu> buildByRecursive(List<AchievementMenu> treeNodes)
	{
		List<AchievementMenu> trees = new ArrayList<AchievementMenu>();
		for (AchievementMenu treeNode : treeNodes)
		{
			if (AchievementMenu.DefaultPid.equals(treeNode.getParentId()))
			{
				trees.add(findChildren(treeNode, treeNodes));
			}
		}
		return trees;
	}
	
	public static AchievementMenu findChildren(AchievementMenu treeNode, List<AchievementMenu> treeNodes)
	{
		for (AchievementMenu it : treeNodes)
		{
			if (treeNode.getId().equals(it.getParentId()))
			{
				if (treeNode.getSubTree() == null)
				{
					treeNode.setSubTree(new ArrayList<AchievementMenu>());
				}
				treeNode.getSubTree().add(findChildren(it, treeNodes));
			}
		}
		return treeNode;
	}
	
}

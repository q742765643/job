package com.htht.job.uus.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.RegionInfoDao;
import com.htht.job.uus.model.RegionInfo;
import com.htht.job.uus.service.RegionInfoService;

/**
 * @ClassName: RegionInfoServiceImpl
 * @Description: 行政区划
 * @author chensi
 * @date 2018年5月11日
 * 
 */
@Service
public class RegionInfoServiceImpl implements RegionInfoService {

	@Resource
	public RegionInfoDao regionInfoDao;

	@Override
	public List<RegionInfo> findRegionInfosByParentId(String parentRegionId) {
		
		List<RegionInfo> regionInfos = regionInfoDao.selectRegionInfosByParentRegionId(parentRegionId);
		List<RegionInfo> regionInfoList = buildByRecursive(regionInfos,parentRegionId);
		return regionInfoList;
	}
	
	@Override
	public RegionInfo findRegionInfoByRegionId(String regionId) {
		
		return regionInfoDao.selectRegionInfoByRegionId(regionId);
	}
	
	/**
	 * 使用递归方法建树
	 * 
	 * @param treeNodes
	 * @return
	 */
	public static List<RegionInfo> buildByRecursive(List<RegionInfo> treeNodes,String parentRegionId)
	{
		if (null == parentRegionId || "".equals(parentRegionId)) {
			parentRegionId = "0";
		}
		List<RegionInfo> trees = new ArrayList<RegionInfo>();
		for (RegionInfo treeNode : treeNodes)
		{
			String regionId = treeNode.getRegionId();
			if ("0".equals(parentRegionId)) {
				regionId = treeNode.getParentRegionId();
			}
			
			if (parentRegionId.equals(regionId))
			{
				trees.add(findChildren(treeNode, treeNodes));
			}
		}
		return trees;
	}
	
	/**
	 * 递归查找子节点
	 * 
	 * @param treeNodes
	 * @return
	 */
	public static RegionInfo findChildren(RegionInfo treeNode, List<RegionInfo> treeNodes)
	{
		for (RegionInfo it : treeNodes)
		{
			if (treeNode.getRegionId().equals(it.getParentRegionId()))
			{
				if (treeNode.getSubRegion() == null)
				{
					treeNode.setSubRegion(new ArrayList<RegionInfo>());
				}
				treeNode.getSubRegion().add(findChildren(it, treeNodes));
			}
		}
		return treeNode;
	}


}

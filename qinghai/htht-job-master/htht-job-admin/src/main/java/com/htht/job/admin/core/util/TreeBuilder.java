package com.htht.job.admin.core.util;

import java.util.ArrayList;
import java.util.List;

import com.htht.job.executor.model.uus.RegionInfo;

/**
* @ClassName: TreeBuilder
* @Description: TODO(行政区划目录)
* @author mao_r
* @date 2018年10月26日
*
*/

public class TreeBuilder {
	
	/**
	 * 使用递归方法建树
	 * 
	 * @param treeNodes
	 * @return
	 */
	public static List<RegionInfo> buildByRecursive(List<RegionInfo> treeNodes)
	{
		List<RegionInfo> trees = new ArrayList<RegionInfo>();
		for (RegionInfo treeNode : treeNodes)
		{
			if ("0".equals(treeNode.getParentRegionId()))
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

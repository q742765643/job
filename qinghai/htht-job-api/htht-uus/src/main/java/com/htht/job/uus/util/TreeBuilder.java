package com.htht.job.uus.util;

import java.util.ArrayList;
import java.util.List;

import com.htht.job.uus.model.ProductTree;

public class TreeBuilder
{
	/**
	 * 使用递归方法建树
	 * 
	 * @param treeNodes
	 * @return
	 */
	public static List<ProductTree> buildByRecursive(List<ProductTree> treeNodes)
	{
		List<ProductTree> trees = new ArrayList<ProductTree>();
		for (ProductTree treeNode : treeNodes)
		{
			if ("0".equals(treeNode.getVirtualParentId()))
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
	public static ProductTree findChildren(ProductTree treeNode, List<ProductTree> treeNodes)
	{
		for (ProductTree it : treeNodes)
		{
			if (treeNode.getVirtualId().equals(it.getVirtualParentId()))
			{
				if (treeNode.getSubTree() == null)
				{
					treeNode.setSubTree(new ArrayList<ProductTree>());
				}
				treeNode.getSubTree().add(findChildren(it, treeNodes));
			}
		}
		return treeNode;
	}
	
}

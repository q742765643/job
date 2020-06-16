package com.htht.job.core.api.datacategory;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.datacategory.DataCategory;
import com.htht.job.executor.model.datacategory.ZtreeView;

import java.util.List;
import java.util.Map;

public interface DataCategoryService {
	public List<DataCategory> getTreeNodeById();
	public List<DataCategory> getTreeNodeById(String treeKey);
	public Map<String, Object> pageList(int start,int length,DataCategory dataCategory);
	public DataCategory saveTreeNode(DataCategory dataCategory);
	public ReturnT<String> deleteTreeNode(String id);
	public DataCategory getById(String rid);
	public List<ZtreeView> tree(String roleId);

}

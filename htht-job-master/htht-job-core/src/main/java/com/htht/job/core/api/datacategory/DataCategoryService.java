package com.htht.job.core.api.datacategory;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.datacategory.DataCategoryDTO;
import com.htht.job.executor.model.datacategory.ZtreeView;

import java.util.List;
import java.util.Map;

public interface DataCategoryService {
    public List<DataCategoryDTO> getTreeNodeById();

    public List<DataCategoryDTO> getTreeNodeById(String treeKey);

    public Map<String, Object> pageList(int start, int length, DataCategoryDTO dataCategoryDTO);

    public DataCategoryDTO saveTreeNode(DataCategoryDTO dataCategoryDTO);

    public ReturnT<String> deleteTreeNode(String id);

    public DataCategoryDTO getById(String rid);

    public List<ZtreeView> tree(String roleId);

}

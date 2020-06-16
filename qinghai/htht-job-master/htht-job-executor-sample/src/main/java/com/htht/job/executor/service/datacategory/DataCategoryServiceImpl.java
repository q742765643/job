package com.htht.job.executor.service.datacategory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.api.datacategory.DataCategoryService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.datacategory.DataCategoryDao;
import com.htht.job.executor.model.datacategory.DataCategory;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.uus.UusRole;
import com.htht.job.executor.service.uus.UusRoleService;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;

@Transactional
@Service("dataCategoryService")
public class DataCategoryServiceImpl extends BaseService<DataCategory> implements
		DataCategoryService {
	@Autowired
	private DataCategoryDao dataCategoryDao;
	@Autowired
	private UusRoleService roleService;

	@Override
	public BaseDao<DataCategory> getBaseDao() {
		// TODO Auto-generated method stub
		return dataCategoryDao;
	}

	@Cacheable(value = "datacategory")
	public List<DataCategory> getTreeNodeById() {
		/** =======1=====获取list============ **/
		Sort sort = new Sort(Sort.Direction.ASC, "createTime");

		List<DataCategory> dataCategories = dataCategoryDao.findAll(sort);
		/** =======2=====递归获取子节点========== **/
		List<DataCategory> dataCategories_new = new ArrayList<DataCategory>();
		for (DataCategory dataCategory : dataCategories) {
			if ("0".equals(dataCategory.getParentId())) {
				dataCategories_new.add(getNodes(dataCategory, dataCategories));
			}
		}
		return dataCategories_new;
	}
//	@Cacheable(value = "treeNode")
	public List<DataCategory> getTreeNodeById(String treeKey) {
		/** =======1=====获取list============ **/
		Sort sort = new Sort(Sort.Direction.ASC, "createTime");
		List<DataCategory> dataCategories = dataCategoryDao.findAll(sort);
		/** =======2=====递归获取子节点========== **/
		List<DataCategory> trees = new ArrayList<DataCategory>();
		for (DataCategory dataCategory : dataCategories) {
			if(treeKey.equals(dataCategory.getTreeKey())){
				if ("0".equals(dataCategory.getParentId())) {
					trees.add(getNodes(dataCategory, dataCategories));
				}
			}
		}
		return trees;
	}

	private DataCategory getNodes(DataCategory dataCategory, List<DataCategory> dataCategories) {
		for (DataCategory it : dataCategories) {
			if (dataCategory.getId().equals(it.getParentId())) {
				if (dataCategory.getNodes() == null) {
					dataCategory.setNodes(new ArrayList<DataCategory>());
				}
				dataCategory.getNodes().add(getNodes(it, dataCategories));
			}

		}

		return dataCategory;
	}
	
	/**分页查询产品
	 * @param start
	 * @param length
	 * @param dataCategory
	 * @return
	 */
	public Map<String, Object> pageList(int start, int length,
			DataCategory dataCategory) {
		Page<DataCategory> page = this.findPageTreeNode(start, length,
                dataCategory);
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", page.getTotalElements()); // 总记录数
		maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
		maps.put("data", page.getContent()); // 分页列表
		return maps;
	}
	
	
	public Page<DataCategory> findPageTreeNode(int start, int length,
                                               DataCategory dataCategory){
			Sort sort = new Sort(Sort.Direction.DESC, "createTime");
			PageRequest d = new PageRequest(start, length, sort);
			Page<DataCategory> page = this.getPage(
					getWhereClause(dataCategory), d);
		 
			return page;
	}
	private Specification<DataCategory> getWhereClause(
			DataCategory dataCategory) {
		SimpleSpecificationBuilder<DataCategory> specification=new SimpleSpecificationBuilder<DataCategory>();
		if (!StringUtils.isEmpty(dataCategory.getText())) {
			specification.add("text","likeAll", dataCategory.getText());
		}
		if (!StringUtils.isEmpty(dataCategory.getMenuId())) {
			specification.add("menuId","likeAll", dataCategory.getMenuId());
		}
		if (!StringUtils.isEmpty(dataCategory.getId())) {
			specification.addOr("id","eq", dataCategory.getId());
		}
		if (!StringUtils.isEmpty(dataCategory.getTreeKey())) {
			specification.addOr("treeKey","eq", dataCategory.getTreeKey());
		}

		return specification.generateSpecification();
	}
	@CacheEvict(value = "datacategory")
	//@CachePut(value = "treeNode",key = "#root.caches[0].name + ':' + #treeNode.id")
	public DataCategory saveTreeNode(DataCategory dataCategory){
		DataCategory newDataCategory =new DataCategory();
		if(!StringUtils.isEmpty(dataCategory.getId())){
			DataCategory yDataCategory =this.getById(dataCategory.getId());
			//yTreeNode.setGdbpath(treeNode.getGdbpath());
			//yTreeNode.setStoragepath(treeNode.getStoragepath());
			yDataCategory.setMenu(dataCategory.getMenu());
			yDataCategory.setMenuId(dataCategory.getMenuId());
			yDataCategory.setUpdateTime(new Date());
			yDataCategory.setText(dataCategory.getText());
			yDataCategory.setTreeKey(dataCategory.getTreeKey());
			yDataCategory.setIconPath(dataCategory.getIconPath());
			newDataCategory =this.save(yDataCategory);

		}else{
			if(StringUtils.isEmpty(dataCategory.getParentId())){
				dataCategory.setParentId("0");
			}
			dataCategory.setCreateTime(new Date());
			newDataCategory =this.save(dataCategory);
		}
		return newDataCategory;
	}
	@CacheEvict(value = "datacategory")
	public ReturnT<String> deleteTreeNode(String id) {
		try {
			dataCategoryDao.deleteGrant(id);
			dataCategoryDao.delete(id);
			return ReturnT.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}

	@Override
	public List<ZtreeView> tree(String roleId) {
		List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        UusRole role = roleService.getById(roleId);
        Set<DataCategory> roleCategorys = role.getCategory();
        resulTreeNodes.add(new ZtreeView("0", null, "产品列表", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id");
        List<DataCategory> all = dataCategoryDao.findAll(sort);
        for (DataCategory category : all) {
        	if("product".equals(category.getTreeKey())) {
        		node = new ZtreeView();
                node.setId(category.getId());
                if (category.getParentId() == null) {
                    node.setpId("0");
                } else {
                    node.setpId(category.getParentId());
                }
                node.setName(category.getText());
                if (roleCategorys != null && roleCategorys.contains(category)) {
                    node.setChecked(true);
                }
                resulTreeNodes.add(node);
        	}
        }
        return resulTreeNodes;
	}

}

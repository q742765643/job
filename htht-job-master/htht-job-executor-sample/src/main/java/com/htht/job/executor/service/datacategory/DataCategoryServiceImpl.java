package com.htht.job.executor.service.datacategory;

import com.htht.job.core.api.datacategory.DataCategoryService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.datacategory.DataCategoryDao;
import com.htht.job.executor.model.datacategory.DataCategoryDTO;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.uus.UusRole;
import com.htht.job.executor.service.uus.UusRoleService;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
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

import java.util.*;

@Transactional
@Service("dataCategoryService")
public class DataCategoryServiceImpl extends BaseService<DataCategoryDTO> implements
        DataCategoryService {
    @Autowired
    private DataCategoryDao dataCategoryDao;
    @Autowired
    private UusRoleService roleService;

    @Override
    public BaseDao<DataCategoryDTO> getBaseDao() {
        return dataCategoryDao;
    }

    @Cacheable(value = "datacategory")
    public List<DataCategoryDTO> getTreeNodeById() {
        /** =======1=====获取list============ **/
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");

        List<DataCategoryDTO> dataCategories = dataCategoryDao.findAll(sort);
        /** =======2=====递归获取子节点========== **/
        List<DataCategoryDTO> dataCategories_new = new ArrayList<DataCategoryDTO>();
        for (DataCategoryDTO dataCategoryDTO : dataCategories) {
            if ("0".equals(dataCategoryDTO.getParentId())) {
                dataCategories_new.add(getNodes(dataCategoryDTO, dataCategories));
            }
        }
        return dataCategories_new;
    }

    //	@Cacheable(value = "treeNode")
    public List<DataCategoryDTO> getTreeNodeById(String treeKey) {
        /** =======1=====获取list============ **/
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        List<DataCategoryDTO> dataCategories = dataCategoryDao.findAll(sort);
        /** =======2=====递归获取子节点========== **/
        List<DataCategoryDTO> trees = new ArrayList<DataCategoryDTO>();
        for (DataCategoryDTO dataCategoryDTO : dataCategories) {
            if (treeKey.equals(dataCategoryDTO.getTreeKey())) {
                if ("0".equals(dataCategoryDTO.getParentId())) {
                    trees.add(getNodes(dataCategoryDTO, dataCategories));
                }
            }
        }
        return trees;
    }

    private DataCategoryDTO getNodes(DataCategoryDTO dataCategoryDTO, List<DataCategoryDTO> dataCategories) {
        for (DataCategoryDTO it : dataCategories) {
            if (dataCategoryDTO.getId().equals(it.getParentId())) {
                if (dataCategoryDTO.getNodes() == null) {
                    dataCategoryDTO.setNodes(new ArrayList<DataCategoryDTO>());
                }
                dataCategoryDTO.getNodes().add(getNodes(it, dataCategories));
            }

        }

        return dataCategoryDTO;
    }

    /**
     * 分页查询产品
     *
     * @param start
     * @param length
     * @param dataCategoryDTO
     * @return
     */
    public Map<String, Object> pageList(int start, int length,
                                        DataCategoryDTO dataCategoryDTO) {
        Page<DataCategoryDTO> page = this.findPageTreeNode(start, length,
                dataCategoryDTO);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return maps;
    }


    public Page<DataCategoryDTO> findPageTreeNode(int start, int length,
                                                  DataCategoryDTO dataCategoryDTO) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        Page<DataCategoryDTO> page = this.getPage(
                getWhereClause(dataCategoryDTO), d);

        return page;
    }

    private Specification<DataCategoryDTO> getWhereClause(
            DataCategoryDTO dataCategoryDTO) {
        SimpleSpecificationBuilder<DataCategoryDTO> specification = new SimpleSpecificationBuilder<DataCategoryDTO>();
        if (!StringUtils.isEmpty(dataCategoryDTO.getText())) {
            specification.add("text", "likeAll", dataCategoryDTO.getText());
        }
        if (!StringUtils.isEmpty(dataCategoryDTO.getMenuId())) {
            specification.add("menuId", "likeAll", dataCategoryDTO.getMenuId());
        }
        if (!StringUtils.isEmpty(dataCategoryDTO.getId())) {
            specification.addOr("id", "eq", dataCategoryDTO.getId());
        }
        if (!StringUtils.isEmpty(dataCategoryDTO.getTreeKey())) {
            specification.addOr("treeKey", "eq", dataCategoryDTO.getTreeKey());
        }

        return specification.generateSpecification();
    }

    @CacheEvict(value = "datacategory")
    //@CachePut(value = "treeNode",key = "#root.caches[0].name + ':' + #treeNode.id")
    public DataCategoryDTO saveTreeNode(DataCategoryDTO dataCategoryDTO) {
        DataCategoryDTO newDataCategoryDTO = new DataCategoryDTO();
        if (!StringUtils.isEmpty(dataCategoryDTO.getId())) {
            DataCategoryDTO yDataCategoryDTO = this.getById(dataCategoryDTO.getId());
            //yTreeNode.setGdbpath(treeNode.getGdbpath());
            //yTreeNode.setStoragepath(treeNode.getStoragepath());
            yDataCategoryDTO.setMenu(dataCategoryDTO.getMenu());
            yDataCategoryDTO.setMenuId(dataCategoryDTO.getMenuId());
            yDataCategoryDTO.setUpdateTime(new Date());
            yDataCategoryDTO.setText(dataCategoryDTO.getText());
            yDataCategoryDTO.setTreeKey(dataCategoryDTO.getTreeKey());
            yDataCategoryDTO.setIconPath(dataCategoryDTO.getIconPath());
            newDataCategoryDTO = this.save(yDataCategoryDTO);

        } else {
            if (StringUtils.isEmpty(dataCategoryDTO.getParentId())) {
                dataCategoryDTO.setParentId("0");
            }
            dataCategoryDTO.setCreateTime(new Date());
            newDataCategoryDTO = this.save(dataCategoryDTO);
        }
        return newDataCategoryDTO;
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
        Set<DataCategoryDTO> roleCategorys = role.getCategory();
        resulTreeNodes.add(new ZtreeView("0", null, "产品列表", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id");
        List<DataCategoryDTO> all = dataCategoryDao.findAll(sort);
        for (DataCategoryDTO category : all) {
            if ("product".equals(category.getTreeKey())) {
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

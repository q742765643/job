package com.htht.job.executor.service.product;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.algorithm.AtomicAlgorithmDao;
import com.htht.job.executor.dao.datacategory.DataCategoryDao;
import com.htht.job.executor.dao.downupload.AlgoNodeRelationDao;
import com.htht.job.executor.dao.product.ProductDao;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.datacategory.DataCategoryDTO;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfoDTO;
import com.htht.job.executor.model.product.ProductDTO;
import com.htht.job.executor.model.uus.UusRole;
import com.htht.job.executor.service.uus.UusRoleService;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zzj on 2018/1/1.
 */
@Transactional
@Service
public class ProductServiceImpl extends BaseService<ProductDTO> implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private DataCategoryDao categoryDao;
    @Autowired
    private AlgoNodeRelationDao algoNodeRelationDao;
    @Autowired
    private AtomicAlgorithmDao atomicAlgorithmDao;
    @Autowired
    private UusRoleService uusRoleService;

    @Override
    public BaseDao<ProductDTO> getBaseDao() {
        return productDao;
    }

    @Cacheable(value = "uusRoleCache", key = "'tree' + #roleId")
    public List<ZtreeView> tree(String roleId) {
        List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        UusRole role = uusRoleService.getById(roleId);
        Set<DataCategoryDTO> roleCategorys = role.getCategory();
        resulTreeNodes.add(new ZtreeView("0", null, "产品列表", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id", "sort");
        List<DataCategoryDTO> all = categoryDao.findAll(sort);
        for (DataCategoryDTO category : all) {
            node = new ZtreeView();
            node.setId(category.getId());
            if (category.getParentId() == null) {
                node.setpId("0");
            } else {
                node.setpId(category.getParentId());
            }
            node.setName(category.getMenu());
            if (roleCategorys != null && roleCategorys.contains(category)) {
                node.setChecked(true);
            }
            resulTreeNodes.add(node);
        }
        return resulTreeNodes;
    }

    public Map<String, Object> pageList(int start, int length,
                                        ProductDTO productDTO) {
        Page<ProductDTO> page = this.findPageProduct(start, length,
                productDTO);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return maps;
    }

    public Page<ProductDTO> findPageProduct(int start, int length, ProductDTO productDTO) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        Page<ProductDTO> page = this.getPage(
                getWhereClause(productDTO), d);
        return page;
    }

    private Specification<ProductDTO> getWhereClause(
            ProductDTO productDTO) {
        SimpleSpecificationBuilder<ProductDTO> specification = new SimpleSpecificationBuilder();
        if (!StringUtils.isEmpty(productDTO.getName())) {
            specification.add("name", "likeAll", productDTO.getName());
        }
        if (!StringUtils.isEmpty(productDTO.getMark())) {
            specification.add("mark", "likeAll", productDTO.getMark());
        }
        if (!StringUtils.isEmpty(productDTO.getTreeId())) {
            specification.addOr("treeId", "eq", productDTO.getTreeId());
        }
        return specification.generateSpecification();
    }

    public ProductDTO saveProduct(ProductDTO productDTO) {

        ProductDTO newProductDTO = new ProductDTO();
        if (!StringUtils.isEmpty(productDTO.getId())) {
            ProductDTO yProductDTO = this.getById(productDTO.getId());
            yProductDTO.setUpdateTime(new Date());
            yProductDTO.setCycle(productDTO.getCycle());
            yProductDTO.setFeatureName(productDTO.getFeatureName());
            yProductDTO.setGdbPath(productDTO.getGdbPath());
            yProductDTO.setMapUrl(productDTO.getMapUrl());
            yProductDTO.setMark(productDTO.getMark());
            yProductDTO.setName(productDTO.getName());
            yProductDTO.setIconPath(productDTO.getIconPath());
            yProductDTO.setSortNo(productDTO.getSortNo());
            yProductDTO.setProductPath(productDTO.getProductPath());
            yProductDTO.setTreeId(productDTO.getTreeId());
            yProductDTO.setBz(productDTO.getBz());
            newProductDTO = this.save(yProductDTO);
        } else {
            productDTO.setCreateTime(new Date());
            newProductDTO = this.save(productDTO);

        }

        return newProductDTO;
    }

    public ReturnT<String> deleteProduct(String id) {
        try {
            this.delete(id);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnT.FAIL;
    }

    public ReturnT<String> deleteProductByTreeId(String id) {
        try {
            productDao.deleteBytreeId(id);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnT.FAIL;
    }

    public List<ProductDTO> findALlProduct() {
        return this.getAll();
    }

    public ProductDTO findById(String id) {
        return productDao.findOne(id);
    }

    @Override
    public List<AlgorithmRelationInfoDTO> queryAogo(String treeid) {
        return algoNodeRelationDao.findBytreeId(treeid);
    }

    @Override
    public AtomicAlgorithmDTO queryAogoInfo(String id) {
        return atomicAlgorithmDao.findInfoById(id);
    }

    @Override
    public ProductDTO getProductIdByTreeId(String treeid) {
        return productDao.findByTreeId(treeid);
    }

}

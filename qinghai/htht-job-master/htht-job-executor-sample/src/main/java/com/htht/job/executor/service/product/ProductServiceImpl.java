package com.htht.job.executor.service.product;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.algorithm.AtomicAlgorithmDao;
import com.htht.job.executor.dao.datacategory.DataCategoryDao;
import com.htht.job.executor.dao.downupload.AlgoNodeRelationDao;
import com.htht.job.executor.dao.product.ProductDao;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.datacategory.DataCategory;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfo;
import com.htht.job.executor.model.product.Product;
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
public class ProductServiceImpl extends BaseService<Product> implements  ProductService{
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
    public BaseDao<Product> getBaseDao() {
        return productDao;
    }
    @Cacheable(value = "uusRoleCache", key = "'tree' + #roleId")
    public List<ZtreeView> tree(String roleId) {
        List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        UusRole role = uusRoleService.getById(roleId);
        Set<DataCategory> roleCategorys = role.getCategory();
        resulTreeNodes.add(new ZtreeView("0", null, "产品列表", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id", "sort");
        List<DataCategory> all = categoryDao.findAll(sort);
        for (DataCategory category : all) {
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
                                        Product product) {
        Page<Product> page = this.findPageProduct(start, length,
                product);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return maps;
    }

    public Page<Product> findPageProduct(int start,int length,Product product){
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        Page<Product> page = this.getPage(
                getWhereClause(product), d);
        return page;
    }
    private Specification<Product> getWhereClause(
            Product product) {
        SimpleSpecificationBuilder<Product> specification=new SimpleSpecificationBuilder();
        if (!StringUtils.isEmpty(product.getName())) {
            specification.add("name","likeAll",product.getName());
        }
        if (!StringUtils.isEmpty(product.getMark())) {
        	specification.add("mark","likeAll",product.getMark());
        }
        if (!StringUtils.isEmpty(product.getTreeId())) {
            specification.addOr("treeId","eq",product.getTreeId());
        }
        return specification.generateSpecification();
    }

    public Product saveProduct(Product product){

        Product newProduct=new Product();
        if(!StringUtils.isEmpty(product.getId())){
            Product yProduct=this.getById(product.getId());
            yProduct.setUpdateTime(new Date());
            yProduct.setCycle(product.getCycle());
            yProduct.setFeatureName(product.getFeatureName());
            yProduct.setGdbPath(product.getGdbPath());
            yProduct.setMapUrl(product.getMapUrl());
            yProduct.setMark(product.getMark());
            yProduct.setName(product.getName());
            yProduct.setIconPath(product.getIconPath());
            yProduct.setSortNo(product.getSortNo());
            yProduct.setProductPath(product.getProductPath());
            yProduct.setTreeId(product.getTreeId());
            yProduct.setBz(product.getBz());
            newProduct=this.save(yProduct);
        }else{
            product.setCreateTime(new Date());
            newProduct=this.save(product);

        }

        return  newProduct;
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

    public List<Product> findALlProduct(){
        return  this.getAll();
    }

    public Product findById(String id){
        return productDao.findOne(id);
    }

    @Override
    public List<AlgorithmRelationInfo> queryAogo(String treeid) {
        return algoNodeRelationDao.findBytreeId(treeid);
    }

    @Override
    public AtomicAlgorithm queryAogoInfo(String id) {
        return atomicAlgorithmDao.findInfoById(id);
    }

    @Override
    public Product getProductIdByTreeId(String treeid) {
        return productDao.findByTreeId(treeid);
    }
	@Override
	public List<Product> getProductsByParentId(String parentId) {
		//根据产品的父Id查找父目录
		List<DataCategory> categorys = categoryDao.findByParentId(parentId);
		List<Product> products = new ArrayList<>();
		for (DataCategory category : categorys) {
			//根据父目录id查找子产品
			Product product = productDao.findByTreeId(category.getId());
			products.add(product);
		}
		
		return products;
	}

}

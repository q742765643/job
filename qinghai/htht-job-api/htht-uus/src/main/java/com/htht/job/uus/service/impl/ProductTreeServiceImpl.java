package com.htht.job.uus.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.ProductCategoryDao;
import com.htht.job.uus.dao.ProductTreeDao;
import com.htht.job.uus.model.Product;
import com.htht.job.uus.model.ProductCategory;
import com.htht.job.uus.model.ProductCycle;
import com.htht.job.uus.model.ProductTree;
import com.htht.job.uus.service.ProductTreeService;

/**
 * @ClassName: ProductTreeServiceImpl
 * @Description: 产品目录树Service
 * @author chensi
 * @date 2018年5月10日
 * 
 */
@Service
public class ProductTreeServiceImpl implements ProductTreeService {
	
	private static Map<String, String> cycleMap;

	 /**
	 *  COTM Cycle of Ten Minute 10min周期
	 *  COOH Cycle of One Hour 实时周期合成产品
	 *  COOD Cycle of One Day 日周期合成产品
	 *  COFD Cycle of Five Day 侯周期合成产品 
	 *  COSD Cycle of Seven Days周周期合成产品
	 *  COTD Cycle of Ten Days 旬周期合成产品 
	 *  COAM Cycle of a Month 月周期合成产品 
	 *  COAQ Cycle of a Quarter 季周期合成产品 
	 *  COAY Cycle of a Year 年周期合成产品
	 */
	static {
		cycleMap = new HashMap<String, String>();
		cycleMap.put("COTM", "10min周期");
		cycleMap.put("COOH", "实时周期");
		cycleMap.put("COOD", "日周期");
		cycleMap.put("COFD", "侯周期");
		cycleMap.put("COSD", "周周期");
		cycleMap.put("COTD", "旬周期");
		cycleMap.put("COAM", "月周期");
		cycleMap.put("COAQ", "季周期");
		cycleMap.put("COAY", "年周期");
	}
	
	@Resource
	public ProductTreeDao productTreeDao;
	
	@Resource
	public ProductCategoryDao productCategoryDao;
	
	@Override
	public List<ProductCycle> findCycleById(String id) {
		
		List<String> cycleList = new ArrayList<>();
		HashSet<String> cycleSet=new HashSet<String>();
		List<ProductCycle> cycleFinal = new ArrayList<ProductCycle>();
		cycleList = productTreeDao.selectCycleById(id);
		for (String cycles : cycleList) {
			String[] cycleArr = cycles.split(",");
			for (String cycle : cycleArr) {
				cycleSet.add(cycle);
			}
		}
		for (String cycle : cycleSet) {
			ProductCycle productCycle = new ProductCycle();
			productCycle.setCode(cycle);
			productCycle.setName(cycleMap.get(cycle));
			cycleFinal.add(productCycle);
		}

		return cycleFinal;
	}

	@Override
	public List<ProductTree> findProductTreeByUserId(String userId) {
		
		List<ProductCategory> productCategoryList = productCategoryDao.selectCategoryByUserId(userId);
		List<ProductTree> productCategoryTrees = changeCategoryToProductTree(productCategoryList);
		List<ProductTree> products = productTreeDao.selectProductTreeByUserId(userId);

		//将productCategoryTrees的ID和父ID全部设置成Products里面的ID和父ID，后面便于构造目录树结构
		List<ProductTree> repeatProducts = new ArrayList<>();
		List<ProductTree> needProducts = new ArrayList<>();
		for (ProductTree productTree : productCategoryTrees) {
			
			for (ProductTree product : products) {
				if (null == product.getId() || "".equals(product.getParentId())) {
					continue;
				}
				if (productTree.getId().equals(product.getParentId())) {
					
					repeatProducts.add(productTree);
					product.setVirtualId(productTree.getVirtualId());
					product.setVirtualParentId(productTree.getVirtualParentId());
					product.setIconPath(productTree.getIconPath());
					needProducts.add(product);
					continue;
				}
			}
		}
		productCategoryTrees.removeAll(repeatProducts);
		productCategoryTrees.addAll(needProducts);
		return productCategoryTrees;
		
	}
	
	/**
	 * 
	 * Description: 将ProductCategory 转化为 ProductTree
	 * @param productCategoryList
	 * @return
	 */
	private List<ProductTree> changeCategoryToProductTree(List<ProductCategory> productCategoryList) {
		
		List<ProductTree> productTreeList = new ArrayList<>();
		for (ProductCategory productCategory : productCategoryList) {
			
			ProductTree productTree = new ProductTree();
			
			productTree.setId(productCategory.getId());
			productTree.setName(productCategory.getText());
			productTree.setParentId(productCategory.getParentId());
			productTree.setIconPath(productCategory.getIconPath());
			
			productTree.setVirtualId(productCategory.getId());
			productTree.setVirtualParentId(productCategory.getParentId());
			
			productTreeList.add(productTree);
		}
		
		return productTreeList;
		
	}

	@Override
	public Product findProductByProductInfoId(String productInfoId) {
		Product product = productCategoryDao.selectProductByProductInfoId(productInfoId);
		return product;
	}

}

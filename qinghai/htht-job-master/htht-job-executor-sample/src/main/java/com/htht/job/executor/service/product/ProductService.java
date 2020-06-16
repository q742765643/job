package com.htht.job.executor.service.product;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfo;
import com.htht.job.executor.model.product.Product;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/2.
 */
public interface ProductService {
    public Map<String, Object> pageList(int start, int length, Product product);
    public Product saveProduct(Product product);
    public ReturnT<String> deleteProduct(String id);
    public ReturnT<String> deleteProductByTreeId(String id);
    public List<Product> findALlProduct();
    public Product findById(String id);
    public List<AlgorithmRelationInfo> queryAogo(String treeid);
    public AtomicAlgorithm queryAogoInfo(String treeid);
    public Product getProductIdByTreeId(String treeid);
    /**
     * 根据产品的父Id查找子产品
     * @param parentId
     * @return List<Product>
     */
    public List<Product> getProductsByParentId(String parentId);

}

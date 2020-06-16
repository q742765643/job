package com.htht.job.executor.service.product;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfoDTO;
import com.htht.job.executor.model.product.ProductDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/2.
 */
public interface ProductService {
    public Map<String, Object> pageList(int start, int length, ProductDTO productDTO);

    public ProductDTO saveProduct(ProductDTO productDTO);

    public ReturnT<String> deleteProduct(String id);

    public ReturnT<String> deleteProductByTreeId(String id);

    public List<ProductDTO> findALlProduct();

    public ProductDTO findById(String id);

    public List<AlgorithmRelationInfoDTO> queryAogo(String treeid);

    public AtomicAlgorithmDTO queryAogoInfo(String treeid);

    public ProductDTO getProductIdByTreeId(String treeid);

}


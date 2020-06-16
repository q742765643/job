package com.htht.job.executor.service.productinfo;

import com.htht.job.executor.model.productinfo.ProductInfoDTO;

import java.util.List;
import java.util.Map;

public interface ProductInfoService {
    public Map<String, Object> pageList(int start, int length, String productId);

    public void deleteProductInfo(String id);

    public ProductInfoDTO save(ProductInfoDTO productInfoDTO);

    public List<ProductInfoDTO> findProductExits(String productId, String issue,
                                                 String cycle, String modelIdentify, String inputFileName,
                                                 String regionId);

    public List<ProductInfoDTO> findProductInfoListByIssueRange(String id, String startIssue, String endIssue);
    
    public List<ProductInfoDTO> findProductExits(String issue, String cycle, String modelIdentify);
}

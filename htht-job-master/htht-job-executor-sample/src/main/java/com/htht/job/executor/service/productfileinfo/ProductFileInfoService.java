package com.htht.job.executor.service.productfileinfo;

import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/3.
 */
public interface ProductFileInfoService {
    public Map<String, Object> pageList(int start, int length,
                                        ProductFileInfoDTO productFileInfoDTO);

    public Map<String, Object> pageLists(int start, int length, String id);

    public ProductFileInfoDTO save(ProductFileInfoDTO productFileInfoDTO);

    public ProductFileInfoDTO deleteById(String id);

    public void batchSaveProductFileInfo(List<ProductFileInfoDTO> list);

    public List<ProductFileInfoDTO> findByWhere(String prdocutId, String issue);

    public List<ProductFileInfoDTO> findByWhereAndRegion(String prdocuttype, String issue, String[] regionandfiletype);

    public List<ProductFileInfoDTO> findByproductInfoId(String id);

    public void deleteByproductInfoId(String id);
}

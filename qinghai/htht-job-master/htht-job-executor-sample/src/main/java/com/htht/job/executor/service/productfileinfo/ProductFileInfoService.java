package com.htht.job.executor.service.productfileinfo;

import com.htht.job.executor.model.productfileinfo.ProductFileInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/3.
 */
public interface ProductFileInfoService {
    public Map<String, Object> pageList(int start, int length,
                                        ProductFileInfo productFileInfo);
    public Map<String, Object> pageLists(int start, int length, String id);
    public ProductFileInfo save( ProductFileInfo productFileInfo);
    public ProductFileInfo deleteById(String id);
    public void batchSaveProductFileInfo(List<ProductFileInfo> list);
    public List<ProductFileInfo> findByWhere(String prdocutId,String issue);
    public List<ProductFileInfo> findByWhereAndRegion(String prdocuttype,String issue,String[] regionandfiletype);
    public List<ProductFileInfo> findByproductInfoId(String id);
    public  void deleteByproductInfoId(String id);
}

package com.htht.job.executor.dao.productinfo;

import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInfoDao extends BaseDao<ProductInfoDTO> {
    Page<ProductInfoDTO> findByProductId(String productId, Pageable pageable);

    @Query(nativeQuery = true, value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "product_id = ?1 and issue = ?2 and cycle = ?3 and input_file_name = ?5 and model_identify = ?4 and region_id = ?6")
    List<ProductInfoDTO> findProductExits(String productId, String issue, String cycle, String modelIdentify, String inputFileName, String regionId);

    @Query(nativeQuery = true, value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "product_id = ?1 and issue = ?2 and cycle = ?3 and model_identify = ?4 and region_id = ?5")
    List<ProductInfoDTO> findProductExits(String productId, String issue, String cycle, String modelIdentify, String regionId);

    @Query(nativeQuery = true, value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "product_id = ?1 and issue >= ?2 and issue <= ?3")
    List<ProductInfoDTO> findProductInfoListByIssueRange(String id, String startIssue, String endIssue);
    
    @Query(nativeQuery = true, value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "issue = ?1 and cycle = ?2 and model_identify = ?3")
    List<ProductInfoDTO> findProductExits(String issue, String cycle, String modelIdentify);

}

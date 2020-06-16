package com.htht.job.executor.dao.productinfo;

import com.htht.job.executor.model.productinfo.ProductInfo;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Repository
public interface ProductInfoDao extends BaseDao<ProductInfo> {
    Page<ProductInfo> findByProductId(String productId, Pageable pageable);

//    @Modifying
    @Query(nativeQuery = true,value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "product_id = ?1 and issue = ?2 and cycle = ?3 and input_file_name = ?5 and model_identify = ?4 and region_id = ?6")
    List<ProductInfo> findProductExits(String productId,String issue,String cycle,String modelIdentify,String inputFileName,String regionId);

    @Query(nativeQuery = true,value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "product_id = ?1 and issue = ?2 and cycle = ?3 and model_identify = ?4 and region_id = ?5")
    List<ProductInfo> findProductExits(String productId,String issue,String cycle,String modelIdentify,String regionId);
    
    @Query(nativeQuery = true,value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "product_id = ?1 and issue = ?2 and cycle = ?3 and model_identify = ?4 ")
    List<ProductInfo> findProductExits(String productId,String issue,String cycle,String modelIdentify);
    
    @Query(nativeQuery = true,value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "product_id = ?1 and issue >= ?2 and issue <= ?3")
	List<ProductInfo> findProductInfoListByIssueRange(String id, String startIssue, String endIssue);
    
    @Query(nativeQuery = true,value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "id = ?1 ")
	ProductInfo selectProductInfoById(String id);

    @Query(nativeQuery = true,value = "select count(id) FROM htht_cluster_schedule_product_info WHERE " +
            "product_id = ?1 and region_id = ?2")
	int countProductInfoByPid(String pid, String regionId);
    
    @Query(nativeQuery = true,value = "select * FROM htht_cluster_schedule_product_info WHERE " +
            "mark = ?1 and issue IN (?2)  and region_id = ?3 order by issue asc")
    List<ProductInfo> findProductByMark(@Param("mark") String mark,@Param("issue") List<String> issue,@Param("regionId") String regionId);
    
}

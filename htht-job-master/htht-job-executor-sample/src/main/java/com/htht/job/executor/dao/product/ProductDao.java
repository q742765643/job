package com.htht.job.executor.dao.product;

import com.htht.job.executor.model.product.ProductDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/1/1.
 */
@Repository
public interface ProductDao extends BaseDao<ProductDTO> {

    void deleteBytreeId(String treeId);

    ProductDTO findByTreeId(String treeid);

    @Modifying
    @Query("SELECT p FROM ProductDTO p WHERE p.id = ?1")
    ProductDTO queryById(String id);
}

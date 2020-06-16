package com.htht.job.executor.dao.product;

import com.htht.job.executor.model.product.Product;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/1/1.
 */
@Repository
public interface ProductDao extends BaseDao<Product> {

    void  deleteBytreeId(String treeId);

	Product findByTreeId(String treeid);

    @Modifying
    @Query("SELECT p FROM Product p WHERE p.id = ?1")
    Product queryById(String id);
}

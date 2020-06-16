package com.htht.job.executor.dao.productfileinfo;

import com.htht.job.executor.model.productfileinfo.ProductFileInfo;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zzj on 2018/1/2.
 */
@Repository
public interface ProductFileInfoDao extends BaseDao<ProductFileInfo> {

    Page<ProductFileInfo> findByProductInfoId(String productInfoId, Pageable pageable);

    List<ProductFileInfo> findByproductInfoId(String productInfoId);

    void deleteByproductInfoId(String id);
}

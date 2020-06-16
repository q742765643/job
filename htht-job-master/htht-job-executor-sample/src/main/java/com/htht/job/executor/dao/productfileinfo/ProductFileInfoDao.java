package com.htht.job.executor.dao.productfileinfo;

import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zzj on 2018/1/2.
 */
@Repository
public interface ProductFileInfoDao extends BaseDao<ProductFileInfoDTO> {

    Page<ProductFileInfoDTO> findByProductInfoId(String productInfoId, Pageable pageable);

    List<ProductFileInfoDTO> findByproductInfoId(String productInfoId);

    void deleteByproductInfoId(String id);
}

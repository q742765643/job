package com.htht.job.executor.dao.fileinfo;

import com.htht.job.executor.model.fileinfo.FileInfoDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/2/5.
 */
@Repository
public interface FileInfoDao extends BaseDao<FileInfoDTO> {
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM htht_file_info WHERE product_file_info_id" +
            " in (select id from htht_product_file_info where product_id=:productId and issue=:issue )")
    void deleteByissue(@Param("productId") String productId, @Param("issue") String issue);
}

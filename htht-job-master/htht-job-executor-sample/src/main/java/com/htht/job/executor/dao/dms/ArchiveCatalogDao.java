package com.htht.job.executor.dao.dms;

import com.htht.job.executor.model.dms.module.ArchiveCatalog;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;


/**
 * @date:2018年9月14日上午10:39:44
 * @author:yss
 */
@Repository
public interface ArchiveCatalogDao extends BaseDao<ArchiveCatalog> {

    ArchiveCatalog getByFid(String fid);

    @Modifying
    @Query(nativeQuery = true, value = "update htht_dms_archive_catalog a set "
            + "a.F_CATALOGNAME=:catalogName,a.update_time=:updateTime,a.F_MAINTABLENAME=:mainTableName,a.F_NODETYPE=:nodeType,a.F_NODEDESC=:nodeDesc where F_ID=:fid and id=:id")
    void update(@Param("catalogName") String catalogName, @Param("id") String id, @Param("fid") String fid,
                @Param("updateTime") Date updateTime, @Param("mainTableName") String mainTableName,
                @Param("nodeType") Integer nodeType, @Param("nodeDesc") String nodeDesc);


}

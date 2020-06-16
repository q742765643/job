package com.htht.job.executor.dao.dms;

import com.htht.job.executor.model.dms.module.ArchiveRules;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author LY 2018-04-2
 */
@Repository
public interface ArchiveRulesDao extends BaseDao<ArchiveRules> {
    @Modifying
    @Query(nativeQuery = true, value = "update HTHT_DMS_SYS_ARCHIVERULES a set "
            + "a.regexpjpg=:regexpjpg,a.regexpstr=:regexpstr,a.regexpxml=:regexpxml,"
            + "a.rulestatus=:rulestatus,a.catalogcode=:catalogcode,a.archivdisk=:archivdisk,"
            + "a.all_file=:allFile,a.create_time=:createTime,a.datalevel=:datalevel,"
            + "a.filetype=:filetype,a.finalstr=:finalstr,a.flowid=:flowid,a.update_time=:updateTime,a.wsp_file=:wspFile where id=:id")
    void update(@Param("id") String id, @Param("regexpjpg") String regexpjpg, @Param("regexpstr") String regexpstr,
                @Param("regexpxml") String regexpxml, @Param("rulestatus") int rulestatus,
                @Param("catalogcode") String catalogcode, @Param("archivdisk") String archivdisk,
                @Param("allFile") String allFile, @Param("createTime") Date createTime,
                @Param("datalevel") String datalevel, @Param("filetype") int filetype, @Param("finalstr") String finalstr,
                @Param("flowid") int flowid, @Param("updateTime") Date updateTime, @Param("wspFile") String wspFile);

    @Modifying
    @Query(nativeQuery = true, value = "update HTHT_DMS_SYS_ARCHIVERULES a set a.rulestatus=:rulestatus where id=:id")
    void updateEnableOrdisable(@Param("id") String id, @Param("rulestatus") int rulestatus);

    @Modifying
    @Query(nativeQuery = true, value = "update HTHT_DMS_SYS_ARCHIVERULES a set a.archivdisk=:archivdisk where id=:id")
    void updateDisk(@Param("id") String id, @Param("archivdisk") String archivdisk);

}

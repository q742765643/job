package com.htht.job.executor.dao.datacategory;

import com.htht.job.executor.model.datacategory.DataCategoryDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataCategoryDao extends BaseDao<DataCategoryDTO> {

    List<DataCategoryDTO> findByParentId(String parentId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM htht_uus_role_category WHERE category_id = :id")
    void deleteGrant(@Param("id") String id);

}

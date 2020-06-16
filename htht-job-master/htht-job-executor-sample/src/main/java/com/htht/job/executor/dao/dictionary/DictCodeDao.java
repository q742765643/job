package com.htht.job.executor.dao.dictionary;

import com.htht.job.executor.model.dictionary.DictCodeDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @date:2018年6月27日下午2:54:50
 * @author:yss
 */
@Repository
public interface DictCodeDao extends BaseDao<DictCodeDTO> {

    @Modifying
    @Query(nativeQuery = true, value = "select * from htht_cluster_schedule_dict_code where parent_id = (select id from htht_cluster_schedule_dict_code WHERE dict_name=:dictName)")
    List<DictCodeDTO> findChildren(@Param("dictName") String dictName);

    DictCodeDTO findByDictName(String string);

    List<DictCodeDTO> findByParentId(String parentId);
}

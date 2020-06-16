package com.htht.job.executor.dao.downupload;

import com.htht.job.executor.model.downupload.CimissDataInfoDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CimissDataInfoDao extends BaseDao<CimissDataInfoDTO> {

    List<CimissDataInfoDTO> findByType(String type);

}

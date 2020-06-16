package com.htht.job.executor.dao.downupload;

import com.htht.job.executor.model.downupload.CimissDownInfoDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;


@Repository
public interface CimissDownResultDao extends BaseDao<CimissDownInfoDTO> {

    CimissDownInfoDTO findByName(String name);

}

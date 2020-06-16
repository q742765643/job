package com.htht.job.executor.dao.downupload;

import com.htht.job.executor.model.downupload.CimissDownInfo;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;


@Repository
public interface CimissDownResultDao extends BaseDao<CimissDownInfo> {

	CimissDownInfo findByName(String name);

}

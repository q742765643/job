package com.htht.job.executor.dao.downupload;

import com.htht.job.executor.model.downupload.CimissDataInfo;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CimissDataInfoDao extends BaseDao<CimissDataInfo> {

	List<CimissDataInfo> findByType(String type);

}

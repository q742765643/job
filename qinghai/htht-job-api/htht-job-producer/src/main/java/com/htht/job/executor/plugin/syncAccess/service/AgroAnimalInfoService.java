package com.htht.job.executor.plugin.syncAccess.service;

import java.util.Date;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.plugin.syncAccess.dao.AgroAnimalInfoDao;
import com.htht.job.executor.plugin.syncAccess.model.AgroAnimalInfo;

@Transactional
@Service("agroAnimalInfoService")
public class AgroAnimalInfoService extends BaseService<AgroAnimalInfo> {

	@Autowired
    private AgroAnimalInfoDao agroAnimalInfoDao;
	
	@Override
	public BaseDao<AgroAnimalInfo> getBaseDao() {
		return agroAnimalInfoDao;
	}

	public Date findMaxFbday() {
		return agroAnimalInfoDao.findbMaxFbday();
	}
	
	public AgroAnimalInfo findByFbdayAndNsta(Date date, String nsta){
		return agroAnimalInfoDao.findByFbdayAndNsta(date,nsta);
	}

}

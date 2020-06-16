package com.htht.job.executor.plugin.syncAccess.dao;

import java.util.Date;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.htht.job.executor.plugin.syncAccess.model.AgroAnimalInfo;

@Repository
public interface AgroAnimalInfoDao extends BaseDao<AgroAnimalInfo>{

	AgroAnimalInfo findByFbdayAndNsta(Date date, String nsta);

	@Query(nativeQuery = true,value = "SELECT MAX(fbday) FROM data_agro_animal_husbandry_eco_station where  is_revise = 0")
	Date findbMaxFbday();

}

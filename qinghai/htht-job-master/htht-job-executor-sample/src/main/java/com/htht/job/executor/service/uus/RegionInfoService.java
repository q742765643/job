package com.htht.job.executor.service.uus;

import java.util.List;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.dao.uus.RegionInfoDao;
import com.htht.job.executor.model.uus.RegionInfo;

/**
* @ClassName: RegionInfoService
* @Description: TODO()
* @author mao_r
* @date 2018年10月26日
*
*/

@Transactional
@Service("regionInfoService")
public class RegionInfoService extends BaseService<RegionInfo> {
	
	@Autowired
	private RegionInfoDao regionInfoDao;

	@Override
	public BaseDao<RegionInfo> getBaseDao() {
		return regionInfoDao;
	}
	
	public List<RegionInfo> findAllRegionInfo(){
		return regionInfoDao.findAll();
	}

}

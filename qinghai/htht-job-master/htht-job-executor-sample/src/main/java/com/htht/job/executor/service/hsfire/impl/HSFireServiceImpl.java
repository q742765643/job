package com.htht.job.executor.service.hsfire.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.dao.hsfire.HSFireDao;
import com.htht.job.executor.model.hsfire.HSFire;
import com.htht.job.executor.service.hsfire.HSFireService;

@Transactional
@Service
public class HSFireServiceImpl extends BaseService<HSFire> implements HSFireService{

	@Autowired
	private HSFireDao hsFireDao;
	@Override
	public BaseDao<HSFire> getBaseDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HSFire> findFireByIssue(String issue) {
		List<HSFire> hsFires = hsFireDao.selectFireByIssue(issue);
		return hsFires;
	}

	@Override
	public HSFire saveHSFire(HSFire hsFire) {
		
		HSFire newFire=new HSFire();
	        if(!StringUtils.isEmpty(hsFire.getId())){
	        	HSFire oldFire=this.getById(hsFire.getId());
	        	oldFire.setIssue(hsFire.getIssue());
	        	oldFire.setCreateTime(new Date());
	        	oldFire.setRegionId(hsFire.getRegionId());
	        	oldFire.setLat(hsFire.getLat());
	        	oldFire.setLon(hsFire.getLon());
	        	oldFire.setTfire(hsFire.getTfire());
	        	oldFire.setTbg(hsFire.getTbg());
	        	oldFire.setArea(hsFire.getArea());
	        	oldFire.setFrp(hsFire.getFrp());
	        	oldFire.setFrpN(hsFire.getFrpN());
	        	oldFire.setLc(hsFire.getLc());
	        	oldFire.setCred(hsFire.getCred());
	        	oldFire.setUpdateTime(new Date());
	        	newFire= hsFireDao.save(oldFire);
				
	        }else{
	        	hsFire.setCreateTime(new Date());
	        	hsFire.setUpdateTime(new Date());
	        	newFire=hsFireDao.saveAndFlush(hsFire);
	        }

	        return  newFire;
	}

}

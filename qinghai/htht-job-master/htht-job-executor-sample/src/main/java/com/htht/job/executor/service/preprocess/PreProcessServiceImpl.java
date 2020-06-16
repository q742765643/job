package com.htht.job.executor.service.preprocess;

import java.util.List;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.dao.preprocess.PreProcessDao;
import com.htht.job.executor.model.preprocess.PreProcess;

@Transactional
@Service
public class PreProcessServiceImpl extends BaseService<PreProcess> implements PreProcessService{
	
	@Autowired
	private PreProcessDao preProcessDao;

	@Override
	public List<PreProcess> findProcessByProductIdAndIssue(String productId, List<String> issues) {
		// TODO Auto-generated method stub
		List<PreProcess> processList = preProcessDao.selectProcessByProductIdAndIssue(productId, issues);
		return processList;
	}

	@Override
	public PreProcess findProcessByProductIdAndIssue(String productId, String issue) {
		
		 PreProcess preProcess = preProcessDao.selectProcessByProductIdAndIssue(productId, issue);
		 
		return preProcess;
	}
	
	@Override
	public PreProcess savePreProcess(PreProcess preProcess){
		PreProcess newPreProcess = new PreProcess();
		if (null != preProcess.getId()) {
			PreProcess preProcess2 = new PreProcess();
			preProcess2.setCreateTime(preProcess.getCreateTime());
			preProcess2.setExecuteTime(preProcess.getExecuteTime());
			preProcess2.setId(preProcess.getId());
			preProcess2.setIssue(preProcess.getIssue());
			preProcess2.setMark(preProcess.getMark());
			preProcess2.setProductId(preProcess.getProductId());
			preProcess2.setStatus(preProcess.getStatus());
			preProcess2.setUpdateTime(preProcess.getUpdateTime());
			preProcess2.setVersion(preProcess.getVersion());
			newPreProcess = preProcessDao.save(preProcess2);
		} else {
			newPreProcess = preProcessDao.save(preProcess);
		}
		
		return newPreProcess;
		
	}

	@Override
	public BaseDao<PreProcess> getBaseDao() {
		// TODO Auto-generated method stub
		return null;
	}

}

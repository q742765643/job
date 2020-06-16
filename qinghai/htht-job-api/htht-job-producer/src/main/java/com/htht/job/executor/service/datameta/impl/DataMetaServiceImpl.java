package com.htht.job.executor.service.datameta.impl;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;

import com.htht.job.executor.model.datametainfo.DataMetaInfo;
import com.htht.job.executor.service.datameta.DataMetaService;
import com.htht.job.executor.service.product.ProductService;

public class DataMetaServiceImpl extends BaseService<DataMetaInfo> implements  DataMetaService {

	@Override
	public BaseDao<DataMetaInfo> getBaseDao() {
		// TODO Auto-generated method stub
		return null;
	}

}

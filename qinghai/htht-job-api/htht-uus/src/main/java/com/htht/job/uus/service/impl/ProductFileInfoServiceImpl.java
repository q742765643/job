package com.htht.job.uus.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.htht.job.uus.dao.ProductFileInfoDao;
import com.htht.job.uus.model.ProductFileInfo;
import com.htht.job.uus.service.ProductFileInfoService;

/**
 * @ClassName: ProductFileInfoServiceImpl
 * @Description: 产品文件信息Service
 * @author chensi
 * @date 2018年5月15日
 * 
 */
@Service
public class ProductFileInfoServiceImpl implements ProductFileInfoService {

	@Resource
	private ProductFileInfoDao productFileInfoDao;
	
	@Override
	public Map<String, Object> findProductFileInfoByIssueAndCycle(String cycle,
			String productInfoId, String[] regionIds, String issue, int pageNum, int pageSize) 
	{
		Map<String, Object> map = new HashMap<String, Object>();
		List<ProductFileInfo> productFileInfoList = null;
		
		productFileInfoList = productFileInfoDao.selectProductFileInfoByIssueAndCycle(cycle, productInfoId, regionIds, issue);
		map.put("productFileInfoList", productFileInfoList);
		map.put("count", productFileInfoList.size());
		map.put("pages", 1);
		
		return map;
	}

}

package com.htht.job.uus.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.htht.job.uus.common.ProductConsts;
import com.htht.job.uus.dao.HSFireDao;
import com.htht.job.uus.dao.ProductCategoryDao;
import com.htht.job.uus.dao.ProductInfoDao;
import com.htht.job.uus.model.HSFire;
import com.htht.job.uus.model.Product;
import com.htht.job.uus.model.ProductInfo;
import com.htht.job.uus.model.viewModel.HSFireView;
import com.htht.job.uus.service.ProductInfoService;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {

	@Resource
	private ProductInfoDao productInfoDao;

	@Resource
	private ProductCategoryDao productCategoryDao;
	
	@Resource
	private HSFireDao hsFireDao;
	
	@Override
	public Map<String, Object> findProductInfoDistinct(Date beginTime, Date endTime, String cycle, String productId,
			String[] regionIds, int pageNum, int pageSize) {

		Map<String, Object> map = new HashMap<String, Object>();
		List<ProductInfo> productInfoList = null;
		String begin = "";
		String end = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		if (beginTime != null) {
			begin = sdf.format(beginTime);
		}
		if (endTime != null) {
			end = sdf.format(endTime);
		}
		System.out.println("###########开始直通车查询");
		System.out.println(System.currentTimeMillis());
		//用于服务直通车
		if(ProductConsts.PRODUCTMAP.containsKey(productId)){
			regionIds[0] = ProductConsts.PRODUCTMAP.get(productId);
		}
		System.out.println("###########开始数据车查询");
		long t1 = System.currentTimeMillis();
		
		try {
		PageHelper.startPage(pageNum, pageSize, true);
			productInfoList = productInfoDao.selectProductInfoDistinct(begin, end, cycle,
					productId, regionIds[0]);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		long t2 = System.currentTimeMillis();
		System.out.println("##############"+(t2-t1));
//		Product product = productCategoryDao.selectProductByProductId(productId);
		
		PageInfo<ProductInfo> pageInfo = new PageInfo<>(productInfoList);
		
		map.put("productInfoList", productInfoList);
		map.put("count", pageInfo.getTotal());
		map.put("pages", pageInfo.getPages());

		return map;
	}
	

}

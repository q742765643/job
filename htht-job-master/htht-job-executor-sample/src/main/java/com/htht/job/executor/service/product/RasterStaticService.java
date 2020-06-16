package com.htht.job.executor.service.product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.product.ProductDTO;

@Transactional
@Service("rasterStaticSerivce")
public class RasterStaticService {
	@Autowired
	private ProductService productService;
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	
	public List<Object[]> rasterStaticList(String productId, String regionId, int level, String cycle, Date startTime,
			Date endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String sql = new StringBuffer("SELECT t.issue, t.region_id, t.`value`  from htht_cluster_schedule_zonal_histogram as t where exists (")
				.append("select t1.id from htht_cluster_schedule_product_info as t1 where t.product_info_id = t1.id and t1.product_id=?")
			.append(") and EXISTS (")
				.append("	select t2.regionid from htht_uus_region_info as t2 where t2.regionid=t.region_id and (t2.regionid=? or t2.t_m_regionid=?)")
			.append(") and  t.issue BETWEEN ? and ? and t.`level`=? ORDER BY t.issue, t.region_id")
			.toString();
		String startStr = sdf.format(startTime);
		String endStr = sdf.format(endTime);
		return baseDaoUtil.getByJpql(sql, new Object[] {productId, regionId, regionId, startStr, endStr, level});
	}

	public List<Object> statisticTypeList() {
		String sql = "SELECT T.product_id, T.`level`, T.level_name FROM htht_cluster_schedule_product_classify AS T";
		Map<String, Map<String, Object>> resultMap = new HashMap<>();
		List<Object[]> resultList = baseDaoUtil.getByJpql(sql);
		for(Object[] objects : resultList) {
			String productId = objects[0].toString();
			Map<String, Object> map = resultMap.get(productId);
			if(null == map) {
				map = new HashMap<>();
				map.put("productId", productId);
				resultMap.put(productId, map);
			}
			String productName = (String) map.get("productName");
			if(null == productName) {
				ProductDTO findById = productService.findById(productId);
				map.put("productName", findById==null?"未知的产品类型":findById.getName());
			}
			@SuppressWarnings("unchecked")
			Map<String, String> levelName = (Map<String, String>) map.get("level");
			if(null == levelName) {
				levelName = new HashMap<>();
				map.put("level", levelName);
			}
			levelName.put(objects[1].toString(), objects[2].toString());
		}
		return new ArrayList<>(resultMap.values());
	}
}

package com.htht.job.executor.service.dms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.model.dms.module.MeataImgAndInfo;
import com.htht.job.executor.model.dms.module.MetaImg;
import com.htht.job.executor.model.dms.module.MetaInfo;

/**
 * @author: yss
 * @time:2018年10月23日 上午11:20:21
 */
@Transactional
@Service("meataImgAndInfoService")
public class MeataImgAndInfoService  {

	@Autowired
	private MetaImgService metaImgService;
	@Autowired
	private MetaInfoService metaInfoService;
	
	
	public Map<String, Object> meataImgAndInfoService(int start, int length, String catalogcode, Date fProducetimeStart, Date fProducetimeEnd, String fLevel, Integer fCloudamount, String fSatelliteid) {
		// TODO Auto-generated method stub
		Map<String, Object> map= metaImgService.pageList(start,length,catalogcode,fProducetimeStart, fProducetimeEnd,fLevel,fCloudamount,fSatelliteid);
		List<MetaInfo> metaInfoList = metaInfoService.findAll();
		List<MetaImg> metaImgList = (List<MetaImg>) map.get("data");
		ArrayList<MeataImgAndInfo> arrayList = new ArrayList<MeataImgAndInfo>();
		for (MetaImg metaImg : metaImgList) {
			for (MetaInfo metaInfo : metaInfoList) {
				if(metaImg.getfDataid().equals(metaInfo.getF_dataid())) {
					MeataImgAndInfo meataImgAndInfo = new MeataImgAndInfo();
					meataImgAndInfo.setMetaImg(metaImg);
					meataImgAndInfo.setMetaInfo(metaInfo);
					arrayList.add(meataImgAndInfo);
				}
			}
		}
		map.put("data", arrayList);
		JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
		//System.out.println(JSON.toJSONString(map,SerializerFeature.WriteMapNullValue));
		return map;
	}


	/**
	 * @param mark
	 * @param id 
	 */
	public void updateRecycleflag(String mark, String id) {
		Integer f_recycleflag=Integer.parseInt(mark);
		metaInfoService.updateRecycleflag(f_recycleflag,id);
		
	}


}

package com.htht.job.executor.service.dms;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.MeataImgAndInfo;
import com.htht.job.executor.model.dms.module.MetaImg;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.service.dictionary.DictCodeService;

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: yss
 * @time:2018年10月23日 上午11:20:21
 */
@Transactional
@Service("meataImgAndInfoService")
public class MeataImgAndInfoService {

    @Autowired
    private MetaImgService metaImgService;
    @Autowired
    private MetaInfoService metaInfoService;
    @Autowired
    private DictCodeService dictCodeService;
    @Autowired
    private DiskService diskService;


    public Map<String, Object> meataImgAndInfoService(int start, int length, String catalogcode, Date fProducetimeStart, Date fProducetimeEnd, String fLevel, Integer fCloudamount, String fSatelliteid) {
        Map<String, Object> map = metaImgService.pageList(start, length, catalogcode, fProducetimeStart, fProducetimeEnd, fLevel, fCloudamount, fSatelliteid);
        List<MetaImg> metaImgList = (List<MetaImg>) map.get("data");
        DictCodeDTO findOneself = dictCodeService.findOneself("快视图路径");
        ArrayList<MeataImgAndInfo> arrayList = new ArrayList<MeataImgAndInfo>();
        MetaInfo metaInfo = null;
        for (MetaImg metaImg : metaImgList) {
        	metaInfo = metaInfoService.getBeanId(metaImg.getfDataid());
            if(null != metaInfo) {
            	MeataImgAndInfo meataImgAndInfo = new MeataImgAndInfo();
            	meataImgAndInfo.setMetaImg(metaImg);
            	meataImgAndInfo.setMetaInfo(metaInfo);
            	if(null!=findOneself) {
            		meataImgAndInfo.setViewPath(findOneself.getDictCode()+"/"+metaInfo.getF_viewdatapath());
            	}
            	arrayList.add(meataImgAndInfo);
            	metaInfo = null;
            }
        }
        map.put("data", arrayList);
        JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
        //System.out.println(JSON.toJSONString(map,SerializerFeature.WriteMapNullValue));
        return map;
    }
    
    public String getDownloadPath(String dataid) {
    	try {
    		MetaInfo metainfo = metaInfoService.getBeanId(dataid);
        	String locationUrl = metainfo.getF_location();
        	List<Disk> disk = diskService.findAll();
        	for (int i = 0; i < disk.size(); i++) {
    			if(locationUrl.indexOf(disk.get(i).getLoginurl()) != -1) {
    				locationUrl = locationUrl.replace(disk.get(i).getLoginurl(), "");
    				break;
    			}
    		}
        	DictCodeDTO findOneself = dictCodeService.findOneself("数据下载路径");
        	return findOneself.getDictCode()+""+locationUrl;
		} catch (Exception e) {
			return "";
		}
    }
    
    // 删除卫星数据
    public String deleteData(String dataid) {
    	try {
    		MetaInfo metainfo = metaInfoService.getBeanId(dataid);
    		FileUtils.deleteQuietly(new File(metainfo.getF_location()));
    		metaInfoService.del(dataid);
    		metaImgService.del(dataid);
    		return "SUCCESS";
    	} catch (Exception e) {
    		return "";
    	}
    }
    
    /**
     * @param mark
     * @param id
     */
    public void updateRecycleflag(String mark, String id) {
        Integer f_recycleflag = Integer.parseInt(mark);
        metaInfoService.updateRecycleflag(f_recycleflag, id);

    }


}

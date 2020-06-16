package com.htht.job.executor.service.dictionary;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.dictionary.DictCodeDao;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;

/**
 * @date:2018年6月27日下午2:47:25
 * @author:yss
 */
@Transactional
@Service
public class DictCodeServiceImpl extends BaseService<DictCode> implements DictCodeService {

	@Autowired
	private DictCodeDao dictCodeDao;

	@Override
	public BaseDao<DictCode> getBaseDao() {
		return dictCodeDao;
	}

	@Cacheable(value = "dictCodeCache")
	@Override
	public List<ZtreeView> allTree() {
		List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
		resulTreeNodes.add(new ZtreeView("0", null, "字典管理", true));
		ZtreeView node;
		Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id", "sortOrder");
		List<DictCode> all = dictCodeDao.findAll(sort);
		for (DictCode dictCode : all) {
			node = new ZtreeView();
			node.setId(dictCode.getId());
			if (dictCode.getParentId() == null) {
				node.setpId("0");
			} else {
				node.setpId(dictCode.getParentId());
			}
			node.setName(dictCode.getDictName());
			resulTreeNodes.add(node);
		}
		return resulTreeNodes;
	}

	/* 
	 * 字典列表
	 */
	@Override
	public String list(int start, int length, String searchText,String id) {
		SimpleSpecificationBuilder<DictCode> builder = new SimpleSpecificationBuilder<DictCode>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if(StringUtils.isNotBlank(searchText)){
            builder.add("dictName", "likeAll", searchText);
        }
        if (!StringUtils.isEmpty(id)) {
        	builder.addOr("parentId","eq",id);
        }
        Page<DictCode> page = this.getPage(builder.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONString(maps,SerializerFeature.WriteMapNullValue);
	}
	
	@CacheEvict(value = "dictCodeCache")
	@Override
	public void saveOrUpdateDicCode(DictCode dictCode) {
		if(dictCode.getId() != null){
			DictCode dbDictCode = getById(dictCode.getId());
            dbDictCode.setUpdateTime(new Date());
            dbDictCode.setDictCode(dictCode.getDictCode());
            dbDictCode.setDictName(dictCode.getDictName());
            dbDictCode.setMemo(dictCode.getMemo());
            //dbDictCode.setParentId(dictCode.getParentId());
            dbDictCode.setSortOrder(dictCode.getSortOrder());
            //dbDictCode.setParent(resource.getParent());
            save(dbDictCode);
        }else{
        	dictCode.setCreateTime(new Date());
        	dictCode.setUpdateTime(new Date());
        	dictCodeDao.save(dictCode);
        }
	}
	@CacheEvict(value = "dictCodeCache")
	@Override
    public void delete(String id) {
		//this.delete(id);
		dictCodeDao.delete(id);
    }

	@Override
	public List<DictCode> findChildren(String string) {
		// TODO Auto-generated method stub
		List<DictCode> findChildren = dictCodeDao.findChildren(string);
		return findChildren;
	}

	@Override
	public DictCode findOneself(String string) {
		// TODO Auto-generated method stub
		return dictCodeDao.findByDictName(string);
	}

}

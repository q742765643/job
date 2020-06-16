package com.htht.job.executor.service.dictionary;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.executor.dao.dictionary.DictCodeDao;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
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

import java.util.*;

/**
 * @date:2018年6月27日下午2:47:25
 * @author:yss
 */
@Transactional
@Service
public class DictCodeServiceImpl extends BaseService<DictCodeDTO> implements DictCodeService {

    @Autowired
    private DictCodeDao dictCodeDao;

    @Override
    public BaseDao<DictCodeDTO> getBaseDao() {
        return dictCodeDao;
    }

    @Cacheable(value = "dictCodeCache")
    @Override
    public List<ZtreeView> allTree() {
        List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        resulTreeNodes.add(new ZtreeView("0", null, "字典管理", true));
        ZtreeView node;
        Sort sort = new Sort(Sort.Direction.ASC, "parentId", "id", "sortOrder");
        List<DictCodeDTO> all = dictCodeDao.findAll(sort);
        for (DictCodeDTO dictCodeDTO : all) {
            node = new ZtreeView();
            node.setId(dictCodeDTO.getId());
            if (dictCodeDTO.getParentId() == null) {
                node.setpId("0");
            } else {
                node.setpId(dictCodeDTO.getParentId());
            }
            node.setName(dictCodeDTO.getDictName());
            resulTreeNodes.add(node);
        }
        return resulTreeNodes;
    }

    /*
     * 字典列表
     */
    @Override
    public String list(int start, int length, String searchText, String id) {
        SimpleSpecificationBuilder<DictCodeDTO> builder = new SimpleSpecificationBuilder<DictCodeDTO>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if (StringUtils.isNotBlank(searchText)) {
            builder.add("dictName", "likeAll", searchText);
        }
        if (!StringUtils.isEmpty(id)) {
            builder.addOr("parentId", "eq", id);
        }
        Page<DictCodeDTO> page = this.getPage(builder.generateSpecification(), d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONString(maps, SerializerFeature.WriteMapNullValue);
    }

    @CacheEvict(value = "dictCodeCache")
    @Override
    public void saveOrUpdateDicCode(DictCodeDTO dictCodeDTO) {
        if (dictCodeDTO.getId() != null) {
            DictCodeDTO dbDictCodeDTO = getById(dictCodeDTO.getId());
            dbDictCodeDTO.setUpdateTime(new Date());
            dbDictCodeDTO.setDictCode(dictCodeDTO.getDictCode());
            dbDictCodeDTO.setDictName(dictCodeDTO.getDictName());
            dbDictCodeDTO.setMemo(dictCodeDTO.getMemo());
            //dbDictCode.setParentId(dictCode.getParentId());
            dbDictCodeDTO.setSortOrder(dictCodeDTO.getSortOrder());
            //dbDictCode.setParent(resource.getParent());
            save(dbDictCodeDTO);
        } else {
            dictCodeDTO.setCreateTime(new Date());
            dictCodeDTO.setUpdateTime(new Date());
            dictCodeDao.save(dictCodeDTO);
        }
    }

    @CacheEvict(value = "dictCodeCache")
    @Override
    public void delete(String id) {
        //this.delete(id);
        dictCodeDao.delete(id);
    }

    @Override
    public List<DictCodeDTO> findChildren(String string) {
        List<DictCodeDTO> findChildren = dictCodeDao.findChildren(string);
        return findChildren;
    }

    @Override
    public DictCodeDTO findOneself(String string) {
        return dictCodeDao.findByDictName(string);
    }
    @Override
    public List<DictCodeDTO> findByParentId(String parentId){
        return dictCodeDao.findByParentId(parentId);
    }
}

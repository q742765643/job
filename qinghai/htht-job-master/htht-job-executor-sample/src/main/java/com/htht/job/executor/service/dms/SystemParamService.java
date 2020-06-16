package com.htht.job.executor.service.dms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import com.htht.job.executor.dao.dms.SystemParamDao;
import com.htht.job.executor.model.dms.module.SystemParam;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.dms.util.db.QueryCondition;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;

/**
 * 
 * @author LY 2018-07-19
 * 
 */
@Transactional
@Service("systemParamService")
public class SystemParamService extends BaseService<SystemParam> {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Autowired
	private SystemParamDao systemParamDao;
	@PersistenceContext
	protected EntityManager em;

	@Override
	public BaseDao<SystemParam> getBaseDao() {
		return systemParamDao;
	}

	public List<SystemParam> findAll() {
		return this.getAll();
	}

	/**
	 * 按照id查询数据
	 */
	public SystemParam getById(String id) {
		return systemParamDao.findOne(id);
	}

	/**
	 * 修改SystemParam实体信息
	 * 
	 * @param SystemParam
	 *            实体对象
	 * @return 返回1 修改成功 0 修改失败
	 * @author LY 2018/03/29
	 */
	public int update(SystemParam SystemParam) {
		int num = 1;
		try {
			systemParamDao.save(SystemParam);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 删除SystemParam实体信息
	 * 
	 * @param id
	 *            需要删除的实体主键
	 * @return 1 删除成功 0删除失败
	 * @author LY 2018-03-29
	 */
	public int del(String id) {
		int num = 1;
		try {
			systemParamDao.delete(id);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 根据code查询
	 * 
	 * @return
	 */
	public List<SystemParam> getSystemParamByCode(String paramcode) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition(" paramcode = " + paramcode));

		List<SystemParam> SystemParams = baseDaoUtil.get(SystemParam.class, queryConditions);

		return SystemParams;
	}

	//获取系统参数列表
	public String list(int start, int length, String searchText, String id) {
		SimpleSpecificationBuilder<SystemParam> builder = new SimpleSpecificationBuilder<SystemParam>();
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        if(StringUtils.isNotBlank(searchText)){
            builder.add("paramname", "likeAll", searchText);
        }
        /*if (!StringUtils.isEmpty(id)) {
        	builder.addOr("parentId","eq",id);
        }*/
        Page<SystemParam> page = this.getPage(builder.generateSpecification(),d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return JSON.toJSONString(maps,SerializerFeature.WriteMapNullValue);
	}

	/**
	 * @param systemParam
	 */
	public void saveOrUpdateSystemParam(SystemParam systemParam) {
		if(StringUtils.isBlank(systemParam.getId())) {
			
			systemParam.setCreateTime(new Date());
			systemParamDao.save(systemParam);
		}
		else{
			
			SystemParam db = systemParamDao.getOne(systemParam.getId());
			db.setParamcode(systemParam.getParamcode());
			db.setParamname(systemParam.getParamname());
			db.setParamvalue(systemParam.getParamvalue());
			db.setUpdateTime(new Date());
			systemParamDao.save(db);
		}
	}

	
	

}

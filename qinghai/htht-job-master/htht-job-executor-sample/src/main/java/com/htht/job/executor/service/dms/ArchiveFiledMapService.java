package com.htht.job.executor.service.dms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.dms.ArchiveFiledMapDao;
import com.htht.job.executor.model.dms.module.ArchiveFiledMap;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.dms.util.db.QueryCondition;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;

/**
 * 
 * @author LY 2018-04-08
 * 
 */
@Transactional
@Service("archiveFiledMapService")
public class ArchiveFiledMapService extends BaseService<ArchiveFiledMap> {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Autowired
	private ArchiveFiledMapDao archiveFiledMapDao;
	@PersistenceContext
	protected EntityManager em;

	@Override
	public BaseDao<ArchiveFiledMap> getBaseDao() {
		return archiveFiledMapDao;
	}

	public List<ArchiveFiledMap> findAll() {
		return this.getAll();
	}

	/**
	 * 按照id查询数据
	 */
	public ArchiveFiledMap getById(String id) {
		return archiveFiledMapDao.findOne(id);
	}

	/**
	 * 修改ArchiveFiledMap实体信息
	 * 
	 * @param archiveFiledMap
	 *            实体对象
	 * @return 返回1 修改成功 0 修改失败
	 * @author LY 2018/04/08
	 */
	public int update(ArchiveFiledMap archiveFiledMap) {
		int num = 1;
		try {
			archiveFiledMapDao.save(archiveFiledMap);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 删除ArchiveFiledMap实体信息
	 * 
	 * @param id
	 *            需要删除的实体主键
	 * @return 1 删除成功 0删除失败
	 * @author LY 2018-04-08
	 */
	public int del(String id) {
		int num = 1;
		try {
			archiveFiledMapDao.delete(id);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 根据cologcode获取需要入库的xml节点对象
	 * 
	 * @return
	 */
	public List<ArchiveFiledMap> getArchiveFiledMap(String catalogcode) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
//		queryConditions.add(new QueryCondition("f_catalogcode", QueryCondition.EQ, catalogcode));
		queryConditions.add(new QueryCondition(" f_catalogcode ='"+catalogcode+"'"));
		try {
			List<ArchiveFiledMap> archiveFiledMaps = baseDaoUtil.get(ArchiveFiledMap.class, queryConditions);
			return archiveFiledMaps;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList();
		}
		
	}

	public int saves(List<ArchiveFiledMap> fileMapList, String id, String catalogcode) {
		List<ArchiveFiledMap> findAll = getByArchiveRuleId(id);
		if(findAll.size()>0) {
			archiveFiledMapDao.deleteInBatch(findAll);
		}
		int i=1;
		try {
			for (ArchiveFiledMap archiveFiledMap : fileMapList) {
				archiveFiledMap.setArchive_rule_id(id);
				archiveFiledMap.setCreateTime(new Date());
				archiveFiledMap.setF_id(UUID.randomUUID().toString().replace("-", ""));
				archiveFiledMap.setF_catalogcode(catalogcode);
				archiveFiledMap.setId(null);
			}
			archiveFiledMapDao.save(fileMapList);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return i=-1;
		}
		
		return i;
	}

	public List<ArchiveFiledMap> getByArchiveRuleId(String archiveRuleId) {
		SimpleSpecificationBuilder<ArchiveFiledMap> builder = new SimpleSpecificationBuilder<ArchiveFiledMap>();
		if(StringUtils.isNotBlank(archiveRuleId)){
            builder.add("archive_rule_id", "eq", archiveRuleId);
        }
		List<ArchiveFiledMap> findAll = archiveFiledMapDao.findAll(builder.generateSpecification());
		return archiveFiledMapDao.findAll(builder.generateSpecification());
	}

	public ReturnT<String> deltearchiveFiledMaps(String id) {
		try {
			SimpleSpecificationBuilder<ArchiveFiledMap> builder = new SimpleSpecificationBuilder<ArchiveFiledMap>();
			builder.add("archive_rule_id", "eq", id);
			List<ArchiveFiledMap> findAll = archiveFiledMapDao.findAll(builder.generateSpecification());
			archiveFiledMapDao.deleteInBatch(findAll);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
		
		return ReturnT.SUCCESS;
	}

}

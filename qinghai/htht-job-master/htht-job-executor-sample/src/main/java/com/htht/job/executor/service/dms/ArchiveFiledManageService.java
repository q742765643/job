package com.htht.job.executor.service.dms;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htht.job.executor.dao.dms.ArchiveFiledManageDao;
import com.htht.job.executor.model.dms.module.ArchiveFiledManage;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.dms.util.db.QueryCondition;

/**
 * 
 * @author LY 2018-04-08
 * 
 */
@Transactional
@Service("archiveFiledManageService")
public class ArchiveFiledManageService extends BaseService<ArchiveFiledManage> {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Autowired
	private ArchiveFiledManageDao archiveFiledManageDao;
	@PersistenceContext
	protected EntityManager em;

	@Override
	public BaseDao<ArchiveFiledManage> getBaseDao() {
		return archiveFiledManageDao;
	}

	public List<ArchiveFiledManage> findAll() {
		return this.getAll();
	}

//	/**
//	 * 按照id查询数据
//	 */
//	public ArchiveFiledManage getById(String id) {
//		return archiveFiledManageDao.findOne(id);
//	}

	/**
	 * 修改ArchiveFiledManage实体信息
	 * 
	 * @param archiveFiledManage
	 *            实体对象
	 * @return 返回1 修改成功 0 修改失败
	 * @author LY 2018/04/08
	 */
	public int update(ArchiveFiledManage archiveFiledManage) {
		int num = 1;
		try {
			archiveFiledManageDao.save(archiveFiledManage);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 删除ArchiveFiledManage实体信息
	 * 
	 * @param id
	 *            需要删除的实体主键
	 * @return 1 删除成功 0删除失败
	 * @author LY 2018-04-08
	 */
	public int del(String id) {
		int num = 1;
		try {
			archiveFiledManageDao.delete(id);
		} catch (Exception e) {
			num = 0;
			// TODO Auto-generated catch block
			throw new RuntimeException();
		}
		return num;
	}

	/**
	 * 按照id查询数据
	 * 
	 * @param id
	 *            
	 * @return
	 */
	public ArchiveFiledManage getById(String id) {
		
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition("f_id", QueryCondition.EQ, id));
		List<ArchiveFiledManage> archiveFiledManage = baseDaoUtil.get(ArchiveFiledManage.class, queryConditions);
		return archiveFiledManage.size() > 0 ? archiveFiledManage.get(0) : null;
	}
	
}

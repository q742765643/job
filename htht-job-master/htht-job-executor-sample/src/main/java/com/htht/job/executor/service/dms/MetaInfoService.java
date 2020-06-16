package com.htht.job.executor.service.dms;

import com.htht.job.executor.dao.dms.MetaInfoDao;
import com.htht.job.executor.model.dms.module.MetaInfo;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.dms.util.db.QueryCondition;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LY 2018-03-29
 */
@Transactional
@Service("metaInfoService")
public class MetaInfoService extends BaseService<MetaInfo> {
    @PersistenceContext
    protected EntityManager em;
    @Autowired
    private BaseDaoUtil baseDaoUtil;
    @Autowired
    private MetaInfoDao metaInfoDao;

    @Override
    public BaseDao<MetaInfo> getBaseDao() {
        return metaInfoDao;
    }

    public List<MetaInfo> findAll() {
        return this.getAll();
    }

//	/**
//	 * 按照id查询数据
//	 */
//	public MetaInfo getById(String id) {
//		return metaInfoDao.findOne(id);
//	}
//	/**
//	 * 按照id查询数据
//	 * 
//	 * @param id
//	 *            
//	 * @return
//	 */
	public MetaInfo getBeanId(String id) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		queryConditions.add(new QueryCondition("id", QueryCondition.EQ, id));
		List<MetaInfo> metaInfo = baseDaoUtil.get(MetaInfo.class, queryConditions);
		return metaInfo.size() > 0 ? metaInfo.get(0) : null;
	}

    /**
     * 修改MetaInfo实体信息
     *
     * @param metaInfo 实体对象
     * @return 返回1 修改成功 0 修改失败
     * @author LY 2018/04/03
     */
    public int update(MetaInfo metaInfo) {
        int num = 1;
        try {
            metaInfoDao.saveAndFlush(metaInfo);
        } catch (Exception e) {
            e.printStackTrace();
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

    /**
     * 删除MetaInfo实体信息
     *
     * @param id 需要删除的实体主键
     * @return 1 删除成功 0删除失败
     * @author LY 2018-04-03
     */
    public int del(String id) {
        int num = 1;
        try {
            metaInfoDao.delete(id);
        } catch (Exception e) {
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

    /**
     * 判断数据是否存在
     *
     * @param dataname 文件名称
     * @return
     */
    public boolean verifyDataExists(String dataname) {
        List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
        queryConditions.add(new QueryCondition("f_datasourcename", QueryCondition.EQ, dataname));
        List<MetaInfo> metaInfos = baseDaoUtil.get(MetaInfo.class, queryConditions);
        return metaInfos.size() > 0 ? true : false;
    }

    /**
     * 修改回收状态
     *
     * @param f_recycleflag
     * @param id
     */
    public void updateRecycleflag(Integer f_recycleflag, String id) {
        metaInfoDao.updateRecycleflag(f_recycleflag, id);
    }

}

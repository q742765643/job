package com.htht.job.executor.service.dms;

import com.htht.job.executor.dao.dms.MetaImgDao;
import com.htht.job.executor.model.dms.module.MetaImg;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;
import com.htht.job.executor.model.dms.util.db.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.search.Specifications;
import org.jeesys.common.jpa.search.Specifications.Builder;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * @author LY 2018-03-29
 */
@Transactional
@Service("metaImgService")
public class MetaImgService extends BaseService<MetaImg> {
    @PersistenceContext
    protected EntityManager em;
    @Autowired
    private BaseDaoUtil baseDaoUtil;
    @Autowired
    private MetaImgDao MetaImgDao;

    @Override
    public BaseDao<MetaImg> getBaseDao() {
        return MetaImgDao;
    }

    public List<MetaImg> findAll() {
        return this.getAll();
    }

//	/**
//	 * 按照id查询数据
//	 */
//	public MetaImg getById(String id) {
//		return MetaImgDao.findOne(id);
//	}
//	/**
//	 * 按照id查询数据
//	 * 
//	 * @param id
//	 *            
//	 * @return
//	 */
//	public MetaImg getBeanId(String id) {
//		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
//		queryConditions.add(new QueryCondition("id", QueryCondition.EQ, id));
//		List<MetaImg> MetaImg = baseDaoUtil.get(MetaImg.class, queryConditions);
//		return MetaImg.size() > 0 ? MetaImg.get(0) : null;
//	}

    /**
     * 修改MetaImg实体信息
     *
     * @param MetaImg 实体对象
     * @return 返回1 修改成功 0 修改失败
     * @author LY 2018/04/03
     */
    public int update(MetaImg MetaImg) {
        int num = 1;
        try {
            MetaImgDao.saveAndFlush(MetaImg);
        } catch (Exception e) {
            e.printStackTrace();
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

    /**
     * 删除MetaImg实体信息
     *
     * @param id 需要删除的实体主键
     * @return 1 删除成功 0删除失败
     * @author LY 2018-04-03
     */
    public int del(String id) {
        int num = 1;
        try {
            MetaImgDao.delete(id);
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
        List<MetaImg> MetaImgs = baseDaoUtil.get(MetaImg.class, queryConditions);
        return MetaImgs.size() > 0 ? true : false;
    }

    public Map<String, Object> pageList(int start, int length, String fCatalogcode, Date fProducetimeStart, Date fProducetimeEnd, String fLevel, Integer fCloudamount, String fSatelliteid) {
//		SimpleSpecificationBuilder<MetaImg> builder = new SimpleSpecificationBuilder<MetaImg>();
        Builder<MetaImg> s = Specifications.builder();
        Sort sort = new Sort(Sort.Direction.DESC, "fDataid");
        PageRequest d = new PageRequest(start, length, sort);
        s.ge("fProducetime", fProducetimeStart);
        s.le("fProducetime", fProducetimeEnd);

        if (StringUtils.isNotBlank(fCatalogcode)) {
            //s.ge(true, "fCatalogcode", fCatalogcode);
            s.like(true, "fCatalogcode", fCatalogcode, MatchMode.START);
        }
        if (StringUtils.isNotBlank(fLevel)) {
            s.like(true, "fLevel", fLevel);
        }
        if (StringUtils.isNotBlank(fSatelliteid)) {
            s.like(true, "fSatelliteid", fSatelliteid);
        }
        if (fCloudamount > 0) {
            s.eq("fCloudamount", fCloudamount);
        }

        Page<MetaImg> page = this.getPage(s.build(), d);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return maps;
    }

}

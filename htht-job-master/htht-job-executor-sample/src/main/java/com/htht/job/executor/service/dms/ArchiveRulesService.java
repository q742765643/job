package com.htht.job.executor.service.dms;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.dms.ArchiveRulesDao;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LY 2018-03-29
 */
@Transactional
@Service("archiveRulesService")
public class ArchiveRulesService extends BaseService<ArchiveRules> {
    @PersistenceContext
    protected EntityManager em;
    @Autowired
    private ArchiveRulesDao archiveRulesDao;

    @Override
    public BaseDao<ArchiveRules> getBaseDao() {
        return archiveRulesDao;
    }

    public List<ArchiveRules> findAll() {
        return this.getAll();
    }

    /**
     * 按照id查询数据
     */
    public ArchiveRules getById(String id) {
        return archiveRulesDao.findOne(id);
    }

    /**
     * 修改ArchiveRules实体信息
     *
     * @param archiveRules 实体对象
     * @return 返回1 修改成功 0 修改失败
     * @author LY 2018/03/29
     */
    public int update(ArchiveRules archiveRules) {
        int num = 1;
        try {
            archiveRulesDao.save(archiveRules);
        } catch (Exception e) {
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

    /**
     * 删除ArchiveRules实体信息
     *
     * @param id 需要删除的实体主键
     * @return 1 删除成功 0删除失败
     * @author LY 2018-03-29
     */
    public int del(String id) {
        int num = 1;
        try {
            archiveRulesDao.delete(id);
        } catch (Exception e) {
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

    public Map<String, Object> pageListArchiveRules(int start, int length, ArchiveRules archiveRules) {
        Page<ArchiveRules> page = this.findArchiveRules(start, length,
                archiveRules);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return maps;
    }

    private Page<ArchiveRules> findArchiveRules(int start, int length, ArchiveRules archiveRules) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        Page<ArchiveRules> page = this.getPage(
                getWhereClause(archiveRules), d);
        return page;
    }

    private Specification<ArchiveRules> getWhereClause(ArchiveRules archiveRules) {
        SimpleSpecificationBuilder<ArchiveRules> specification = new SimpleSpecificationBuilder();
        if (!StringUtils.isEmpty(archiveRules.getCatalogcode())) {
            specification.add("catalogcode", "likeAll", archiveRules.getCatalogcode());
        }
        return specification.generateSpecification();
    }

    public int saveArchiveRules(ArchiveRules archiveRules) {
        int num = 1;
        try {
            if (!StringUtils.isEmpty(archiveRules.getId())) {
                ArchiveRules dbArchiveRules = this.getById(archiveRules.getId());
                archiveRules.setCreateTime(dbArchiveRules.getCreateTime());
                archiveRules.setUpdateTime(new Date());

                archiveRulesDao.update(archiveRules.getId(), archiveRules.getRegexpjpg(),
                        archiveRules.getRegexpstr(), archiveRules.getRegexpxml(),
                        archiveRules.getRulestatus(), archiveRules.getCatalogcode(),
                        archiveRules.getArchivdisk(), archiveRules.getAllFile(), archiveRules.getCreateTime(),
                        archiveRules.getDatalevel(), archiveRules.getFiletype(), archiveRules.getFinalstr(),
                        archiveRules.getFlowid(), archiveRules.getUpdateTime(),
                        archiveRules.getWspFile());
            } else {
                archiveRules.setCreateTime(new Date());
                archiveRulesDao.save(archiveRules);
            }
        } catch (Exception e) {
            num = -1;
            e.printStackTrace();
        }


        return num;
    }

    public String findArchiveRules(ArchiveRules archiveRules) {
        SimpleSpecificationBuilder<ArchiveRules> builder = new SimpleSpecificationBuilder<ArchiveRules>();
        if (StringUtils.isNotBlank(archiveRules.getRegexpstr())) {
            builder.add("regexpstr", "eq", archiveRules.getRegexpstr());
        }
        if (StringUtils.isNotBlank(archiveRules.getCatalogcode())) {
            builder.add("catalogcode", "eq", archiveRules.getCatalogcode());
        }
        if (StringUtils.isNotBlank(archiveRules.getRegexpxml())) {
            builder.add("regexpxml", "eq", archiveRules.getRegexpxml());
        }
        if (StringUtils.isNotBlank(archiveRules.getRegexpjpg())) {
            builder.add("regexpjpg", "eq", archiveRules.getRegexpjpg());
        }
        if (StringUtils.isNotBlank(archiveRules.getArchivdisk())) {
            builder.add("archivdisk", "eq", archiveRules.getArchivdisk());
        }
        if (StringUtils.isNotBlank(archiveRules.getFinalstr())) {
            builder.add("finalstr", "eq", archiveRules.getFinalstr());
        }
        if (StringUtils.isNotBlank(archiveRules.getDatalevel())) {
            builder.add("datalevel", "eq", archiveRules.getDatalevel());
        }
        if (StringUtils.isNotBlank(archiveRules.getWspFile())) {
            builder.add("wspFile", "eq", archiveRules.getWspFile());
        }
        if (StringUtils.isNotBlank(archiveRules.getAllFile())) {
            builder.add("allFile", "eq", archiveRules.getAllFile());
        }
        if (archiveRules.getRulestatus() > 0) {
            builder.add("rulestatus", "eq", archiveRules.getRulestatus());
        }
        if (archiveRules.getFlowid() > 0) {
            builder.add("flowid", "eq", archiveRules.getFlowid());
        }
        if (archiveRules.getFiletype() > 0) {
            builder.add("filetype", "eq", archiveRules.getFiletype());
        }
        ArchiveRules findOne = archiveRulesDao.findOne(builder.generateSpecification());
        return findOne.getId();
    }

    public ReturnT<String> findArchiveRulesList(String catalogCode) {
        SimpleSpecificationBuilder<ArchiveRules> builder = new SimpleSpecificationBuilder<ArchiveRules>();
        if (StringUtils.isNotBlank(catalogCode)) {
            builder.add("catalogcode", "likeL", catalogCode);
        }
        List<ArchiveRules> findAll = archiveRulesDao.findAll(builder.generateSpecification());
        if (findAll.size() > 0) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    public int enableOrdisable(String id, String rulestatus) {
        int num = 1;
        try {
            archiveRulesDao.updateEnableOrdisable(id, Integer.parseInt(rulestatus));
        } catch (Exception e) {
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

    public int updateDisk(String id, String archivdisk) {
        int num = 1;
        try {
            archiveRulesDao.updateDisk(id, archivdisk);
        } catch (Exception e) {
            num = 0;
            throw new RuntimeException();
        }
        return num;
    }

}

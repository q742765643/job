package com.htht.job.executor.service.algorithm;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.algorithm.AtomicAlgorithmDao;
import com.htht.job.executor.dao.downupload.AlgoNodeRelationDao;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
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
import javax.persistence.Query;
import java.util.*;

/**
 * @program: htht-job
 * @description: 算法模型逻辑层
 * @author: zzj
 * @create: 2018-03-25 11:13
 **/
@Transactional(rollbackFor = Exception.class)
@Service("atomicAlgorithmService")
public class AtomicAlgorithmServiceImpl extends BaseService<AtomicAlgorithmDTO> implements
        AtomicAlgorithmService {
    @Autowired
    private AtomicAlgorithmDao atomicAlgorithmDao;
    @Autowired
    private AlgoNodeRelationDao algoNodeRelationDao;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BaseDao<AtomicAlgorithmDTO> getBaseDao() {
        return atomicAlgorithmDao;
    }

    @Override
    public AtomicAlgorithmDTO saveParameter(AtomicAlgorithmDTO atomicAlgorithmDTO) {
        AtomicAlgorithmDTO newparameterModel = new AtomicAlgorithmDTO();
        if (!StringUtils.isEmpty(atomicAlgorithmDTO.getId())) {
            AtomicAlgorithmDTO yparameterModel = this.getById(atomicAlgorithmDTO
                    .getId());
            yparameterModel.setUpdateTime(new Date());
            yparameterModel.setModelName(atomicAlgorithmDTO.getModelName());
            yparameterModel.setModelIdentify(atomicAlgorithmDTO.getModelIdentify());
            yparameterModel.setFixedParameter(atomicAlgorithmDTO
                    .getFixedParameter());
            yparameterModel.setDynamicParameter(atomicAlgorithmDTO
                    .getDynamicParameter());
            yparameterModel.setType(atomicAlgorithmDTO.getType());
            yparameterModel.setUrl(atomicAlgorithmDTO.getUrl());
            if (!com.mysql.jdbc.StringUtils.isNullOrEmpty(atomicAlgorithmDTO.getAlgoPath())) {
                yparameterModel.setAlgoPath(atomicAlgorithmDTO.getAlgoPath());
            }
            yparameterModel.setAlgoType(atomicAlgorithmDTO.getAlgoType());
            yparameterModel.setExecutorBlockStrategy(atomicAlgorithmDTO.getExecutorBlockStrategy());
            yparameterModel.setDealAmount(atomicAlgorithmDTO.getDealAmount());
            newparameterModel = this.save(yparameterModel);
        } else {
            atomicAlgorithmDTO.setCreateTime(new Date());
            newparameterModel = this.save(atomicAlgorithmDTO);
        }
        return newparameterModel;
    }

    @Override
    public Map<String, Object> pageList(int start, int length,
                                        AtomicAlgorithmDTO atomicAlgorithmDTO) {
        Page<AtomicAlgorithmDTO> page = this.findPageParameter(start, length,
                atomicAlgorithmDTO);
        Map<String, Object> maps = new HashMap<String, Object>(20);
        // 总记录数
        maps.put("recordsTotal", page.getTotalElements());
        // 过滤后的总记录数
        maps.put("recordsFiltered", page.getTotalElements());
        // 分页列表
        maps.put("data", page.getContent());
        return maps;
    }

    public Page<AtomicAlgorithmDTO> findPageParameter(int start, int length,
                                                      AtomicAlgorithmDTO atomicAlgorithmDTO) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        Page<AtomicAlgorithmDTO> page = this.getPage(
                getWhereClause(atomicAlgorithmDTO), d);
        return page;
    }

    @Override
    public List<AtomicAlgorithmDTO> findListParameter() {
        SimpleSpecificationBuilder<AtomicAlgorithmDTO> specification = new SimpleSpecificationBuilder();
        specification.add("modelIdentify", "notEqual", "").add("modelIdentify", "isNotNull", "");

        List<AtomicAlgorithmDTO> list = this.getAll(specification.generateSpecification());
        return list;
    }

    @Override
    public ReturnT<String> deleteParameter(String id) {
        int b = algoNodeRelationDao.findByalgoId(id).size();
        int c = atomicAlgorithmDao.findById(id).size();
        if (!(c > 0) && !(b > 0)) {
            try {
                this.delete(id);
                return ReturnT.SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ReturnT<String> ret = new ReturnT<String>();
            ret.setCode(ReturnT.FAIL_CODE);
            ret.setMsg("in use");
            return ret;
        }
        return ReturnT.FAIL;
    }

    @Override
    public AtomicAlgorithmDTO findParameterById(String id) {
        return this.getById(id);
    }

    /**
     * 动态生成where语句
     *
     * @param
     * @param
     * @return
     */
    private Specification<AtomicAlgorithmDTO> getWhereClause(
            AtomicAlgorithmDTO atomicAlgorithmDTO) {
        SimpleSpecificationBuilder<AtomicAlgorithmDTO> specification = new SimpleSpecificationBuilder();
        if (!StringUtils.isEmpty(atomicAlgorithmDTO.getModelName())) {
            specification.add("modelName", "likeAll", atomicAlgorithmDTO.getModelName());
        }
        if (!StringUtils.isEmpty(atomicAlgorithmDTO.getModelIdentify())) {
            specification.add("modelIdentify", "likeAll", atomicAlgorithmDTO.getModelIdentify());
        }
        if (!StringUtils.isEmpty(atomicAlgorithmDTO.getTreeId())) {
            specification.add("treeId", "eq", atomicAlgorithmDTO.getTreeId());
        }
        return specification.generateSpecification();
    }


    public String findHaveDataBySql() {
        String sql = "select CONCAT(t.parent_id,',',t.id) as ids from htht_cluster_schedule_data_category t,htht_cluster_schedule_atomic_algorithm p where p.tree_id=t.id";

        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(org.hibernate.SQLQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> list = query.getResultList();
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < list.size(); i++) {
            String[] ids = ((String) list.get(i).get("ids")).split(",");
            for (int j = 0; j < ids.length; j++) {
                set.add(ids[j]);
            }
        }
        List<String> listIds = new ArrayList<String>();
        listIds.addAll(set);
        String inId = "";
        for (int i = 0; i < listIds.size(); i++) {
            if (i == (listIds.size() - 1)) {
                inId += "'" + listIds.get(i) + "'";
            } else {
                inId += "'" + listIds.get(i) + "'" + ",";
            }

        }
        return inId;
    }

    @Override
    public List<Map> findTreeListBySql() {
        String ids = this.findHaveDataBySql();

        String sql = "select id,parent_id,text from htht_cluster_schedule_data_category  where 1=1 ";
        if (!StringUtils.isEmpty(ids)) {
            sql += " and id in (" + ids + ")";
        }
        sql += "union select id,tree_id as parent_id,model_name as text from htht_cluster_schedule_atomic_algorithm ";
        sql += "union select id,'flow' as parent_id,process_chname as text from htht_cluster_schedule_flow_chart ";

        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(org.hibernate.SQLQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> list = query.getResultList();
        Map map = new HashMap();
        map.put("id", "flow");
        map.put("parent_id", "0");
        map.put("text", "流程");
        list.add(map);
        return list;

    }

    @Override
    public boolean updateForTree(String id, String treeId) {
        AtomicAlgorithmDTO atomicAlgorithmDTO = this.getById(id);
        atomicAlgorithmDTO.setTreeId(treeId);
        this.save(atomicAlgorithmDTO);
        return true;
    }

    @Override
    public AtomicAlgorithmDTO queryAogoInfo(String id) {
        return atomicAlgorithmDao.findInfoById(id);
    }

    @Override
    public AtomicAlgorithmDTO findModelIdentifyById(String id) {
        return atomicAlgorithmDao.findModelIdentifyById(id);
    }
}

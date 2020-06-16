package com.htht.job.executor.service.algorithm;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.dao.algorithm.AtomicAlgorithmDao;
import com.htht.job.executor.dao.downupload.AlgoNodeRelationDao;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
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
@Transactional(rollbackFor=Exception.class)
@Service("atomicAlgorithmService")
public class AtomicAlgorithmServiceImpl extends BaseService<AtomicAlgorithm> implements
		AtomicAlgorithmService {
	@Autowired
	private AtomicAlgorithmDao atomicAlgorithmDao;
	@Autowired
	private AlgoNodeRelationDao algoNodeRelationDao;
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public BaseDao<AtomicAlgorithm> getBaseDao() {
		// TODO Auto-generated method stub
		return atomicAlgorithmDao;
	}
	@Override
	public AtomicAlgorithm saveParameter(AtomicAlgorithm atomicAlgorithm) {
		AtomicAlgorithm newparameterModel = new AtomicAlgorithm();
		if (!StringUtils.isEmpty(atomicAlgorithm.getId())) {
			AtomicAlgorithm yparameterModel = this.getById(atomicAlgorithm
					.getId());
			yparameterModel.setUpdateTime(new Date());
			yparameterModel.setModelName(atomicAlgorithm.getModelName());
			yparameterModel.setModelIdentify(atomicAlgorithm.getModelIdentify());
			yparameterModel.setFixedParameter(atomicAlgorithm
					.getFixedParameter());
			yparameterModel.setDynamicParameter(atomicAlgorithm
					.getDynamicParameter());
            yparameterModel.setType(atomicAlgorithm.getType());
            yparameterModel.setUrl(atomicAlgorithm.getUrl());
            if(!com.mysql.jdbc.StringUtils.isNullOrEmpty(atomicAlgorithm.getAlgoPath())){
            	yparameterModel.setAlgoPath(atomicAlgorithm.getAlgoPath());
            }
            yparameterModel.setAlgoType(atomicAlgorithm.getAlgoType());
            yparameterModel.setExecutorBlockStrategy(atomicAlgorithm.getExecutorBlockStrategy());
			yparameterModel.setDealAmount(atomicAlgorithm.getDealAmount());
			newparameterModel = this.save(yparameterModel);
		} else {
			atomicAlgorithm.setCreateTime(new Date());
			newparameterModel = this.save(atomicAlgorithm);
		}
		return newparameterModel;
	}
    @Override
	public Map<String, Object> pageList(int start, int length,
			AtomicAlgorithm atomicAlgorithm) {
		Page<AtomicAlgorithm> page = this.findPageParameter(start, length,
				atomicAlgorithm);
		Map<String, Object> maps = new HashMap<String, Object>(20);
		// 总记录数
		maps.put("recordsTotal", page.getTotalElements());
		// 过滤后的总记录数
		maps.put("recordsFiltered", page.getTotalElements());
		// 分页列表
		maps.put("data", page.getContent());
		return maps;
	}

	public Page<AtomicAlgorithm> findPageParameter(int start, int length,
												   AtomicAlgorithm atomicAlgorithm) {
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		PageRequest d = new PageRequest(start, length, sort);
		Page<AtomicAlgorithm> page = this.getPage(
				getWhereClause(atomicAlgorithm), d);
		return page;
	}
	@Override
	public List<AtomicAlgorithm> findListParameter() {
		SimpleSpecificationBuilder<AtomicAlgorithm> specification=new SimpleSpecificationBuilder();
		specification.add("modelIdentify","notEqual","").add("modelIdentify","isNotNull","");

		List<AtomicAlgorithm> list = this.getAll(specification.generateSpecification());
		return list;
	}
	@Override
	public ReturnT<String> deleteParameter(String id) {
		int b = algoNodeRelationDao.findByalgoId(id).size();
		int c = atomicAlgorithmDao.findById(id).size();
		if(!(c > 0)&&!(b > 0)) {
			try {
				this.delete(id);
				return ReturnT.SUCCESS;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			ReturnT<String> ret = new ReturnT<String>();
			ret.setCode(ReturnT.FAIL_CODE);
			ret.setMsg("in use");
			return ret;
		}
		return ReturnT.FAIL;
	}
	@Override
	public AtomicAlgorithm findParameterById(String id) {
		return this.getById(id);
	}

	/**
	 * 动态生成where语句
	 *
	 * @param
	 * @param
	 * @return
	 */
	private Specification<AtomicAlgorithm> getWhereClause(
			AtomicAlgorithm atomicAlgorithm) {
		SimpleSpecificationBuilder<AtomicAlgorithm> specification=new SimpleSpecificationBuilder();
		if (!StringUtils.isEmpty(atomicAlgorithm.getModelName())) {
			specification.add("modelName","likeAll", atomicAlgorithm.getModelName());
		}
		if (!StringUtils.isEmpty(atomicAlgorithm.getModelIdentify())) {
			specification.add("modelIdentify","likeAll", atomicAlgorithm.getModelIdentify());
		}
		if (!StringUtils.isEmpty(atomicAlgorithm.getTreeId())) {
			specification.add("treeId","eq", atomicAlgorithm.getTreeId());
		}
		return specification.generateSpecification();
	}


	public String findHaveDataBySql(){
		String sql="select CONCAT(t.parent_id,',',t.id) as ids from htht_cluster_schedule_data_category t,htht_cluster_schedule_atomic_algorithm p where p.tree_id=t.id";

		Query query = entityManager.createNativeQuery(sql);
		query.unwrap(org.hibernate.SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map>  list = query.getResultList();
		Set<String> set=new  HashSet<String>();
		for (int i=0;i<list.size();i++){
                String[] ids= ((String) list.get(i).get("ids")).split(",");
                for(int j=0;j<ids.length;j++){
					set.add(ids[j]);
				}
		}
		List<String> listIds=new ArrayList<String>();
		listIds.addAll(set);
		String inId="";
		for(int i=0;i<listIds.size();i++){
			if(i==(listIds.size()-1)){
				inId += "'"+listIds.get(i)+"'";
			}else {
				inId += "'"+listIds.get(i)+"'"+",";
			}

		}
        return inId;
	}
	@Override
	public List<Map> findTreeListBySql(){
		String ids =this.findHaveDataBySql();

		String sql="select id,parent_id,text from htht_cluster_schedule_data_category  where 1=1 ";
		if(!StringUtils.isEmpty(ids)){
			sql+=" and id in ("+ids+")";
		}
		sql+="union select id,tree_id as parent_id,model_name as text from htht_cluster_schedule_atomic_algorithm ";
		sql+="union select id,'flow' as parent_id,process_chname as text from htht_cluster_schedule_flow_chart ";

		Query query = entityManager.createNativeQuery(sql);
		query.unwrap(org.hibernate.SQLQuery.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map>  list = query.getResultList();
		Map map=new HashMap();
		map.put("id","flow");
		map.put("parent_id","0");
		map.put("text","流程");
		list.add(map);
		return  list;

	}
	@Override
	public boolean updateForTree(String id, String treeId) {
		AtomicAlgorithm atomicAlgorithm = this.getById(id);
    	atomicAlgorithm.setTreeId(treeId);
		this.save(atomicAlgorithm);
		return true;
	}

	@Override
	public AtomicAlgorithm queryAogoInfo(String id) {
		return atomicAlgorithmDao.findInfoById(id);
	}

	@Override
	public AtomicAlgorithm findModelIdentifyById(String id) {
		return atomicAlgorithmDao.findModelIdentifyById(id);
	}
}

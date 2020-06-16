package com.htht.job.executor.service.registryalgo;

import com.htht.job.executor.dao.registryalgo.RegistryAlgoDao;
import com.htht.job.executor.model.registryalgo.RegistryAlgo;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
@Service("registryAlgoService")
public class RegistryAlgoService extends BaseService<RegistryAlgo>{
	
    @Autowired
    private RegistryAlgoDao registryAlgoDao;

	@Override
	public BaseDao<RegistryAlgo> getBaseDao() {
		return registryAlgoDao;
	}
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public RegistryAlgo save(RegistryAlgo registryAlgo){
		RegistryAlgo saveRegistryAlgo = registryAlgoDao.save(registryAlgo);
		return saveRegistryAlgo;
	}

	public List<RegistryAlgo> registryAlgoService(String id) {
		SimpleSpecificationBuilder<RegistryAlgo> specification=new SimpleSpecificationBuilder();
		if (!StringUtils.isEmpty(id)) {
			specification.add("algoId","eq",id);
			List<RegistryAlgo> list = this.getAll(specification.generateSpecification());
			return list;
		}
		return null;
	}

	public void deleteRegistryAlgoByAlgoId(String id) {
		registryAlgoDao.deleteRegistryAlgoByAlgoId(id);
	}

	public void delAlgoRegByRegId(String id) {
		registryAlgoDao.delAlgoRegByRegId(id);
	}

}



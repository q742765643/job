package com.htht.job.executor.service.registryalgo;

import com.htht.job.executor.dao.registryalgo.RegistryAlgoDao;
import com.htht.job.executor.model.registryalgo.RegistryAlgoDTO;
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
public class RegistryAlgoService extends BaseService<RegistryAlgoDTO> {

    @Autowired
    private RegistryAlgoDao registryAlgoDao;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BaseDao<RegistryAlgoDTO> getBaseDao() {
        return registryAlgoDao;
    }
    

    public RegistryAlgoDTO save(RegistryAlgoDTO registryAlgoDTO) {
        RegistryAlgoDTO saveRegistryAlgoDTO = registryAlgoDao.save(registryAlgoDTO);
        return saveRegistryAlgoDTO;
    }

    public List<RegistryAlgoDTO> registryAlgoService(String id) {
        SimpleSpecificationBuilder<RegistryAlgoDTO> specification = new SimpleSpecificationBuilder();
        if (!StringUtils.isEmpty(id)) {
            specification.add("algoId", "eq", id);
            List<RegistryAlgoDTO> list = this.getAll(specification.generateSpecification());
            return list;
        }
        return null;
    }

	
	public List<RegistryAlgoDTO> getRegistListByRegistryId(String id) {
		SimpleSpecificationBuilder<RegistryAlgoDTO> specification=new SimpleSpecificationBuilder();
		if (!StringUtils.isEmpty(id)) {
			specification.add("registryId","eq",id);
			List<RegistryAlgoDTO> list = this.getAll(specification.generateSpecification());
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


	public void deleteRegistryAlgoByRegistryAlgo(String regId, String algoId) {
		registryAlgoDao.deleteRegistryAlgoByRegistryAlgo(regId,algoId);
		
	}

}



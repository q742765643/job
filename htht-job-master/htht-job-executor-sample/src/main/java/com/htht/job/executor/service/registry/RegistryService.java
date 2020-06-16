package com.htht.job.executor.service.registry;

import com.htht.job.executor.dao.registry.RegistryDao;
import com.htht.job.executor.model.registry.RegistryDTO;
import com.htht.job.executor.model.registryalgo.RegistryAlgoDTO;
import com.htht.job.executor.service.registryalgo.RegistryAlgoService;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zzj on 2018/1/30.
 */
@Transactional
@Service("registryService")
public class RegistryService extends BaseService<RegistryDTO> {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private RegistryDao registryDao;
    @Autowired
    private RegistryAlgoService registryAlgoService;

    @Override
    public BaseDao<RegistryDTO> getBaseDao() {
        return registryDao;
    }

    @CacheEvict(value = "RegistryIp", key = "'ip' + #registryDTO.registryIp")
    public void saveOrUpdate(RegistryDTO registryDTO) {
        RegistryDTO yRegistryDTO = this.findByWhere(registryDTO.getRegistryIp());
        if (yRegistryDTO == null) {
            registryDTO.setCreateTime(new Date());
            registryDao.save(registryDTO);
        }/*else{
            yRegistry.setMqConcurrency(registry.getMqConcurrency());
            yRegistry.setMqDestination(registry.getMqDestination());
            yRegistry.setDeploySystem(registry.getDeploySystem());
            yRegistry.setUpdateTime(new Date());
            registryDao.save(yRegistry);
        }*/
    }

    public RegistryDTO findByWhere(String registryIp) {
        SimpleSpecificationBuilder<RegistryDTO> specification = new SimpleSpecificationBuilder();
        //specification.add("registryKey","eq",registryKey);
        specification.add("registryIp", "eq", registryIp);
        //specification.add("port","eq",port);
        RegistryDTO registryDTO = super.getBySpecification(specification.generateSpecification());
        return registryDTO;
    }

    public RegistryDTO findById(String id) {
        SimpleSpecificationBuilder<RegistryDTO> specification = new SimpleSpecificationBuilder();
        specification.add("id", "eq", id);
        RegistryDTO registryDTO = super.getBySpecification(specification.generateSpecification());
        return registryDTO;
    }

    @Cacheable(value = "RegistryIp", key = "'ip' + #registryIp")
    public RegistryDTO findByRegistryIp(String registryIp) {
        return registryDao.findByRegistryIp(registryIp);
    }

    public List<RegistryDTO> findAll() {
        return registryDao.findAll();
    }

    @CacheEvict(value = "RegistryIp", key = "'ip' + #registryDTO.registryIp")
    public RegistryDTO update(RegistryDTO registryDTO) {
        RegistryDTO yRegistryDTO = this.findById(registryDTO.getId());
        if (yRegistryDTO == null) {
            registryDTO.setId("");
            return registryDTO;
        } else {
            yRegistryDTO.setDeploySystem(registryDTO.getDeploySystem());
            yRegistryDTO.setRegistryKey(registryDTO.getRegistryKey());
            yRegistryDTO.setRegistryIp(registryDTO.getRegistryIp());
            yRegistryDTO.setConcurrency(registryDTO.getConcurrency());
            yRegistryDTO.setUpdateTime(new Date());
            RegistryDTO save = registryDao.save(yRegistryDTO);
            return save;
        }
    }

    public String remove(String id) {
        try {
            registryDao.delete(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public List<String> findAddressList(String id, int dealAmount) {
        ArrayList<String> addressList = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();
        List<RegistryAlgoDTO> registryAlgoDTOList = registryAlgoService.registryAlgoService(id);
        for (RegistryAlgoDTO registryAlgoDTO : registryAlgoDTOList) {
            ids.add(registryAlgoDTO.getRegistryId());
        }
        if (ids.size() > 0) {
            SimpleSpecificationBuilder<RegistryDTO> specification = new SimpleSpecificationBuilder();
            specification.add("id", "in", ids);

            List<RegistryDTO> registryDTOList = this.getAll(specification.generateSpecification());
            for (RegistryDTO registryDTO : registryDTOList) {
                if (registryDTO.getConcurrency() > dealAmount) {
                    addressList.add(registryDTO.getRegistryIp());
                }
            }
        }
        return addressList;
    }

    public boolean existTable() {
        try {
            String sql_p = " SHOW TABLES LIKE '%htht_cluster_schedule_executesql_log%'";
            List<String> pList = entityManager.createNativeQuery(sql_p).getResultList();
            if (pList.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}

package com.htht.job.executor.service.registry;

import com.htht.job.executor.dao.registry.RegistryDao;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.executor.model.registryalgo.RegistryAlgo;
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
public class RegistryService extends BaseService<Registry>{
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private RegistryDao registryDao;
    @Autowired
    private RegistryAlgoService registryAlgoService;
    @Override
    public BaseDao<Registry> getBaseDao() {
        return registryDao;
    }
    @CacheEvict(value = "RegistryIp", key = "'ip' + #registry.registryIp")
    public void saveOrUpdate(Registry registry){
        Registry yRegistry=this.findByWhere(registry.getRegistryIp());
        if(yRegistry==null){
            registry.setCreateTime(new Date());
            registryDao.save(registry);
        }/*else{
            yRegistry.setMqConcurrency(registry.getMqConcurrency());
            yRegistry.setMqDestination(registry.getMqDestination());
            yRegistry.setDeploySystem(registry.getDeploySystem());
            yRegistry.setUpdateTime(new Date());
            registryDao.save(yRegistry);
        }*/
    }
    public Registry findByWhere(String registryIp){
        SimpleSpecificationBuilder<Registry> specification=new SimpleSpecificationBuilder();
        //specification.add("registryKey","eq",registryKey);
        specification.add("registryIp","eq",registryIp);
        //specification.add("port","eq",port);
        Registry registry=super.getBySpecification(specification.generateSpecification());
        return registry;
    }
    public Registry findById(String id){
        SimpleSpecificationBuilder<Registry> specification=new SimpleSpecificationBuilder();
        specification.add("id","eq",id);
        Registry registry=super.getBySpecification(specification.generateSpecification());
        return registry;
    }
    @Cacheable(value = "RegistryIp", key = "'ip' + #registryIp")
    public Registry findByRegistryIp(String registryIp){
        return  registryDao.findByRegistryIp(registryIp);
    }

    public List<Registry> findAll(){
        return registryDao.findAll();
    }
    @CacheEvict(value = "RegistryIp", key = "'ip' + #registry.registryIp")
    public Registry update(Registry registry) {
		Registry yRegistry=this.findById(registry.getId());
        if(yRegistry==null){
            registry.setId("");
            return registry;
        }else{
        	yRegistry.setRegistryKey(registry.getRegistryKey());
            yRegistry.setRegistryIp(registry.getRegistryIp());
            yRegistry.setConcurrency(registry.getConcurrency());
            yRegistry.setUpdateTime(new Date());
            Registry save = registryDao.save(yRegistry);
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

    public ArrayList<String> findAddressList(String id,int dealAmount){
        ArrayList<String> addressList=new ArrayList<String>();
        List<String> ids=new ArrayList<String>();
        List<RegistryAlgo> registryAlgoList=registryAlgoService.registryAlgoService(id);
        for(RegistryAlgo registryAlgo:registryAlgoList){
            ids.add(registryAlgo.getRegistryId());
        }
        if(ids.size()>0) {
            SimpleSpecificationBuilder<Registry> specification = new SimpleSpecificationBuilder();
            specification.add("id", "in", ids);

            List<Registry> registryList = this.getAll(specification.generateSpecification());
            for (Registry registry : registryList) {
                if(registry.getConcurrency()>dealAmount) {
                    addressList.add(registry.getRegistryIp());
                }
            }
        }
        return addressList;
    }

    public boolean existTable() {
        try {
            String sql_p = " SHOW TABLES LIKE '%htht_cluster_schedule_executesql_log%'";
            List<String> pList = entityManager.createNativeQuery(sql_p).getResultList();
            if (pList.size()>0){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}

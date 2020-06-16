package com.htht.job.executor.dao.registry;

import com.htht.job.executor.model.registry.RegistryDTO;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * Created by zzj on 2018/1/30.
 */
@Repository
public interface RegistryDao extends BaseDao<RegistryDTO> {
    RegistryDTO findByRegistryIp(String registryIp);
}

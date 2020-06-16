package com.htht.job.executor.service.monitor;

import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.executor.model.registry.RegistryDTO;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.vo.NodeMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zzj on 2018/1/31.
 */
@Transactional
@Service("monitorService")
public class MonitorService {
    @Autowired
    private RegistryService registryService;
    @Autowired
    private RedisService redisService;

    public List<NodeMonitor> findAll() {
        List<NodeMonitor> monitors = new ArrayList<NodeMonitor>();
        List<RegistryDTO> registries = registryService.findAll();
        for (RegistryDTO registryDTO : registries) {
            NodeMonitor monitor = new NodeMonitor();
            monitor.setAppName(registryDTO.getRegistryKey());
            monitor.setIp(registryDTO.getRegistryIp());
            String key = registryDTO.getRegistryIp();
            Set<String> nodeLineQueue = redisService.fuzzyQuery(key + MonitorQueue.NODE_LINE_QUEUE);
            long lineNum = nodeLineQueue.size();
            //   long lineNum=redisService.getListSize(MonitorQueue.NODE_LINE_QUEUE +key);
            Set<String> nodeOperationQueue = redisService.fuzzyQuery(key + MonitorQueue.NODE_OPERATION_QUEUE);
            long operationNum = nodeOperationQueue.size();
            //   long operationNum=redisService.getListSize(MonitorQueue.NODE_OPERATION_QUEUE +key);;
            monitor.setLineNum(lineNum);
            monitor.setOperationNum(operationNum);
            monitor.setzNum(registryDTO.getConcurrency());
            monitor.setId(registryDTO.getId());
            monitor.setDeploySystem(registryDTO.getDeploySystem());
            monitors.add(monitor);
        }
        return monitors;
    }


}

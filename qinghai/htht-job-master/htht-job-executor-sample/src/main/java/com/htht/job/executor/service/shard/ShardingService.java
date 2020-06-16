package com.htht.job.executor.service.shard;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.util.SpringContextUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/12.
 */
@Transactional
@Service("shardingService")
public class ShardingService {

    public ResultUtil<List<String>> execute(String params, String handler, LinkedHashMap fixmap, LinkedHashMap dymap)  {
        ResultUtil<List<String>> result= null;
        try {
            SharingHandler sharingHandler = (SharingHandler) SpringContextUtil.getBean(handler+"Shard");
            result = sharingHandler.execute(params,fixmap,dymap);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return result;
    }

}

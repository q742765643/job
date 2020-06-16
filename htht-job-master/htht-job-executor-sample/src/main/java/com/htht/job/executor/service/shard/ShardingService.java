package com.htht.job.executor.service.shard;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboCallBackService;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/12.
 */
@Transactional
@Service("shardingService")
public class ShardingService {
    @Autowired
    private DubboCallBackService dubboCallBackService;

    public void execute(String methodMap) {
        Runnable race = () -> {
            Map map = (Map) JSON.parse(methodMap);
            String modelParameters = (String) map.get(JobConstant.MODEL_PARAMETERS);
            Map<String, String> fixmap = (Map) map.get(JobConstant.FIX_MAP);
            Map<String, String> dymap = (Map) map.get(JobConstant.DY_MAP);
            String handler = (String) map.get(JobConstant.JOB_HANDLER);
            LinkedHashMap fixLinkMap = new LinkedHashMap();
            LinkedHashMap dyLinkMap = new LinkedHashMap();

            ResultUtil<List<String>> result = null;
            Iterator<Map.Entry<String, String>> entries = fixmap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                fixLinkMap.put(entry.getKey(), entry.getValue());
            }
            Iterator<Map.Entry<String, String>> entriesDy = dymap.entrySet().iterator();
            while (entriesDy.hasNext()) {
                Map.Entry<String, String> entry = entriesDy.next();
                dyLinkMap.put(entry.getKey(), entry.getValue());
            }

            try {
                SharingHandler sharingHandler = (SharingHandler) SpringContextUtil.getBean(handler + "Shard");
                result = sharingHandler.execute(modelParameters, fixLinkMap, dyLinkMap);
                if(null==result.getResult()||result.getResult().isEmpty()){
                    dubboCallBackService.insertFailLog("没有数据",methodMap,200);
                    return;
                }
                if(!result.isSuccess()){
                    dubboCallBackService.insertFailLog(result.getMessage(),methodMap,500);
                    return;
                }
                dubboCallBackService.broadScheduler(result.getResult(), methodMap, fixLinkMap, dyLinkMap);

            } catch (Exception e) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                String errorMsg = stringWriter.toString();
                dubboCallBackService.insertFailLog(errorMsg,methodMap,500);
            }

        };
        race.run();


    }

}

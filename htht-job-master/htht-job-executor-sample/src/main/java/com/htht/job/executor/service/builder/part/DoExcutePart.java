package com.htht.job.executor.service.builder.part;

import com.htht.job.core.biz.model.ReturnT;

import java.util.Map;

public interface DoExcutePart {
    void doExcute(String inputXml, ReturnT<String> result, Map<String, Object> jobDataMap);
}

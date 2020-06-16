package com.htht.job.executor.service.builder.part;

import com.htht.job.core.biz.model.ReturnT;

import java.util.Map;

public interface PraseXmlPart {
    void toDb(String outputXml, ReturnT<String> result, Map<String, Object> jobDataMap);
}

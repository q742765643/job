package com.htht.job.executor.service.builder;

import com.htht.job.executor.service.builder.part.*;

import java.util.HashMap;
import java.util.Map;

public class ProductHandlerBuilder {
    private IssuePart issueHandler;
    private IsExistPart isExistPart;
    private CreateXmlPart createXmlPart;
    private DoExcutePart doExcutePart;
    private PraseXmlPart praseXmlPart;
    private Map<String, Object> jobDataMap;

    private ProductHandlerBuilder() {
        super();
        jobDataMap = new HashMap<String, Object>();
    }

    public static ProductHandlerBuilder newBuilder() {
        return new ProductHandlerBuilder();
    }

    public ProductHandlerBuilder useIssueHandler(IssuePart issueHandler) {
        this.issueHandler = issueHandler;
        return this;
    }

    public ProductHandlerBuilder useIsExistPart(IsExistPart isExistPart) {
        this.isExistPart = isExistPart;
        return this;
    }

    public ProductHandlerBuilder useCreateXmlPart(CreateXmlPart createXmlPart) {
        this.createXmlPart = createXmlPart;
        return this;
    }

    public ProductHandlerBuilder useDoExcutePart(DoExcutePart doExcutePart) {
        this.doExcutePart = doExcutePart;
        return this;
    }

    public ProductHandlerBuilder usePraseXmlPart(PraseXmlPart praseXmlPart) {
        this.praseXmlPart = praseXmlPart;
        return this;
    }

    public ProductHandlerBuilder useJobDataMap(Map<String, Object> jobDataMap) {
        this.jobDataMap.putAll(jobDataMap);
        return this;
    }

    public ProductHandlerBuilder useJobDataMap(String key, Object value) {
        this.jobDataMap.put(key, value);
        return this;
    }

    public ProductHandler builer() {
        return new ProductHandler(issueHandler, isExistPart, createXmlPart, doExcutePart, praseXmlPart, jobDataMap);
    }
}

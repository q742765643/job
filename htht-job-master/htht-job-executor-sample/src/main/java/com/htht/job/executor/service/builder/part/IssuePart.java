package com.htht.job.executor.service.builder.part;

import com.htht.job.core.biz.model.ReturnT;

import java.util.List;
import java.util.Map;

public interface IssuePart {
    List<String> getIssueList(ReturnT<String> result, Map<String, Object> jobDataMap);
}

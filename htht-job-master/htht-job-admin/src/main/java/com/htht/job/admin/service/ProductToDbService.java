package com.htht.job.admin.service;

import com.htht.job.core.biz.model.ReturnT;

/**
 * 统一产品入库接口
 *
 * @author Administrator
 *         2018年10月22日
 */
public interface ProductToDbService {
    @Deprecated
    public ReturnT<String> toDb(String startIssue, String endIssue, String productType);

    public ReturnT<String> toDbByJson(String json);
}

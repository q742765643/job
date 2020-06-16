package com.htht.job.admin.service;

import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.core.util.ResultUtil;

/**
 * Created by zzj on 2018/3/27.
 */
public interface FlowService {
    ResultUtil<String> add(XxlJobInfo jobInfo);

    ResultUtil<String> updateSave(XxlJobInfo jobInfo);
}

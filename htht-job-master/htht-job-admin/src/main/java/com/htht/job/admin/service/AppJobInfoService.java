package com.htht.job.admin.service;

import com.htht.job.admin.core.model.app.AppXxlJobInfo;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.CommonParameter;

import java.util.List;

/**
 * Created by zzj on 2018/3/14.
 */
public interface AppJobInfoService {

    public ResultUtil<String> add(AppXxlJobInfo jobInfoAppXxlJobInfo, List<CommonParameter> fixedParameter, List<CommonParameter> dynamicParameter);

}

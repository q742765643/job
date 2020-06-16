package com.htht.job.executor.hander.webservicehandler.service.Impl;

import java.util.Date;
import java.util.List;

import com.htht.job.core.util.DateUtil;
import com.htht.job.executor.hander.webservicehandler.service.GetDataByWebServiceService;
import com.htht.job.executor.hander.webservicehandler.utils.ModisUtil;

import org.springframework.stereotype.Service;
import org.apache.axis2.AxisFault;

/**
 * @program: htht-job-api
 * @description: ServiceClient方式接口数据下载服务
 * @author: dingjiancheng
 * @create: 2018-08-24 09:38
 */
@Service("GetDataByServiceClientServiceImpl")
public class GetDataByWebServiceServiceImpl implements GetDataByWebServiceService
{

    @Override
    public List<String> getUrlsByServiceClient(Date beginTime, Date endTime, String dataType) throws AxisFault {
        return ModisUtil.getSingleton().getURLs(DateUtil.formatDateTime(beginTime, "yyyy-MM-dd"),
        		DateUtil.formatDateTime(endTime, "yyyy-MM-dd"), dataType);
    }
}

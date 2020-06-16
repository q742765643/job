package com.htht.job.executor.hander.webservicehandler.service.Impl;

import com.htht.job.executor.hander.webservicehandler.service.GetDataByWebServiceService;
import com.htht.job.executor.hander.webservicehandler.utils.ModisUtil;
import org.springframework.stereotype.Service;

import org.apache.axis2.AxisFault;
import java.util.*;

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
        return ModisUtil.getSingleton().getURLs(org.htht.util.DateUtil.dateToStr(beginTime),
                org.htht.util.DateUtil.dateToStr(endTime), dataType);
    }
}

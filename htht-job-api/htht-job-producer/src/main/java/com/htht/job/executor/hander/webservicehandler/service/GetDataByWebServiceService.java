package com.htht.job.executor.hander.webservicehandler.service;

import org.apache.axis2.AxisFault;

import java.util.Date;
import java.util.List;

public interface GetDataByWebServiceService {
    public List<String> getUrlsByServiceClient(Date beginTime, Date endTime, String dataType) throws AxisFault;
}

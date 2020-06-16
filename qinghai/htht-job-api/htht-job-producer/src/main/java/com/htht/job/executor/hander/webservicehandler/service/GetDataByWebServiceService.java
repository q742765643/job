package com.htht.job.executor.hander.webservicehandler.service;

import org.apache.axis2.AxisFault;
import org.springframework.stereotype.Service;
import org.apache.axiom.om.OMElement;

import javax.xml.stream.XMLStreamReader;
import java.util.Date;
import java.util.List;

public interface GetDataByWebServiceService {
    public List<String> getUrlsByServiceClient(Date beginTime, Date endTime, String dataType) throws AxisFault;
}

package com.htht.job.executor.hander.cimiss.service;

/**
 * Cimiss 业务接口
 * 
 * @author gqy
 *
 */
public interface CimissInterfaceService {
	
	public boolean getCimissData(String interfaceId,String dataCode,String times ,String adminCodes ,String elements ,String dataFormat ,String queryCondition,String limitCnt,String onlyAdd)throws Exception;

	
}

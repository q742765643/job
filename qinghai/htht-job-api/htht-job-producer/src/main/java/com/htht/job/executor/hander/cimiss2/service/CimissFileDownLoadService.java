package com.htht.job.executor.hander.cimiss2.service;

import java.util.Map;

/**
 * Cimiss 业务接口
 *
 */
public interface CimissFileDownLoadService {
	
	public void getCimissData(String ip, String port, String userid, String password,String filepath, String filename,
							  Map<String,String> map)throws Exception;

	
}

package com.htht.job.executor.hander.predatahandler.service;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface DataMataInfoService {
	
	
	public List<String> findDataToProject(Map<String, Object> map);
	public Date findDataByFileName(String fileName);
	public List<String> findDataToProjectNdvi(Map<String, Object> map);
	public Date findDataByFileNameAndLevel(String fileName, String level);

}

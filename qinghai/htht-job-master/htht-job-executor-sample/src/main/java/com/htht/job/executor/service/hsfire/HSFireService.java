package com.htht.job.executor.service.hsfire;

import java.util.List;

import com.htht.job.executor.model.hsfire.HSFire;

public interface HSFireService {
	
	public List<HSFire> findFireByIssue(String issue);
	
	public HSFire saveHSFire(HSFire hsFire);

}

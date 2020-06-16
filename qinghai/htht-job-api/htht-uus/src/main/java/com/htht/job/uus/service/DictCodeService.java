package com.htht.job.uus.service;

import com.htht.job.uus.model.DictCode;

public interface DictCodeService {

	public DictCode getNameByDictCode(String dictCode); 
	
	public DictCode getDictCodeByName(String dictName); 
}

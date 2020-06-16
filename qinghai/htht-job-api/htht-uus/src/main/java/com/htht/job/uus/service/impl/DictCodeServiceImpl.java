package com.htht.job.uus.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.htht.job.uus.dao.DictCodeDao;
import com.htht.job.uus.model.DictCode;
import com.htht.job.uus.service.DictCodeService;

@Service
public class DictCodeServiceImpl implements DictCodeService {

	@Resource
	private DictCodeDao dictCodeDao;

	@Override
	public DictCode getNameByDictCode(String dictCode) {

		DictCode uusTitle = new DictCode();
		if (dictCode == null) {
			dictCode = "uusTitle";
		}
		uusTitle = dictCodeDao.getNameByDictCode(dictCode);
		if (null == uusTitle) {
			uusTitle = new DictCode("遥感发布平台");
		}

		return uusTitle;
	}

	@Override
	public DictCode getDictCodeByName(String dictName) {
		
		DictCode dictCode = new DictCode();
		dictCode = dictCodeDao.getDictCodeByName(dictName);
		return dictCode;
	}

}

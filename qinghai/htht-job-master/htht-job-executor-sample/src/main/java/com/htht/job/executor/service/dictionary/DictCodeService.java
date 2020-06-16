package com.htht.job.executor.service.dictionary;

import java.util.List;

import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.datacategory.ZtreeView;

/**
 * @date:2018年6月27日下午2:45:44
 * @author:yss
 */
public interface DictCodeService {


	/**
	 * @return
	 */
	List<ZtreeView> allTree();

	/**
	 * @param start
	 * @param length
	 * @param searchText
	 * @return
	 */
	String list(int start, int length, String searchText,String id);

	/**
	 * @param dictCode
	 */
	void saveOrUpdateDicCode(DictCode dictCode);

	/**
	 * @param id
	 */
	void delete(String id);

	List<DictCode> findChildren(String string);

	DictCode findOneself(String string);

}

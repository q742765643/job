package com.htht.job.executor.service.dictionary;

import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dictionary.DictCodeDTO;

import java.util.List;

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
    String list(int start, int length, String searchText, String id);

    /**
     * @param dictCodeDTO
     */
    void saveOrUpdateDicCode(DictCodeDTO dictCodeDTO);

    /**
     * @param id
     */
    void delete(String id);

    List<DictCodeDTO> findChildren(String string);

    DictCodeDTO findOneself(String string);

    List<DictCodeDTO> findByParentId(String parentId);

}

package com.htht.job.executor.model.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.htht.job.core.util.BaseEntity;

/**
 * @date:2018年6月27日下午2:16:03
 * @author:yss 字典项管理表
 */
@Entity
@Table(name = "htht_cluster_schedule_dict_code")
public class DictCode extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	
	private String parentId;

	/**
	 * 字典编码
	 */
	private String dictCode;

	/**
	 * 字典项名称
	 */
	private String dictName;


	/**
	 * 是否树
	 */
	private Integer isTree;

	/**
	 * 备注
	 */
	private String memo;

	/**
	 * 排序值，一般值越小，越靠前
	 */
	private Integer sortOrder;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 修改人
	 */
	private String updateBy;

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getDictCode() {
		return dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public Integer getIsTree() {
		return isTree;
	}

	public void setIsTree(Integer isTree) {
		this.isTree = isTree;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	
	


}

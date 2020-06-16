package com.htht.job.core.utilbean;

import java.io.Serializable;

public class AlgoManageEntity  implements Serializable{
	public static final long serialVersionUID = 42L;
	
	private String algoId;
	private String modelName;
	private String algoZipName;
	public String getRegistryId() {
		return registryId;
	}
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}
	private Boolean isMapping;
	private Boolean isDownload;
	private String registryId;
	
	public String getAlgoId() {
		return algoId;
	}
	public Boolean getIsMapping() {
		return isMapping;
	}
	public void setIsMapping(Boolean isMapping) {
		this.isMapping = isMapping;
	}
	public Boolean getIsDownload() {
		return isDownload;
	}
	public void setIsDownload(Boolean isDownload) {
		this.isDownload = isDownload;
	}
	public void setAlgoId(String algoId) {
		this.algoId = algoId;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getAlgoZipName() {
		return algoZipName;
	}
	public void setAlgoZipName(String algoZipName) {
		this.algoZipName = algoZipName;
	}

	
}

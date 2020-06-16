package com.htht.job.admin.core.model.resolve;

import java.util.List;

public class ProjType extends ZtreeViewPie {


	private String projType;

	private List<Level> levelList;

	public String getProjType() {
		return projType;
	}

	public void setProjType(String projType) {
		this.projType = projType;
	}

	public List<Level> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<Level> levelList) {
		this.levelList = levelList;
	}
	
	

}

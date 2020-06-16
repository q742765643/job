package com.htht.job.uus.model.viewModel;

import java.util.List;

import com.htht.job.uus.model.HSFire;
import com.htht.job.uus.model.ProductInfo;

public class HSFireView extends ProductInfo {

	private List<HSFire> h8Fires;

	public List<HSFire> getH8Fires() {
		return h8Fires;
	}

	public void setH8Fires(List<HSFire> h8Fires) {
		this.h8Fires = h8Fires;
	}

	public HSFireView() {
		super();
	}
	
	public HSFireView(List<HSFire> h8Fires) {
		super();
		this.h8Fires = h8Fires;
	}
	
	public HSFireView(ProductInfo productInfo, List<HSFire> h8Fires) {
		super();
		this.setCreateTime(productInfo.getCreateTime());
		this.setCycle(productInfo.getCycle());
		this.setFeatureName(productInfo.getFeatureName());
		this.setGdbPath(productInfo.getGdbPath());
		this.setH8Fires(h8Fires);
		this.setId(productInfo.getId());
		this.setInputFileName(productInfo.getInputFileName());
		this.setIssue(productInfo.getIssue());
		this.setMapUrl(productInfo.getMapUrl());
		this.setMark(productInfo.getMark());
		this.setModelIdentify(productInfo.getModelIdentify());
		this.setMosaicFile(productInfo.getMosaicFile());
		this.setProductId(productInfo.getProductId());
		this.setProductPath(productInfo.getProductPath());
		this.setRegionId(productInfo.getRegionId());
		this.setUpdateTime(productInfo.getUpdateTime());
		this.setVersion(productInfo.getVersion());
		this.setName(productInfo.getName());
		this.setBz(productInfo.getBz());
		this.setIsRelease(productInfo.getIsRelease());
	}
	
	
}

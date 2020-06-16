package com.htht.job.executor.hander.cimiss2.module;

import java.util.List;
import java.util.Map;

public class ResultBeanOne {
	private String returnCode;
	private String returnMessage;
	private String fileCount;
	private String colCount;
	private String requestParams;
	private String requestTime;
	private String responseTime;
	private String takeTime;
	private String fieldNames;
	private String fieldUnits;
	private List<Map<String,String>> DS;

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public String getFileCount() {
		return fileCount;
	}

	public void setFileCount(String fileCount) {
		this.fileCount = fileCount;
	}

	public String getColCount() {
		return colCount;
	}

	public void setColCount(String colCount) {
		this.colCount = colCount;
	}

	public String getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getTakeTime() {
		return takeTime;
	}

	public void setTakeTime(String takeTime) {
		this.takeTime = takeTime;
	}

	public String getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String getFieldUnits() {
		return fieldUnits;
	}

	public void setFieldUnits(String fieldUnits) {
		this.fieldUnits = fieldUnits;
	}

	public List<Map<String, String>> getDS() {
		return DS;
	}

	public void setDS(List<Map<String, String>> DS) {
		this.DS = DS;
	}
}

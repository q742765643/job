package com.htht.job.executor.hander.cimiss.module;

import java.util.List;

public class ResultBean {
	private String returnCode;
	private String returnMessage;
	private String rowCount;
	private String colCount;
	private String requestParams;
	private String requestTime;
	private String responseTime;
	private String takeTime;
	private String[] fieldNames;
	private String[] fieldUnits;
	private List data;

	public List getData() {
		return this.data;
	}

	public void setData(List data) {
		this.data = data;
	}

	public String getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMessage() {
		return this.returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public String getRowCount() {
		return this.rowCount;
	}

	public void setRowCount(String rowCount) {
		this.rowCount = rowCount;
	}

	public String getColCount() {
		return this.colCount;
	}

	public void setColCount(String colCount) {
		this.colCount = colCount;
	}

	public String getRequestParams() {
		return this.requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}

	public String getRequestTime() {
		return this.requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getResponseTime() {
		return this.responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getTakeTime() {
		return this.takeTime;
	}

	public void setTakeTime(String takeTime) {
		this.takeTime = takeTime;
	}

	public String[] getFieldNames() {
		return this.fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String[] getFieldUnits() {
		return this.fieldUnits;
	}

	public void setFieldUnits(String[] fieldUnits) {
		this.fieldUnits = fieldUnits;
	}
}

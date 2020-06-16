package com.htht.job.core.util;

import java.io.Serializable;

public class ResultUtil<T> implements Serializable{

	private int code = ReturnCodeEnum.SUCCESS.getKey();
	private String message;
	private T result;
	
	
	public boolean isSuccess() {
		if(ReturnCodeEnum.SUCCESS.getKey()==code) {
			return true;
		}
		return false;
	}
	public void setErrorMessage(ReturnCodeEnum code) {
    	this.code = code.getKey();
    	this.message = code.getValue();
    }
	public void setErrorMessage(String message) {
    	this.code = ReturnCodeEnum.FIAL.getKey();
    	this.message = message;
    }
    public void setMessage(ReturnCodeEnum code,String message) {
    	this.code = code.getKey();
    	this.message = message;
    }
    
    
    
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
	public String toString() {
		return "ReturnT [code=" + code + ", msg=" + message + ", result=" + result + "]";
	}


}

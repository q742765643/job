package com.htht.job.uus.util;

public class ResponseModel
{
	private int status;
	private int code;
	private Object data;

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object dada)
	{
		this.data = dada;
	}

	public ResponseModel()
	{
		super();
	}

}

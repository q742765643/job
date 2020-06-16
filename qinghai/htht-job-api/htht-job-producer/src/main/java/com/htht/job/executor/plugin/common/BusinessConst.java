package com.htht.job.executor.plugin.common;

public final class BusinessConst {
	/**
	 * 调度任务类型：1实时    2历史
	 */
	public static final int  PROCESSTASKPRODUCT_DATETYPE_REAL_TIME=1;
	
	/**
	 * 调度任务类型：1实时    2历史
	 */
	public static final int PROCESSTASKPRODUCT_DATETYPE_HISTORICAL_TIME= 2;
	
	/**
	 * 结果 : 0成功 1失败
	 */
	public static final int RESULT_CODE_FAIL = 1;
	
	/**
	 * 结果 : 0成功 1失败
	 */
	public static final int RESULT_CODE_SUCCESS = 0;
	
	/**
	 * 产品基础路径
	 */
	public static final String BASE_PRODUCT_PATH = "E:/QH_Data/production";
	
	/**
	 * inputxml基础路径
	 */
	public static final String BASE_INPUTXML_PATH = "E:/QH_NQdata/inputXML/";
	
	/**
	 * log日志基础路径
	 */
	public static final String BASE_LOG_PATH = "E:/QH_NQData/log/";
	
}

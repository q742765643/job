package com.htht.job.executor.util;
/**
 * 业务产品辅助工具类
 * @author zhanghongda
 *
 */
public class ProductUtil {
	
	/**
	 * 期号采用12位：文件时间yyyyMMddHHmm,不足后面补0
	 * @param issue
	 * @param length
	 * @return
	 */
	public static String makeIssuetoLength(String issue,int length){
		if(issue!=null&&issue.length()<length){
			StringBuilder issueSb = new StringBuilder(issue);
			for(int i=0;i<length-issue.length();i++){
				issueSb.append("0");
			}
			return issueSb.toString();
		}else{
			return issue;
		}
	}
}

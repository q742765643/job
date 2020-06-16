package org.htht.util;

/**
 * @(#)UtilInteger.java  1.00 
 * Apr 26, 2008 4:14:47 PM
 * Copyright (c) 2007-2008 __MyCorp 有限公司 版权所有
 * __Mycorp Company of China. All rights reserved.
 * 
 * This software is the confidential and proprietary
 * information of __Mycorp Company of China.
 *
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with __Mycorp.
 * 
 * 整型工具类
 */

public class UtilInteger {
	/**
	 * 将字符串转换为int
	 * @param string 指定字符串
	 * @return 返回转换后的int类型值
	 */
	public static int getInteger(String string){
		try{
			return Integer.parseInt(string);
		}catch(Exception e){
			return -1;
		}
	}
	/**
	 * 将字符串转换为int,出现异常时返回默认值
	 * @param string 指定字符串
	 * @param d 默认值
	 * @return 返回转换后的int类型值
	 */
	public static int getInteger(String string,int d){
		try{
			return Integer.parseInt(string);
		}catch(Exception e){
			return d;
		}
	}
	
	/**
	 * 将短整型转换为int型
	 * @param i 指定的短整型
	 * @return 返回转换后的int类型值
	 */
	public static int getInteger(short i){
		return (int)i;
	}

}

package org.htht.util;
/**
 * @(#)WebUtil.java  1.00 
 * Apr 26, 2008 4:27:56 PM
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
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;




public class WebUtil {
	/**
	 * 获取包含context的完整路径：http://127.0.0.1/xxx ("/xxx"为contextPath)
	 * @param request
	 * @return
	 */
	public static String getBasePath(HttpServletRequest request){
		String urlStr = request.getRequestURL().toString();
		String basePath = urlStr.substring(0,urlStr.indexOf(request.getContextPath()));
		return request.getContextPath();
	}
	/**
	 * 获取“WEB-INF”之前的绝对路径
	 * @return
	 */
	public static String getBasePath() {
		URL url = WebUtil.class.getResource("WebUtil.class");
		String path = url.getPath();
		path = path.substring(0, path.lastIndexOf("WEB-INF"));
		return path;
	}




	/**
	 * 获取URL中？号后的参数
	 * @param url
	 *           URL对象
	 * @param key
	 *           URL query key
	 * @return value
	 */
	public static String getParameter(URL url,String key){
		return UtilString.string2map(url.getQuery(),"&").get(key);
	}
	/**
	 * 把一个符合URL格式的字符串转换成URL对象
	 * @param string
	 * @return url(URL对象) 非URL对象返回null
	 * @throws MalformedURLException
	 */
	public static URL string2url(String string){
		try {
			return new URL(string);
		} catch (MalformedURLException e) {
			return null;
		}
	}
	/**
	 * 获取Parameter，
	 * @param request
	 * @return Map<String,String>
	 */
	public static Map<String,String> getParameter(HttpServletRequest request){
		Map<String,String> map = new HashMap();
		Map<String,String[]> pMap = request.getParameterMap();
		Enumeration<String> e = request.getParameterNames();
		try {
			while(e.hasMoreElements()){
				String key = e.nextElement();
				String[] value = pMap.get(key);
				map.put(key,new String(value[0].getBytes("ISO8859_1")));
				//map.put(key, value[0]);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return map;
	}

	public static void main(String[] arg){
		System.out.println(getBasePath());  //  /E:/workspace/testcms/WebRoot/
	}
}

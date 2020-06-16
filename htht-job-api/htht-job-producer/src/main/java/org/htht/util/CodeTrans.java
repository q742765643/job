package org.htht.util;
/**
 * @(#)CodeTrans.java  1.00 
 * Apr 26, 2008 3:50:06 PM
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class CodeTrans {
	
	public static String trans(String path,String template) {
		String s, s2 = new String();
		try(
			BufferedReader reader = new BufferedReader(new FileReader(path));
			){
			while ((s = reader.readLine()) != null) {
				s = UtilString.replaceAll(s,"\"", "\\\"");
				s = UtilString.replaceAll(s,"\"", "\\\"");		
				s = template.replaceAll("%line%",s);
				s2 += s;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s2;
	}

	public static void main(String[] args) {
		String p = WebUtil.getBasePath();
		System.out.println(p);
		String path="d:/code.txt";
		String template="code.append(\"%line%\\n\");\n";
		try {
			System.out.println(trans(path,template));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
}

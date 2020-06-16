package org.htht.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class TxtUtil {

	/**
	 * 功能描述：读文件。
	 * @param path: 文件路径。
	 * @return 文件的所有内容。
	 */	
	public String readTxt(String path){
		StringBuffer strContext = null;
		String line = "";
		try(
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"GBK"));  
			){
			strContext = new StringBuffer();
			while ((line = reader.readLine()) != null) {					
				line = line+" ";			
				line = line.replaceAll("\\s{1,}", " ").trim();
				if (line.length() == 0){
					continue;
				}
				strContext.append(line + " ");
			}	
			if(strContext != null){
				return strContext.toString();
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 创建文本文件
	 * @param fileName
	 * @param context
	 * @param path
	 * @return
	 */
	public String creatTxt(String filePath,String context){
		File filename = new File(filePath);
		File parentFile = new File(filename.getParent());
		if(!parentFile.exists()) {
			@SuppressWarnings("unused")
			boolean flag = parentFile.mkdirs();
		}
	    if (!filename.exists()) {
            try {
				filename.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        String filein = context;
		OutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            out.write(filein.getBytes());
        }catch(Exception ex){
        	ex.printStackTrace();
        	System.out.println("创建"+filePath+"失败");
        	
        	return "";
        }finally{
        	if(out != null){
        		try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
		        	System.out.println("创建"+filePath+"失败");
				}
        	}
        }
        System.out.println("创建"+filePath+"完成");
        return context;
	}
}

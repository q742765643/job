package com.htht.job.executor.hander.dataarchiving.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Repository;

@Repository(value = "computerConnUtil")
public class ComputerConnUtil {
	/**
	 * 登录网上邻居
	 * 
	 * @param host
	 * @param user
	 * @param pass
	 */
//	public static boolean login(String host, String user, String pass) {
//		String remotePhotoUrl = "smb://"+user+":"+pass+"@"+host+"/"; // 存放图片的共享目录
//		SmbFile remoteFile;
//		try {
//			remoteFile = new SmbFile(remotePhotoUrl);
//			remoteFile.connect(); // 尝试连接
//			return true;
//		} catch (IOException e) {
//			return false;
//		} 
//	}

	/**
	 * 登录网上邻居
	 * 
	 * @param host
	 * @param user
	 * @param pass
	 */
	public static boolean login(String host, String user, String pass) {
		boolean flag = false;
		String info = "net use " + host + " " + pass + " /user:" + user;
		try {
			Process process = Runtime.getRuntime().exec("cmd.exe /c "+info);  
			InputStream is = process.getInputStream();
			BufferedReader buf = new BufferedReader(new InputStreamReader(is, "gbk"));
			String data = null;
			while ((data = buf.readLine()) != null) {
//				System.out.println(data);
				flag = true;
			}
			is.close();
			buf.close();
			System.out.println("[" + host + "] 登录"+(flag?"成功":"失败"));
			return flag ;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 获取网上邻居中的一个目录列表
	 */
	public static void listFiles(String path) {
//		String path = "\\\\10.1.7.36\\soa_share\\";
		File file = new File(path);
		// System.out.println(file.delete());
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				System.out.println(" dir : " + f.getAbsolutePath());
			} else {
				System.out.println("file : " + f.getAbsolutePath());
			}
		}
	}

}

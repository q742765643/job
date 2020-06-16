package com.htht.job.executor.util;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipCompressor {
	static final int BUFFER = 8192;

	private ZipOutputStream out = null;

	public ZipCompressor(String pathName) throws FileNotFoundException {
		File zipFile = new File(pathName);
		FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
				new CRC32());
		out = new ZipOutputStream(cos);
	}

	public void closeCompress() throws Exception{
		out.close();
	}
	
	public void compress(String srcPathName) throws Exception {
		File file = new File(srcPathName);
		if (!file.exists()){
			throw new Exception("“"+srcPathName + "” 不存在！");
		}
		out.setEncoding("GBK");
		String basedir = "";
		compress(file, basedir);
	}
	
	public void compress(File file, String basedir) throws Exception {
		/* 判断是目录还是文件 */
		if (file.isDirectory()) {
			System.out.println("压缩：" + basedir + file.getName());
			this.compressDirectory(file, basedir);
		} else {
			System.out.println("压缩：" + basedir + file.getName());
			this.compressFile(file, basedir);
		}
	}

	/** 压缩一个目录 
	 * @throws Exception */
	public void compressDirectory(File dir, String basedir) throws Exception {
		if (!dir.exists()){
			return;
		}
		File[] files = dir.listFiles();
		if(files!=null && files.length>0){
			for (int i = 0; i < files.length; i++) {
				compress(files[i], basedir + dir.getName() + "/");
			}
		}
	}

	/** 压缩一个文件 
	 * @throws Exception */
	public void compressFile(File file, String basedir) throws Exception {
		if (!file.exists()) {
			return;
		}
		BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(file));
		ZipEntry entry = new ZipEntry(basedir + file.getName());
		out.putNextEntry(entry);
		int count;
		byte data[] = new byte[BUFFER];
		while ((count = bis.read(data, 0, BUFFER)) != -1) {
			out.write(data, 0, count);
		}
		bis.close();
	}

	public static Boolean unZip(String filePath, String outPath) {
		try {
			ZipInputStream zin = new ZipInputStream(new FileInputStream(filePath));
			java.util.zip.ZipEntry entry;
			
			// 创建文件夹
			while ((entry = zin.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					File directory = new File(outPath, entry.getName());
					if (!directory.exists()) {
						if (!directory.mkdirs()) {
							System.exit(0);
						}
					}
					zin.closeEntry();
				} else {
					File myFile = new File(entry.getName());
					FileOutputStream fout = new FileOutputStream(outPath
							+ myFile.getPath());
					DataOutputStream dout = new DataOutputStream(fout);
					byte[] b = new byte[1024];
					int len = 0;
					while ((len = zin.read(b)) != -1) {
						dout.write(b, 0, len);
					}
					dout.close();
					fout.close();
					zin.closeEntry();
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
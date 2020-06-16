package com.htht.job.executor.hander.dataarchiving.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.stereotype.Repository;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

/**
 * 解压工具
 * 
 * @author LY
 * @time 2018-04-04
 */
@Repository(value = "unFileUtil")
public class UnFileUtil {
	// 判断解压文件类型
	public static boolean checkUnType (String filePath,String wspFile) {
		boolean flag = false;
		String[] UN_TYPE = wspFile.split("#");
		for (int i = 0; i < UN_TYPE.length; i++) {
			if(filePath.toLowerCase().endsWith(UN_TYPE[i])) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	public static boolean unGzipFile(String sourcedir, String ouputfile) {
		boolean flag = false;
		System.out.println(ouputfile);
		ouputfile = sourcedir.substring(0, sourcedir.lastIndexOf('.'));
		ouputfile = ouputfile.substring(0, ouputfile.lastIndexOf('.'));
		try(
			FileInputStream fin = new FileInputStream(sourcedir);
			FileOutputStream fout = new FileOutputStream(ouputfile);
			){
			// 建立gzip压缩文件输入流
			// 建立gzip解压工作流
			GZIPInputStream gzin = new GZIPInputStream(fin);
			// 建立解压文件输出流
			int num;
			byte[] buf = new byte[1024];

			while ((num = gzin.read(buf, 0, buf.length)) != -1) {
				fout.write(buf, 0, num);
			}
			flag = true;
		} catch (Exception ex) {
			System.err.println(ex.toString());
		}
		return flag;
	}

	/**
	 * 解压zip格式的压缩文件到指定位置
	 * 
	 * @param zipFileName
	 *            压缩文件
	 * @param extPlace
	 *            解压目录
	 * @throws Exception
	 */
	public static boolean unZip(String zipFileName, String extPlace,String wspFile) {
		System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding"));
		try {
			(new File(extPlace)).mkdirs();
			File f = new File(zipFileName);
			ZipFile zipFile = new ZipFile(zipFileName, "GBK"); // 处理中文文件名乱码的问题
			if ((!f.exists()) && (f.length() <= 0)) {
				throw new Exception("要解压的文件不存在!");
			}
			String strPath, gbkPath, strtemp;
			File tempFile = new File(extPlace);
			strPath = tempFile.getAbsolutePath();
			Enumeration<?> e = zipFile.getEntries();
			while (e.hasMoreElements()) {
				ZipEntry zipEnt = (ZipEntry) e.nextElement();
				gbkPath = zipEnt.getName();
				
				if (zipEnt.isDirectory()) {
					strtemp = strPath + File.separator + gbkPath;
					File dir = new File(strtemp);
					dir.mkdirs();
					continue;
				} else { // 读写文件
					if(!checkUnType(gbkPath,wspFile)) {
						continue;
					}
					InputStream is = zipFile.getInputStream(zipEnt);
					BufferedInputStream bis = new BufferedInputStream(is);
					gbkPath = zipEnt.getName();
					strtemp = strPath + File.separator + gbkPath;// 建目录
					String strsubdir = gbkPath;
					for (int i = 0; i < strsubdir.length(); i++) {
						if (strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
							String temp = strPath + File.separator + strsubdir.substring(0, i);
							File subdir = new File(temp);
							if (!subdir.exists())
								subdir.mkdir();
						}
					}
					try(
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(strtemp));
						){
						int c;
						while ((c = bis.read()) != -1) {
							bos.write((byte) c);
						}
						bos.close();
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 解压tar.gz 文件
	 * 
	 * @param file
	 *            要解压的tar.gz文件对象
	 * @param outputDir
	 *            要解压到某个指定的目录下
	 * @throws IOException
	 */
	public static boolean unTarGz(File file, String outputDir,String wspFile) {
		boolean flag = false;
		TarInputStream tarIn = null;
		try {
			tarIn = new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))),
					1024 * 2);

			createDirectory(outputDir, null);// 创建输出目录

			TarEntry entry = null;
			while ((entry = tarIn.getNextEntry()) != null) {
				if (entry.isDirectory()) {// 是目录
					entry.getName();
					createDirectory(outputDir, entry.getName());// 创建空目录
				} else {// 是文件
					if(!checkUnType(entry.getName(),wspFile)) {
						continue;
					}
					File tmpFile = new File(outputDir + "/" + entry.getName());
					createDirectory(tmpFile.getParent() + "/", null);// 创建输出目录
					OutputStream out = null;
					try {
						out = new FileOutputStream(tmpFile);
						int length = 0;

						byte[] b = new byte[2048];

						while ((length = tarIn.read(b)) != -1) {
							out.write(b, 0, length);
						}

					} catch (IOException ex) {
						throw ex;
					} finally {

						if (out != null)
							out.close();
					}
				}
			}
			flag = true;
		} catch (IOException ex) {
			System.err.println(ex.toString());
			return false;
		} finally {
			try {
				if (tarIn != null) {
					tarIn.close();
				}
			} catch (IOException ex) {
				System.err.println(ex.toString());
			}
		}
		return flag;
	}

	/**
	 * 构建目录
	 * 
	 * @param outputDir
	 * @param subDir
	 */
	public static void createDirectory(String outputDir, String subDir) {
		File file = new File(outputDir);
		if (!(subDir == null || subDir.trim().equals(""))) {// 子目录不为空
			file = new File(outputDir + "/" + subDir);
		}
		if (!file.exists()) {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			file.mkdirs();
		}
	}

	/**
	 * 根据原始rar路径，解压到指定文件夹下.
	 * 
	 * @param srcRarPath
	 *            原始rar路径
	 * @param dstDirectoryPath
	 *            解压到的文件夹
	 */
	public static boolean unRarFile(String srcRarPath, String dstDirectoryPath,String wspFile) {
		boolean flag = false;
		if (!srcRarPath.toLowerCase().endsWith(".rar")) {
			System.out.println("非rar文件！");
			return false;
		}
		File dstDiretory = new File(dstDirectoryPath);
		if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
			dstDiretory.mkdirs();
		}
		try(
			Archive a = new Archive(new File(srcRarPath));
			){
			if (a != null) {
				// a.getMainHeader().print(); // 打印文件信息.
				FileHeader fh = a.nextFileHeader();
				while (fh != null) {

					// 防止文件名中文乱码问题的处理

					String fileName = fh.getFileNameW().isEmpty() ? fh.getFileNameString() : fh.getFileNameW();
					
					if (fh.isDirectory()) { // 文件夹
						File fol = new File(dstDirectoryPath + File.separator + fileName);
						fol.mkdirs();
					} else { // 文件
						if(!checkUnType(fileName,wspFile)) {
							continue;
						}
						File out = new File(dstDirectoryPath + File.separator + fileName.trim());
						if (!out.exists()) {
							if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
								out.getParentFile().mkdirs();
							}
							out.createNewFile();
						}
						FileOutputStream os = new FileOutputStream(out);
						a.extractFile(fh, os);
						os.close();
					}
					fh = a.nextFileHeader();
				}
				a.close();
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public boolean unFile(String sourceFile, String outputDir,String wspFile) {
		boolean flag = false;
		if (sourceFile.toLowerCase().endsWith(".tar.gz")) {
			flag = unTarGz(new File(sourceFile), outputDir,wspFile);
		} else if (sourceFile.toLowerCase().endsWith(".gz")) {
			flag = unGzipFile(sourceFile, outputDir);
		} else if (sourceFile.toLowerCase().endsWith(".zip")) {
			flag = unZip(sourceFile, outputDir,wspFile);
		} else if (sourceFile.toLowerCase().endsWith(".rar")) {
			flag = unRarFile(sourceFile, outputDir,wspFile);
		}
		return flag;
	}

	public static void main(String[] args) {
		// String SEPARATOR = File.separator;
		// // 1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
		// String destFileName = "";
		// String destDirName = "";
		// if (SEPARATOR.equals("/")) { // 非windows系统
		// destFileName = (destPath +
		// fileHeader.getFileNameW()).replaceAll("\\\\", "/");
		// destDirName = destFileName.substring(0,
		// destFileName.lastIndexOf("/"));
		// } else { // windows系统
		// destFileName = (destPath + fileHeader.getFileNameW()).replaceAll("/",
		// "\\\\");
		// destDirName = destFileName.substring(0,
		// destFileName.lastIndexOf("\\"));
		// }
	}
}
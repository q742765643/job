package com.htht.job.uus.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: FileUtil
 * @Description: 文件以及文件夹处理类
 * @author chensi
 * @date 2018年5月14日
 * 
 */
public class FileUtil
{

	/**
	 * @Description: 将源文件/文件夹生成指定格式的压缩文件,格式zip 
	 * @param files	要压缩的文件集合
	 * @param targetName 目标文件名
	 * @return File
	 * @throws 
	 * 
	 */
	public static File zipFiles(List<File> files, String targetName, String tempPath) 
	{
		String targetPath = tempPath;
		File targetFile = new File(targetPath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		ZipOutputStream zipOutputStream = null;
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(targetFile+"//"+targetName);
			zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));
			if (null != files && 0 != files.size()) {
				for (int i = 0; i < files.size(); i++) {
					compress(zipOutputStream, files.get(i), "");
				}
			}
			System.out.println(targetName + " 压缩完毕");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				zipOutputStream.close();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new File(targetFile, targetName);
		
	}
	
	/**
	 * @Description: 生成压缩文件。 如果是文件夹，则使用递归，进行文件遍历、压缩 如果是文件，直接压缩
	 * @param out 
	 * @param file 
	 * @param string 
	 * @return 
	 * @throws
	 * 
	 */
	private static void compress(ZipOutputStream out, File file, String string)
	{
		// 如果当前的是文件夹，则进行进一步处理
		try
		{
			if (file.isDirectory())
			{
				// 得到文件列表信息
				File[] files = file.listFiles();
				// 将文件夹添加到下一级打包目录
				out.putNextEntry(new ZipEntry(string + "/"));
				string = string.length() == 0 ? "" : string + "/";
				// 循环将文件夹中的文件打包
				for (int i = 0; i < files.length; i++)
				{
					compress(out, files[i], string + files[i].getName()); // 递归处理
				}
			} else
			{ // 当前的是文件，打包处理
				// 文件输入流
				FileInputStream fis = new FileInputStream(file);
				out.putNextEntry(new ZipEntry(file.getName()+string));
				// 进行写操作
				int j = 0;
				byte[] buffer = new byte[1024];
				while ((j = fis.read(buffer)) > 0)
				{
					out.write(buffer, 0, j);
				}
				// 关闭输入 流
				fis.close();
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static List<File> getSubFiles(File file) 
	{
		List<File> subFileList = new ArrayList<>();

		File[] temp = file.listFiles();
		if (temp != null) {
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].isFile()) {
					subFileList.add(temp[i]);
				}
				if (temp[i].isDirectory()) {
					subFileList.addAll(getSubFiles(temp[i]));
				}
			}
		}
		return subFileList;
		
	}

}

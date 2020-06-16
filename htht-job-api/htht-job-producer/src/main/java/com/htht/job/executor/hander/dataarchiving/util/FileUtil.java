package com.htht.job.executor.hander.dataarchiving.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.stereotype.Repository;

/**
 * 文件处理工具
 * 
 * @author LY on 2018/04/02
 *
 */
@Repository(value = "fileUtil")
public class FileUtil {
	private final String CACHEPATH = "ArchiveCache";
	private final String ERRORPATH = "ArchiveError";

	/**
	 * 无级遍历文件
	 * 
	 * @param path       遍历路径
	 * @param extensions 文件过滤
	 * @return
	 */
	public Iterator<File> listFiles(String path, String[] extensions) {
		IOFileFilter filter = new SuffixFileFilter(extensions, IOCase.INSENSITIVE);
		Iterator<File> iter = FileUtils.iterateFiles(new File(path), filter, DirectoryFileFilter.DIRECTORY);
		return iter;
	}

	/**
	 * 无级遍历文件
	 * 
	 * @param path       遍历路径
	 * @param extensions 文件过滤
	 * @return
	 */
	public Iterator<File> listFilesAndDir(String path, String[] extensions) {
		IOFileFilter filter = new SuffixFileFilter(extensions, IOCase.INSENSITIVE);
		Iterator<File> iter = FileUtils.iterateFilesAndDirs(new File(path), filter, DirectoryFileFilter.DIRECTORY);
		return iter;
	}

	/**
	 * 获取工程根目录
	 * 
	 * @return
	 */
	public String getRootPath() {
		return new File(FileUtil.class.getClassLoader().getResource("").getPath()).getParentFile().getParentFile()
				.getParentFile().getParentFile().getParentFile().getAbsolutePath();
	}

	/**
	 * 获取不带后缀名的文件名
	 * 
	 * @param file
	 * @return
	 */
	public String getFileNameWithoutSuffix(File file) {
		String file_name = file.getName();
		if (file_name.endsWith(".tar.gz")) {
			return file_name.replace(".tar.gz", "");
		} else {
			return file_name.substring(0, file_name.lastIndexOf("."));
		}
	}

	/**
	 * 获取文件名的后缀名
	 * 
	 * @param file
	 * @return
	 */
	public String getFileNameSuffix(File file) {
		String file_name = file.getName();
		if (file_name.endsWith(".tar.gz")) {
			return ".tar.gz";
		} else {
			return file_name.substring(file_name.lastIndexOf("."));
		}
	}

	/**
	 * 获取数据处理路径
	 * 
	 * @return
	 */
	public String getArchivePath() {
		return this.getRootPath() + "/" + this.CACHEPATH;
	}

	/**
	 * 数据处理失败目录
	 * 
	 * @return
	 */
	public String getErrorPath() {
		return this.getRootPath() + "/" + this.ERRORPATH;
	}

	/**
	 * 根据正则匹配文件
	 * 
	 * @param files
	 * @param regexp
	 * @return
	 */
	public File getFileByRegexp(Iterator<File> files, String regexp) {
		File f = null;
		while (files.hasNext()) {
			f = (File) files.next();
			// 根据正则匹配需要解析的xml
			boolean isMatch = Pattern.matches(regexp, f.getName());
			if (isMatch) {
				break;
			} else {
				f = null;
			}
		}
		return f;
	}

	/**
	 * 根据文件结尾匹配文件
	 * 
	 * @param files
	 * @param endWidth
	 * @return
	 */
	public File getFileByEndWith(Iterator<File> files, String endWith) {
		File f = null;
		while (files.hasNext()) {
			f = (File) files.next();
			// 根据正则匹配需要解析的xml
			boolean isMatch = f.getName().toLowerCase().endsWith(endWith.toLowerCase());
			if (isMatch) {
				break;
			} else {
				f = null;
			}
		}
		return f;
	}

	/**
	 * 获取文件总大小
	 * 
	 * @param file
	 * @return
	 */
	public Long getDirSize(File file) {
		// 判断文件是否存在
		if (file.exists()) {
			// 如果是目录则递归计算其内容的总大小
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				Long size = 0L;
				for (File f : children)
					size += getDirSize(f);
				return size;
			} else {// 如果是文件则直接返回其大小,以“兆”为单位
				Long size = file.length();
				return size;
			}
		} else {
			System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
			return 0L;
		}
	}

	public static String formatePath(String path) {
		path = path.replace("\\\\", "/").replace("\\", "/");
		if (!path.endsWith("/")) {
			path += "/";
		}
		return path;
	}

	/**
	 * 通过匹配输入/输出路径
	 * 
	 * @param inputPath
	 * @param issue
	 * @return
	 */
	public static String getPathByDate(String path, Date date) {
		int begin = path.indexOf("{");
		int end = 0;
		while (begin > 0) {
			end = path.indexOf("}", begin) + 1;
			String str1 = path.substring(0, begin);
			String str2 = path.substring(begin, end);
			String str3 = path.substring(end);
			SimpleDateFormat formate = new SimpleDateFormat(str2);
			path = str1 + formate.format(date) + str3;
			begin = path.indexOf("{", end) + 1;
		}
		path = path.replace("{", "").replace("}", "");
		return path;
	}

	public static void main(String[] args) {
		boolean isMatch = Pattern.matches(
				"(?<grp0>GF5_AHSI_E[\\.\\d]+_N[\\.\\d]+_[^\\D]+_[^\\D]+_L1[^\\D]+\\.meta\\.xml)",
				"GF5_AHSI_E67.3_N24.4_20161223_000001_L10002063882.meta.xml");
		System.out.println(isMatch);
	}

	static public boolean createFile(File file) {
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}

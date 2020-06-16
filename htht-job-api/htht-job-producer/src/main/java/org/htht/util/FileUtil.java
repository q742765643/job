package org.htht.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;


public class FileUtil {
	
	/**
	 * 遍历rootFile目录下所有的文件
	 * @param rootFile
	 * @param fileRegex
	 * @return
	 */
	public static List<File> iteratorAllFile(File rootFile, String fileRegex) {

		List<File> fileList = new ArrayList<File>();
		if (!rootFile.exists()) {
			return fileList;
		}

		if (rootFile.isDirectory()) {
			File[] fileArr = rootFile.listFiles();
			for (File file : fileArr) {
				fileList.addAll(iteratorFile(file, fileRegex));
			}
		} else {
			if (fileRegex == null) {
				fileList.add(rootFile);
			} else {
				if (rootFile.getName().matches(fileRegex)) {
					fileList.add(rootFile);
				}
			}
		}

		return fileList;
	}

	/**
	 * 遍历rootFile当前目录下的文件
	 * @param rootFile
	 * @param fileRegex
	 * @return
	 */
	public static List<File> iteratorFile(File rootFile, String fileRegex) {

		List<File> fileList = new ArrayList<File>();
		if (!rootFile.exists() || rootFile.isFile()) {
			return fileList;
		}

		if (rootFile.isDirectory()) {
			File[] fileArr = rootFile.listFiles();
			for (File file : fileArr) {
				if (fileRegex == null) {
					fileList.add(file);
				} else {
					if (file.getName().matches(fileRegex)) {
						fileList.add(file);
					}
				}
			}
		} else {
			if (fileRegex == null) {
				fileList.add(rootFile);
			} else {
				if (rootFile.getName().matches(fileRegex)) {
					fileList.add(rootFile);
				}
			}
		}

		return fileList;
	}

	/**
	 * 层级遍历rootFile目录下的所有符合标准的文件
	 * @param rootFile
	 * @param fileRegex
	 * @return
	 */
	public static List<File> iteratorFileAndDirectory(File rootFile, String fileRegex) {

		if (!rootFile.exists() || !rootFile.isDirectory()) {
			return null;
		}

		RegexFileFilter regexFileFilter  = new RegexFileFilter(fileRegex);
		List<File> listFiles = (List<File>)FileUtils.listFiles(rootFile, regexFileFilter, DirectoryFileFilter.INSTANCE);
		return listFiles;
	}
	
	/**
	 * 遍历rootFile目录下的所有目录
	 * @param rootFile
	 * @param fileRegex
	 * @return
	 */
	public static List<File> iteratorDirectory(File rootFile, String fileRegex) {

		if (!rootFile.exists() || !rootFile.isDirectory()) {
			return new ArrayList<File>();
		}

		List<File> fileList = new ArrayList<File>();

		if (rootFile.isDirectory()) {
			if(rootFile.getName().matches(fileRegex)) {
				fileList.add(rootFile);
			} 
			File[] fileArr = rootFile.listFiles();
			if(fileArr!=null&&fileArr.length>0) {
				for (File file : fileArr) {
					fileList.addAll(iteratorDirectory(file, fileRegex));
				}
			}
		}
		return fileList;
	}

}

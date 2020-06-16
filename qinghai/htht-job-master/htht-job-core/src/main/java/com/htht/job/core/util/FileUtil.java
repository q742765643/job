package com.htht.job.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
	public static int Video = 0;
	public static int Pic = 1;
	public static int Share = 2;
	public static String[] pictype = { ".gif", ".jpg", ".jpeg", ".bmp", ".png" };
	public static String[] videotype = { ".rm", ".ram", ".rmvb", ".wmv", ".mp3", ".wav", ".avi", ".swf", ".asf", ".mov",
			".ra", ".mpg", ".mpeg", ".mpe" };
	public static String[] sharefiletype = { ".zip", ".rar", ".exe", ".doc", ".ppt", ".bmp", ".gif", ".jpg", ".jpeg",
			".rm", ".ram", ".rmvb", ".wmv", ".mp3", ".wav", ".avi", ".swf", ".asf", ".mov", ".ra", ".mpg", ".mpeg",
			".mpe", ".ppt", ".xsl", ".swf", ".html", ".txt", ".xls" };

	public FileUtil() {

	}

	public static String getFilePhysicalLocation(long userid) {
		String uploadPath = "";
		uploadPath = "uploadfiles/album/";
		String[] dirs = new String[3];
		for (int i = 1; i < 4; i++) {
			long temp = (long) (userid / Math.pow(10, i));
			dirs[i - 1] = (new Long(temp % 10)).toString();
		}

		try {
			String root = null;
			try {
				URL url = FileUtil.class.getResource("/");
				java.io.File classDir = new java.io.File(url.getPath());
				java.io.File f = new java.io.File(classDir.getParent());
				java.io.File f1 = new java.io.File(f.getParent());
				root = f1.getAbsolutePath();
			} catch (Exception e) {
			}
			root = URLDecoder.decode(root, "UTF-8");
			java.io.File dir = new java.io.File(root + System.getProperty("file.separator") + uploadPath
					+ System.getProperty("file.separator") + dirs[2] + System.getProperty("file.separator") + dirs[1]
					+ System.getProperty("file.separator") + dirs[0]);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			String tempdir = dir.toString();
			int i = tempdir.indexOf("uploadfiles");
			tempdir = tempdir.substring(i);
			tempdir = tempdir.replace('\\', '/');
			return tempdir;
		} catch (Exception e) {
		}
		return null;
	}

	public static boolean deleteFile(File file) {
		return file.delete();
	}

	public static boolean deleteFile(File[] file) {
		for (File file2 : file) {
			deleteFile(file2);
		}
		return true;
	}

	/**
	 * 根据目录名称和匹配规则获取改目录下的所有匹配文件
	 * 
	 * @param filepath
	 * @param reg
	 * @return
	 */
	public static List<File> getAllFiles(String filepath, String reg) {
		List<File> allUploadFiles = new ArrayList<File>();
		String myreg = reg.replace("?", "[\\s\\S]{1}").replace("*", "[\\s\\S]*");
		getAllFiles(filepath, myreg, allUploadFiles);
		return allUploadFiles;

	}

	private static void getAllFiles(String filepath, String reg, List<File> allFiles) {
		File file = new File(filepath);
		if (file.isFile()) {
			Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);// ?代表一个，*代表多个
			Matcher m = p.matcher(file.getName());
			boolean b = m.matches();
			if (b) {
				allFiles.add(file);
			}
		} else {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					getAllFiles(files[i].getPath(), reg, allFiles);
				}
			}
		}
	}

	/**
	 * 获取文件夹下说有文件
	 * 
	 * @param filepath
	 * @param fileSet
	 * @return
	 */
	public static Set<File> getAllFiles(File file, Set<File> fileSet) {
		if (fileSet == null) {
			fileSet = new HashSet<File>();
		}
		if (null != file) {
			if (file.isFile()) {
				fileSet.add(file);
			}else if(file.isDirectory()) {
				for (File tempFile : file.listFiles()) {
					getAllFiles(tempFile, fileSet);
				}
			}
		}
		return fileSet;
	}
	/**
	 * 
	 * @param file
	 * @param fileSet
	 * @param reg 文件名正则
	 * @return
	 */
	public static Set<File> getAllFiles(File file, Set<File> fileSet, String reg) {
		if (fileSet == null) {
			fileSet = new HashSet<File>();
		}
		Pattern p=Pattern.compile(reg);
		getAllFiles(file, fileSet);
		Set<File> resultSet = new HashSet<File>();
		for(File tempFile : fileSet) {
			if(p.matcher(tempFile.getName()).matches()) {
				resultSet.add(tempFile);
			}	
		}
		fileSet.clear();
		fileSet.addAll(resultSet);
		return fileSet;
	}

	/**
	 *
	 * @param userid
	 * @param uploadPath
	 * @return
	 */
	public static String getFilePhysicalLocation(long userid, String uploadPath) {
		String[] dirs = new String[3];
		for (int i = 1; i < 4; i++) {
			long temp = (long) (userid / Math.pow(10, i));
			dirs[i - 1] = (new Long(temp % 10)).toString();
		}

		try {
			String root = null;
			try {
				URL url = FileUtil.class.getResource("/");
				java.io.File classDir = new java.io.File(url.getPath());
				java.io.File f = new java.io.File(classDir.getParent());
				java.io.File f1 = new java.io.File(f.getParent());
				root = f1.getAbsolutePath();
			} catch (Exception e) {
			}
			root = URLDecoder.decode(root, "UTF-8");
			java.io.File dir = new java.io.File(root + System.getProperty("file.separator") + uploadPath
					+ System.getProperty("file.separator") + dirs[2] + System.getProperty("file.separator") + dirs[1]
					+ System.getProperty("file.separator") + dirs[0]);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			String tempdir = dir.toString();
			int i = tempdir.indexOf("uploadfiles");
			tempdir = tempdir.substring(i);
			tempdir = tempdir.replace('\\', '/');
			return tempdir;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 
	 * @param path
	 * @throws Exception
	 */
	static public void Create_dir(String path) throws Exception {
		File dirName = new File(path);
		if (dirName.exists())
			dirName.delete();
		dirName.mkdir();//
	}

	/**
	 * 
	 * @param path
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	static public File Create_file(String path, String filename) throws Exception {
		File fileName = new File(path, filename);
		if (fileName.exists())
			fileName.delete();
		fileName.createNewFile();//
		return fileName;
	}

	/**
	 * 
	 * @param str
	 * @param file
	 * @throws Exception
	 */
	static public void Write_file(String str, File file) throws Exception {
		FileWriter fw = new FileWriter(file);
		fw.write(str);
		fw.close();
	}

	static public void Write_file_UTF8(String str, File file) throws Exception {
		try {
			OutputStream out = new FileOutputStream(file);
			BufferedWriter rd = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
			rd.write(str);
			rd.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static public void Write_file(byte[] bytes, File file) throws Exception {
		FileOutputStream fo = new FileOutputStream(file);
		fo.write(bytes, 0, bytes.length);
		fo.flush();
		fo.close();
	}

	static public void Write_file(byte[] bytes, String file) throws Exception {
		Write_file(bytes, new File(file));
	}

	/**
	 * 获取文件扩展名称
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileExtensionName(File file) {
		String fname = file.getName();
		int index = fname.lastIndexOf(".");
		if (index != -1) {
			return fname.substring(index);
		} else {
			return "";
		}
	}

	/**
	 * 获取文件扩展名称
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileExtensionName(String file) {
		return getFileExtensionName(new File(file));
	}

	/**
	 * 获取文件名称不包括扩展名
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileName(String file) {
		return getFileName(new File(file));
	}

	/**
	 * 获取文件名称不包括扩展名
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileName(File file) {
		String fname = file.getName();
		int index = fname.lastIndexOf(".");
		if (index != -1) {
			return fname.substring(0, index);
		} else {
			return fname;
		}
	}

	/**
	 * 
	 * @param path
	 * @throws Exception
	 */
	static public void Delete_dir(String path) throws Exception {
		File dirName = new File(path);
		if (dirName.exists())
			dirName.delete();//
	}

	/**
	 * 递归删除文件目录
	 * 
	 * @param vpath
	 * @return
	 */
	static public boolean Deldir(String vpath) {
		String pathName = "";
		try {
			pathName = vpath;
			boolean rBool = true;
			File delPath = new File(pathName);
			if (delPath.exists()) {
				File[] childFiles = delPath.listFiles();
				for (int i = 0; i < childFiles.length; i++) {
					File childFile = childFiles[i];
					if (childFile.isDirectory()) {
						rBool = Deldir(childFile.getPath());
					} else {
						rBool = childFile.delete();
					}
				}
				rBool = delPath.delete();
				return rBool;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public String getFileTypeImg(String filename) {
		String str = "00.gif";
		if (filename == null || filename.trim().length() < 5) {
			str = "00.gif";
		} else {
			filename = filename.trim();
			filename = filename.substring(filename.length() - 4, filename.length());
			filename = filename.toLowerCase();

			if (filename.equals(".txt")) {
				str = "txt.gif";
			} else if (filename.equals(".xml")) {
				str = "xml.gif";
			} else if (filename.equals(".xsl") || filename.equals("xslt")) {
				str = "xsl.gif";
			} else if (filename.equals(".doc")) {
				str = "doc.gif";
			} else if (filename.equals(".css")) {
				str = "css.gif";
			} else if (filename.equals(".htm") || filename.equals("html")) {
				str = "htm.gif";
			} else if (filename.equals(".gif")) {
				str = "gif.gif";
			} else if (filename.equals(".jpg") || filename.equals("jpeg")) {
				str = "jpg.gif";
			} else if (filename.equals(".psd")) {
				str = "psd.gif";
			} else if (filename.equals(".mid")) {
				str = "mid.gif";
			} else if (filename.equals(".wav")) {
				str = "wav.gif";
			} else if (filename.equals(".avi")) {
				str = "avi.gif";
			} else if (filename.equals(".rar")) {
				str = "rar.gif";
			} else if (filename.equals(".zip")) {
				str = "zip.gif";
			} else {
				str = "00.gif";
			}
			filename = filename.substring(filename.length() - 3, filename.length());
			if (filename.equals(".js")) {
				str = "js.gif";
			}
		}
		return str;
	}

	static public byte[] ReadFile(File file) throws Exception {
		byte[] by = new byte[(int) file.length()];

		FileInputStream in = new FileInputStream(file);
		in.read(by, 0, by.length);
		in.close();
		return by;

	}

	/**
	 *
	 * @param path
	 * @throws Exception
	 */
	static public void Delete_file(String path) throws Exception {
		File fileName = new File(path);
		if (fileName.exists())
			fileName.delete();// ɾ���Ӧ���ļ�
	}

	/**
	 *
	 * @param dirpath
	 * @return
	 * @throws Exception
	 */
	static public String[] List_files(String dirpath) throws Exception {
		File file = new File(dirpath);
		File files[] = file.listFiles();
		String[] fileslist = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			String NewsFileName = files[i].getName();
			fileslist[i] = dirpath + "\\" + NewsFileName;
		}
		return fileslist;
	}

	/**
	 *
	 * 
	 * @param from java.lang.String
	 * @param to   java.lang.String
	 */

	static public boolean copy(String from, String to) {
		try {
			to = replace(to, " \\", "/ <file://\\> ");
			String toPath = to.substring(0, to.lastIndexOf("/"));
			File f = new File(toPath);
			if (!f.exists())
				f.mkdirs();
			BufferedInputStream bin = new BufferedInputStream(new FileInputStream(from));
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(to));
			int c;
			while ((c = bin.read()) != -1)
				bout.write(c);
			bin.close();
			bout.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 *
	 * 
	 * @return java.lang.String
	 * @param ss java.lang.String
	 */
	static public String replace(String srcStr, String oldStr, String newStr) {
		int i = srcStr.indexOf(oldStr);
		StringBuffer sb = new StringBuffer();
		if (i == -1)
			return srcStr;
		sb.append(srcStr.substring(0, i) + newStr);
		if (i + oldStr.length() < srcStr.length())
			sb.append(replace(srcStr.substring(i + oldStr.length(), srcStr.length()), oldStr, newStr));
		return sb.toString();
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @throws Exception
	 */
	static public void MoveFile(String from, String to) throws Exception {
		boolean ok = copy(from, to);
		if (ok)
			Delete_file(from);
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
		path = FileUtil.formatePath(path);
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
		System.out.println(FileUtil.getFileExtensionName(new File("*-msss.tif")));
		System.out.println(FileUtil.getFileName(new File("c://2.xml.txt")));

		String path = "/zzj/data";

		List<File> files = getAllFiles(path, "*test 2的副本.sh");
		for (int i = 0; i < files.size(); i++) {
			System.out.println(files.get(i).getPath());
		}

	}

}

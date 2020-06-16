package org.htht.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * 文件服务.
 * <p>
 * 
 * 
 * @author <a href="mailto:ouzf@vip.sina.com">区钊锋</a>
 * @since BASE 0.1
 */
public class UtilFile {
	private static Logger logger = LoggerFactory.getLogger(UtilFile.class);
	// 默认的读入块大小
	private static final int DEFAULT_READ_BLOCK_SIZE = 4096;

	private UtilFile() {
	}

	/**
	 * 从文件读入，输出到文件.
	 * 
	 * @see #bitWrite(InputStream, OutputStream, int)
	 * 
	 */
	public static void bitWrite(String strInFile, OutputStream out,
			int iReadBlockSize) throws IOException, Exception {
		File f = new File(strInFile);
		if (!f.isFile())
			throw new Exception("未找到文件：" + strInFile);

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
		bitWrite(in, out, iReadBlockSize);
		in.close();
	}

	/**
	 * 输出到文件.
	 * 
	 * @see #bitWrite(InputStream, OutputStream, int)
	 * 
	 */
	public static void bitWrite(InputStream in, String strFile,
			int iReadBlockSize) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(strFile));
		bitWrite(in, out, iReadBlockSize);
		out.close();
	}

	/**
	 * 输出到文件,使用默认块大小4096.
	 * 
	 * @see #bitWrite(InputStream, OutputStream, int)
	 * 
	 */
	public static void bitWrite(InputStream in, String strFile)
			throws IOException {
		bitWrite(in, strFile, DEFAULT_READ_BLOCK_SIZE);
	}

	/**
	 * 从输入流读取数据，写到输出流.
	 * <p>
	 * 使用bit方式读写. <br>
	 * <b>Example:</b> <blockquote>
	 * 
	 * <pre>
	 *  
	 *   try {
	 *     Connection c = getConnection();
	 *     java.sql.Statement stmt = c.createStatement();
	 *     java.sql.ResultSet rs = stmt.executeQuery(strQuery);
	 *     int i = 0;
	 *     while (rs.next()) {
	 *       i++;
	 *       java.sql.Blob blob = (java.sql.Blob) rs.getObject(1);
	 *       &lt;b&gt;InputStream in = blob.getBinaryStream();&lt;/b&gt;  //获得输入流
	 *       &lt;b&gt;com.jtv.base.core.utils.UtilFile.bitWrite(in, strSaveFile + &quot;.&quot; + i);  //输出到文件&lt;/b&gt;
	 *       in.close();
	 *     }
	 *     c.close();
	 *   }
	 *  
	 *   
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出目标流
	 * @param iReadBlockSize
	 *            读写块大小
	 * @throws IOException
	 * @throws Exception
	 * @since BASE 0.1
	 * 
	 */
	public static void bitWrite(InputStream in, OutputStream out,
			int iReadBlockSize) throws IOException {
		int readLen;
		readLen = -1;
		byte[] b = new byte[iReadBlockSize];
		while ((readLen = in.read(b)) != -1) {
			out.flush();
			out.write(b);
		}
		// out.close();
		// in.close();
	}

	/**
	 * 从输入流读取数据，写到输出流，使用默认块大小.
	 * 
	 * @see #bitWrite(InputStream, OutputStream, int)
	 * 
	 */
	public static void bitWrite(InputStream in, OutputStream out)
			throws IOException {
		bitWrite(in, out, DEFAULT_READ_BLOCK_SIZE);
	}

	/**
	 * 文件copy.
	 * 
	 * @param src
	 *            源文件
	 * @param dest
	 *            目标文件
	 * @param cover
	 *            是否覆盖写入
	 * @throws IOException
	 */
	public static void copy(File src, File dest, boolean cover)
			throws IOException {
		FileInputStream fis = new FileInputStream(src);
		FileOutputStream fos = new FileOutputStream(dest);
		byte buf[] = new byte[1024];
		int i;
		try {
			while ((i = fis.read(buf)) >= 0) {
				fos.write(buf, 0, i);
			}
		} finally {
			try {
				fis.close();
			} catch (Exception exception1) {
			}

			try {
				fos.close();
			} catch (Exception exception2) {
			}
		}
	}

	/**
	 * 文件copy.
	 * 
	 * @param src源文件
	 * @param dest目标文件
	 * @param cover是否覆盖写入
	 * @throws IOException
	 */

	public static void copy(String src, String dest, boolean cover)
			throws IOException {
		File fsrc = new File(src);
		File fdest = new File(dest);
		copy(fsrc, fdest, cover);
	}

	/**
	 * 文件COPY包含子目录.
	 * 
	 * @param src
	 *            源目录
	 * @param dest
	 *            目标目录
	 * @param cover
	 *            是否覆盖写入
	 * @throws IOException
	 */
	public static void copytree(File src, File dest, boolean cover)
			throws IOException {
		if (src.isFile()) {
			copy(src, dest, cover);
		} else {
			File children[] = src.listFiles();
			for (int i = 0; i < children.length; i++) {
				File f = new File(dest, children[i].getName());
				if (children[i].isDirectory()) {
					f.mkdirs();
					copytree(children[i], f, cover);
				} else {
					copy(children[i], f, cover);
				}
			}
		}
	}

	/**
	 * 文件COPY包含子目录.
	 * 
	 * @param src
	 *            源目录
	 * @param dest
	 *            目标目录
	 * @param cover
	 *            是否覆盖写入
	 * @throws IOException
	 */
	public static void copytree(String src, String dest, boolean cover)
			throws IOException {
		File fsrc = new File(src);
		File fdest = new File(dest);
		copytree(fsrc, fdest, cover);
	}

	/**
	 * 文件MOVE（包含子目录）.
	 * 
	 * @param src源路径
	 * @param dest目标路径
	 * @param cover是否覆盖写入
	 * @throws IOException
	 */
	public static void movetree(File src, File dest, boolean cover)
			throws IOException {
		copytree(src, dest, cover);
		deltree(src);
	}

	/**
	 * 删除文件、目录包括子目录.
	 * 
	 * @param f
	 *            FILE对象
	 */
	public static void deltree(File f) {
		File children[] = f.listFiles();
		if (children != null && children.length != 0) {
			for (int i = 0; i < children.length; i++) {
				deltree(children[i]);
			}
		}
		f.delete();
	}

	/**
	 * 删除文件、目录（包含子目录）.
	 * 
	 * @param path路径
	 */
	public static void deltree(String path) {
		File f = new File(path);
		deltree(f);
	}

	/**
	 * 文件MOVE.
	 * 
	 * @param src源路径
	 * @param dest目录路径
	 * @param cover是否覆盖写入
	 * @throws IOException
	 */
	public static void move(String src, String destFolder, boolean cover)
			throws IOException {
		File fsrc = new File(src);

		String destFile = assembleFilePath(destFolder, getFileName(src));

		File fdest = new File(destFile);
		copy(fsrc, fdest, cover);
		fsrc.delete();
	}

	/**
	 * 文件查找.
	 * 
	 * @param root
	 * @param filter
	 * @return
	 */
	public static boolean find(File root, FileFilter filter) {
		if (filter.accept(root)) {
			return true;
		}

		File children[] = root.listFiles();
		if (children == null || children.length == 0) {
			return false;
		}

		for (int i = 0; i < children.length; i++) {
			if (find(children[i], filter)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 获得文件的后缀名.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExt(String fileName) {
		String ext = "";
		int dot = fileName.lastIndexOf(".");
		if (dot != -1) {
			ext = fileName.substring(dot + 1);
		}
		return ext;
	}

	/**
	 * 获得文件不包括后缀名.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileNoExt(String fileName) {
		String ext = "";
		int dot = fileName.lastIndexOf(".");
		if (dot != -1) {
			ext = fileName.substring(0, dot);
		}
		return ext;
	}

	/**
	 * 通过文件全路径获得文件名.
	 * 
	 * @param strFilePath
	 * @return
	 */
	public static String getFileName(String strFilePath) {
		String fileName = convertDOSPath(strFilePath);
		int i = fileName.lastIndexOf("/");
		if (i != -1) {
			fileName = fileName.substring(i + 1);
		}
		return fileName;
	}

	/**
	 * 将DOS文件路径转换标准文件路径.
	 * 
	 * @param strPath
	 * @return
	 */
	public static String convertDOSPath(String strPath) {
		// 使用正则表达式替换
		return strPath.replaceAll("\\\\", "/");
	}

	/**
	 * 根据目录名,文件名生成完整路径.
	 * 
	 * @param strPath
	 * @param strFileName
	 * @return 完整路径
	 */
	public static String assembleFilePath(String strPath, String strFileName) {
		String path = convertDOSPath(strPath);

		if (path.charAt(path.length() - 1) != "/".charAt(0)) {
			path = path + "/";
		}

		return path + strFileName;
	}

	/**
	 * 通过文件全路径获得所在目录.
	 * 
	 * @param strFilePath
	 *            文件名（包含路径）
	 * @return 所在目录
	 */
	public static String getFilePath(String strFilePath) {
		// 替换DOS系统的路径分隔符
		String filePath = convertDOSPath(strFilePath);
		String path = "";
		int i = filePath.lastIndexOf("/");
		if (i != -1) {
			path = filePath.substring(0, i);
		}
		return path;
	}

	/**
	 * 获得目录下的文件列表.
	 * <p>
	 * <b>Example:</b> <blockquote>
	 * 
	 * <pre>
	 * String[] fileList = UitlFile.getFileList(&quot;c:/oracle/admin/kmis&quot;);
	 * for (int i = 0; i &lt; fileList.length; i++) {
	 * 	System.out.println(fileList[i]);
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param directory
	 *            目录名,如果参数非目录则返回NULL
	 * @return String[] 文件列表（全路径）,参数错误或不存在文件时返回null
	 * @throws IOException
	 * @since BASE 0.1
	 */
	public static String[] getFileList(String directory) throws IOException {
		directory = convertDOSPath(directory);
		if (directory.charAt((directory.length() - 1)) == 47)
			directory = directory.substring(0, directory.length() - 1);

		File path = new File(directory);
		if (!path.isDirectory())
			return null;
		ArrayList rc = new ArrayList();
		String[] list = path.list();

		for (int i = 0; i < list.length; i++) {
			list[i] = directory + "/" + list[i];
			// 是目录则递归调用
			File item = new File(list[i]);
			if (item.isDirectory()) {
				String[] tmp = getFileList(list[i]);
				for (int j = 0; j < tmp.length; j++) {
					rc.add(tmp[j]);
				}
				continue;
			}
			// 是文件则记录
			rc.add(list[i]);
		}

		return (String[]) rc.toArray(new String[rc.size()]);
	}

	/**
	 * 往文件中追加字符串.
	 * 
	 * @param path
	 * @param info
	 * @return
	 */
	public static boolean fileAppend(String path, String info) {
		File f = new File(path);
		try {
			if (f.exists()) {
				try(
					PrintWriter	out = new PrintWriter(new FileWriter(path, true));
					){
					out.print(info + "\n");
					return true;
				}
			} else {
				try(
					PrintWriter	out = new PrintWriter(new FileWriter(f));
					){
					out.print(info + "\n");
					return true;
				}
			}
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 文件读入字符串.
	 * 
	 * @param srcfile
	 *            File对象
	 * @return
	 */
	public static String getFileContent(File srcfile) {
		String strReturn = "";

		try(
			BufferedInputStream buff = new BufferedInputStream(new FileInputStream(srcfile));
			){

			int in = 0;
			do {
				in = buff.read();
				if (in != -1) {
					strReturn += (char) in;
				}
			} while (in != -1);
			buff.close();

			return strReturn;
		} catch (Exception e) {
			return strReturn;
		}
	}

	/**
	 * 文件读入字符串.
	 * 
	 * @param fpath
	 *            文件路径
	 * @return
	 */
	public static String getFileContent(String fpath) {
		File srcfile = new File(fpath);
		return getFileContent(srcfile);

	}

	/**
	 * 累加目录.
	 * 
	 * @param sourceDir
	 *            String
	 * @param appendDir
	 *            String
	 * @return String
	 */
	public static String appendDirectory(String sourceDir, String appendDir) {
		sourceDir = convertDOSPath(sourceDir);
		appendDir = convertDOSPath(appendDir);
		if (appendDir.startsWith("/")) {
			appendDir = appendDir.substring(1);
		}
		String rc = null;
		if (sourceDir.endsWith("/")) {
			rc = sourceDir + appendDir;
		} else {
			rc = sourceDir + "/" + appendDir;
		}
		return rc;
	}

	/**
	 * 读取文件到字符串.
	 * 
	 * @param filePath
	 * @param iReadBlockSize
	 * @return StringBuffer
	 * @throws IOException
	 * @since BASE 0.1
	 */
	public static StringBuffer readTextFile(String filePath, int iReadBlockSize){
		File file = new File(filePath);
		try(
			BufferedReader in = new BufferedReader(new FileReader(file));
			){
			int readLen = -1;
			char[] c = new char[iReadBlockSize];
			StringBuffer buf = new StringBuffer();
			while ((readLen = in.read(c)) != -1) {
				buf.append(c);
			}
			return buf;
		}catch(IOException e) {
			logger.error(e.toString());
		}
		return null;
	}

	/**
	 * 读取文件到字符串,使用默认缓冲区.
	 * 
	 * @param filePath
	 * @return StringBuffer
	 * @throws IOException
	 * @since BASE 0.1
	 */
	public static StringBuffer readTextFile(String filePath) throws IOException {
		return readTextFile(filePath, DEFAULT_READ_BLOCK_SIZE);

	}
	/**
	 * 将String写入文件
	 *
	 * @param data
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeTextFile(String data, String filePath)
			throws IOException {
		BufferedReader in = new BufferedReader(new StringReader(data));
		try(
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
			){
			String s;
			while ((s = in.readLine()) != null)
				out.println(s);
		} catch (EOFException ex) {
			System.out.println("End of stream");
		}

	}
	/**
	 * 格式化文件路径
	 *
	 * @param path
	 * @return
	 */
	public static String formatPath(String path){
		path=convertDOSPath(path);
		if(!path.endsWith("/"))
			path+="/";
		return path;
	}
}
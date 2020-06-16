package com.htht.job.core.util;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.security.Key;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    public static final String FILE_SEPARATOR = "file.separator";

    public static String getFilePhysicalLocation(long userid) {
        String uploadPath = "";
        uploadPath = "uploadfiles/album/";
        String[] dirs = new String[3];
        for (int i = 1; i < 4; i++) {
            long temp = (long) (userid / Math.pow(10, i));
            dirs[i - 1] = (Long.valueOf(temp % 10)).toString();
        }

        try {
            String root = null;
            URL url = FileUtil.class.getResource("/");
            java.io.File classDir = new java.io.File(url.getPath());
            java.io.File f = new java.io.File(classDir.getParent());
            java.io.File f1 = new java.io.File(f.getParent());
            root = f1.getAbsolutePath();

            root = URLDecoder.decode(root, "UTF-8");
            java.io.File dir = new java.io.File(root + System.getProperty(FILE_SEPARATOR) + uploadPath
                    + System.getProperty(FILE_SEPARATOR) + dirs[2] + System.getProperty(FILE_SEPARATOR) + dirs[1]
                    + System.getProperty(FILE_SEPARATOR) + dirs[0]);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            String tempdir = dir.toString();
            int i = tempdir.indexOf("uploadfiles");
            tempdir = tempdir.substring(i);
            tempdir = tempdir.replace('\\', '/');
            return tempdir;
        } catch (Exception e) {
        	logger.error(e.toString());
        }
        return null;
    }

    public static boolean deleteFile(File file) {
        try {
			return Files.deleteIfExists(file.toPath());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return false;
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
        List<File> allUploadFiles = new ArrayList<>();
        String myreg = reg.replace("?", "[\\s\\S]{1}").replace("*", "[\\s\\S]*");
        getAllFiles(filepath, myreg, allUploadFiles);
        return allUploadFiles;

    }

    public static void getScanFiles(File scanPath, File childFile, List<File> mssList) {
        File[] listFiles = scanPath.listFiles();
        for (File file : listFiles) {
            if (file.getName().contains(childFile.getName().replace(".tiff", ""))) {
                mssList.add(childFile);
            }
        }
    }

    public static List<File> getDateAllFiles(String filepath, String reg) {
        List<File> allUploadFiles = new ArrayList<>();
        String myreg = reg.replace("?", "[\\s\\S]{1}").replace("*", "[\\s\\S]*");
        getDateAllFiles(filepath, myreg, allUploadFiles);
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

    private static void getDateAllFiles(String filepath, String reg, List<File> allFiles) {

        File file = new File(filepath); //T:\soa_share\GF1测试数据\GF1-廊坊\langfang
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
                    if (files[i].isFile()) {
                        getDateAllFiles(files[i].getPath(), reg, allFiles);
                    } else {
                        //日期正则2018-01-01
                        Pattern p = Pattern.compile("(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)");
                        Matcher m = p.matcher(files[i].getName());
                        if (m.matches()) {
                            String dateFolder = files[i].getName();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date parse = simpleDateFormat.parse(dateFolder);
                                Date date = new Date();
                                Calendar calendar = new GregorianCalendar();
                                calendar.setTime(date);
                                calendar.add(Calendar.DATE, -2); //把日期往后增加一天,整数  往后推,负数往前移动
                                date = calendar.getTime(); //这个时间就是日期往后推一天的结果
                                if (parse.getTime() >= date.getTime()) {
                                    getDateAllFiles(files[i].getPath(), reg, allFiles);
                                }
                            } catch (ParseException e) {
                                logger.error(e.toString());
                            }
                        }
                    }
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
            fileSet = new HashSet<>();
        }
        if (null != file) {
            if (file.isFile()) {
                fileSet.add(file);
            } else if (file.isDirectory()) {
                for (File tempFile : file.listFiles()) {
                    getAllFiles(tempFile, fileSet);
                }
            }
        }
        return fileSet;
    }

    /**
     * @param file
     * @param fileSet
     * @param reg     文件名正则
     * @return
     */
    public static Set<File> getAllFiles(File file, Set<File> fileSet, String reg) {
        if (fileSet == null) {
            fileSet = new HashSet<>();
        }
        Pattern p = Pattern.compile(reg);
        getAllFiles(file, fileSet);
        Set<File> resultSet = new HashSet<>();
        for (File tempFile : fileSet) {
            if (p.matcher(tempFile.getName()).matches()) {
                resultSet.add(tempFile);
            }
        }
        fileSet.clear();
        fileSet.addAll(resultSet);
        return fileSet;
    }

    /**
     * @param userid
     * @param uploadPath
     * @return
     */
    public static String getFilePhysicalLocation(long userid, String uploadPath) {
        String[] dirs = new String[3];
        for (int i = 1; i < 4; i++) {
            long temp = (long) (userid / Math.pow(10, i));
            dirs[i - 1] = (Long.valueOf(temp % 10)).toString();
        }

        try {
            String root = null;
            URL url = FileUtil.class.getResource("/");
            java.io.File classDir = new java.io.File(url.getPath());
            java.io.File f = new java.io.File(classDir.getParent());
            java.io.File f1 = new java.io.File(f.getParent());
            root = f1.getAbsolutePath();

            root = URLDecoder.decode(root, "UTF-8");
            java.io.File dir = new java.io.File(root + System.getProperty(FILE_SEPARATOR) + uploadPath
                    + System.getProperty(FILE_SEPARATOR) + dirs[2] + System.getProperty(FILE_SEPARATOR) + dirs[1]
                    + System.getProperty(FILE_SEPARATOR) + dirs[0]);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            String tempdir = dir.toString();
            int i = tempdir.indexOf("uploadfiles");
            tempdir = tempdir.substring(i);
            tempdir = tempdir.replace('\\', '/');
            return tempdir;
        } catch (Exception e) {
        	logger.error(e.toString());
        }
        return null;
    }

    /**
     * @param path
     * @throws Exception
     */
    public static  void createFir(String path) {
        File dirName = new File(path);
        try {
			Files.deleteIfExists(dirName.toPath());
		} catch (IOException e) {
			logger.error(e.toString());
		}
        dirName.mkdir();
    }

    /**
     * @param path
     * @param filename
     * @return
     * @throws IOException 
     * @throws Exception
     */
    public static File createFile(String path, String filename) throws IOException{
        File fileName = new File(path, filename);
        Files.deleteIfExists(fileName.toPath());
        boolean isCreate = fileName.createNewFile();
        if(isCreate) {
        	logger.info(fileName.getAbsolutePath());
        }
        return fileName;
    }

    /**
     * @param str
     * @param file
     * @throws Exception
     */
    public static void writeFile(String str, File file){
        
		try(
			FileWriter fw = new FileWriter(file);
			){
			fw.write(str);
		} catch (IOException e) {
			logger.error(e.toString());
		}
    }

    public static void writeFileUTF8(String str, File file) {
        try(
        		OutputStream out = new FileOutputStream(file);
        		BufferedWriter rd = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
        	){
            rd.write(str);
        } catch (IOException e) {
            logger.error(e.toString());
        }

    }

    public static void writeFile(byte[] bytes, File file){
		try(
			FileOutputStream fo = new FileOutputStream(file);
			){
			fo.write(bytes, 0, bytes.length);
			fo.flush();
		} catch (IOException e) {
			logger.error(e.toString());
		}
    }

    public static void witeFile(byte[] bytes, String file) {
    	writeFile(bytes, new File(file));
    }

    /**
     * 获取文件扩展名称
     *
     * @param file
     * @return
     */
    public static String getFileExtensionName(File file) {
        String fname = file.getName();
        int index = fname.lastIndexOf('.');
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
    public static String getNoFileExtensionName(String file) {
        return getNoFileExtensionName(new File(file));
    }

    /**
     * 获取文件名称不包括扩展名
     *
     * @param file
     * @return
     */
    public static String getNoFileExtensionName(File file) {
        String fname = file.getName();
        int index = fname.lastIndexOf('.');
        if (index != -1) {
            return fname.substring(0, index);
        } else {
            return fname;
        }
    }
    /**
	 * 根据文件路径返回文件名称
	 * 
	 * @param filePath文件路径
	 * @return 文件名称
	 */
	public static String getFileName(String filePath) {

		if (filePath != null && !filePath.isEmpty()) {
			if (filePath.lastIndexOf('\\') > filePath.lastIndexOf('/')) {
				filePath = filePath.substring(filePath.lastIndexOf('\\') + 1);
			} else {
				filePath = filePath.substring(filePath.lastIndexOf('/') + 1);
			}
		}
		return filePath;
	}

	/**
	 * 根据文件路径返回文件目录
	 * 
	 * @param filePath文件路径
	 * @return 文件名称
	 */
	public static String getFilePath(String filePath) {
		if (filePath.isEmpty()) {
			return null;
		}
		String fileName = getFileName(filePath);

		return filePath.replace(fileName, "");
	}
	static public long copyAndEncrypt(String from, String to) {
		try {
			String fileName = getFileName(to);
			SecureRandom sr = new SecureRandom();
			Key privateKey = getKey(fileName);// 存放解密密码
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec spec = new IvParameterSpec(privateKey.getEncoded());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey, spec, sr);

			File f = new File(to);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			try(
				FileInputStream bin = new FileInputStream(from);
				FileOutputStream bout = new FileOutputStream(to);
				){
				int c = 0;
				long result = 0;
				byte[] d;
				byte[] bytes = new byte[1024];
				while ((c = bin.read(bytes)) != -1) {
					result += c;
					if (c != 1040) {
						d = new byte[c];
						for (int i = 0; i < c; i++) {
							d[i] = bytes[i];
						}
						bout.write(cipher.doFinal(d));
					} else {
						bout.write(cipher.doFinal(bytes));
					}
				}
				return result;
			}
		} catch (Exception e) {
			return 0;
		} 
	}
	
	/**
	 * 生成加密key
	 * 
	 * @param strKey
	 * @return
	 */
	private static SecretKey getKey(String strKey) {
		try {
			KeyGenerator _generator = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(strKey.getBytes());
			_generator.init(128, secureRandom);
			return _generator.generateKey();
		} catch (Exception e) {
			throw new RuntimeException("初始化密钥出现异常");
		}
	}
	
    /**
     * @param path
     * @throws Exception
     */
    public static void deleteDir(String path) throws IOException {
        File dirName = new File(path);
        Files.deleteIfExists(dirName.toPath());
    }

    /**
     * 递归删除文件目录
     *
     * @param vpath
     * @return
     */
    public static boolean delDir(String vpath) {
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
                       delDir(childFile.getPath());
                    } else {
                    	Files.deleteIfExists(childFile.toPath());
                    }
                }
                rBool = Files.deleteIfExists(delPath.toPath());
                return rBool;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static byte[] readFile(File file){
        byte[] by = new byte[(int) file.length()];
		try(
			FileInputStream in = new FileInputStream(file);
			){
		    in.read(by, 0, by.length);
			return by;
		}catch (IOException e) {
			logger.error(e.toString());
		}
         return by;
    }

    /**
     * @param path
     * @throws Exception
     */
    public static void deleteFile(String path){
        File fileName = new File(path);
        try {
			Files.deleteIfExists(fileName.toPath());
		} catch (IOException e) {
			logger.error(e.toString());
		}
    }

    /**
     * @param dirpath
     * @return
     * @throws Exception
     */
    public static String[] listFiles(String dirpath){
        File file = new File(dirpath);
        File[] files = file.listFiles();
        String[] fileslist = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String newsFileName = files[i].getName();
            fileslist[i] = dirpath + "\\" + newsFileName;
        }
        return fileslist;
    }

    /**
     * @param from java.lang.String
     * @param to   java.lang.String
     */

    public static boolean copy(String from, String to) {
    	to = replace(to, " \\", "/ <file://\\> ");
    	String toPath = to.substring(0, to.lastIndexOf('.'));
    	File f = new File(toPath);
    	if (!f.exists())
    		f.mkdirs();
        try(
    		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(from));
    		BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(to));
        	){
            int c;
            while ((c = bin.read()) != -1)
                bout.write(c);
            return true;
        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }

    }

    /**
     * @param ss java.lang.String
     * @return java.lang.String
     */
    public static String replace(String srcStr, String oldStr, String newStr) {
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
     * @param from
     * @param to
     * @throws Exception
     */
    public static void moveFile(String from, String to) {
        boolean ok = copy(from, to);
        if (ok)
            deleteFile(from);
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
     * @param path
     * @param date
     * @return
     */
    public static String getPathByDate(String path, Date date) {
        path = FileUtil.formatePath(path);
        int begin = path.indexOf('{');
        int end = 0;
        while (begin > 0) {
            end = path.indexOf('}', begin) + 1;
            String str1 = path.substring(0, begin);
            String str2 = path.substring(begin, end);
            String str3 = path.substring(end);
            SimpleDateFormat formate = new SimpleDateFormat(str2);
            path = str1 + formate.format(date) + str3;
            begin = path.indexOf('{', end) + 1;
        }
        path = path.replace("{", "").replace("}", "");
        return path;
    }
    /**
	 * nio拷贝
	 * 
	 * @param inFile
	 *            源文件
	 * @param outFile
	 *            目标文件
	 * @return
	 * @throws Exception
	 */
	public static long fileChannelCopy(File inFile, File outFile){
		try(
			FileInputStream fin = new FileInputStream(inFile);
			FileOutputStream fout = new FileOutputStream(outFile);
			){
			FileChannel inc = fin.getChannel();
			FileChannel outc = fout.getChannel();
			int bufferLen = 2097152;
			ByteBuffer bb = ByteBuffer.allocateDirect(bufferLen);
			while (true) {
				int ret = inc.read(bb);
				if (ret == -1) {
					fin.close();
					fout.flush();
					fout.close();
					break;
				}
				bb.flip();
				outc.write(bb);
				bb.clear();
			}
			return inFile.length();
		}catch(IOException e) {
			return 0;
		}

	}
}

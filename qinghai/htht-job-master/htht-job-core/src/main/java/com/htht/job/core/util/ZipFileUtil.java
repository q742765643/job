package com.htht.job.core.util;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipException;



public class ZipFileUtil {
	private static final int buffLength = 1024;

	public final static void createZip(String baseDir, String toDirZip)
			throws Exception {
		List<File> fileList = getSubFiles(new File(baseDir));
		if (fileList.size() <= 0)
			return;
		File existsFile = new File(toDirZip);
		if (existsFile.exists()) {
			existsFile.delete();
		}
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(toDirZip));
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		int readLen = 0;
		for (int i = 0; i < fileList.size(); i++) {
			File f = (File) fileList.get(i);
			ze = new ZipEntry(getAbsFileName(baseDir, f));
			ze.setSize(f.length());
			ze.setTime(f.lastModified());
			zos.putNextEntry(ze);
			InputStream is = new BufferedInputStream(new FileInputStream(f));
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				zos.write(buf, 0, readLen);
			}
			is.close();
		}
		zos.close();
	}
	
	/**
	 * 将多个文件打包成zip 输出到指定的流中
	 * @param ous 输出流
	 * @param fileList 文件集合
	 */
	public static void writeZipFile(OutputStream ous, List<File> fileList) {
		ZipOutputStream zos = new ZipOutputStream(ous);
		byte[] buf=new byte[1024];
		int readLen = -1;
		try{
			for(File file : fileList){
				ZipEntry ze = new ZipEntry(file.getName());
				ze.setSize(file.length());
				ze.setTime(file.lastModified());
				zos.putNextEntry(ze);
				InputStream is = new BufferedInputStream(new FileInputStream(file));
				// 读取文件流，写入到zip 输出流中
				while ((readLen = is.read(buf, 0, 1024))!=-1) {
					zos.write(buf, 0, readLen);
				}
				is.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				zos.close();
			} catch (IOException e) {
			}
		}
	}
	public static void writeZipFile(OutputStream ous, File[] fileList) {
		List<File> list = new ArrayList<File>(fileList.length);
		for (int i = 0; i < fileList.length; i++) {
			list.add(fileList[i]);
		}
		writeZipFile(ous,list);
	}

	public static void writeZipFile(OutputStream ous, String[] files){
		File[] roots = new File[files.length];
		for (int i = 0 ;i < files.length;i++) {
			File file = new File(files[i]);
			roots[i] = file;
		}
		writeZipFile(ous,roots);
	}
	/**
	 * 建一个文件流写入到zip流中
	 * @param zipOus zip 输出文件流
	 * @param inputstreams 写入文件流
	 * @param fileName zip包中的文件名称
	 * @throws IOException
	 */
	public static void writeZipFile(ZipOutputStream zipOus, InputStream inputstreams,String fileName)
	throws IOException {
		byte[] buf = new byte[1024];
		int readLen = -1;
		ZipEntry ze = new ZipEntry(fileName);
		ze.setSize(inputstreams.available());
		ze.setTime(System.currentTimeMillis());
		zipOus.putNextEntry(ze);
		InputStream is = new BufferedInputStream(inputstreams);
		// 读取文件流，写入到zip 输出流中
		while ((readLen = is.read(buf, 0, 1024)) != -1) {
			zipOus.write(buf, 0, readLen);
		}
		is.close();
}
	
	public static void writeZipFile(ZipOutputStream zipOus, byte[] bytes,String fileName)
	throws IOException {
		writeZipFile(zipOus,new ByteArrayInputStream(bytes),fileName);
	}
	
	private static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				ret = new File(ret, dirs[i]);
			}
		}
		if (!ret.exists()) {
			ret.mkdirs();
		}
		ret = new File(ret, dirs[dirs.length - 1]);
		return ret;
	}

	public static String getAbsFileName(String baseDir, File realFileName) {
		File real = realFileName;
		File base = new File(baseDir);
		String ret = real.getName();
		while (true) {
			real = real.getParentFile();
			if (real == null)
				break;
			if (real.equals(base))
				break;
			else {
				ret = real.getName() + "/" + ret;
			}
		}
		return ret;
	}

	public final static List<File> getSubFiles(File baseDir) {
		List<File> ret = new ArrayList<File>();
		File[] tmp = baseDir.listFiles();
		if (tmp != null) {
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i].isFile()) {
					ret.add(tmp[i]);
				}
				if (tmp[i].isDirectory()) {
					ret.addAll(getSubFiles(tmp[i]));
				}
			}
		}
		return ret;
	}

	public final static List<File> getRootFiles(File baseDir) {

		List<File> ret = new ArrayList<File>();
		File[] tmp = baseDir.listFiles();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].isFile())
				ret.add(tmp[i]);
		}
		return ret;
	}
	/**
	 * 去掉文件名后缀
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf(".");
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
	/**
	 * 删除目录
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	/**
	 * 解压文件
	 * @param toPath  解压后所在目录
	 * @param zipFilePath  待解压文件全路径
	 */
	public static void decompressFiles(String toPath, String zipFilePath) {
		if (!zipFilePath.toLowerCase().endsWith(".rar")) {
			decompressZipFiles(toPath,zipFilePath);
		}else{
			decompressRarFiles(toPath,zipFilePath);
		}
	}
	
	/**
	 * 解压rar
	 * @param toPath
	 * @param zipFilePath
	 */
	public final static void decompressRarFiles(String toPath,String zipFilePath) {
		String dirOrFileName = "";
		File dstDiretory = new File(toPath);
        if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
            dstDiretory.mkdirs();
        }
        Archive a = null;
        try {
            a = new Archive(new File(zipFilePath));
            if (a != null) {
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                	if(fh.isUnicode()){
                    	dirOrFileName = fh.getFileNameW().trim();
                    }else{
                    	dirOrFileName = fh.getFileNameString().trim();
                    }
                    if (fh.isDirectory()) { // 文件夹 
                        File fol = new File(toPath + File.separator + dirOrFileName);
                        fol.mkdirs();
                    } else { // 文件
                        File out = new File(toPath + File.separator+ dirOrFileName);
                        //System.out.println(out.getAbsolutePath());
                        try {// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压. 
                            if (!out.exists()) {
                                if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录. 
                                    out.getParentFile().mkdirs();
                                }
                                out.createNewFile();
                            }
                            FileOutputStream os = new FileOutputStream(out);
                            a.extractFile(fh, os);
                            os.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 解压zip
	 * @param toPath
	 * @param zipFilePath
	 */
	public final static void decompressZipFiles(String toPath, String zipFilePath) {
		zipFilePath = zipFilePath.replace('\\', '/');
		File zFile = new File(zipFilePath);
		try {
			String outputDirectory = toPath+ File.separator;
			ZipFile zipFile = new ZipFile(zFile, "GBK");
			Enumeration e = zipFile.getEntries();
			ZipEntry zipEntry = null;
			
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				if (zipEntry.isDirectory()) {
					createDirectory(outputDirectory, "");
				} else {
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');
					if (fileName.indexOf("/") != -1) {
						createDirectory(outputDirectory, fileName.substring(0,fileName.lastIndexOf("/")));
					}

					File f = new File(outputDirectory + zipEntry.getName());

					f.createNewFile();
					InputStream in = zipFile.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);

					int c;
					byte[] buff = new byte[buffLength];
					while ((c = in.read(buff)) != -1) {
						out.write(buff, 0, c);
					}
					out.close();
					in.close();
				}
			}
			zipFile.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// zFile.delete();
		}
	}
	
	/**
	 * 解压缩zip或rar包中某个文件，解压到目标文件中
	 * 
	 * @param zipFilePath zip 压缩包
	 * @param toFilePath  目标文件路径
	 */
	public static void decompressFile(String zipOrRarFilePath,String fileName,String toPath) {
		if (!zipOrRarFilePath.toLowerCase().endsWith(".rar")) {
			decompressZipFile(zipOrRarFilePath,fileName,toPath);
		}else{
			decompressRarFile(zipOrRarFilePath,fileName,toPath);
		}
	}
	
	/**
	 * 解压缩zip包中某个文件，解压到目标文件中
	 * 
	 * @param zipFilePath zip 压缩包
	 * @param toFilePath  目标文件路径
	 */
	public static void decompressZipFile(String zipFilePath,String fileName,String toPath) {
		zipFilePath = zipFilePath.replace('\\', '/');
		File zFile = new File(zipFilePath);
		try {
			ZipFile zipFile = new ZipFile(zFile, "GBK");
			Enumeration e = zipFile.getEntries();
			ZipEntry zipEntry = null;
			
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				if (zipEntry.isDirectory()) {
				} else {
					String zipEntryName = zipEntry.getName();
					File zipEntryFile = new File(zipEntryName);
					// 文件mign
					if(getFileNameNoEx(zipEntryFile.getName()).equals(fileName) ){
						unZip(zipFile,zipEntry,new File(toPath,zipEntryFile.getName()).getPath());
					}
				}
			}
			zipFile.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
	}
	
	private static void unZip(ZipFile zipFile,ZipEntry zipEntry,String toFile) throws ZipException, IOException{
		InputStream in = zipFile.getInputStream(zipEntry);
		FileOutputStream out = new FileOutputStream(toFile);
		int c;
		byte[] buff = new byte[buffLength];
		while ((c = in.read(buff)) != -1) {
			out.write(buff, 0, c);
		}
		out.close();
		in.close();
	}
	
	/**
	 * 解压缩rar包中某个文件，解压到目标文件中
	 * 
	 * @param zipFilePath rar 压缩包
	 * @param toFilePath  目标文件路径
	 */
	public static void decompressRarFile(String rarFilePath,String fileName,String toPath) {
		String dirOrFileName = "";
		File dstDiretory = new File(toPath);
        if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
            dstDiretory.mkdirs();
        }
        Archive a = null;
        try {
            a = new Archive(new File(rarFilePath));
            if (a != null) {
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                	if(fh.isUnicode()){
                    	dirOrFileName = fh.getFileNameW().trim();
                    }else{
                    	dirOrFileName = fh.getFileNameString().trim();
                    }
                    if (fh.isDirectory()) {
                    } else { // 文件
    					File zipEntryFile = new File(dirOrFileName);
    					// 文件mign
    					if(getFileNameNoEx(zipEntryFile.getName()).equals(fileName) ){
    						unRar(a,fh,new File(toPath,zipEntryFile.getName()).getPath());
    					}
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	
	}
	
	private static void unRar(Archive a,FileHeader fh,String toFile) throws ZipException, IOException{
		InputStream in = null;
		try {
			in = a.getInputStream(fh);
		} catch (RarException e) {
			e.printStackTrace();
		}
		FileOutputStream out = new FileOutputStream(toFile);
		int c;
		byte[] buff = new byte[buffLength];
		while ((c = in.read(buff)) != -1) {
			out.write(buff, 0, c);
		}
		out.close();
		in.close();
	}
	
	private static void createDirectory(String directory, String subDirectory) {
		String dir[];
		File fl = new File(directory);
		try {
			if (subDirectory.equals("") && fl.exists() != true)
				fl.mkdir();
			else if (!subDirectory.equals("")) {
				dir = subDirectory.replace('\\', '/').split("/");
				for (int i = 0; i < dir.length; i++) {
					File subFile = new File(directory + File.separator + dir[i]);
					if (subFile.exists() == false)
						subFile.mkdir();
					directory += File.separator + dir[i];
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	/**
	 * 判断算法zip所属类型，exe,jar,c++
	 * @param zipFilePath
	 */
	public static String checkZipType(String zipFilePath) {
		zipFilePath = zipFilePath.replace('\\', '/');
		File zFile = new File(zipFilePath);
		try {
			ZipFile zipFile = new ZipFile(zFile, "GBK");
			Enumeration e = zipFile.getEntries();
			ZipEntry zipEntry = null;
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				if (zipEntry.isDirectory()) {
				} else {
					String zipEntryName = zipEntry.getName();
					File zipEntryFile = new File(zipEntryName);
					if(FileUtil.getFileExtensionName(zipEntryFile).equals(".jar")){
						return "jar";
					}else if(FileUtil.getFileExtensionName(zipEntryFile).equals(".exe")){
						return "exe";
					}
				}
			}
			zipFile.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
		return "c++";
	}
	
	/**
	 * 获取dll名称
	 * @param zipFilePath
	 * @return
	 */
	public static String getDllName(String zipFilePath) {
		zipFilePath = zipFilePath.replace('\\', '/');
		File zFile = new File(zipFilePath);
		String dllName = "";
		try {
			ZipFile zipFile = new ZipFile(zFile, "GBK");
			Enumeration e = zipFile.getEntries();
			ZipEntry zipEntry = null;
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				if (zipEntry.isDirectory()) {
				} else {
					String zipEntryName = zipEntry.getName();
					File zipEntryFile = new File(zipEntryName);
					if(FileUtil.getFileExtensionName(zipEntryFile).equals(".xml")){
						dllName = FileUtil.getFileName(zipEntryFile);
						break;
					}
				}
			}
			zipFile.close();
			return dllName;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
		return null;
	}
	
	/**
	 * 将xml文件放到已上传的zip包中
	 * @param xml
	 * @param zipPath
	 */
	public static void addXmlFileToZip(String xml,String zipPath){
		//已经上传的zip包
		File file = new File(zipPath);
		//创建一个解压到哪里的文件夹
		File fileDir = new File(file.getParent() + File.separator + ZipFileUtil.getFileNameNoEx(file.getName()));
		fileDir.mkdir();
		//先将压缩包解压到当前文件夹
		ZipFileUtil.decompressZipFiles(fileDir.getAbsolutePath(), zipPath);
		//删除原有zip包
		boolean isDelete = file.delete();
		//判断是否已经删除
		if(isDelete == true){
			try {
				//xml文件名
				String xmlName = ZipFileUtil.getFileNameNoEx(file.getName());
				//在解压的文件夹中创建xml文件
				File xmlFile = new File(fileDir.getAbsolutePath()+File.separator+xmlName+File.separator+xmlName+".xml");
				//将xml内容写入到xml文件中
				OutputStream out = new FileOutputStream(xmlFile);
				BufferedWriter rd = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
				rd.write(xml);
				rd.close();
				out.close();
				//将文件夹重新打包成zip
				ZipFileUtil.createZip(fileDir.getAbsolutePath(),zipPath);
				//然后删除文件夹
				ZipFileUtil.deleteDir(fileDir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		ZipFileUtil a = new ZipFileUtil();
//		a.decompressFile("Y:\\UPLOAD_ZIP_PATH\\ytwps.zip","ytwps","Y:\\UPLOAD_ZIP_PATH");
		a.decompressRarFile("C:\\Users\\Administrator.dz-PC\\Desktop\\ExtractTarExe.rar", "ICSharpCode.SharpZipLib", "D:\\测测");
		
	}
}

package org.htht.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class FileZip {
	public final static void createZip(String baseDir, String toDirZip)throws Exception{
		List fileList=getSubFiles(new File(baseDir));
		//System.out.println("-----------------------------------------"+(fileList.size()));
		if(fileList.size()<=0)return;
		File existsFile = new File(toDirZip);
		if(existsFile.exists()){
			FileUtils.deleteQuietly(existsFile);
		}
		ZipOutputStream zos=new ZipOutputStream(new FileOutputStream(toDirZip));
		ZipEntry ze=null;
		byte[] buf=new byte[1024];
		int readLen=0;
		for (int i = 0; i <fileList.size(); i++) {
		File f=(File)fileList.get(i);
		ze=new ZipEntry(getAbsFileName(baseDir, f));
		ze.setSize(f.length());
		ze.setTime(f.lastModified());
		zos.putNextEntry(ze);
		InputStream is=new BufferedInputStream(new FileInputStream(f));
		while ((readLen=is.read(buf, 0, 1024))!=-1) {
		zos.write(buf, 0, readLen);
		}
		is.close();
		}
		zos.close();
	}
	
	
	public final static void unZip(String toPath, String basePath)throws Exception {
		ZipFile zfile = new ZipFile(basePath);
		System.out.println(zfile.getName());
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				continue;
			}
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(toPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
			System.out.println("Extracted: " + ze.getName());
		}
		zfile.close();
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

	public final static List getSubFiles(File baseDir) {

		List ret = new ArrayList();
		File[] tmp = baseDir.listFiles();
		if(tmp!=null){
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
	
	public final static List getRootFiles(File baseDir){
		
		List ret = new ArrayList();
		File[] tmp = baseDir.listFiles();
		for(int i = 0 ; i<tmp.length ; i++){
			if(tmp[i].isFile()) ret.add(tmp[i]);
		}
		return ret;
	}
}

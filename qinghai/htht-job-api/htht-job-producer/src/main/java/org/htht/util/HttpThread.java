package org.htht.util;


import javax.net.ssl.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpThread implements Runnable {
	
	private static void trustAllHttpsCertificates()
			throws NoSuchAlgorithmException, KeyManagementException
	{
		TrustManager[] trustAllCerts = new TrustManager[1];
		trustAllCerts[0] = new TrustAllManager();
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(
				sc.getSocketFactory());
	}

	private static class TrustAllManager
			implements X509TrustManager
	{
		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}
		@Override
		public void checkServerTrusted(X509Certificate[] certs,
									   String authType)
				throws CertificateException
		{
		}
		@Override
		public void checkClientTrusted(X509Certificate[] certs,
									   String authType)
				throws CertificateException
		{
		}
	}


	private String url;
	private String downloadPath;
	private long remoteSize;
	

	public String getUrl() {
		return url;
	}

	public HttpThread(String url, String downloadPath, long remoteSize) {
		super();
		this.url = url;
		this.downloadPath = downloadPath;
		this.remoteSize = remoteSize;
	}

	public HttpThread(){
	}
	
	@Override
	public void run() {
		File dir = new File(downloadPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		// /archive/allData/6/MOD09Q1/2017/001/MOD09Q1.A2017001.h21v03.006.2017017151100.hdf
		String fileName = url.substring(url.lastIndexOf("/")+1);
		String filePath = dir.getPath() +"/"+ fileName;
		httpDownload(url, filePath, remoteSize); 
	}
	
	
	
	/**
	 * 格式化文件大小
	 * @param fileSize
	 * @return
	 */
	public static String formatSize(Long fileSize) {
		String formatSize = "";
		DecimalFormat df = new DecimalFormat("0.##");
		if (fileSize / (1024 * 1024 * 1024) > 0) {
			formatSize = df.format(Float.parseFloat(fileSize.toString())/ (1024 * 1024 * 1024))+ " GB";
		} else if (fileSize / (1024 * 1024) > 0) {
			formatSize = df.format(Float.parseFloat(fileSize.toString())/ (1024 * 1024))+ " MB";
		} else if (fileSize / (1024) > 0) {
			formatSize = df.format(Float.parseFloat(fileSize.toString())/ (1024))+ " KB";
		} else {
			formatSize = fileSize + " 字节";
		}
		return formatSize;
	}

	/**
	 * 断点续传HTTP模式资源
	 * @param srcHttpFile
	 * @param destFile
	 * @return
	 */
	public static long httpDownload(String urlStr, String destFile, long remoteSize) {
		destFile = destFile.replaceAll("\\\\", "/");
		String tempFileName = destFile+".temp";
		File temp = new File(tempFileName);
		
		// 下载网络文件
		long bytesum = 0;
		int byteread = 0;
		InputStream is = null;
		RandomAccessFile raf = null;
		HttpURLConnection httpConnection = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		long fileSize = 0;		//temp文件的大小
		try {
			URL url = new URL(urlStr);
			String fileName = destFile.substring(destFile.lastIndexOf('/')+1);  
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("Accept-Encoding", "identity");
			httpConnection.setRequestProperty("User-Agent", "NetFox");
			httpConnection.setConnectTimeout(3000000);  		//连接超时
			httpConnection.setReadTimeout(3000000);  			//读取超时
			httpConnection.setRequestProperty ("Authorization", " Bearer 2866CBF6-6F30-11EA-B8A3-0049E8CA057A");
			if(temp.exists() ){
				if (!temp.renameTo(temp)) {			//该文件正在被下载
					System.out.println(destFile+"文件正在下载");
					return 0;
				}
				fileSize = temp.length();
				if(temp.length() == remoteSize && renameFile(temp)){
					System.out.println(destFile+"下载完成");
					return remoteSize;						//下载完成，返回
				}else if(fileSize > remoteSize){		//如果temp文件大小大于远程文件，则说明temp文件错误，删除
					temp.delete();
					fileSize = 0;
				}
			}else if(!temp.getParentFile().exists()){
				temp.getParentFile().mkdirs();
			}
			httpConnection.setRequestProperty("RANGE", "bytes=" +  fileSize + "-");
			byte[] buffer = new byte[1024 * 20];
			
			
			raf = new RandomAccessFile(temp, "rw");
			
			if(remoteSize == 0 ){
				return fileSize;
			}else {
				raf.seek(fileSize);
				is = httpConnection.getInputStream();
				if(is == null){
					System.out.println(new Date()+"获取文件流失败："+destFile);
					httpConnection.disconnect();
					return 0;
				}

				if(remoteSize == -1){
					remoteSize = httpConnection.getContentLengthLong();
				}
			    long step = (remoteSize) /100; 
			    if(step == 0){
			    	 System.out.println(sdf.format(new Date())+"http download progress： 0% ("+fileName+",文件下载失败！");   
			    	return 0;
			    }
		        long process= (fileSize+bytesum) /step;
		        System.out.println(formatSize((long)httpConnection.getContentLength()));
		        System.out.println(sdf.format(new Date())+"http download progress： "+process +"% ("+fileName+","+formatSize(fileSize+bytesum)+")：");   
		        
				while ((byteread = is.read(buffer)) != -1) {
					bytesum += byteread;
					raf.write(buffer, 0, byteread);

					long nowProcess = (fileSize+bytesum) /step;   
		            if(nowProcess > process){   
		                  process = nowProcess;   
		                 System.out.println("http download progress： "+process +"% ("+fileName+","+formatSize(fileSize+bytesum)+")");   
		            }   
				}
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if (raf != null) {
					raf.close();
				}
				if (is != null) {
					is.close();
				}
				if (httpConnection != null) {
					httpConnection.disconnect();
				}
			} catch (IOException e) {
				 e.printStackTrace();
			}
		}
		renameFile(temp);
		System.out.println(destFile+"下载完成");
		return bytesum;
	}

	public static long getRemoteSize(URL url2) throws IOException {
		try {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier
					(
							new HostnameVerifier() {
								@Override
								public boolean verify(String urlHostName, SSLSession session)
								{
									return true;
								}
							}
					);
		} catch (Exception e)  {
			e.printStackTrace();
		}


		long size = 0L;
		HttpURLConnection httpConnection = null;
		httpConnection = (HttpURLConnection) url2.openConnection();
		httpConnection.setRequestProperty("Accept-Encoding", "identity");
		httpConnection.setRequestProperty("User-Agent", "NetFox");
		httpConnection.setConnectTimeout(30000);  		//连接超时
		httpConnection.setReadTimeout(30000);  			//读取超时
		httpConnection.setRequestProperty ("Authorization", " Bearer 2866CBF6-6F30-11EA-B8A3-0049E8CA057A");
		size = httpConnection.getContentLengthLong();
		httpConnection.disconnect();
		return size;
	}

	/*
	 * 去掉temp后缀
	 */
	private static boolean renameFile(File file){
		String fileName = file.getAbsolutePath();
		int pos = fileName.indexOf(".temp");
		if(pos>0){
			if(file.renameTo(new File(fileName.substring(0, pos)))){
				file.delete();
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
//		String src="http://218.94.134.42:2073/epr/epr/attached/1/2018年江苏省野外核查土地利用土地覆盖边界灾害表.xls";
//		String src="http://10.181.89.55/cimiss-web/file?url=L3NwYWNlL2NpbWlzc19CRVhOL2RhdGEvbWV0ZGIvc2F0ZS9GWS0yRS9TQVRFX0wzX0YyRV9WSVNT%0AUl9JUjFfT1RHX1RCQi8yMDE1MDcvU0FURV9MM19GMkVfVklTU1JfSVIxX09UR19UQkItMDAxLTIw%0AMTUwNzI1LTA2MTUuQVdY";
		//String src="http://10.181.89.55/cimiss-web/file?url=L3NwYWNlL2NpbWlzc19CRVhOL2RhdGEvbWV0ZGIvc2F0ZS9GWS0yRS9TQVRFX0wzX0YyRV9WSVNT%0AUl9JUjFfT1RHX1RCQi8yMDE1MDcvU0FURV9MM19GMkVfVklTU1JfSVIxX09UR19UQkItMDAxLTIw%0AMTUwNzI2LTExMTUuQVdY";
//		String src = "https://ladsweb.modaps.eosdis.nasa.gov/archive/allData/6/MOD09A1/2018/001/MOD09A1.A2018001.h22v03.006.2018011145237.hdf";
		String src = "https://ladsweb.modaps.eosdis.nasa.gov/archive/allData/6/MOD09GQ/2019/001/MOD09GQ.A2019001.h00v08.006.2019003024734.hdf";
		String file = "D:/data/2019.hdf";
		URL url = new URL(src);
		httpDownload(src, file, getRemoteSize(url));
	}
}

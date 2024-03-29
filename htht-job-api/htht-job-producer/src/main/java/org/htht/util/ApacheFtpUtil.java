package org.htht.util;

/**
 * FTP上传工具类
 * 如果上传的是中文目录和含有中文的文件，需要
 * this.ftpClient.setControlEncoding("GBK");
 * 还需要修改Serv-U中的字符编码
 * 方法如下：
1、打开Serv-U 控制台，点击“限制和设置
2、点击“FTP设置”选项卡，下面有个“全局属性”点它。
3、在新打开的选项卡中，选中“高级选项”
4、在高级选项”里，把“对所有收发的路径和文件名使用UFT-8编码”前面的复选框，钩选去掉！ 

 * @author luyl
 * 
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.htht.job.executor.model.ftp.FtpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApacheFtpUtil {
	private FTPClient ftpClient;
	private String strIp;
	private int intPort;
	private String user;
	private String password;
	
	
	public String getStrIp() {
		return strIp;
	}

	public void setStrIp(String strIp) {
		this.strIp = strIp;
	}

	private static Logger logger = LoggerFactory.getLogger(ApacheFtpUtil.class.getName());

	/**
	 * Ftp构造函数
	 */
	public ApacheFtpUtil(String strIp, int intPort, String user, String password) {
		this.strIp = strIp;
		this.intPort = intPort;
		this.user = user;
		this.password = password;
		this.ftpClient = new FTPClient();
	}
	/**
	 * Ftp构造函数
	 */
	public ApacheFtpUtil(FtpDTO ftpDTO) {
		this.strIp = ftpDTO.getIpAddr();
		this.intPort = ftpDTO.getPort();
		this.user = ftpDTO.getUserName();
		this.password = ftpDTO.getPwd();
		this.ftpClient = new FTPClient();
	}
	
	/**
	 * Ftp构造函数
	 */
	public ApacheFtpUtil(String strIp, String user, String password) {
		this(strIp,21,user,password);
	}
	
	/**
	 * @return 判断是否登入成功
	 * */
	public boolean connectServer() {
		boolean isLogin = false;
		FTPClientConfig ftpClientConfig = new FTPClientConfig();
		ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
		this.ftpClient.setControlEncoding("GBK");
		this.ftpClient.configure(ftpClientConfig);
		try {
			if (this.intPort > 0) {
				this.ftpClient.connect(this.strIp, this.intPort);
			} else {
				this.ftpClient.connect(this.strIp);
			}
			// FTP服务器连接回答
			int reply = this.ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				this.ftpClient.disconnect();
				logger.error("登录FTP服务失败！");
				return isLogin;
			}
			this.ftpClient.login(this.user, this.password);
			// 设置传输协议
			this.ftpClient.enterLocalPassiveMode();
			this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			String printWorkingDirectory=ftpClient.printWorkingDirectory();
			logger.info("恭喜{}成功登陆FTP服务器:{}",this.user ,printWorkingDirectory);
			isLogin = true;
		} catch (Exception e) {
			logger.error("{}登录FTP服务失败！{}",this.user,e.getMessage());
			closeServer();
		}
		this.ftpClient.setBufferSize(1024 * 8);
		this.ftpClient.setConnectTimeout(30 * 1000);
		this.ftpClient.setDataTimeout(60 * 1000);
		return isLogin;
	}

	/**
	 * @退出关闭服务器链接
	 * */
	public void closeServer() {
		if (null != this.ftpClient && this.ftpClient.isConnected()) {
			try {
				boolean reuslt = this.ftpClient.logout();// 退出FTP服务器
				if (reuslt) {
					logger.info("成功退出服务器");
				}
			} catch (IOException e) {
				logger.warn("退出FTP服务器异常！{}", e.getMessage());
			} finally {
				try {
					this.ftpClient.disconnect();// 关闭FTP服务器的连接
				} catch (IOException e) {
					logger.warn("关闭FTP服务器的连接异常！");
				}
			}
		}
	}
	
	/**
	 * 取得相对于当前连接目录的某个目录下所有文件列表
	 * 
	 * @param path
	 * @return
	 */
	public List<String> getFileList(String path) {
		ftpClient.enterLocalPassiveMode();
		List<String> list = new ArrayList<>();
		try {
			FTPFile[] ftplist = ftpClient.listFiles(path);
			for(FTPFile ftpFile : ftplist){
				list.add(ftpFile.getName());
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
	/**
	 * 根据文件名规则获取指定目录下的文件列表
	 * @param path 文件目录
	 * @param pattern 文件名规则
	 * @return
	 * @throws IOException
	 */
	public FTPFile[] getDataFileList(String path,final String pattern) throws IOException{
		ftpClient.enterLocalPassiveMode();
		FTPFile[] list;
		list= ftpClient.listFiles(path,
            file->{
				Pattern p = Pattern.compile(pattern);
				boolean flag=false;
				if (file.isFile() && p.matcher(file.getName()).find()){
					flag=true;
				}
				return flag ;
			});
		return list;
	}
	
	/**
	 * 获取指定目录下的文件列表，返回DataFile类型的数据
	 * @param path
	 * @return
	 * @throws ParseException
	 
	public List<DataFile> getDataFileList(String path) throws ParseException  {
		List<DataFile> fileList = new ArrayList<DataFile>();
		try {
			FTPFile[] list = ftpClient.listFiles(path);
			for(FTPFile ftpFile : list){
				DataFile dataFile = new DataFile(ftpFile.getName());
				dataFile.setDirectory(ftpFile.isDirectory());
				dataFile.setGroup(ftpFile.getGroup());
				dataFile.setLastMofify(ftpFile.getTimestamp().getTime());
				dataFile.setName(ftpFile.getName());
				dataFile.setPath(path);
				dataFile.setPowerStr(ftpFile.getRawListing());
				dataFile.setSize(ftpFile.getSize());
				
				fileList.add(dataFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileList;
	}
	*/
	/**
	 * 获取FTP文件的大小
	 * @param filename
	 * @return
	 */
	public long getFileSize(String filename) {
		
		long fileSize = -1;
		
		filename = filename.replaceAll("\\\\", "/");
		try {
			FTPFile[] list = ftpClient.listFiles(filename);
			if(list!=null&&list.length>0){
				return list[0].getSize();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return fileSize;
	}
   
	/***
	 * 上传Ftp文件
	 * @param localFile 当地文件
	 * @param romotUpLoadePath上传服务器路径 
	 * */
	public boolean uploadFile(File localFile, String romotUpLoadePath) {
		ftpClient.enterLocalPassiveMode();
		boolean success = false;
		try(BufferedInputStream	inStream = new BufferedInputStream(new FileInputStream(localFile))) {
			this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
			logger.info("{}开始上传.....",localFile.getName() );
			success = this.ftpClient.storeFile(localFile.getName(), inStream);
			if (success) {
				logger.info("{}上传成功",localFile.getName());
				return success;
			}
		} catch (FileNotFoundException e) {
			logger.error("{}未找到",localFile);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return success;
	}
	
	/***
	 * 上传Ftp文件重载方法
	 * @param localFile 当地文件路径
	 * @param romotUpLoadePath上传服务器绝对路径,路径以 "/"分开 
	 * */
	public boolean uploadFile(String localFilepath, String romotUpLoadePath) {
		ftpClient.enterLocalPassiveMode();
		File localFile = new File(localFilepath);
		boolean success = false;
		try(BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(localFile))) {
			String romoteparent = romotUpLoadePath.substring(0,romotUpLoadePath.lastIndexOf('/'));
			createDir(romoteparent);

			logger.info("{}开始上传.....",localFile.getName());

			if (!romotUpLoadePath.startsWith("/")) {
				romotUpLoadePath = '/'+ romotUpLoadePath;
			}
			success = ftpClient.storeFile(new String(romotUpLoadePath.getBytes("UTF-8"),"iso-8859-1"),inStream);

			if (success) {
				logger.info("{}上传成功",localFile.getName());
				return success;
			}
		} catch (FileNotFoundException e) {
			logger.error("{}未找到",localFile);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return success;
	}
	
	/***
	 * 下载文件
	 * @param remoteFileName   FTP待下载文件名称
	 * @param localDires 下载到本地目录如：d:/download
	 * @param remoteDownLoadPath remoteFileName FTP上remoteFileName所在的目录
	 * */
	public boolean downloadFile(String remoteFileName, String localDires,
			String remoteDownLoadPath) {
		String strFilePath = localDires + remoteFileName;
		boolean success = false;
		try(BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(strFilePath))) {
			this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);

			logger.info("{}开始下载....",remoteFileName);
			success = this.ftpClient.retrieveFile(remoteFileName, outStream);
			if (success) {
				logger.info("{}成功下载到{}",remoteFileName,strFilePath);
				return success;
			}
		} catch (Exception e) {
			logger.error("{}下载失败",remoteFileName);
		}
		if (!success) {
			logger.error("{}下载失败!!!",remoteFileName);
		}
		return success;
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
	 * 断点续传FTP资源
	 * @param remote
	 * @param local
	 * @return
	 * @throws IOException
	 */
	public long downloadFTP(String remote, String local,long lRemoteSize)  {

		File f = new File(local);
		InputStream is = null;
		BufferedInputStream bis = null;
		long bytesum = 0;
		int byteread = 0;
		long fileSize = 0;
		try {
			ftpClient.enterLocalPassiveMode();
			//文件名称
			String fileName = remote.substring(remote.lastIndexOf('/')+1);

			if (f.exists()) {
				fileSize = f.length();
			}
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}

			try(OutputStream out = new FileOutputStream(f, true)) {


				String progressInfo = "(" + fileName + "," + formatSize(fileSize + bytesum) + "/" + formatSize(lRemoteSize) + ")";

				//如果文件已经下载完毕，直接返回
				if (f.length() >= lRemoteSize) {
					logger.info("ftp download progress：已下载完毕{} ", progressInfo);
					return f.length();
				}
				ftpClient.setRestartOffset(fileSize);
				ftpClient.setBufferSize(1204 * 8);
				byte[] buffer = new byte[1204 * 8];

				is = ftpClient.retrieveFileStream(remote);
				bis = new BufferedInputStream(is);
				long step = lRemoteSize / 100;
				long process = (fileSize + bytesum) / step;
				logger.info("ftp download progress:{}%{} ", process, progressInfo);

			while ((byteread = bis.read(buffer)) != -1) {
				bytesum += byteread;
				out.write(buffer, 0, byteread);
				long nowProcess = (fileSize+bytesum) /step;
	            if(nowProcess > process){
	                 process = nowProcess;
	                 String fileSizeByteSum=formatSize(fileSize+bytesum);
					 String remoteSize=formatSize(lRemoteSize);
					 logger.info("ftp download progress:{}%({},{}/{})", process, fileName,fileSizeByteSum,remoteSize);
				}
			}
			}
		}catch(SocketTimeoutException e){
			return -2;
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			return -1;
		} finally{
			try{
				if(is!=null){
					is.close();
					ftpClient.completePendingCommand();//is的close()后面调用，避免程序死掉
				}
				if(bis!=null){
					bis.close();
				}
			}catch(IOException ie){
				logger.error(ie.getMessage(),ie);
			}
		}

		return f.length();
	}

	/***
	 * 上传文件夹
	 * @param localDirectory
	 *            当地文件夹
	 * @param remoteDirectoryPath
	 *            Ftp 服务器路径 以"/"分隔 (FTP上的文件夹)
	 * */
	public boolean uploadDirectory(String localDirectory, String remoteDirectoryPath) {
		File src = new File(localDirectory);
		createDir(remoteDirectoryPath);
		File[] allFile = src.listFiles();
		for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
			if (!allFile[currentFile].isDirectory()) {
				String srcName = allFile[currentFile].getPath();
				uploadFile(new File(srcName), remoteDirectoryPath);
			}
		}
		for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
			if (allFile[currentFile].isDirectory()) {
				// 递归
				String remoteDirPath =remoteDirectoryPath +'/'+allFile[currentFile].getName();
				uploadDirectory(allFile[currentFile].getPath(),remoteDirPath);
			}
		}
		return true;
	}
	
	/**
	 * 在当前目录创建目录
	 */
	private boolean createDir(String remoteDirectoryPath) {
		
		try {
			if(!this.ftpClient.changeWorkingDirectory(remoteDirectoryPath)){
				String[] pathdir = remoteDirectoryPath.split("/");
				StringBuilder tempRemote = new StringBuilder();
				for(int i=1;i<pathdir.length;i++){
					tempRemote.append ("/"+pathdir[i]);
					if(!this.ftpClient.changeWorkingDirectory(tempRemote.toString())){
						this.ftpClient.makeDirectory(tempRemote.toString());
					}
				}
			}
			return true;
		} catch (IOException e) {
			logger.info("{}目录创建失败",remoteDirectoryPath);
		}
		return false;
	}
	
	/***
	 * 下载文件夹
	 * @param localDirectoryPath本地地址
	 * @param remoteDirectory 远程文件夹
	 * */
	public boolean downLoadDirectory(String localDirectoryPath,String remoteDirectory) {
		try {
			String fileName = new File(remoteDirectory).getName();
			localDirectoryPath = localDirectoryPath + fileName + '/';
			new File(localDirectoryPath).mkdirs();
			FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);
			for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
				if (!allFile[currentFile].isDirectory()) {
					downloadFile(allFile[currentFile].getName(),localDirectoryPath, remoteDirectory);
				}
			}
			for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
				if (allFile[currentFile].isDirectory()) {
					String strremoteDirectoryPath = remoteDirectory + '/'+ allFile[currentFile].getName();
					downLoadDirectory(localDirectoryPath,strremoteDirectoryPath);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			logger.info("下载文件夹失败");
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * 获取指定FTP，指定目录下的所有文件信息
	 * 
	 * @param ftpId 指定FTP标识
	 * @param path 指定FTP目录
	 * @param dayPeriod 返回时间范围内的数据
	 * @return 返回文件信息列表
	 * @throws ParseException 
	 
	public List<DataFile> getDataFileListByPeriod(String ftpId, String path, Date startDate,Date endDate) throws ParseException {
		List<DataFile> dataFileList = new ArrayList<DataFile>();
		List<DataFile> tempList = getDataFileList(path);
		if(tempList!=null && !tempList.isEmpty()){
			for(DataFile dataFile : tempList){
				if(dataFile.getLastMofify().after(startDate) && dataFile.getLastMofify().before(endDate)){
					dataFileList.add(dataFile);
				}
			}
		}
		
		return dataFileList;
	}
	*/
	/**
	 * 获取当前目录
	 * @param path
	 * @return
	 */
	public String getCurrentDirectory() {
		try {
			return ftpClient.printWorkingDirectory();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
	
	/**
	 * 取得相对于当前连接目录的某个目录下所有文件列表，倒序排列
	 * 
	 * @param path
	 * @return
	 */
	public List<FTPFile> getFileListWithSort(String path) {
		ftpClient.enterLocalPassiveMode();
		List<FTPFile> ftplist =new ArrayList<>();
		try {
			ftpClient.changeWorkingDirectory(path);
			FTPFile[] list = ftpClient.listFiles();
			for(FTPFile ftpFile:list){
				ftplist.add(ftpFile);
			}
			Collections.reverse(ftplist);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return ftplist;
	}
	
	/**
	 * 取得相对于当前连接目录的某个目录下所有文件列表，倒序排列
	 * 
	 * @param path
	 * @return
	 */
	public List<FTPFile> getFileListWithSort(String path,final String pattern) {
		if(pattern == null || pattern.length() == 0){
			return getFileListWithSort(path);
		}
		ftpClient.enterLocalPassiveMode();
		List<FTPFile> ftplist =new ArrayList<>();
		try {
			ftpClient.changeWorkingDirectory(path);
			FTPFile[] list = ftpClient.listFiles(path,
				file->{
					boolean flag=false;
					if(pattern == null || pattern.length() == 0){
						flag=true;
					}
					Pattern p = Pattern.compile(pattern);
					if(file.isDirectory()){
						flag=true;
					}
					if (file.isFile() && p.matcher(file.getName()).find()) {
						flag=true;
					}
					return flag ;
				}
				) ;

			for(FTPFile ftpFile:list){
				ftplist.add(ftpFile);
			}
			Collections.reverse(ftplist);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return ftplist;
	}
	
	// FtpClient的Set 和 Get 函数
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	
	public static void main(String[] args) throws  Exception {

		ApacheFtpUtil ftp =new ApacheFtpUtil("ladsweb.nascom.nasa.gov",21,"anonymous","anonymous");
		ftp.connectServer();//".*(h25v05|h25v06).*hdf$"
		FTPFile[] fileList=ftp.getDataFileList("allData/6/MOD13Q1/2016/145/",".*hdf$");
		for(int i=0;i<fileList.length;i++){
			FTPFile df = fileList[i];
			logger.error("开始下载文件：{}",df.getName());
			ftp.downloadFTP("allData/6/MOD13Q1/2016/145/"+df.getName(), "D:\\temp\\"+df.getName(), df.getSize());
		}

		ftp.closeServer();
	}

}


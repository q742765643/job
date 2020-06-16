package com.htht.job.executor.hander.databasebackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.htht.job.core.util.DateUtil;
import com.htht.job.core.util.PropertiesUtil;
import com.htht.job.executor.util.MailUtil;
import com.htht.job.executor.util.ZipCompressor;

/**
 * 后台自动运行的数据库备份
 * Created by zzp on 2019/01/24.
 */

@Configuration
@EnableScheduling
public class DatabaseBackupTask {
	private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
	private static String hostIP;
	private static String savePath;
	private static String userName;
	private static String password;
	private static String databaseName;
	private static String toMails;
	static {
		hostIP = PropertiesUtil.getString("databaseBackup.hostIP");
		userName = PropertiesUtil.getString("databaseBackup.userName");
		password = PropertiesUtil.getString("databaseBackup.password");
		databaseName = PropertiesUtil.getString("databaseBackup.databaseName");
		
		savePath = PropertiesUtil.getString("databaseBackup.savePath");
		toMails = PropertiesUtil.getString("databaseBackup.toMails");
	}
	
//	@Scheduled(cron = "0 0/1 * * * ?") // 每分钟执行一次
//	@Scheduled(cron = "0 30 2 * * ?") // 每天执行一次
    public void databaseBackup() {

		logger.debug( "开始执行数据库备份程序");
		
        String fileName = DateUtil.dateToStr(new Date());
        
        File zipFile = new File(savePath+File.separator+fileName+".zip");
        File sqlFile = new File(savePath+File.separator+fileName+".sql");
		try {
			boolean  export = this.exportDatabaseTool(hostIP, userName, password, savePath, sqlFile.getName(), databaseName);
			logger.debug("数据库备份,导出"+export);
			if(export){
				ZipCompressor  zipCom = new ZipCompressor(zipFile.getPath());
				zipCom.compress(sqlFile.getPath());
				zipCom.closeCompress();
				
				logger.debug("数据库备份，压缩成功");
				
				String mailBody = "<html><head><meta http-equiv="
                        + "Content-Type"
                        + " content="
                        + "text/html; charset=gb2312"
                        + "></head><body><h1>数据库备份</h1>"+fileName+"数据库已经备份成功，请下载附件查看"
                        + "</body></html>";

                List<File>files = new ArrayList<File>();
                files.add(zipFile);
                if(toMails.indexOf(",")>-1){
                	String[] mails = toMails.split(",");
                	for(String toAddress : mails){
                		if(StringUtils.isNotEmpty(toAddress)
        						&& toAddress.matches("[a-zA-Z_]{0,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}")){
                			MailUtil.sendMail(toAddress, fileName+"数据库备份", mailBody, files);
                			logger.debug("数据库备份，发送邮件成功" + toAddress);
                		}
                	}
                }else{
                	if(StringUtils.isNotEmpty(toMails)
    						&& toMails.matches("[a-zA-Z_]{0,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}")){
            			MailUtil.sendMail(toMails, fileName+"数据库备份", mailBody, files);
            			logger.debug("数据库备份，发送邮件成功" + toMails);
            		}
                }
                
                sqlFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		
		logger.debug("数据库备份，完毕");
		
	}



	/**
	 * Java代码实现MySQL数据库导出
	 * 
	 * @param hostIP
	 *            MySQL数据库所在服务器地址IP
	 * @param userName
	 *            进入数据库所需要的用户名
	 * @param password
	 *            进入数据库所需要的密码
	 * @param savePath
	 *            数据库导出文件保存路径
	 * @param fileName
	 *            数据库导出文件文件名
	 * @param databaseName
	 *            要导出的数据库名
	 * @return 返回true表示导出成功，否则返回false。
	 */
	public boolean exportDatabaseTool(String hostIP, String userName, String password, String savePath, String fileName, String databaseName) throws InterruptedException {
		File saveFile = new File(savePath);
		if (!saveFile.exists()) {// 如果目录不存在
			saveFile.mkdirs();// 创建文件夹
		}
		if (!savePath.endsWith(File.separator)) {
			savePath = savePath + File.separator;
		}

		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		try {
			printWriter = new PrintWriter(savePath + fileName, "utf8");
			Process process = Runtime.getRuntime().exec(
					" mysqldump -h" + hostIP + " -u" + userName + " -p"
							+ password + " --set-charset=UTF8 " + databaseName);
			InputStreamReader inputStreamReader = new InputStreamReader(
					process.getInputStream(), "utf8");
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				printWriter.println(line);
			}
			printWriter.flush();
			if (process.waitFor() == 0) {// 0 表示线程正常终止。
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (printWriter != null) {
					printWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}

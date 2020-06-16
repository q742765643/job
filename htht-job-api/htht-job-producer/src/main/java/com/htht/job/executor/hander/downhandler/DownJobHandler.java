package com.htht.job.executor.hander.downhandler;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobFileAppender;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.downupload.DownResultDTO;
import com.htht.job.executor.model.ftp.FtpDTO;

import org.htht.util.HttpThread;

import com.htht.job.executor.model.paramtemplate.DownParam;
import com.htht.job.executor.service.downupload.DownResultService;
import com.htht.job.executor.service.ftp.FtpService;

import org.htht.util.ApacheFtpUtil;
import org.htht.util.SftpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;


/**
 * Created by zzj on 2018/1/9.
 */

@JobHandler(value = "downJobHandler")
@Service
public class DownJobHandler extends IJobHandler{

    private DownResultService downResultService;

    @Autowired
    public void setDownResultService(DownResultService downResultService) {
        this.downResultService = downResultService;
    }

    @Autowired
    private FtpService ftpService;

    @Value("${cluster.tempFtpPath}")
    String tempFtpPath;
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
            ResultUtil<String> result=new ResultUtil<String>();
            result=this.down(triggerParam,result);
            if(!result.isSuccess()){
                return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
            }
            return ReturnT.SUCCESS;
    }

    public ResultUtil<String> down(TriggerParam triggerParam, ResultUtil<String> result){

    	DownResultDTO downResult=new DownResultDTO();
    	String outputlogpath=triggerParam.getLogFileName();
        try {
            DownParam downParam = JSON.parseObject(triggerParam.getModelParameters(),DownParam.class);

            String forFilePath = null;
            String toFilePath = null;

            String url = null;
            String fileName = null;
            long fileSize = 0;
            //包括："原路径＋原名称＋","＋新路径＋新名称＋","+文件大小"+","+文件的时间
            String fileParam[]=triggerParam.getExecutorParams().split(",");
            forFilePath=fileParam[0];//源文件的绝对地址
            toFilePath=fileParam[1];//目标文件的绝对地址
            fileSize = Long.parseLong(fileParam[2]);//文件的大小
            long fileDateLong = Long.parseLong(fileParam[3]);//文件的时间
            Date fileDate = new Date(fileDateLong); //文件的时间
            downResult.setDataTime(fileDate);
            downResult.setFileName(new File(toFilePath).getName());
            downResult.setRealFileName(new File(forFilePath).getName());
            downResult.setFileSize(fileSize);
            downResult.setFormat(toFilePath.substring(toFilePath.lastIndexOf(".")));
            downResult.setZt("0");//准备下载
            downResult.setFilePath(new File(toFilePath).getParent());
            downResult.setBz(downParam.getForSouceType()+"2"+downParam.getToSouceType());
            XxlJobFileAppender.makeLogFileNameByPath(outputlogpath);
            // 回调需要用到
            triggerParam.setLogFileName(outputlogpath);
            XxlJobLogger.logByfile(outputlogpath, downResult.getFileName()+"开始执行下载");
            downResultService.saveDownResult(downResult);
            boolean flag=false;
            if ("ftp".equals(downParam.getForSouceType())&&"file".equals(downParam.getToSouceType())) {//ftp下载到硬盘
                FtpDTO ftp = ftpService.getById(downParam.getForFtp());
                ApacheFtpUtil ftpUtil=new ApacheFtpUtil(ftp);
                if(ftpUtil.connectServer()){
                	long lRemoteSize = ftpUtil.downloadFTP(forFilePath, toFilePath, fileSize);
                	ftpUtil.closeServer();
                	if(lRemoteSize>=fileSize){
                		flag=true;
                	}
                }
            }else  if ("file".equals(downParam.getForSouceType())&&"file".equals(downParam.getToSouceType())) {//硬盘下载到硬盘
            	File localFile = new File(toFilePath);
				if(localFile.exists()){
					if(localFile.length() != fileSize){
						localFile.delete();
						FileUtil.copy(forFilePath,toFilePath+".tmp");
						File file2 = new File(toFilePath+".tmp");
						while (file2.exists() && file2.renameTo(file2)) {
							file2.renameTo(localFile);
						}
					}
				}else {
					FileUtil.copy(forFilePath,toFilePath+".tmp");
					File file2 = new File(toFilePath+".tmp");
					while (file2.exists() && file2.renameTo(file2)) {
						file2.renameTo(localFile);
					}
				}
				flag = true;
            }else if ("file".equals(downParam.getForSouceType())&&"ftp".equals(downParam.getToSouceType())){//硬盘上传到ftp
                FtpDTO ftp = ftpService.getById(downParam.getToFtp());
                ApacheFtpUtil ftpUtil=new ApacheFtpUtil(ftp);
                if(ftpUtil.connectServer()){
                	flag = ftpUtil.uploadFile(forFilePath, toFilePath);
                	ftpUtil.closeServer();
                }
            }else if ("ftp".equals(downParam.getForSouceType())&&"ftp".equals(downParam.getToSouceType())){
                FtpDTO forftp = ftpService.getById(downParam.getForFtp());
                FtpDTO toftp = ftpService.getById(downParam.getToFtp());
                ApacheFtpUtil forFtpUtil=new ApacheFtpUtil(forftp);
                File tempFile=new File(toFilePath);
                if(forFtpUtil.connectServer()){
                	forFtpUtil.downloadFTP(forFilePath,tempFile.getAbsolutePath(), fileSize);
                	forFtpUtil.closeServer();
                }

                ApacheFtpUtil toFtpUtil=new ApacheFtpUtil(toftp);
                if(toFtpUtil.connectServer()){
                	flag = toFtpUtil.uploadFile(tempFile.getAbsolutePath(), toFilePath);
                	toFtpUtil.closeServer();
                }
                tempFile.delete();

            }else if("sftp".equals(downParam.getForSouceType())&&"ftp".equals(downParam.getToSouceType())){
                FtpDTO forftp = ftpService.getById(downParam.getForFtp());
                FtpDTO toftp = ftpService.getById(downParam.getToFtp());
                
                //把远程的文件下载回来
                SftpUtils forSftpUtil=new SftpUtils(forftp);
                File tempFile=new File(toFilePath);
                //连接
                forSftpUtil.connect();
            	forSftpUtil.downloadFile(forFilePath,tempFile.getAbsolutePath());
            	//关闭连接
            	forSftpUtil.disconnect();
            	
            	//ftp上传
                ApacheFtpUtil toFtpUtil=new ApacheFtpUtil(toftp);
                if(toFtpUtil.connectServer()){
                	flag = toFtpUtil.uploadFile(tempFile.getAbsolutePath(), toFilePath);
                	toFtpUtil.closeServer();
                }
                tempFile.delete();
            }else if("sftp".equals(downParam.getForSouceType())&&"sftp".equals(downParam.getToSouceType())){
                FtpDTO forftp = ftpService.getById(downParam.getForFtp());
                FtpDTO tosftp = ftpService.getById(downParam.getToFtp());
                
                //sftp把远程的文件下载回来
                SftpUtils forSftpUtil=new SftpUtils(forftp);
                File tempFile=new File(toFilePath);
                //连接
                forSftpUtil.connect();
            	forSftpUtil.downloadFile(forFilePath,tempFile.getAbsolutePath());
            	//关闭连接
            	forSftpUtil.disconnect();
            	
            	//sftp上传
            	SftpUtils toSftpUtil=new SftpUtils(tosftp);
            	toSftpUtil.connect();
            	flag = toSftpUtil.uploadFile(toFilePath,tempFile.getAbsolutePath());
            	toSftpUtil.disconnect();
                tempFile.delete();
            }else if("sftp".equals(downParam.getForSouceType())&&"file".equals(downParam.getToSouceType())){
                FtpDTO forftp = ftpService.getById(downParam.getForFtp());
                
                //sftp把远程的文件下载回来
                SftpUtils forSftpUtil=new SftpUtils(forftp);
                //连接
                forSftpUtil.connect();
            	forSftpUtil.downloadFile(forFilePath,toFilePath);
            	//关闭连接
            	forSftpUtil.disconnect();
            }else if("ftp".equals(downParam.getForSouceType())&&"sftp".equals(downParam.getToSouceType())){
                FtpDTO forftp = ftpService.getById(downParam.getForFtp());
                FtpDTO tosftp = ftpService.getById(downParam.getToFtp());
                 ApacheFtpUtil forFtpUtil=new ApacheFtpUtil(forftp);
                 File tempFile=new File(toFilePath);
                 if(forFtpUtil.connectServer()){
                 	forFtpUtil.downloadFTP(forFilePath,tempFile.getAbsolutePath(), fileSize);
                 	forFtpUtil.closeServer();
                 }

               //sftp上传
             	SftpUtils toSftpUtil=new SftpUtils(tosftp);
             	toSftpUtil.connect();
             	flag = toSftpUtil.uploadFile(toFilePath,tempFile.getAbsolutePath());
             	toSftpUtil.disconnect();
             	tempFile.delete();
            }else if("file".equals(downParam.getForSouceType())&&"sftp".equals(downParam.getToSouceType())){
                FtpDTO tosftp = ftpService.getById(downParam.getToFtp());
            	//sftp上传
            	SftpUtils toSftpUtil=new SftpUtils(tosftp);
            	toSftpUtil.connect();
            	flag = toSftpUtil.uploadFile(toFilePath,forFilePath);
            	toSftpUtil.disconnect();
            }else if("modis".equals(downParam.getForSouceType())&&"file".equals(downParam.getToSouceType())){
                //modis数据接口下载到硬盘
                HttpThread.httpDownload(forFilePath, toFilePath,fileSize);
                flag = true;
            }else if("modis".equals(downParam.getForSouceType())&&"ftp".equals(downParam.getToSouceType())){
                //modis数据接口下载到ftp
                HttpThread.httpDownload(forFilePath, toFilePath, fileSize);

                //ftp上传
                FtpDTO ftp = ftpService.getById(downParam.getToFtp());
                ApacheFtpUtil ftpUtil=new ApacheFtpUtil(ftp);
                if(new File(toFilePath).exists() && ftpUtil.connectServer()){
                    flag = ftpUtil.uploadFile(toFilePath, toFilePath);
                    ftpUtil.closeServer();
                    new File(toFilePath).delete();
                }
            }else if("modis".equals(downParam.getForSouceType())&&"sftp".equals(downParam.getToSouceType())){
                //modis数据接口下载到ftp
                HttpThread.httpDownload(url, toFilePath, fileSize);

                FtpDTO tosftp = ftpService.getById(downParam.getToFtp());
                //sftp上传
                SftpUtils toSftpUtil=new SftpUtils(tosftp);
                if(new File(toFilePath).exists()){
                    toSftpUtil.connect();
                    flag = toSftpUtil.uploadFile(toFilePath, toFilePath);
                    toSftpUtil.disconnect();
                    new File(toFilePath).delete();
                }
            }
            if (flag) {
            	downResult.setZt("1");
                downResultService.saveDownResult(downResult);
                XxlJobLogger.logByfile(outputlogpath, downResult.getFileName()+"下载成功");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.setErrorMessage("下载出错");
            downResult.setZt("2");
            downResultService.saveDownResult(downResult);
            XxlJobLogger.logByfile(outputlogpath, downResult.getFileName()+"下载出错");
            throw new RuntimeException();
        }
        result.setMessage("数据汇集成功");
        return result;
    }

}

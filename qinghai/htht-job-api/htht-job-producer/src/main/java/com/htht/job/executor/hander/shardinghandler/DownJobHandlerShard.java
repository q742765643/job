package com.htht.job.executor.hander.shardinghandler;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.webservicehandler.service.GetDataByWebServiceService;

import org.htht.util.HttpThread;

import com.htht.job.executor.model.downupload.DownResult;
import com.htht.job.executor.model.ftp.Ftp;
import com.htht.job.executor.model.paramtemplate.DownParam;
import com.htht.job.executor.service.downupload.DownResultService;
import com.htht.job.executor.service.ftp.FtpService;

import org.apache.commons.net.ftp.FTPFile;
import org.htht.util.ApacheFtpUtil;
import org.htht.util.DataTimeHelper;
import org.htht.util.DateUtil;
import org.htht.util.FileUtil;
import org.htht.util.SftpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

@Service("downJobHandlerShard")
public class DownJobHandlerShard implements SharingHandler {
	
	private static Logger logger = LoggerFactory.getLogger(DownJobHandlerShard.class.getName());

	private GetDataByWebServiceService getDataByServiceClientService;

	@Autowired
	public void setGetDataByServiceClientService(GetDataByWebServiceService getDataByServiceClientService) {
		this.getDataByServiceClientService = getDataByServiceClientService;
	}

	@Autowired
	private DownResultService downResultService;
	@Autowired
	private FtpService ftpService;
	private Date beginTime;
	private Date endTime;

	@SuppressWarnings("rawtypes")
	@Override
	public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
		System.out.println("===========进入downJobHandlerShard===========");
		ResultUtil<List<String>> result = new ResultUtil<List<String>>();
		DownParam downParam = JSON.parseObject(params, DownParam.class);
		/*** ======1.获取开始和结束日期========== ***/
		String toPath = downParam.getToPath();
		System.out.println("===========进入downJobHandlerShard===========downParam=="+toPath);
 
		this.getBeginAndEndTime(downParam, result);
		if (!result.isSuccess()) {
			return result;
		}
		/*** ======2.获取该时间段内数据库中的数据========== ***/

		List<String> existFileList = downResultService.findFilesByTime(beginTime, endTime);
		System.out.println("===========进入downJobHandlerShard== "+toPath+" ==existFileList==" + (null==existFileList?"0":existFileList.size()));
		/*** ======3.获取文件符合条件的文件list========== ***/

		List<String> files = this.getFileList(downParam, beginTime, endTime, existFileList, result);
		System.out.println("===========进入downJobHandlerShard== "+toPath+" ==files==" + (null==files?"0":files.size()));
		
		if (!result.isSuccess()) {
			return result;
		}

		result.setResult(files);
		return result;
	}

	private void getBeginAndEndTime(DownParam downParam, ResultUtil<List<String>> result) {
		try {
			Calendar calendar = Calendar.getInstance();
			if ("now".equals(downParam.getDownloadType())) {
				int days = Integer.parseInt(downParam.getDownloadDays());
				endTime = calendar.getTime();
				calendar.add(Calendar.DAY_OF_YEAR, -days);
				beginTime = calendar.getTime();
			} else if ("history".equals(downParam.getDownloadType())) {
				// 2018-02-09 00:00:00;2018-02-09 23:59:59
				String[] temp = downParam.getDownloadDate().split(" - ");
				beginTime = org.htht.util.DateUtil.getDate(temp[0], "yyyy-MM-dd HH:mm:ss");
				endTime = org.htht.util.DateUtil.getDate(temp[1], "yyyy-MM-dd HH:mm:ss");
			}
		} catch (Exception e) {
			result.setErrorMessage("数据开始时间和结束时间获取失败");
			System.out.println("DownJobHandlerShard getBeginAndEndTime error:"+e.getMessage());
			logger.error("DownJobHandlerShard getBeginAndEndTime error:"+e.getMessage());
		}
	}

	public List<String> getFileList(DownParam downParam, Date beginTime, Date endTime, List<String> existFileList,
			ResultUtil<List<String>> result) {

		//包括："原路径＋原名称＋","＋新路径＋新名称＋","+文件大小"+","+文件的时间
		List<String> fileList = new ArrayList<String>();

		// 把遍历的目录保存到pathList中 确保目录只遍历一次
		List<String> pathList = this.getPathList(downParam.getForPath(), beginTime, endTime);
		for (String forPath : pathList) {
			try {
				if ("ftp".equals(downParam.getForSouceType())) {
					Ftp ftp = ftpService.getById(downParam.getForFtp());
					// FtpUtil ftpUtil = new FtpUtil();
					ApacheFtpUtil ftpUtil = new ApacheFtpUtil(ftp);
					if (ftpUtil.connectServer()) {
						FTPFile[] list = ftpUtil.getDataFileList(forPath, downParam.getDownFileNamePattern());
						ftpUtil.closeServer();
						for (FTPFile file : list) {
							// 文件名中的时间
							Date fileDate = new Date(DataTimeHelper.getDataTimeFromFileNameByPattern(file.getName(),
									downParam.getDataTimePattern()));
							// 判断该时间是否在开始和结束时间之间
							if (fileDate.after(endTime) || fileDate.before(beginTime)) {
								continue; 
							}
							// 重命名
							String downloadFileName = file.getName();
							if (StringUtils.isEmpty(downParam.getDownloadFileName())
									|| !"-1".equals(downParam.getDownloadFileName())) {
								downloadFileName = DateUtil.getPathByDate(downParam.getDownloadFileName(), fileDate);
							}
							if (!StringUtils.isEmpty(downParam.getDownloadFileName())
									&& downParam.getDownloadFileName().contains("(")) {
								String renameFilePattern = downParam.getDownloadFileName();
								Integer begin = Integer.parseInt(renameFilePattern
										.substring(renameFilePattern.indexOf("(") + 1, renameFilePattern.indexOf(")")));
								downloadFileName = downloadFileName.substring(begin);
							}

							// 目标路径 用文件的时间替换目标文件的通配符
							String toPath = DateUtil.getPathByDate(downParam.getToPath(), fileDate);
							//
							if (existFileList.contains(toPath + "#" + downloadFileName)) {
								continue;
							}
							toPath.replaceAll("\\\\", "/");
							if (!toPath.endsWith("/")) {
								toPath += "/";
							}
							// 源路径 用文件的时间替换目标文件的通配符
							forPath = forPath.replaceAll("\\\\", "/");
							if (!forPath.endsWith("/")) {
								forPath += "/";
							}
							//包括："原路径＋原名称＋","＋新路径＋新名称＋","+文件大小"+","+文件的时间
							fileList.add(forPath + file.getName() + "," + toPath + downloadFileName + ","
									+ file.getSize() + "," + fileDate.getTime());
						}
					}

				} else if ("file".equals(downParam.getForSouceType())) {
					// 得到符合文件名正则的文件
					List<File> files = FileUtil.iteratorFileAndDirectory(new File(forPath), downParam.getDownFileNamePattern());
					for (File file : files) {
						// 文件名中的时间
						Date fileDate = new Date(DataTimeHelper.getDataTimeFromFileNameByPattern(file.getName(),
								downParam.getDataTimePattern()));

						// 判断该时间是否在开始和结束时间之间
						if (fileDate.after(endTime) || fileDate.before(beginTime)) {
							continue;
						}
						// 重命名
						String downloadFileName = file.getName();
						if ((StringUtils.isEmpty(downParam.getDownloadFileName())
								|| !"-1".equals(downParam.getDownloadFileName()))
								&& !downParam.getDownloadFileName().contains("(")) {
							downloadFileName = DateUtil.getPathByDate(downParam.getDownloadFileName(), fileDate);
						}
						if (!StringUtils.isEmpty(downParam.getDownloadFileName())
								&& downParam.getDownloadFileName().contains("(")) {
							String renameFilePattern = downParam.getDownloadFileName();
							Integer begin = Integer.parseInt(renameFilePattern
									.substring(renameFilePattern.indexOf("(") + 1, renameFilePattern.indexOf(")")));
							downloadFileName = downloadFileName.substring(begin);
						}

						if (!downloadFileName.contains(".")) {
							downloadFileName += file.getName().substring(file.getName().lastIndexOf("."));
						}
						// 目标路径
						String toPath = DateUtil.getPathByDate(downParam.getToPath(), fileDate);
						// 判断是否已经下载了
						if (existFileList.contains(toPath + "#" + downloadFileName)) {
							continue;
						}
						// 目标路径 用文件的时间替换目标文件的通配符
						toPath = toPath.replaceAll("\\\\", "/");
						if (!toPath.endsWith("/")) {
							toPath += "/";
						}
						//包括："原路径＋原名称＋","＋新路径＋新名称＋","+文件大小"+","+文件的时间
						fileList.add(file.getAbsolutePath() + "," + toPath + downloadFileName + "," + file.length()
								+ "," + fileDate.getTime());
					}
				} else if ("sftp".equals(downParam.getForSouceType())) {
					Ftp sftp = ftpService.getById(downParam.getForFtp());
					SftpUtils sFtpUtils = new SftpUtils(sftp);
					List<String> fileNameAndSizes = sFtpUtils.getDataFileList(forPath,
							downParam.getDownFileNamePattern());
					for (String fileNameAndSize : fileNameAndSizes) {
						String[] fns = fileNameAndSize.split("#");
						// 文件名中的时间
						Date fileDate = new Date(DataTimeHelper.getDataTimeFromFileNameByPattern(fns[0],
								downParam.getDataTimePattern()));
						// 判断该时间是否在开始和结束时间之间
						if (fileDate.after(endTime) || fileDate.before(beginTime)) {
							continue;
						}
						// 重命名
						String downloadFileName = fns[0];
						if (StringUtils.isEmpty(downParam.getDownloadFileName())
								|| !"-1".equals(downParam.getDownloadFileName())) {
							downloadFileName = DateUtil.getPathByDate(downParam.getDownloadFileName(), fileDate);
						}
						if (!StringUtils.isEmpty(downParam.getDownloadFileName())
								&& downParam.getDownloadFileName().contains("(")) {
							String renameFilePattern = downParam.getDownloadFileName();
							Integer begin = Integer.parseInt(renameFilePattern
									.substring(renameFilePattern.indexOf("(") + 1, renameFilePattern.indexOf(")")));
							downloadFileName = downloadFileName.substring(begin);
						}

						if (!downloadFileName.contains(".")) {
							downloadFileName += fns[0].substring(fns[0].lastIndexOf("."));
						}
						//
						if (existFileList.contains(forPath + "#" + downloadFileName)) {
							continue;
						}
						// 目标路径 用文件的时间替换目标文件的通配符
						String toPath = DateUtil.getPathByDate(downParam.getToPath(), fileDate).replaceAll("\\\\", "/");
						if (!toPath.endsWith("/")) {
							toPath += "/";
						}
						// 源路径 用文件的时间替换目标文件的通配符
						forPath = forPath.replaceAll("\\\\", "/");
						if (!forPath.endsWith("/")) {
							forPath += "/";
						}
						//包括："原路径＋原名称＋","＋新路径＋新名称＋","+文件大小"+","+文件的时间
						fileList.add(forPath + fns[0] + "," + toPath + downloadFileName + "," + fns[1] + ","
								+ fileDate.getTime());
					}

				} else if ("modis".equals(downParam.getForSouceType())) {
					// 先拿urls
					List<String> urls = getDataByServiceClientService.getUrlsByServiceClient(beginTime, endTime,
							downParam.getModisDataType());

					for (int i = 0; i < urls.size(); i++) {
						// 获取重命名后的名称判断数据库是否存在该记录
						// 文件名中的时间 文件格式类似xxxx/xxxx/xxxx.xxx
						String[] param = urls.get(i).split("/");
						String fileDateStr = param[param.length - 1];
						
						
						// 重命名
						String downloadFileName = param[param.length - 1];
						if (!downloadFileName.matches(downParam.getDownFileNamePattern())) {
							logger.info("文件 :" + downloadFileName + ",不符合要求，舍弃");
							continue;
						}
						Date fileDate = DateUtil.strToDate(param[param.length - 3] + param[param.length - 2], "yyyyDD");
						if (StringUtils.isEmpty(downParam.getDownloadFileName())
								|| !"-1".equals(downParam.getDownloadFileName())) {
							downloadFileName = fileDateStr;
						}else{
							downloadFileName = DateUtil.getPathByDate(downParam.getDownloadFileName(), fileDate);
						}
						if (!StringUtils.isEmpty(downParam.getDownloadFileName())
								&& downParam.getDownloadFileName().contains("(")) {
							String renameFilePattern = downParam.getDownloadFileName();
							Integer begin = Integer.parseInt(renameFilePattern
									.substring(renameFilePattern.indexOf("(") + 1, renameFilePattern.indexOf(")")));
							downloadFileName = downloadFileName.substring(begin);
						}
						//
						if (existFileList.contains(forPath + "#" + downloadFileName)) {
							continue;
						}
						// 目标路径 用文件的时间替换目标文件的通配符
						String toPath = DateUtil.getPathByDate(downParam.getToPath(), fileDate).replaceAll("\\\\", "/") ;
						if (!toPath.endsWith("/")) {
							toPath += "/";
						}
						long fileSize = HttpThread.getRemoteSize(new URL(urls.get(i)));
						// 下载指定目录下下载过此文件就不进行下载
						List<DownResult> resList = downResultService.findDownloadFiles(
								downloadFileName
										+ param[param.length - 1].substring(param[param.length - 1].lastIndexOf(".")),
								toPath, fileSize, "0");
						if (resList.size() > 0) {
							continue;
						}
						//包括："原路径＋原名称＋","＋新路径＋新名称＋","+文件大小"+","+文件的时间
						fileList.add(urls.get(i) + "," + toPath + downloadFileName	+ "," + fileSize + ","  + fileDate.getTime());

					}
				}
			} catch (Exception e) {
				System.out.println("DownJobHandlerShard getFileList error:"+e.getMessage());
				logger.error("DownJobHandlerShard getFileList error:"+e.getMessage());
				e.printStackTrace();
			}

		}
		return fileList;
	}

	private List<String> getPathList(String formPath, Date beginTime, Date endTime) {
		List<String> pathList = new ArrayList<String>();
		if (formPath.contains("{")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(beginTime);
			// calendar.add(Calendar.HOUR, -8);//转换成标准时间
			while (!calendar.getTime().after(endTime)) {
				String forPath = DateUtil.getPathByDate(formPath, calendar.getTime());
				calendar.add(Calendar.DAY_OF_YEAR, 1);
				if (pathList.contains(forPath)) {
					continue;
				}
				pathList.add(forPath);
			}
		} else {
			pathList.add(formPath);
		}
		return pathList;
	}
	

}

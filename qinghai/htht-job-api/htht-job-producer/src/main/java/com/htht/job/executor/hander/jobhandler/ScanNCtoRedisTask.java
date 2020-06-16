package com.htht.job.executor.hander.jobhandler;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.Consts;
import org.htht.util.DateUtil;
import org.htht.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.htht.job.executor.redis.RedisService;


@Configuration
@EnableScheduling
public class ScanNCtoRedisTask {
	private static Logger logger = LoggerFactory.getLogger(ScanNCtoRedisTask.class);

	 @Autowired
	 private RedisService redisService;
	 
	@Scheduled(cron = "0 0/5 * * * ?") // 每分钟执行一次
    public void databaseBackup() {

		logger.debug( "开始执行扫描nc数据程序");
		Calendar c = Calendar.getInstance();
		long ts = c.getTimeInMillis();
		c.add(Calendar.DAY_OF_YEAR, -4);
		while(c.getTimeInMillis() < ts){
			c.add(Calendar.DAY_OF_YEAR, 1);
			String ym = DateUtil.formatDateTime(c.getTime(), "yyyyMM");
			String ymd = DateUtil.formatDateTime(c.getTime(), "yyyyMMdd");
			String outFolder = "Y:/data/reprocess/nc/" + ym + File.separator + ymd;
			logger.debug("扫描路径为" + outFolder);
			List<File> allFilesList = FileUtil.iteratorFileAndDirectory(new File(outFolder), ".*.tif");
			logger.debug("扫描路径为" + outFolder + " 共有文件为："  + allFilesList.size());
			for(File f :allFilesList){
				addRedisByNcName(f.getAbsolutePath());
			}
		}
		
	}
	/**
	 * 功能：把文件归类到对应的redis中格式：ADFP_nc201907240101：d:/xxx.tif,d:/sss.tif
	 * @param filePath
	 * @param issue 
	 * @return
	 */
	private boolean addRedisByNcName(String filePath){
		
		boolean flag = false;
		//确保文件绝对路径和文件名不为空
		if(StringUtils.isNotEmpty(filePath) ){
			//获取文件名
			String fileName = new File(filePath).getName();
			
			//获取issue
			int len = fileName.length();
			if(len < 14){
				return false;
			}
			String issue = fileName.substring(len-14,len-4);
			if(!StringUtils.isNumeric(issue)){
				return false;
			}
			if(!isAccordFile(fileName, issue)){
				return false;
			}
			//遍历ADFPKEY
			for(String key : Consts.NcProduct.ADFPKEY){
				if(fileName.indexOf(key) > -1){
					String value = "";
					//若redis中存有部分数据 则继续添加
					if(redisService.exists(Consts.NcProduct.ADFP + issue)){
						value = (String) redisService.get(Consts.NcProduct.ADFP + issue);
						if(value.indexOf(fileName) > -1){
							flag = true;
							break;
						}
						redisService.set(Consts.NcProduct.ADFP + issue, value + "," + filePath, 3600*24*4L);
						flag = true;
						break;
					}else{
						redisService.set(Consts.NcProduct.ADFP + issue, filePath, 3600*24*4L);
						flag = true;
						break;
					}
				}
			}
			//遍历SHKEY
			for(String key : Consts.NcProduct.SHKEY){
				if(fileName.indexOf(key) > -1){
					String value = "";
					//若redis中存有部分数据 则继续添加
					if(redisService.exists(Consts.NcProduct.SH + issue)){
						value = (String) redisService.get(Consts.NcProduct.SH + issue);
						if(value.indexOf(fileName) > -1){
							flag = true;
							break;
						}
						redisService.set(Consts.NcProduct.SH + issue, value + "," + filePath, 3600*24*4L);
						flag = true;
						break;
					}else{
						redisService.set(Consts.NcProduct.SH + issue, filePath, 3600*24*4L);
						flag = true;
						break;
					}
				}
			}
			//遍历SRHKEY
			for(String key : Consts.NcProduct.SRHKEY){
				if(fileName.indexOf(key) > -1){
					String value = "";
					//若redis中存有部分数据 则继续添加
					if(redisService.exists(Consts.NcProduct.SRH + issue)){
						value = (String) redisService.get(Consts.NcProduct.SRH + issue);
						if(value.indexOf(fileName) > -1){
							flag = true;
							break;
						}
						redisService.set(Consts.NcProduct.SRH + issue, value + "," + filePath, 3600*24*4L);
						flag = true;
						break;
					}else{
						redisService.set(Consts.NcProduct.SRH + issue, filePath, 3600*24*4L);
						flag = true;
						break;
					}
				}
			}
			//遍历STKEY
			for(String key : Consts.NcProduct.STKEY){
				if(fileName.indexOf(key) > -1){
					String value = "";
					//若redis中存有部分数据 则继续添加
					if(redisService.exists(Consts.NcProduct.ST + issue)){
						value = (String) redisService.get(Consts.NcProduct.ST + issue);
						if(value.indexOf(fileName) > -1){
							flag = true;
							break;
						}
						redisService.set(Consts.NcProduct.ST + issue, value + "," + filePath, 3600*24*4L);
						flag = true;
						break;
					}else{
						redisService.set(Consts.NcProduct.ST + issue, filePath, 3600*24*4L);
						flag = true;
						break;
					}
				}
			}
		}
		
		return flag;
	}
	private boolean isAccordFile(String fileName, String time){
		/*
		（1）'0P0625' 字段不存在文件名，过滤文件名
		（2）文件后缀部位 .tif 过滤文件
		（3）'NRT' 存在于文件名中，文件为 NRT 类型，否则为 RT 类型（这个千万不要反了）
		（4）文件的期次是倒数 -14 到 倒数 -4 个字段  issue = base_name[-14:-4]  # 精确到小时
		（5）期次中存在 _ 就过滤文件
		（6）‘HOR’存在于文件名中为 HOR类型，否则为 DAY 类型
		*/
		if(fileName.indexOf("0P0625") < 0 || fileName.indexOf(".tif") < 0 || fileName.indexOf(time) < 0){
			return false;
		}
		
		return true;
	}
}

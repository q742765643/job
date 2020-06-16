package com.htht.job.executor.plugin.syncAccess.hander;

import java.io.File;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.log.XxlJobLogger;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.ftp.Ftp;
import com.htht.job.executor.plugin.syncAccess.AccessJDBC;
import com.htht.job.executor.plugin.syncAccess.model.AgroAnimalInfo;
import com.htht.job.executor.plugin.syncAccess.service.AgroAnimalInfoService;
import com.htht.job.executor.service.ftp.FtpService;

import org.apache.commons.lang3.StringUtils;
import org.htht.util.ApacheFtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 1.查询mysql表中最大的时间 time1 
 * 2.以time1为参数，查询access数据库中的数据 
 * 3.如果有则入到mysql；没有则停止。
 * 
 * 实施过程中，如果某些数据入库出错，只需要把该期次以后的数据删除即可
 */
@JobHandler(value = "syncAccessHandler")
@Service
public class SyncAccessHandler extends IJobHandler {

	@Autowired
	AgroAnimalInfoService agroAnimalInfoService;
	
	@Autowired
    private FtpService ftpService;
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<String>();

		String logPath = triggerParam.getLogFileName();
		XxlJobLogger.logByfile(logPath, "开始执行ACCESS数据库同步程序！");
		/** =======2.执行业务============================= **/
		// 固定参数（必须参数）
		LinkedHashMap fixmap = triggerParam.getFixedParameter();
		//ftp上的access文件的路径
		String ftpPath = (String) fixmap.get("ftpPath");
		
		//存在本地的access文件路径
		String accessPath = (String) fixmap.get("accessPath");
		File accessFile = new File(accessPath);
		accessFile.delete();
		String fileName = "sthjdb.mdb";
		
		//把文件名定死了。
		if(ftpPath.indexOf("mdb") < 0){
			ftpPath = ftpPath + File.separator + fileName; 
		}

		if(accessPath.indexOf("mdb") < 0){
			accessPath = accessPath + File.separator + fileName; 
		}

		Ftp ftp = new Ftp();
		//支持通过FTPid来下载数据
		if(fixmap.containsKey("fptId")){
			String fptId = (String) fixmap.get("fptId");
			ftp = ftpService.getById(fptId);
		}else{
			ftp.setIpAddr(getValue(fixmap, "ipAddr"));
			if(StringUtils.isNumeric(getValue(fixmap, "port"))){
				ftp.setPort(Integer.parseInt(getValue(fixmap, "port")));
			}
			ftp.setUserName(getValue(fixmap, "userName"));
			ftp.setPwd(getValue(fixmap, "pwd"));
		}
        ApacheFtpUtil ftpUtil=new ApacheFtpUtil(ftp);
        if(ftpUtil.connectServer()){
        	ftpUtil.downloadFTP(ftpPath, accessPath, 100000000);
        	XxlJobLogger.logByfile(logPath, "下载access文件！" + accessPath);
        	ftpUtil.closeServer();
        }
		
		Date maxFbday = null;
		//判断access文件是否存在
		if(accessFile.exists()){
			//查县mysql库中最大日期
			maxFbday = agroAnimalInfoService.findMaxFbday();
        	XxlJobLogger.logByfile(logPath, "数据库最大的日期为" + maxFbday);

			List<Map<String,String>> list = AccessJDBC.readFileACCESS(accessPath, maxFbday.toString());
        	XxlJobLogger.logByfile(logPath, "需要同步的数据条数为：" + list.size());
			if(list.size() > 0){
				for(Map<String,String> rs :list){
					AgroAnimalInfo aai = new AgroAnimalInfo();
					aai.setCreateTime(new Date());
					Date fbday = reviseFbday(rs.get("fbday"));
					if(fbday == null){
						continue;
					}
					aai.setFbday(fbday);
					aai.setNsta(rs.get("nsta"));
					aai.setCsta(rs.get("csta"));
					aai.setH11(rs.get("h11"));
					aai.setH12(rs.get("h12"));
					aai.setH13(rs.get("h13"));
					aai.setH21(rs.get("h21"));
					aai.setH22(rs.get("h22"));
					aai.setH23(rs.get("h23"));
					aai.setW101(rs.get("w101"));
					aai.setW102(rs.get("w102"));
					aai.setW103(rs.get("w103"));
					
					aai.setW201(rs.get("w201"));
					aai.setW202(rs.get("w202"));
					aai.setW203(rs.get("w203"));

					aai.setW301(rs.get("w301"));
					aai.setW302(rs.get("w302"));
					aai.setW303(rs.get("w303"));
					AgroAnimalInfo aaiExist = agroAnimalInfoService.findByFbdayAndNsta(aai.getFbday(), aai.getNsta());
					if(aaiExist !=null){
						agroAnimalInfoService.delete(aaiExist.getId());
					}
					agroAnimalInfoService.save(aai);
				}
	        	XxlJobLogger.logByfile(logPath, "同步的数据保存成功！");
			}else{
				result.setErrorMessage("access文件中没有新的数据，不需要同步。最终日期为：" + maxFbday);
	        	XxlJobLogger.logByfile(logPath, "access文件中没有新的数据，不需要同步。最终日期为：" + maxFbday);
	        	accessFile.delete();
				return new ReturnT<String>(ReturnT.SUCCESS_CODE, result.toString());
			}
		}else{
			result.setErrorMessage("access文件不存在，不需要同步。" + accessFile);
        	XxlJobLogger.logByfile(logPath, "access文件不存在，不需要同步。" + accessFile);

			return new ReturnT<String>(ReturnT.SUCCESS_CODE, result.toString());
		}
		
		//执行成功后把该文件删除！
		accessFile.delete();
		result.setMessage("同步成功！最终日期为:" + maxFbday);
    	XxlJobLogger.logByfile(logPath, "同步成功！最终日期为:" + maxFbday);
		return ReturnT.SUCCESS;
	}
	
	private String getValue(LinkedHashMap<Object, Object> map, String key){
		if(map.containsKey(key)){
			return (String) map.get(key);
		}
		return "";
	}
	/**
	 * 修正access数据库中的fbday的错误
	 * @param fbday
	 * @return
	 * @throws ParseException 
	 */
	private Date reviseFbday(String fbday) throws ParseException{
		if(StringUtils.isEmpty(fbday)){
			return null;
		}
		if(fbday.length() != 10){
			return null;
		}
		if(!StringUtils.isNumeric(fbday.replaceAll("-", ""))){
			return null;
		}
		
		int yyyy = Integer.parseInt(fbday.substring(0,4));
		String mmStr = fbday.substring(5,7);
		int mm = Integer.parseInt(mmStr);
		String ddStr = fbday.substring(8,10);
		int dd = Integer.parseInt(ddStr);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//如果大于当前日期，则舍弃
		if(sdf.parse(fbday).after(new Date())){
			return null;
		}
		
		//日期超过本月的最大值
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, yyyy);
		c.set(Calendar.MONTH, mm-1);
//		int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//		if(dd > maxDay){
//			c.add(Calendar.MONTH, 1);
//			c.set(Calendar.DAY_OF_MONTH, 1);
//			return c.getTime();
//		}
		
		//日的数值不是01，11， 21
		if("01,11,21".indexOf(ddStr) < 0){
			if(dd > 21){
				c.add(Calendar.MONTH, 1);
				c.set(Calendar.DAY_OF_MONTH, 1);
			}else if(dd > 11){
				c.set(Calendar.DAY_OF_MONTH, 21);
			}else if(dd > 1){
				c.set(Calendar.DAY_OF_MONTH, 11);
			}
			//如果大于当前日期，则舍弃
			if(c.getTime().after(new Date())){
				return null;
			}
			return c.getTime();
		}
		c.set(Calendar.DAY_OF_MONTH, dd);
		
		return c.getTime();
	}
}

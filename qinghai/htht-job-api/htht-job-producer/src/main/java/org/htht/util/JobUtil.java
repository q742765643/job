package org.htht.util;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.htht.util.DateUtil;

public class JobUtil {
	public static String getSubCurrPath(String schedulerName) {
		String subPath = "";
		try {
			Calendar curr = Calendar.getInstance();
//			curr.add(-1, Calendar.DAY_OF_MONTH);
			String currDate = DateUtil.formatDateTime(curr.getTime(),
					"yyyyMMdd");
			String[] ssr = schedulerName.split("_");
			subPath = File.separator + ssr[0] + File.separator + ssr[1] + File.separator + currDate;
		} catch (Exception e) {
			return "ErrorDir";
		}
		return subPath;
	}
	
	public static String getSubPath(String schedulerName, Date dateTime) {
		String subPath = "";
		try {
			String currDate = DateUtil.formatDateTime(dateTime,
					"yyyyMMdd");
			String[] ssr = schedulerName.split("_");
			subPath = File.separator + ssr[0] + File.separator + ssr[1] + File.separator + currDate + File.separator;
		} catch (Exception e) {
			return "ErrorDir";
		}
		return subPath;
	}
	
	/**
	 * 
	 * 根据条件创建文件名
	 * 
	 * @param schedulerName 任务名称 如:FY3B_VIRR_1000M
	 * @param productType 产品类型  如:LST_DBLV_FTC_0MAX_10
	 * @param dateTime 当前时间
	 * @param dataType 数据类型 如:dat、jpg
	 * @return
	 */
	public static String createDataFileName(String productType, String schedulerName, Date dateTime, String dataType){
		StringBuffer dateFileName = new StringBuffer();
		try{
			
			if(productType != null && !productType.isEmpty()){
				dateFileName.append(productType);
		    }
						
			if(schedulerName != null && !schedulerName.isEmpty()){
				if(dateFileName.length() > 0){
					dateFileName.append("_");
				}
				dateFileName.append(schedulerName);
			}
		 
			if(dateTime != null){
				if(dateFileName.length() > 0){
					dateFileName.append("_");
				}
			   String dateTimeStr = DateUtil.formatDateTime(dateTime, "yyyyMMdd000000");
			   dateFileName.append(dateTimeStr);
			}
			
			dateFileName.append(".").append(dataType.toLowerCase());
			
			return dateFileName.toString();
		
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 根据文件名获取卫星、传感器、日期等信息。
	 * eg.FY3A_VIRRX_201605190235_1000M.HDF
	 * eg.MOD11A1_h25v05_201605220000_1000M.hdf
	 * @param inputFilePath
	 * @return
	 * @throws ParseException
	 */
	public static String getSubDir(String inputFilePath) throws ParseException {
		String dateStr = DataTimeHelper.getFormatDate(inputFilePath, "yyyyMMddHHmm", "yyyy");//DataTimeHelper.getDataDate(inputFilePath);
		File f = new File(inputFilePath);
		String fileName = f.getName();
		if(fileName.startsWith("FY")){
			String ssInfo[] = fileName.split("_");
			String satallite = ssInfo[0];
			String sensor = ssInfo[1];
			String retDir = "/" + satallite + "/" + sensor + "/" + dateStr + "/";
			return retDir;
		}
		else if (fileName.startsWith("MOD")) {
			String ssInfo[] = fileName.split("_");
			return "/"+ssInfo[1]+"/MODIS"+"/"+dateStr+"/";
		}
		return "";
	}


	/**
	 * @param type
	 * @param metaDateTypes
	 * @return
	 */
	public static boolean checkContainsProcess(String type,
			String[] metaDateTypes) {
		for (int i = 0; i < metaDateTypes.length; i++) {
			if (type.equals(metaDateTypes[i]))
				return true;
		}
		return false;
	}
	
	public static Float toFormatFloat(String res) {
		Float r = Float.valueOf(res == null || "".equals(res) ? "0" : res);
		if (r < 1) {
			r = r * 100000;
		}
		return r;
	}
	
	public static Integer toFormatInt(String res) {
		int r = Integer.valueOf(res == null || "".equals(res) ? "0" : res);
		if (r < 1) {
			r = r * 100000;
		}
		return r;
	}

}

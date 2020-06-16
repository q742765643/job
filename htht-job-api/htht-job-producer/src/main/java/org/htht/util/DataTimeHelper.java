package org.htht.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.htht.job.core.util.DateUtil;


public class DataTimeHelper {
	
 
	
	 
	/**从文件名获取文件时间
	 * @param filePath : p1bg06n4.n18.14
	 * @return
	 * @throws ParseException 
	 */
	public static long getDataTimeFromFileName(String filePath) throws ParseException {
		if (filePath.contains("FY4A-")) {
			return getDataTimeFromFileName(filePath,"yyyyMMddHHmmss");
		}
		return getDataTimeFromFileName(filePath,"yyyyMMdd_HHmm");
	}
	
	/**从文件名获取文件时间
	 * @param filePath : p1bg06n4.n18.14
	 * @return
	 * @throws ParseException 
	 */
	public static long getDataTimeFromFileName(String filePath,String dateFormat) throws ParseException {
		if(!(filePath==null || "".equals(filePath))){
			String fileName = new File(filePath).getName();
			if(fileName.startsWith("p1b")){
				Date d = getDataTime_NOAA(fileName);
				if(d!=null){
					return d.getTime();
				}
			}
		//	/** 2014-01-03 数据在下载时已经重命名，时间格式被统一成 “yyyyMMdd_HHmm”
//			String dataPprefixStr = ConfigUtil.getProperty("dataDateTimePatterns","dataPprefix");
//			String dataPprefixArr[] = dataPprefixStr == null ? null : dataPprefixStr.split(",");
//			for(String dataPprefix : dataPprefixArr){
//				if(!"".equals(dataPprefix) && fileName.startsWith(dataPprefix)){
//					String dataFilePattern = ConfigUtil.getProperty("dataDateTimePatterns",dataPprefix);
//					if(filePath.contains("_MOSA.ldf")){
//						//拼接后的数据文件格式已经被格式化成固定格式  yyyyMMdd_HHmm
//						dataFilePattern = "yyyyMMdd_HHmm";
//					}
//					return getDataTimeFromFileNameByPattern(fileName,dataFilePattern);
//				}
//			}
			//*/
			/** 2014-01-03 数据在下载时已经重命名，时间格式被统一成 “yyyyMMdd_HHmm” ，所以上面一段改成如下：*/
			return getDataTimeFromFileNameByPattern(fileName,dateFormat);
		}
		return -1;
	}
	
	public static long getDataTimeFromFileNameByPattern(String fileName,String dataFilePattern){
		if(dataFilePattern != null && !"".equals(dataFilePattern)){
			Pattern p = Pattern.compile(getRegFromDatePattern(dataFilePattern));
			Matcher m = p.matcher(fileName);
			if(m.find()){
				String timeStr = m.group(0);
				Date d = DateUtil.strToDate(timeStr,dataFilePattern);
				return d.getTime();
			}
		}
		return -1;
	}
	
	private static String getRegFromDatePattern(String datePattern){
		String reg = new String(datePattern);
		reg = reg.replaceAll("[ymdhsYMDHS]", "\\\\d");
		return reg;
	}
	
	public static String getDataDate(String inputFilePath) throws ParseException{
		 long d = DataTimeHelper.getDataTimeFromFileName(inputFilePath);
		 String dateStr = DateUtil.formatDateTime(new Date(d), "yyyyMMdd");
		 return dateStr;
	}
	
	public static String getDataDateTime(String inputFilePath) throws ParseException{
		 long d = DataTimeHelper.getDataTimeFromFileName(inputFilePath);
		 String dateStr = DateUtil.formatDateTime(new Date(d), "yyyyMMddHHmm");
		 return dateStr;
	}
	
	/**
	 * 解析文件名中的日期，并且返回指定格式的日期字符串
	 * @param inputFilePath
	 * @param filePattern
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static String getFormatDate(String inputFilePath,String filePattern,String dateFormat) throws ParseException{
		 long d = DataTimeHelper.getDataTimeFromFileName(inputFilePath,filePattern);
		 String dateStr = DateUtil.formatDateTime(new Date(d), dateFormat);
		 return dateStr;
	}
	
	/** NOAA 的文件名命名不规范，获得 以p1b开头的文件数据时间
	 * @param fileName  :p1bg06n4.n18.14
	 * @return
	 * @throws ParseException
	 */
	public static Date getDataTime_NOAA(String fileName) throws ParseException{
		return getDataTime_NOAA(fileName,new Date());
	}
	
	/** NOAA 的文件名命名不规范，获得 以p1b开头的文件数据时间。文件的年月信息从文件的最后修改时间获得
	 * @param fileName
	 * @return
	 * @throws ParseException
	 */
	public static Date getDataTime_NOAA(String fileName,Date lastModified) throws ParseException{
		//p1bg06n4.n18.14  "p1bn1805.n18.15"
		String day = "" + fileName.charAt(4) + fileName.charAt(5);
		char mchar = fileName.charAt(3);
		String month = null;
		switch(mchar){
			case 'g':
				month = "08";
				break;
			case 's':
				month = "09";
				break;
			case 'o':
				month = "10";
				break;
			case 'n':
				month = "11";
				break;
			case 'd':
				month = "12";
				break;
		}
		//小时转换
		char hchar = fileName.charAt(6);
		int h  = 0;
		if(Character.isDigit(hchar)){
			 h = hchar - "0".charAt(0);
		}else{
			 h = 10+ (hchar - "a".charAt(0));
		}
		String hour = "" + (h<10 ? "0"+h : h);
		String mu = ""+fileName.charAt(7);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String year_month = sdf.format(lastModified);
		String newDateStr = year_month + day + "_" + hour + mu + 0 ;
		if(month!=null){
			 sdf.applyPattern("yyyy");
			 year_month = sdf.format(lastModified);
			 newDateStr =  year_month +month + day + "_" + hour + mu + 0 ;
		}
		

		sdf.applyPattern("yyyyMMdd_HHmm");
		
		Date d = sdf.parse(newDateStr);
		//sdf.parse(newDateStr);
		
		if(d.after(new Date())){
			d.setMonth(d.getMonth()-1);
		}
		return d;
	}
public static void main(String[] args)
{
	try
	{
		System.out.println(DataTimeHelper.getDataTimeFromFileName("EOSA_MODIS_20180517152123_0250M_MOD02QKM.hdf","yyyyMMddHHmmss"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("HHmm");
		long obsDateTime = DataTimeHelper.getDataTimeFromFileName("EOSA_MODIS_20180517152123_0250M_MOD02QKM.hdf","yyyyMMddHHmmss");
		Date observTime = new Date(obsDateTime);
		String observDateStr = sdf.format(observTime);
		String observTimeStr = sdf1.format(observTime);
		System.out.println(observDateStr+""+observTimeStr);
	} catch (ParseException e)
	{
		e.printStackTrace();
	}
}	
}

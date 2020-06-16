package org.htht.util;

import org.apache.commons.lang3.StringUtils;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public final class DateUtil {
	private static SimpleDateFormat _formatter = new SimpleDateFormat(
			"dd'/'MM'/'yyyy", Locale.SIMPLIFIED_CHINESE);
	private static SimpleDateFormat _formatterDateTime = new SimpleDateFormat(
			"dd'/'MM'/'yyyy' 'HH':'mm", Locale.SIMPLIFIED_CHINESE);
	private static SimpleDateFormat _formatterDateTime2 = new SimpleDateFormat(
			"yyyy'/'MM'/'dd' 'HH':'mm':'ss", Locale.SIMPLIFIED_CHINESE);

	/**
	 * 将短时间格式时间转换为字符串 yyyy-MM-dd
	 *
	 * @param dateDate
	 * @return
	 */
	public static synchronized String dateToStr(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(dateDate);
	}

	/**
	 * Creates a new DateUtil object
	 */
	private DateUtil() {
	}

	/**
	 * Returns the date of the day in form of a String
	 * 
	 * @return The Date of the day in a "JJ/MM/AAAA" format
	 */
	private static synchronized String getCurrentDateString(
			SimpleDateFormat formatter) {
		return formatDateTime(new java.util.Date(), formatter);
	}

	private static synchronized String formatDateTime(
			java.util.Date targetDate, java.text.DateFormat formatter) {
		if (targetDate == null)
			return "";
		return formatter.format(targetDate);
	}

	public static synchronized String getCurrentDateString() {
		return getCurrentDateString(_formatter);
	}

	public static synchronized String getCurrentTimeString() {
		return getCurrentDateString(_formatterDateTime2);
	}

	public static synchronized String getCurrentDateString(String formatStr) {
		return formatDateTime(new java.util.Date(), formatStr);
	}
	public static synchronized String getCurrentDateStringWithOffset(String formatStr,Integer offset) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, offset);
		return formatDateTime(new Date(c.getTimeInMillis()), formatStr);
	}

	public static synchronized String formatDateTime(java.util.Date targetDate,
			String formatStr) {
		java.text.DateFormat dateFormater = new java.text.SimpleDateFormat(
				formatStr, Locale.SIMPLIFIED_CHINESE);
		return formatDateTime(targetDate, dateFormater);
	}
	public static synchronized java.util.Date addDate(Date date, int field, int amount) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}
	public static synchronized int getDateOnlyHour(java.util.Date date) {
		if (date != null) {
			Calendar instance = Calendar.getInstance();
			instance.setTime(date);
			return instance.get(Calendar.HOUR_OF_DAY);
		}
		
		return 0;
	}
	public static synchronized String formateDate(Date date, String formatDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
		return simpleDateFormat.format(date);
	}
	public static synchronized String getCurrentDateStringWithHourOffset(String formatStr,Integer offset) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, offset);
		return formatDateTime(new Date(c.getTimeInMillis()), formatStr);
	}
	/**
	 * ȡ��}������֮��,����Сʱ��
	 * 
	 * @param startday
	 * @param endday
	 * @return
	 */
	public static int getIntervalHours(Date startday, Date endday) {
		if (startday.after(endday)) {
			Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60));
	}

	/**
	 * Converts a String date in a "jj/mm/aaaa" format in a java.sql.Date type
	 * date
	 * 
	 * @param strDate
	 *            The String Date to convert, in a date in the "jj/mm/aaaa"
	 *            format
	 * @return The date in form of a java.sql.Date type date
	 */
	public static synchronized java.sql.Date getDateSql(String strDate) {
		ParsePosition pos = new ParsePosition(0);
		java.util.Date date = _formatter.parse(strDate, pos);

		if (date != null) {
			return new java.sql.Date(date.getTime());
		}

		return null;
	}

	/**
	 * Converts a String date in a "jj/mm/aaaa" format in a java.util.Date type
	 * date
	 * 
	 * @param strDate
	 *            The String Date to convert, in a date in the "jj/mm/aaaa"
	 *            format
	 * @return The date in form of a java.sql.Date tyep date
	 */
	public static synchronized java.util.Date getDate(String strDate) {
		ParsePosition pos = new ParsePosition(0);
		java.util.Date date = _formatter.parse(strDate, pos);

		return date;
	}
	
	public static synchronized java.util.Date getDate(String strDate,String pattern ) throws ParseException {
		_formatter.applyPattern(pattern);
		java.util.Date date = _formatter.parse(strDate);
		return date;
	}
	/**
	 * Converts a String date in a "jj/mm/aaaa" format in a java.sql.Timestamp
	 * type date
	 * 
	 * @param strDate
	 *            The String Date to convert, in a date in the "jj/mm/aaaa"
	 *            format
	 * @return The date in form of a java.sql.Date tyep date
	 */
	public static synchronized java.sql.Timestamp getTimestamp(String strDate) {
		ParsePosition pos = new ParsePosition(0);
		java.util.Date date = _formatter.parse(strDate, pos);

		if (date != null) {
			return (new java.sql.Timestamp(date.getTime()));
		}

		return null;
	}

	
	/**
	 * Converts a String date in a "yyyy'/'MM'/'dd' 'HH':'mm'：'ss" format in a java.sql.Timestamp
	 * type date
	 * 
	 * @param strDate
	 *            The String Date to convert, in a date in the "yyyy'/'MM'/'dd' 'HH':'mm'：'ss"
	 *            format
	 * @return The date in form of a java.sql.Date tyep date
	 */
	public static synchronized java.sql.Timestamp getTimestamp2(String strDate) {
		ParsePosition pos = new ParsePosition(0);
		java.util.Date date = _formatterDateTime2.parse(strDate, pos);

		if (date != null) {
			return (new java.sql.Timestamp(date.getTime()));
		}

		return null;
	}
	
	/**
	 * Converts a java.sql.Date type date in a String date with a "jj/mm/aaaa"
	 * format
	 * 
	 * @param date
	 *            java.sql.Date date to convert
	 * @return strDate The date converted to String in a "jj/mm/aaaa" format or
	 *         an empty String if the date is null
	 */
	public static synchronized String getDateString(java.sql.Date date) {
		if (date != null) {
			StringBuffer strDate = new StringBuffer();
			_formatter.format(date, strDate, new FieldPosition(0));

			return strDate.toString();
		}

		return "";
	}

	// /////////////////////////////////////////////////////////////////////////
	// methodes using the java.sql.Timestamp type

	/**
	 * Converts une java.sql.Timestamp date in a String date in a "jj/mm/aaaa"
	 * format
	 * 
	 * @param date
	 *            java.sql.Timestamp date to convert
	 * @return strDate The String date in a "jj/mm/aaaa" format or the emmpty
	 *         String if the date is null
	 */
	public static synchronized String getDateString(java.sql.Timestamp date) {
		if (date != null) {
			StringBuffer strDate = new StringBuffer();
			_formatter.format(date, strDate, new FieldPosition(0));

			return strDate.toString();
		}

		return "";
	}

	// /////////////////////////////////////////////////////////////////////////
	// methodes using the java.util.Date type

	/**
	 * Converts a java.util.Date date in a String date in a "jj/mm/aaaa" format
	 * 
	 * @param date
	 *            java.util.Date date to convert
	 * @return strDate A String date in a "jj/mm/aaaa" format or an empty String
	 *         if the date is null
	 */
	public static synchronized String getDateString(java.util.Date date) {
		if (date != null) {
			StringBuffer strDate = new StringBuffer();
			_formatter.format(date, strDate, new FieldPosition(0));

			return strDate.toString();
		}

		return "";
	}

	// /////////////////////////////////////////////////////////////////////////
	// methods using a long value

	/**
	 * Converts a long value to a String date in a "jj/mm/aaaa hh:mm" format
	 * 
	 * @param lTime
	 *            The long value to convert
	 * @return The formatted string
	 */
	public static synchronized String getDateTimeString(long lTime) {
		StringBuffer strDate = new StringBuffer();
		_formatterDateTime.format(new java.util.Date(lTime), strDate,
				new FieldPosition(0));

		return strDate.toString();
	}

	public static synchronized java.util.Date strToDate(String strDate) {
		try {
			if(strDate.indexOf("'") != -1) {
				strDate = strDate.replaceAll("'", "");
			}
			
			SimpleDateFormat ft = null;
			if(strDate.indexOf("-") != -1) {
				ft = new SimpleDateFormat("yyyy-MM-dd");
				if (strDate.length() > 10) {
					ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
			} else if(strDate.indexOf("/") != -1) {
				ft = new SimpleDateFormat("yyyy/MM/dd");
				if (strDate.length() > 10) {
					ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				}
			} else if(strDate.indexOf("\\") != -1) {
				ft = new SimpleDateFormat("yyyy\\MM\\dd");
				if (strDate.length() > 10) {
					ft = new SimpleDateFormat("yyyy\\MM\\dd HH:mm:ss");
				}
			} else {
				ft = new SimpleDateFormat("yyyyMMdd");
				if (strDate.length() > 10) {
					ft = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				}
			}
			
			
			
			
			
			java.util.Date d = ft.parse(strDate);
			return new java.sql.Date(d.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date(Calendar.getInstance().getTime().getTime());
		}
	}

	public static synchronized java.util.Date strToDate(String strDate,
			String formatStr) {
		if(strDate.indexOf("'") != -1) {
			strDate = strDate.replaceAll("'", "");
		}
		try {
			SimpleDateFormat ft = new SimpleDateFormat(formatStr);
			java.util.Date d = ft.parse(strDate);
			return new java.util.Date(d.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date(Calendar.getInstance().getTime().getTime());
		}
	}
	/**
	 * 时间差
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String getDateDifference(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return "";
		}
		long between = (endDate.getTime() - startDate.getTime()) / 1000; // 除以1000是为了转换成秒
		if (between == 0)
			between = 1;
		long day1 = between / (24 * 3600);
		long hour1 = between % (24 * 3600) / 3600;
		long minute1 = between % 3600 / 60;
		long second1 = between % 60;
		StringBuffer dataString = new StringBuffer("");
		if (day1 > 0) {
			dataString.append(day1 + "天");
		}
		if (hour1 > 0) {
			dataString.append(hour1 + "小时");
		}
		if (minute1 > 0) {
			dataString.append(minute1 + "分");
		}
		if (second1 > 0) {
			dataString.append(second1 + "秒");
		}
		return dataString.toString();
	}
	
	public static String formatDateWithDay(int year,int dayOfYear,String format) {
		Calendar calendar =Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
		return formatDateTime(calendar.getTime(), format);
	}
	/**
	 * 文件名时间串转化为date
	 * @param timeString
	 * @return
	 */
	public  static Date  getDataFromFileName(String  timeString){	
		
		String year = timeString.substring(0, 4);
		String month = timeString.substring(4, 6);
		String day = timeString.substring(6, 8);
		String hour = timeString.substring(8, 10);
		String min = timeString.substring(10, 12);
		String second = timeString.substring(12, 14);
		String filesnameTime = "" + year + "-" + month + "-" + day + " " + hour
				+ ":" + min + ":" + second;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(filesnameTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;

	}
	/**
	 * 文件名时间串转化为date
	 * @param timeString
	 * @return
	 */
	public static long   StringDateToLong(String dateStr,String formatter){
		
		//String pas = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat sf = new SimpleDateFormat(formatter);
		Date d1 = null;
		try {
			d1 = sf.parse(dateStr);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
	return  d1.getTime();
	}
	/**
	 * 
	 * @param arg
	 */
	public static boolean CopByFileName(String beginTime,String fileTime,String endTime){
		Date begin= DateUtil.strToDate(beginTime, "yyyyMMddHHmmss");
		Date end=DateUtil.strToDate(endTime, "yyyyMMddHHmmss");
		Date ft=DateUtil.strToDate(fileTime, "yyyyMMddHHmmss");
		if(begin.getTime()<=ft.getTime() && ft.getTime()<=end.getTime()){
			return true;
		}
		return false;
	}
	public static String getPathByFormat(String path,String format,Date date){
		String time = formatDateTime(date, format);
		return path.replace("{"+format+"}", time);
	}
	public static String getPathFormat(String path){
		if(StringUtils.isBlank(path)){
			return "";
		}
		if(path.contains("{") && path.contains("}")){
			return path.substring(path.indexOf("{")+1,path.indexOf("}"));
		}
		return "";
	}
	public static String getPathByDate(String path,Date date){
		
		String format = "";
		String result = path;
		while (result.contains("{") && result.contains("}")) {
			format = getPathFormat(result);
			result = getPathByFormat(result, format, date);
		}
		
		return result;
//		String format = getPathFormat(path);
//		return getPathByFormat(path, format, date);
	}
	/**
	 * 获取当前北京时间
	 * @return
	 */
	public static Date getBeiJingTime() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				        
			//北京时间
			sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			String time1 = sdf.format(new Date());
		
			Date time2 = sdf1.parse(time1);
			return time2;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 系统时间转北京时
	 * @param date 
	 * @return
	 */
	public static Date getSystemToBeiJingTime(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf_sys = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf_bj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 查看当前的时区
			ZoneId defaultZone = ZoneId.systemDefault();
			String dateStr = sdf.format(date);	
			//当前时区的时间
			sdf_sys.setTimeZone(TimeZone.getTimeZone(defaultZone));
			Date date_sys = sdf_sys.parse(dateStr);
			//北京时间
			sdf_bj.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			//时间转换
			String time1 = sdf_bj.format(date_sys);
			Date time2 = sdf.parse(time1);
			return time2;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 北京时转系统时间
	 * @param date 时
	 * @return
	 */
	public static Date getBeiJingToSystemTime(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf_sys = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf_bj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			// 查看当前的时区
			ZoneId defaultZone = ZoneId.systemDefault();
				
			String dateStr = sdf.format(date);	
			//北京时间
			sdf_bj.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			Date date_bj = sdf_bj.parse(dateStr);
			//当前时区的时间
			sdf_sys.setTimeZone(TimeZone.getTimeZone(defaultZone));
			//时间转换
			String time1 = sdf_sys.format(date_bj);
			Date time2 = sdf.parse(time1);
			return time2;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}

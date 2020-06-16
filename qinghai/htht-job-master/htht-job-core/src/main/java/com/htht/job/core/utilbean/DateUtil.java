package com.htht.job.core.utilbean;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 日期型对象应用类
 * 
 * @author JiahaoWong
 * 
 */
public final class DateUtil {
	public static SimpleDateFormat _formatter = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
	public static SimpleDateFormat _formatterDateTime = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);

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

	public static synchronized String getCurrentDateString(String formatStr) {
		return formatDateTime(new java.util.Date(), formatStr);
	}

	public static synchronized String formatDateTime(java.util.Date targetDate,
			String formatStr) {
		java.text.DateFormat dateFormater = new java.text.SimpleDateFormat(formatStr);
		return formatDateTime(targetDate, dateFormater);
	}
	
	public static synchronized String formatDateTime(java.util.Date targetDate) {
		return formatDateTime(targetDate, _formatterDateTime);
	}

	/**
	 * 
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
		java.util.Date date = _formatterDateTime.parse(strDate, pos);

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
			_formatterDateTime.format(date, strDate, new FieldPosition(0));

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
		_formatterDateTime.format(new java.util.Date(lTime), strDate,new FieldPosition(0));

		return strDate.toString();
	}

	public static synchronized String getDateTimeString(Date date) {
		StringBuffer strDate = new StringBuffer();
		_formatterDateTime.format(date, strDate, new FieldPosition(0));

		return strDate.toString();
	}
	public static void main(String[]agrs){
		final Map<String,String> map = new HashMap<String,String>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(1970, 0, 1, 8, 0, 0);
		Date date = calendar.getTime();
		
		long s = calendar.getTimeInMillis();
		System.out.println(s);
	    s = date.getTime();
		System.out.println(s);
		s = System.currentTimeMillis();
		System.out.println(DateUtil.formatDateTime(new Date(System.currentTimeMillis()),"yyyy-MM-dd HH:mm:ss"));
		System.out.println(DateUtil.formatDateTime(new Date(System.nanoTime()),"yyyy-MM-dd hh:mm:ss"));
		
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(){
				public void run(){
					for (int j = 0; j < 100000000; j++) {
						String key = System.nanoTime()+"";
						String value = map.get(key);
						if(value != null){
							System.out.println("map get key "+ value);
						}else{
							map.put(key, key);
						}
					}
				}
			};
			thread.start();
		}
	}
}

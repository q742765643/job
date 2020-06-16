package com.htht.job.core.util;

import java.util.Calendar;
import java.util.Date;

public class MatchTime {

	/**
	 * 
	 * COTM Cycle of Ten Minute 10min周期 
	 * COOH Cycle of One Hour 小时时周期合成产品
	 * COOM Cycle of One Minute 分钟周期合成产品（实时周期合成产品）
	 * CORT Cycle of Real Time 实时周期合成产品
	 * COOD Cycle of One Day 日周期合成产品
	 * COFD Cycle of Five Day 侯周期合成产品
	 * COSD Cycle of Seven Days周周期合成产品 
	 * COTD Cycle of Ten Days 旬周期合成产品 
	 * COAM Cycle of a Month 月周期合成产品 
	 * COAQ Cycle of a Quarter 季周期合成产品 
	 * COAY Cycle of a Year 年周期合成产品
	 * 
	 * @param date  传入的时间
	 * @param str  时间运算 格式{yyyy+-数字}{MM＋－数字}{dd＋－数字}{HH＋－数字}{mm+-数字}{ss+-数字}
	 * @param cycle  周期
	 * @return
	 */
	public static String matchIssue(Date date,String str,String cycle){
		Calendar calendar = getCalendar(date,str);
		String issue = null;
		cycle = cycle.trim().toUpperCase();
		if("COAY".equals(cycle)){//年周期
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy")+"01010000";
		}else if("COAQ".equals(cycle)){//季产品
			int month = calendar.get(Calendar.MONTH) + 1;
			if(month==12 || month<3){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy")+"01010000";
			}else if(month<6){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy")+"03010000";
			}else if(month<9){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy")+"06010000";
			}else{
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy")+"09010000";
			}
		}else if("COAM".equals(cycle)){//月产品
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"010000";
		}else if("COTD".equals(cycle)){//旬周期合成产品
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			if(day<11){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"010000";
			}else if(day <21){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"110000";
			}else{
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"210000";
			}
		}else if("COSD".equals(cycle)){// COSD	Cycle of Seven Days周周期合成产品 
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			if(day<8){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"010000";
			}else if(day <15){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"080000";
			}else if(day <22){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"150000";
			}else {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"220000";
			}
		}else if("COFD".equals(cycle)){//COFD   Cycle of Five Day  侯周期合成产品
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			if(day<6){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"010000";
			}else if(day <11){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"060000";
			}else if(day <16){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"110000";
			}else if(day <21){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"160000";
			}else if(day <26){
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"210000";
			}else {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM")+"260000";
			}
		}else if("COOD".equals(cycle)){//COOD  Cycle of One Day	 日周期合成产品
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMMdd")+"0000";
		}else if("COOH".equals(cycle)){//COOH  Cycle of One Hour	小时周期合成产品
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHHmm");
		}else if("COTM".equals(cycle)){//COTM  Cycle of Ten Minute 10min周期
			int minute = calendar.get(Calendar.MINUTE)/10*10;
			if(minute>9){
				issue =DateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHH")+minute;
			}else{
				issue =DateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHH")+"0"+minute;
			}
		}else if("COOM".equals(cycle)){//COOM Cycle of One Minute 分钟周期合成产品 
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHHmm");
		}else if("CORT".equals(cycle)){
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHHmm");
		}
		return issue;
	}

	/**
	 * 重写方法
	 * 
	 * @param date
	 * @param cycle
	 * @return
	 */
	public static String matchIssue(Date date, String cycle) {
		String str = DateUtil.formatDateTime(date, "yyyyMMddHHmm");
		return matchIssue(date, str, cycle);
	}

	/**
	 * 重写方法
	 * 
	 * @param date
	 * @param cycle
	 * @return
	 */
	public static String matchIssue(String str, String cycle) {
		Date date = new Date();
		return matchIssue(date, str, cycle);
	}

	/**
	 * 根据初始的date和需要运算的日期通配符，计算出需要的新的Calendar
	 * 
	 * @param date
	 * @param str
	 *            格式{yyyy+-数字}{MM＋－数字}{dd＋－数字}{HH＋－数字}{mm+-数字}{ss+-数字}
	 * @return
	 */
	public static Calendar getCalendar(Date date, String str) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (str.indexOf("{") > -1) {
			String str1 = str.replace("}", ",").replace("{", "");
			String[] strSplit = str1.split(",");
			int index = strSplit.length;
			if (index > 0 && strSplit[0].length() > 4) {
				int year = Integer.parseInt(strSplit[0].substring(4));
				calendar.add(Calendar.YEAR, year);
			}
			if (index > 1 && strSplit[1].length() > 2) {
				int month = Integer.parseInt(strSplit[1].trim().substring(2));
				calendar.add(Calendar.MONTH, month);
			}
			if (index > 2 && strSplit[2].length() > 2) {
				int day = Integer.parseInt(strSplit[2].trim().substring(2));
				calendar.add(Calendar.DAY_OF_YEAR, day);
			}
			if (index > 3 && strSplit[3].length() > 2) {
				int hour = Integer.parseInt(strSplit[3].trim().substring(2));
				calendar.add(Calendar.HOUR_OF_DAY, hour);
			}
			if (index > 4 && strSplit[4].length() > 2) {
				int minute = Integer.parseInt(strSplit[4].trim().substring(2));
				calendar.add(Calendar.MINUTE, minute);
			}
		} else {
			for (int i = str.length(); i < 12; i++) {
				str += "0";
			}
			Date strDate = DateUtil.strToDate(str, "yyyyMMddHHmm");
			calendar.setTime(strDate);
		}
		return calendar;

	}

	/**
	 * 根据初始的date和需要运算的日期通配符，计算出需要的新的date
	 * 
	 * @param date
	 * @param str
	 *            格式{yyyy+-数字}{MM＋－数字}{dd＋－数字}{HH＋－数字}{mm+-数字}{ss+-数字}
	 * @return
	 */
	public static Date getDate(Date date, String str) {
		Calendar c = getCalendar(date, str);
		return c.getTime();
	}
	
}
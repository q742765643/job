package org.htht.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

public class MatchTimeX {

	public static void main(String[] args) {
	
		// String str = "{yyyy-5}年{MM-1}月{dd+3}日{hh+3}时{mm+9}分";
		String str = "201803121212";
		String path1 = "{yyyy}{MM}{dd-3}{HH}";
		// String str = "{yyyy}{MM}{dd-3} {hh-23} ";
		MatchTimeX m = new MatchTimeX();
		String s = m.matchIssue(path1);

		// System.out.println(m.tenDays("20140301"));
		// m.matchIssue(str, "COOD");
		// System.out.println(m.match(str));
		
	}

	/**
	 * 
	 * COTM Cycle of Ten Minute 10min周期 COOH Cycle of One Hour 实时周期合成产品 COOD
	 * Cycle of One Day 日周期合成产品 COFD Cycle of Five Day 侯周期合成产品 COSD Cycle of
	 * Seven Days周周期合成产品 COTD Cycle of Ten Days 旬周期合成产品 COAM Cycle of a Month
	 * 月周期合成产品 COAQ Cycle of a Quarter 季周期合成产品 COAY Cycle of a Year 年周期合成产品
	 * 
	 * @param date
	 *            传入的时间
	 * @param str
	 *            时间运算
	 * @param cycle
	 *            周期
	 * @return
	 */
	public String matchIssue(Date date, String str, String cycle) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String issue = null;
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
			try {
				Date strDate = DateUtil.getDate(str, "yyyyMMddHHmm");
				calendar.setTime(strDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if ("COAY".equals(cycle)) {// 年周期
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy") + "01010000";
		} else if ("COAQ".equals(cycle)) {// 季产品
			int month = calendar.get(Calendar.MONTH) + 1;
			if (month == 12 || month < 3) {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy") + "01010000";
			} else if (month < 6) {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy") + "03010000";
			} else if (month < 9) {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy") + "06010000";
			} else {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyy") + "09010000";
			}
		} else if ("COAM".equals(cycle)) {// 月产品
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "010000";
		} else if ("COTD".equals(cycle)) {// 旬周期合成产品
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			if (day < 11) {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "010000";
			} else if (day < 21) {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "110000";
			} else {
				issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "210000";
			}
		} else if ("COSD".equals(cycle)) {// COSD Cycle of Seven Days周周期合成产品
											// TODO

		} else if ("COFD".equals(cycle)) {// COFD Cycle of Five Day 侯周期合成产品 TODO

		} else if ("COOD".equals(cycle)) {// COOD Cycle of One Day 日周期合成产品
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMMdd") + "0000";
		} else if ("COOH".equals(cycle)) {// COOH Cycle of One Hour 实时周期合成产品
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHHmmss");
		} else if ("COTM".equals(cycle)) {// COTM Cycle of Ten Minute 10min周期
			int minute = calendar.get(Calendar.MINUTE) / 10 * 10;
			issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHH") + minute + "00";
		}

		return issue;
	}

	public static String matchIssue(String str) {
		Calendar calendar = Calendar.getInstance();
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
			try {
				Date strDate = DateUtil.getDate(str, "yyyyMMddHHmm");
				calendar.setTime(strDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return str;
		}
		String issue = DateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHHmm");
		return issue;
	}

	/**
	 * 重写方法
	 * 
	 * @param date
	 * @param cycle
	 * @return
	 */
	public String matchIssue(Date date, String cycle) {
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
	public String matchIssue(String str, String cycle) {
		Date date = new Date();
		return matchIssue(date, str, cycle);
	}

	/**
	 * 匹配日期时间
	 * 
	 * @return
	 */
	public String match(String str) {
		String dTime = "";

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR); // 年
		int month = c.get(Calendar.MONTH) + 1; // 月
		int day = c.get(Calendar.DAY_OF_MONTH); // 日
		int time = c.get(Calendar.HOUR_OF_DAY); // 时
		int min = c.get(Calendar.MINUTE); // 分

		// String str = "{yyyy-5}年{MM-1}月{dd+3}日{hh+3}:{mm+9}分";
		String str1 = str.replace("}", ",").replace("{", ",").substring(1, str.length());
		str1 = str1 + ",";
		String[] strSplit = str1.split(",");
		// 转换年
		int yearNum = 0;
		if (strSplit.length > 0) {
			String yearAll = strSplit[0].substring(0, strSplit[0].length());
			String yearSub = yearAll.substring(4, yearAll.length());
			if (yearAll.length() > 4) {
				if (yearSub.contains("+")) {
					yearSub = yearSub.replace("+", "");
				}
				yearNum = year + Integer.parseInt(yearSub);
			} else {
				yearNum = year;
			}
		}
		// //转换月
		int monthNum = 0;
		if (strSplit.length > 2) {
			String monthAll = strSplit[2].substring(0, strSplit[2].length());
			String monthSub = monthAll.substring(2, monthAll.length());
			if (monthAll.length() > 2) {
				if (monthSub.contains("+")) {
					monthSub = monthSub.replace("+", "");
				}
				monthNum = month + Integer.parseInt(monthSub);
				if (monthNum <= 0) {
					yearNum = yearNum - 1;
					monthNum = 12 - Math.abs(monthNum);
				} else if (monthNum > 12) {
					yearNum = yearNum + 1;
					monthNum = monthNum - 12;
				}
			} else {
				monthNum = month;
			}
		}

		// //转换日
		int dayNum = 0;
		if (strSplit.length > 4) {
			String dayAll = strSplit[4].substring(0, strSplit[4].length());
			String daySub = dayAll.substring(2, dayAll.length());
			if (dayAll.length() > 2) {
				if (daySub.contains("+")) {
					daySub = daySub.replace("+", "");
				}
				dayNum = day + Integer.parseInt(daySub);
				if (dayNum <= 0) {
					monthNum = monthNum - 1;
					dayNum = calculateDays(yearNum, monthNum) - Math.abs(dayNum);
				} else if (dayNum > calculateDays(yearNum, monthNum)) {
					monthNum = monthNum + 1;
					dayNum = dayNum - calculateDays(yearNum, monthNum);
				}
				if (monthNum <= 0) {
					yearNum = yearNum - 1;
					monthNum = 12 - Math.abs(monthNum);
				} else if (monthNum > 12) {
					yearNum = yearNum + 1;
					monthNum = monthNum - 12;
				}
			} else {
				dayNum = day;
			}
		}
		String monthStr = (monthNum < 10 ? "0" + monthNum : monthNum).toString();
		String dayStr = (dayNum < 10 ? "0" + dayNum : dayNum).toString();

		// //转换时
		int timeNum = 0;
		if (strSplit.length > 6) {
			String timeAll = strSplit[6].substring(0, strSplit[6].length());
			String timeSub = timeAll.substring(2, timeAll.length());
			if (timeAll.length() > 2) {
				if (timeSub.contains("+")) {
					timeSub = timeSub.replace("+", "");
				}
				timeNum = time + Integer.parseInt(timeSub);
				if (timeNum < 0) {
					dayNum = dayNum - 1;
					timeNum = 24 - Math.abs(timeNum);
				} else if (timeNum >= 24) {
					dayNum = dayNum + 1;
					timeNum = timeNum - 24;
				}
				if (dayNum <= 0) {
					monthNum = monthNum - 1;
					dayNum = calculateDays(yearNum, monthNum) - Math.abs(dayNum);
				} else if (dayNum > calculateDays(yearNum, monthNum)) {
					monthNum = monthNum + 1;
					dayNum = dayNum - calculateDays(yearNum, monthNum);
				}
				if (monthNum <= 0) {
					yearNum = yearNum - 1;
					monthNum = 12 - Math.abs(monthNum);
				} else if (monthNum > 12) {
					yearNum = yearNum + 1;
					monthNum = monthNum - 12;
				}
			} else {
				timeNum = time;
			}
			dayStr = (dayNum < 10 ? "0" + dayNum : dayNum).toString();
		} else {
			if (strSplit.length > 5) {
				dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5];
			} else {
				dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr;
			}
			return dTime;
		}
		String timeStr = (timeNum < 10 ? "0" + timeNum : timeNum).toString();
		// //转换分
		int minNum = 0;
		if (strSplit.length > 8) {
			String minAll = strSplit[8].substring(0, strSplit[8].length());
			String minSub = minAll.substring(2, minAll.length());
			if (minAll.length() > 2) {
				if (minSub.contains("+")) {
					minSub = minSub.replace("+", "");
				}
				minNum = min + Integer.parseInt(minSub);
				if (minNum <= 0) {
					timeNum = timeNum - 1;
					minNum = 60 - Math.abs(minNum);
				} else if (minNum > 60) {
					timeNum = timeNum + 1;
					minNum = minNum - 60;
				}
				if (timeNum < 0) {
					dayNum = dayNum - 1;
					timeNum = 24 - Math.abs(timeNum);
				} else if (timeNum >= 24) {
					dayNum = dayNum + 1;
					timeNum = timeNum - 24;
				}
				if (dayNum <= 0) {
					monthNum = monthNum - 1;
					dayNum = calculateDays(yearNum, monthNum) - Math.abs(dayNum);
				} else if (dayNum > calculateDays(yearNum, monthNum)) {
					monthNum = monthNum + 1;
					dayNum = dayNum - calculateDays(yearNum, monthNum);
				}
				if (monthNum <= 0) {
					yearNum = yearNum - 1;
					monthNum = 12 - Math.abs(monthNum);
				} else if (monthNum > 12) {
					yearNum = yearNum + 1;
					monthNum = monthNum - 12;
				}
			} else {
				minNum = min;
			}
		} else {
			if (strSplit.length > 7) {
				dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + timeStr + strSplit[7];
			} else {
				dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + timeStr;
			}
			return dTime;
		}
		String minStr = (minNum < 10 ? "0" + minNum : minNum).toString();
		if (strSplit.length > 9) {
			dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + timeStr + strSplit[7]
					+ minStr + strSplit[9];
		} else {
			dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + timeStr + strSplit[7]
					+ minStr;
		}
		return dTime;
	}

	/**
	 * 计算上一旬
	 * 
	 * @param str
	 * @return
	 */
	public String tenDays(String str) {
		int year = Integer.parseInt(str.substring(0, 4));
		int month = Integer.parseInt(str.substring(4, 6));
		int day = Integer.parseInt(str.substring(6, 8));

		int monthDay = 0;
		int lastTenDay = 0;

		if (day < 10 && month == 1) {
			year = year - 1;
			month = 12;
			lastTenDay = calculateLastTenDay(year, monthDay);
			day = 31 - (lastTenDay - day);
		} else if (day < 10) {
			month = month - 1;
			monthDay = calculateDays(year, month);
			lastTenDay = calculateLastTenDay(year, monthDay);
			day = monthDay - (lastTenDay - day);
		} else {
			lastTenDay = calculateLastTenDay(year, monthDay);
			day = lastTenDay - 10;
		}
		// 补零
		String monthStr = (String) (month < 10 ? "0" + month : month);

		return year + "" + monthStr + "" + day;
	}

	public int calculateLastTenDay(int year, int month) {
		int lastTenDay = 0;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			lastTenDay = 11;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			lastTenDay = 10;
		} else {
			if (year / 4 == 0 || (year / 100 == 0 && year / 400 != 0)) {
				lastTenDay = 9;
			} else {
				lastTenDay = 8;
			}
		}
		return lastTenDay;
	}

	/**
	 * 计算月份天数
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return
	 */
	public int calculateDays(int year, int month) {
		int monthDay = 0;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			monthDay = 31;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			monthDay = 30;
		} else {
			if (year / 4 == 0 || (year / 100 == 0 && year / 400 != 0)) {
				monthDay = 29;
			} else {
				monthDay = 28;
			}
		}
		return monthDay;
	}

}
package com.htht.job.executor.util;

import java.util.Calendar;

public class CImissMatchTime {
	

	public static String match(Calendar c, String str) {

		if (str.length() <= 14) {
			for (int i = str.length(); i < 14; i++) {
				str = str + "0";
			}
			return str;
		} else if (str.length() > 25) {
			return str;
		}

		String dTime = "";

		int year = c.get(1);
		int month = c.get(2) + 1;
		int day = c.get(5);
		int time = c.get(11);
		int min = c.get(12);

		String str1 = str.replace("}", ",").replace("{", ",").substring(1, str.length());
		str1 = str1 + ",";
		String[] strSplit = str1.split(",");

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
					yearNum--;
					monthNum = 12 - Math.abs(monthNum);
				} else if (monthNum > 12) {
					yearNum++;
					monthNum -= 12;
				}
			} else {
				monthNum = month;
			}

		}

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
					monthNum--;
					dayNum = calculateDays(yearNum, monthNum) - Math.abs(dayNum);
				} else if (dayNum > calculateDays(yearNum, monthNum)) {
					monthNum++;
					dayNum -= calculateDays(yearNum, monthNum);
				}
				if (monthNum <= 0) {
					yearNum--;
					monthNum = 12 - Math.abs(monthNum);
				} else if (monthNum > 12) {
					yearNum++;
					monthNum -= 12;
				}
			} else {
				dayNum = day;
			}
		}
		String monthStr = (monthNum < 10 ? "0" + monthNum : Integer.valueOf(monthNum)).toString();
		String dayStr = (dayNum < 10 ? "0" + dayNum : Integer.valueOf(dayNum)).toString();

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
					dayNum--;
					timeNum = 24 - Math.abs(timeNum);
				} else if (timeNum >= 24) {
					dayNum++;
					timeNum -= 24;
				}
				if (dayNum <= 0) {
					monthNum--;
					dayNum = calculateDays(yearNum, monthNum) - Math.abs(dayNum);
				} else if (dayNum > calculateDays(yearNum, monthNum)) {
					monthNum++;
					dayNum -= calculateDays(yearNum, monthNum);
				}
				if (monthNum <= 0) {
					yearNum--;
					monthNum = 12 - Math.abs(monthNum);
				} else if (monthNum > 12) {
					yearNum++;
					monthNum -= 12;
				}
			} else {
				timeNum = time;
			}
			dayStr = (dayNum < 10 ? "0" + dayNum : Integer.valueOf(dayNum)).toString();
		} else {
			if (strSplit.length > 5)
				dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5];
			else {
				dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr;
			}
			return dTime;
		}
		String timeStr = (timeNum < 10 ? "0" + timeNum : Integer.valueOf(timeNum)).toString();

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
					timeNum--;
					minNum = 60 - Math.abs(minNum);
				} else if (minNum > 60) {
					timeNum++;
					minNum -= 60;
				}
				if (timeNum < 0) {
					dayNum--;
					timeNum = 24 - Math.abs(timeNum);
				} else if (timeNum >= 24) {
					dayNum++;
					timeNum -= 24;
				}
				if (dayNum <= 0) {
					monthNum--;
					dayNum = calculateDays(yearNum, monthNum) - Math.abs(dayNum);
				} else if (dayNum > calculateDays(yearNum, monthNum)) {
					monthNum++;
					dayNum -= calculateDays(yearNum, monthNum);
				}
				if (monthNum <= 0) {
					yearNum--;
					monthNum = 12 - Math.abs(monthNum);
				} else if (monthNum > 12) {
					yearNum++;
					monthNum -= 12;
				}
			} else {
				minNum = min;
			}
		} else {
			if (strSplit.length > 7)
				dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + timeStr + strSplit[7];
			else {
				dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + timeStr;
			}
			return dTime;
		}
		String minStr = (minNum < 10 ? "0" + minNum : Integer.valueOf(minNum)).toString();
		if (strSplit.length > 9)
			dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + timeStr + strSplit[7]
					+ minStr + strSplit[9];
		else {
			dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + timeStr + strSplit[7]
					+ minStr;
		}
		return dTime;
	}

	public static String tenDays(String str) {
		int year = Integer.parseInt(str.substring(0, 4));
		int month = Integer.parseInt(str.substring(4, 6));
		int day = Integer.parseInt(str.substring(6, 8));

		int monthDay = 0;
		int lastTenDay = 0;

		if ((day < 10) && (month == 1)) {
			year--;
			month = 12;
			lastTenDay = calculateLastTenDay(year, monthDay);
			day = 31 - (lastTenDay - day);
		} else if (day < 10) {
			month--;
			monthDay = calculateDays(year, month);
			lastTenDay = calculateLastTenDay(year, monthDay);
			day = monthDay - (lastTenDay - day);
		} else {
			lastTenDay = calculateLastTenDay(year, monthDay);
			day = lastTenDay - 10;
		}

		String monthStr = (String) (month < 10 ? "0" + month : Integer.valueOf(month));

		return year + monthStr + day;
	}

	public  static int calculateLastTenDay(int year, int month) {
		int lastTenDay = 0;
		if ((month == 1) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10)
				|| (month == 12))
			lastTenDay = 11;
		else if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
			lastTenDay = 10;
		} else if ((year / 4 == 0) || ((year / 100 == 0) && (year / 400 != 0)))
			lastTenDay = 9;
		else {
			lastTenDay = 8;
		}

		return lastTenDay;
	}

	public static int calculateDays(int year, int month) {
		int monthDay = 0;
		if ((month == 0) || (month == 1) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10)
				|| (month == 12))
			monthDay = 31;
		else if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
			monthDay = 30;
		} else if ((year / 4 == 0) || ((year / 100 == 0) && (year / 400 != 0)))
			monthDay = 29;
		else {
			monthDay = 28;
		}

		return monthDay;
	}
}
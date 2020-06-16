package com.htht.job.executor.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class RedLineMatchTime {

    /**
     * 匹配日期时间{yyyy}{MM}{dd},{yyyy}{MM}{dd}{HH},{yyyy}{MM}{dd}{HH}{Mi}
     *
     * @return
     */
    public static String match(String str) {
        String dTime = "";

        Calendar c = Calendar.getInstance();

        String str1 = str.replace("}", ",").replace("{", ",").substring(1, str.length());
        String[] strSplit = str1.split(",");
        //转换年
        if (strSplit.length > 0) {
            String yearAll = strSplit[0].substring(0, strSplit[0].length());
            String yearSub = yearAll.substring(4);
            if (yearAll.length() > 4) {
                if (yearSub.contains("+")) {
                    yearSub = yearSub.replace("+", "");
                }
                c.add(Calendar.YEAR, Integer.parseInt(yearSub));
            }
        }
//		//转换月
        if (strSplit.length > 2) {
            String monthAll = strSplit[2].substring(0, strSplit[2].length());
            String monthSub = monthAll.substring(2, monthAll.length());
            if (monthAll.length() > 2) {
                if (monthSub.contains("+")) {
                    monthSub = monthSub.replace("+", "");
                }
                c.add(Calendar.MONTH, Integer.parseInt(monthSub));
            }
        }
//		转换日
        if (strSplit.length > 4) {
            String dayAll = strSplit[4].substring(0, strSplit[4].length());
            String daySub = dayAll.substring(2, dayAll.length());
            if (dayAll.length() > 2) {
                if (daySub.contains("+")) {
                    daySub = daySub.replace("+", "");
                }
                c.add(Calendar.DATE, Integer.parseInt(daySub));
            }
        }

//		//转换时
        if (strSplit.length > 6) {
            String timeAll = strSplit[6].substring(0, strSplit[6].length());
            String timeSub = timeAll.substring(2, timeAll.length());
            if (timeAll.length() > 2) {
                if (timeSub.contains("+")) {
                    timeSub = timeSub.replace("+", "");
                }
                c.add(Calendar.HOUR, Integer.parseInt(timeSub));
            }
        }

//		//转换分
        if (strSplit.length > 8) {
            String minAll = strSplit[8].substring(0, strSplit[8].length());
            String minSub = minAll.substring(2, minAll.length());
            if (minAll.length() > 2) {
                if (minSub.contains("+")) {
                    minSub = minSub.replace("+", "");
                }
                c.add(Calendar.MINUTE, Integer.parseInt(minSub));
            }
        }

        int yearNum = c.get(Calendar.YEAR);        //年
        int monthNum = c.get(Calendar.MONTH) + 1;    //月
        int dayNum = c.get(Calendar.DAY_OF_MONTH);    //日
        int hourNum = c.get(Calendar.HOUR_OF_DAY); //时
        int minNum = c.get(Calendar.MINUTE);        //分

        String monthStr = (monthNum < 10 ? "0" + monthNum : monthNum).toString();
        String dayStr = (dayNum < 10 ? "0" + dayNum : dayNum).toString();

        if (strSplit.length <= 6) {
            //YYYYMMDD的情况
            if (strSplit.length > 5) {
                dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5];
            } else {
                dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr;
            }
            return dTime;
        } else if (strSplit.length <= 8) {
            //YYYYMMDDHH的情况
            String HourStr = (hourNum < 10 ? "0" + hourNum : hourNum).toString();
            if (strSplit.length > 7) {
                dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + HourStr + strSplit[7];
            } else {
                dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + HourStr;
            }
            return dTime;
        } else {
            //YYYYMMDDHHMi的情况
            String HourStr = (hourNum < 10 ? "0" + hourNum : hourNum).toString();
            String minStr = (minNum < 10 ? "0" + minNum : minNum).toString();
            if (strSplit.length > 9) {
                dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + HourStr + strSplit[7] + minStr + strSplit[9];
            } else {
                dTime = yearNum + strSplit[1] + monthStr + strSplit[3] + dayStr + strSplit[5] + HourStr + strSplit[7] + minStr;
            }
            return dTime;
        }

    }

    /**
     * COTM  Cycle of Ten Minute 10min周期
     * COOH  Cycle of One Hour	实时周期合成产品
     * COOD  Cycle of One Day	 日周期合成产品
     * COFD   Cycle of Five Day  侯周期合成产品
     * COSD	Cycle of Seven Days周周期合成产品
     * COTD	Cycle of Ten Days	 旬周期合成产品
     * COAM	Cycle of a Month	 月周期合成产品
     * COAQ	Cycle of a Quarter	 季周期合成产品
     * COAY	Cycle of a Year	 年周期合成产品
     *
     * @param str
     * @param cycle
     * @return
     */
    public static String matchIssue(String str, String cycle) {
        Calendar calendar = Calendar.getInstance();
        String issue = null;
        if (str.indexOf("{") > -1) {
            String str1 = str.replace("}", ",").replace("{", "");
            String[] strSplit = str1.split(",");
            int index = strSplit.length;
            if (index > 0 && strSplit[0].length() > 4) {
                int year = Integer.parseInt(strSplit[0].trim().substring(4));
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
            Date date = RedLineDateUtil.getDate(str, "yyyyMMddHHmm");
            calendar.setTime(date);
        }
        if ("COAY".equals(cycle)) {//年周期
            issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyy") + "01010000";
        } else if ("COAQ".equals(cycle)) {//季产品
            int month = calendar.get(Calendar.MONTH) + 1;
            if (month == 12 || month < 3) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyy") + "01010000";
            } else if (month < 6) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyy") + "03010000";
            } else if (month < 9) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyy") + "06010000";
            } else {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyy") + "09010000";
            }
        } else if ("COAM".equals(cycle)) {//月产品
            issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "010000";
        } else if ("COHM".equals(cycle)) {//16天产品				从每年1月1日开始，每次加16天
            int year = calendar.get(Calendar.YEAR);

            Calendar c = Calendar.getInstance();
            c.set(year, 1, 1);
            while (c.before(calendar)) {
                c.add(Calendar.DAY_OF_YEAR, 16);
            }
            issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMMdd") + "0000";
        } else if ("COTD".equals(cycle)) {//旬周期合成产品
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day < 11) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "100000";
            } else if (day < 21) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "200000";
            } else {
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMMdd") + "0000";
            }
        } else if ("COSD".equals(cycle)) {// COSD	Cycle of Seven Days周周期合成产品
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day < 8) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "010000";
            } else if (day < 15) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "080000";
            } else if (day < 22) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "150000";
            } else {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "220000";
            }
        } else if ("COFD".equals(cycle)) {//COFD   Cycle of Five Day  侯周期合成产品
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (day < 6) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "010000";
            } else if (day < 11) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "060000";
            } else if (day < 16) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "110000";
            } else if (day < 21) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "160000";
            } else if (day < 26) {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "210000";
            } else {
                issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMM") + "260000";
            }

        } else if ("COOD".equals(cycle)) {//COOD  Cycle of One Day	 日周期合成产品
            issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMMdd") + "0000";
        } else if ("COOH".equals(cycle)) {//COOH  	实时周期合成产品
            issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHHmmss");
        } else if ("COTM".equals(cycle)) {//COTM  Cycle of Ten Minute 10min周期
            int minute = calendar.get(Calendar.MINUTE) / 10 * 10;
            issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHH") + minute + "00";
        }


        return issue;
    }

    public static String matchIssue(String str) {
        Calendar calendar = Calendar.getInstance();
        String issue = null;
        if (str.indexOf("{") > -1) {
            String str1 = str.replace("}", ",").replace("{", "");
            String[] strSplit = str1.split(",");
            int index = strSplit.length;
            if (index > 0 && strSplit[0].length() > 4) {
                int year = Integer.parseInt(strSplit[0].trim().substring(4));
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
            issue = RedLineDateUtil.formatDateTime(calendar.getTime(), "yyyyMMddHHmm");
        } else {
            for (int i = str.length(); i < 12; i++) {
                str += "0";
            }
            issue = str;
        }
        return issue;
    }

    public static void main(String[] args) throws ParseException {
        String str = "999{yyyy-5}年{MM-1}月{dd+3}日{hh+3}:{mm+9}分";
//		String str = "{yyyy}{MM}{dd}";
//		MatchTime m = new MatchTime();
//		String 	dateStr = m.match(str);
//		String 	dateStr12 = "1234";
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		Date date2 = sdf.parse(dateStr);
////		System.out.println(m.tenDays("20140301"));
//		System.out.println(sdf.format(date2));
//		System.out.println(dateStr);
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
        //补零
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
     * @param year  年
     * @param month 月
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

    /**
     * 地面旬值资料使用：根据当前时间得出上一个旬的时间，例如20160522，换算之后为20160502，其中最后的02表示是中旬，
     */
    public String changeDateOfPeriod(String str) {
        int year = Integer.parseInt(str.substring(0, 4));
        int month = Integer.parseInt(str.substring(4, 6));
        int day = Integer.parseInt(str.substring(6, 8));
        String newDay = "";
        if (day < 11 && month == 1) { //如果满足1月份上旬日期，则可以获取去年12月份下旬数据
            year = year - 1;
            month = 12;
            newDay = "03";
        } else if (day < 11) { //除1月份其他月份的上旬日期，则可以获取上个月的下旬数据
            month = month - 1;
            newDay = "03";
        } else if (day < 21) { //如果处于中旬日期，则可以获取当月上旬数据
            newDay = "01";
        } else {
            newDay = "02";
        }
        String monthStr = String.valueOf(month < 10 ? "0" + month : month);
        return String.valueOf(year) + monthStr + newDay;
    }

    /**
     * 中国地面月值资料使用：根据当月获取上一个月的格式例如：20150822---->20150701
     */
    public String changeDateOfMonth(String str) {
        int year = Integer.parseInt(str.substring(0, 4));
        int month = Integer.parseInt(str.substring(4, 6));
        if (month == 1) {
            year = year - 1;
            month = 12;
        } else {
            month = month - 1;
        }
        String monthStr = String.valueOf(month < 10 ? "0" + month : month);
        return String.valueOf(year) + monthStr + "01";
    }
}

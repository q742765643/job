package com.htht.job.executor.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RedLineDateUtil {
	@SuppressWarnings({ "deprecation", "unused" })
	public static String getIssue(Date date){
		SimpleDateFormat format =  new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH);
	    int day = calendar.get(Calendar.DATE);

		System.out.println(day);
		if(day>0&&day<=10){
			calendar.set(Calendar.DAY_OF_MONTH,0);
		}
		if(day>10&&day<=20){
			calendar.set(year, month, 10);
		}
		if(day>20){
			calendar.set(year, month, 20);
		}
		return  format.format(calendar.getTime());
	}
	
	@SuppressWarnings("unused")
	public static String getYear(Date date){
		SimpleDateFormat format =  new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year=calendar.get(Calendar.YEAR);
		return  String.valueOf(year);
	}

	 public static void main(String[] args){
		 //DateUtil.getDatelist("yyDateUtil.getHistoryDatelist("2018-01-01","2018-02-02","yyyy/yyyy/MM");yy/rr","5");
		 //DateUtil.getHistoryDatelist("2018-01-01","2018-02-02","yyyy/yyyy/MM");
		 List<Date> list=DateUtil.getHistoryDatelist("2018-01-01","2018-02-02");
		 List<String> dateList=new ArrayList<String>();
		 Set<String> set=new HashSet<String>();
		 for(int i=0;i<list.size();i++){
			 String date=DateUtil.getIssue(list.get(i));
			 set.add(date);
		 }
		 dateList.addAll(set);
		 System.out.print(dateList);
		/* String str="20180120";
		 SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
		 try {
			Date birthday = sdf.parse(str);
			 getIssue(birthday);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	 }
    //获取前几天所有日期
	public static List<List<String>> getDatelist(String format,String days){
	 	int day=Integer.parseInt(days);
		List<String> list=new ArrayList<String>();
		List<String> datelist=new ArrayList<String>();
		List<List<String>> zlist=new ArrayList<List<String>>();
		String formatRule=format;
		formatRule=formatRule.replace("rr","'rr'");
		formatRule=formatRule.replace("{","'");
		formatRule=formatRule.replace("}","'");
		SimpleDateFormat ysdf =  new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf =  new SimpleDateFormat(formatRule);
		for (int i=0;i<day;i++) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -i);
			String date = sdf.format(calendar.getTime());
			String date1 = ysdf.format(calendar.getTime());
			datelist.add(date1);
			date=date.replace("rr",getDays(calendar));
			list.add(date);
		}
		zlist.add(list);
		zlist.add(datelist);
		return zlist;
	}
	public static List<List<String>> getHistoryDatelist(String startDay,String endDay,String format){
		List<String> list = new ArrayList<String>();
		List<String> datelist=new ArrayList<String>();
		List<List<String>> zlist=new ArrayList<List<String>>();
		try {

			String formatRule=format;
			formatRule=formatRule.replace("rr","'rr'");
			formatRule=formatRule.replace("{","'");
			formatRule=formatRule.replace("}","'");

			SimpleDateFormat ysdf =  new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf =  new SimpleDateFormat(formatRule);
			Date d1=ysdf.parse(startDay);
			Date d2=ysdf.parse(endDay);
			Calendar calendar = Calendar.getInstance();
            calendar.setTime(d1);
			while (calendar.getTime().before(d2)||calendar.getTime().equals(d2)){
                String date = sdf.format(calendar.getTime());
				String date1 = ysdf.format(calendar.getTime());
				datelist.add(date1);
				date=date.replace("rr",getDays(calendar));
				list.add(date);
                calendar.add(Calendar.DATE, +1);
            }
			zlist.add(list);
			System.out.print(list);
			zlist.add(datelist);
		} catch (ParseException e) {
			 throw new RuntimeException();
		}
		return zlist;
	}
	public static List<Date> getHistoryDatelist(String startDay,String endDay){
		List<Date> list = new ArrayList<Date>();
		try {
			SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat ysdf =  new SimpleDateFormat("yyyy-MM-dd");
			Date d1=ysdf.parse(startDay);
			Date d2=ysdf.parse(endDay);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d1);
			while (calendar.getTime().before(d2)||calendar.getTime().equals(d2)){
				list.add(calendar.getTime());
				calendar.add(Calendar.DATE, +1);
			}
		} catch (ParseException e) {
			 throw new RuntimeException();
		}
		return list;
	}
	public static String getDays(Calendar calendar){
		int i= calendar.get(Calendar.DAY_OF_YEAR);
		String r;
		if(i<10){
			 r= "0"+String.valueOf(i);
		}else{
			 r= String.valueOf(i);
		}
		return r;
	}

	public static Date getDate(String time, String formate) {
		Date date = null;
		try {
			date = (new SimpleDateFormat(formate)).parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String formatDateTime(Date time, String formate) {
		return (new SimpleDateFormat(formate)).format(time);
	}

}

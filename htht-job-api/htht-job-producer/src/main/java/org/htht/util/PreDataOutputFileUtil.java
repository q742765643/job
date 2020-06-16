package org.htht.util;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;

/**
 * 
 * @author yuguoqing
 * @Date 2018年4月13日 下午3:05:57
 *
 *
 */
public class PreDataOutputFileUtil
{

	/**
	 * 获得一个相对路径。相对路径格式为：“卫星/传感器/数据日期/”，其中的卫星和传感器信息从任务名中获得，数据日期从文件名中获得。
	 * 
	 * @param sch
	 * @param inputFilePath
	 * @return
	 * @throws ParseException
	 */
	public static String getSubDir(String schName, String inputFilePath)
	{

		String dateStr = "";
		try
		{
			dateStr = DataTimeHelper.getDataDate(inputFilePath);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}

		String[] ssr = schName.split("_");

		String satellite = ssr[0];
		String sensor = ssr[1];

		String retDir = File.separator + satellite + File.separator + sensor + File.separator + dateStr;
		return retDir;
	}

	public static String getSubDirByDay(String schName, String inputFilePath)
	{
		String[] ssr = schName.split("_");

		String satellite = ssr[0];
		String sensor = ssr[1];

		String retDir = File.separator + satellite + File.separator + sensor + File.separator
				+ getCurrentYearMonthDay();
		return retDir;
	}

	public static String getCurrentYearMonthDay()
	{
		Calendar c = Calendar.getInstance();
		String yearStr = c.get(Calendar.YEAR) + "";
		Integer month = c.get(Calendar.MONTH)+1;
		String monthStr = month< 10 ? "0" + month : month + "";
		String dayStr = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH)
				: c.get(Calendar.DAY_OF_MONTH) + "";
		return yearStr + File.separator + monthStr + File.separator + dayStr;
	}

	public static void main(String[] args)
	{
		String schName = "FY3B_VIRRX_1000";
		System.out.println(PreDataOutputFileUtil.getSubDir(schName, "FY3B_VIRRX_GBAL_L1_20160602_0020_1000M_MS.HDF"));
		System.out.println(PreDataOutputFileUtil.getCurrentYearMonthDay());
	}

}

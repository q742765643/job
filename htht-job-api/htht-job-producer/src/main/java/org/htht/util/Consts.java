package org.htht.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public interface Consts
{
	// private static final Configuration config =
	// ConfigurationManager.getConfiguration("jHdht");
	class PreDateType
	{
		public static String DAY_RANGE_INT = "1";// 最近N天
		public static String DAY_RANGE_STR = "2";// 时间范围
	}

	class DateForMat
	{
		public static String yyMMddFormat = "yyyyMMdd";
		public static String yyMMddFormatSplited = "yyyy-MM-dd";
	}

	class DateFormatMap
	{
		/*static
		{
			ResourceBundle resource = ResourceBundle.getBundle("com/test/config/config");
			String key = resource.getString("keyWord");
		}*/
		
		public static Map<String, String> dateSatelliteMap = new HashMap<String, String>();
		static
		{
			dateSatelliteMap.put("FY3A", "yyyyMMdd_HHmm");
			dateSatelliteMap.put("FY3B", "yyyyMMdd_HHmm");
			dateSatelliteMap.put("FY3C", "yyyyMMdd_HHmm");
			dateSatelliteMap.put("FY4A", "yyyyMMddHHmmss");
			dateSatelliteMap.put("AQUA", "yyyy_MM_dd_HH_mm");
			dateSatelliteMap.put("TERRA", "yyyyMMddHHmm");
			dateSatelliteMap.put("NOAA18", "yyyy_MM_dd_HH_mm");
			dateSatelliteMap.put("EOSA", "yyyy_MM_dd_HH_mm");
			dateSatelliteMap.put("EOST", "yyyy_MM_dd_HH_mm");
			
		}
	}
	
	class FileSeparator
	{
		public static String FILE_SEPARATOR = "/";
	}
}

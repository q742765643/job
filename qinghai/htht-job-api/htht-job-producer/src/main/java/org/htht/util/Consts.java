package org.htht.util;

import java.util.HashMap;
import java.util.Map;

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
		public static String yyMMddHHmmFormat = "yyyyMMddHHmm";
		public static String yyMMddFormatSplited = "yyyy-MM-dd";
		public static String yyMMddHHmmssFormatSplited = "yyyy-MM-dd HH:mm:ss";
	}

	
	class DayOrNight
	{
		public static String DAY = "D";
		public static String NIGHT = "N";
	}
	class PreDateOutType
	{
		public static String PROJECTION = "projection";
		public static String MOSAIC = "mosaic";
		public static String BLOCK = "block";
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
	
	class NcProduct{
		public static  String ADFP = "ADFP_nc";
		public static  String SH = "SH_nc";
		public static  String SRH = "SRH_nc";
		public static  String ST = "ST_nc";

		public static  String[] NCPRODUCT = {"ADFP","SH","SRH","ST"};
		public static  String[] NCPRODUCTKEYS = {"ADFPKEY","SHKEY","SRHKEY","STKEY"};
		
		
		public static  String[] ADFPKEY = {"TMP","PRS","SHU","WIN","SSRA","PRE"};
		public static  String[] SHKEY = {"SM000005", "SM000010", "SM010040", "SM040100", "SM100200"};
		public static  String[] SRHKEY = {"RSM000010", "RSM000020", "RSM000050"};
		public static  String[] STKEY = {"GST005", "GST010", "GST040", "GST005", "GST100", "GST200"};
		
		public static Map<String, Map<String,String[]>> ncProductMap = new HashMap<String, Map<String,String[]>>();
		static{
			ncProductMap.put("", null);
		}
		
	}
}

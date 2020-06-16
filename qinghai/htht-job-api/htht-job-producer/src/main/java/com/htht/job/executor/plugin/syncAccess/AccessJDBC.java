package com.htht.job.executor.plugin.syncAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class AccessJDBC {

	/**
	 * 读取文件access
	 * 
	 * @param filePath
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static List<Map<String,String>> readFileACCESS(String filePath, String date) {
		List<Map<String,String>> maplist = new ArrayList<Map<String,String>>();
		Properties prop = new Properties();
		prop.put("charSet", "gb2312");// 这里是解决中文乱码
		prop.put("user", "kys");
		prop.put("password", "kys");
		String url = "jdbc:access:///" + filePath; // 文件地址
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.hxtt.sql.access.AccessDriver");
			Connection conn = DriverManager.getConnection(url, prop);
			stmt = (Statement) conn.createStatement();
			rs = stmt.executeQuery("select * from tstable where fbday >='"
					+ date + "'  order by fbday asc");
			ResultSetMetaData data=rs.getMetaData();

			while (rs.next()) {
				HashMap<String,String> map = new LinkedHashMap<String,String>();
				for (int i = 1; i <= data.getColumnCount(); i++) {
					String columnName = data.getColumnName(i); // 列名
					String columnValue = rs.getString(i);
					System.out.println(columnName);
					System.out.println(columnValue);
					map.put(columnName, columnValue);
				}
				maplist.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return maplist;
	}
}

package com.htht.job.executor.hander.cimiss.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RestUtil {
	private final int timeoutInMilliSeconds = 120000;

	public String getRestData(String params) {
		StringBuilder retStr = new StringBuilder();
		URL url = null;
		BufferedReader reader = null;
		try {
			url = new URL(params);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(timeoutInMilliSeconds);
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = reader.readLine();
			int i = 0;
			while (line != null) {
				i++;
				System.out.println(i + " : " + line.length());
				retStr.append(line).append("\r\n");
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception ex1) {
			ex1.printStackTrace();
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return retStr.toString();
	}

	public String setRestData(String params, String inString) {
		StringBuilder retStr = new StringBuilder();
		URL url = null;
		BufferedReader reader = null;

		params = params + "&instring=" + inString;
		try {
			url = new URL(params);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(timeoutInMilliSeconds);
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				retStr.append(line).append("\r\n");
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception ex1) {
			ex1.printStackTrace();
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return retStr.toString();
	}
}

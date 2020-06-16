package org.htht.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class ConnectionUtils {
	public static String getUTFURLBody(String url) throws IOException {

		String sCurrentLine;
		StringBuffer sTotalString = new StringBuffer();
		HttpURLConnection l_connection = null;
		java.io.InputStream l_urlStream = null;

		try {
			java.net.URL l_url = new java.net.URL(url);
			l_connection = (HttpURLConnection) l_url.openConnection();
			l_connection.connect();
			l_urlStream = l_connection.getInputStream();
			java.io.BufferedReader l_reader = new java.io.BufferedReader(new java.io.InputStreamReader(l_urlStream));
			while ((sCurrentLine = l_reader.readLine()) != null){
				sTotalString.append(sCurrentLine);
			}
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (l_urlStream != null) l_urlStream.close();
			if(null != l_connection){
				l_connection.disconnect();
			}
		}
		return sTotalString.toString();
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(ConnectionUtils.getUTFURLBody("http://www.baidu.com"));
	}
	

}

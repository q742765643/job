package org.htht.util;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtil {
	private AppUtil() {
	}

	private static AppUtil appuitls = new AppUtil();

	public static String getApplicationAbsolutePath() {
		URL l = appuitls.getClass().getClassLoader().getResource("/");
		String path = l.getPath();
		return path.substring(1, path.indexOf("WEB-INF") - 1);
	}

	public static Date Format(String dStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(dStr);

		} catch (Exception pe) {
			pe.printStackTrace();
		}
		return null;
	}
}

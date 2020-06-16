package org.htht.util;

/**
 * @version 0.9 2005-9-16 17:46:48
 * @author
 */
public class UtilGenerator {
	/**
	 * exemple: transBZEName("Sys_user_name") return sysUserName
	 * 
	 * @param eName
	 * @return
	 */
	public static String transBZEName(String eName) {
		eName = eName.toLowerCase();
		if (eName.endsWith("_"))
			eName = eName.substring(0, eName.length() - 1);
		int index = 0;
		while (true) {
			index = eName.indexOf("_");
			if (index == -1)
				break;
			eName = eName.substring(0, index)
					+ eName.substring(index + 1, index + 2).toUpperCase()
					+ eName.substring(index + 2);
		}
		return eName;
	}

	/**
	 * exemple: transMDEName("sysUserName") return Sys_User_Name
	 * @param eName
	 * @return
	 */
	public static String transMDEName(String eName) {
		for (int i = 0; i < eName.length(); i++) {
			char c = eName.charAt(i);
			if (Character.isUpperCase(c)) {
				eName = eName.substring(0, i) + "_" + eName.substring(i);
				i++;
			}
		}
		return eName;
	}


	/**
	 * ����ĸ��д
	 * 
	 * @param eName
	 * @return
	 */
	public static String transHeadUpperCase(String eName) {
		String str = eName.substring(0, 1).toUpperCase() + eName.substring(1);
		return str;
	}
	/**
	 * ����ĸСд
	 * 
	 * @param eName
	 * @return
	 */
	public static String transHeadLowerCase(String eName) {
		String str = eName.substring(0, 1).toLowerCase() + eName.substring(1);
		return str;
	}
	/**
	 * ��package��ת����·��
	 * 
	 * @param packageName
	 * @return
	 */
	public static String transPackagePath(String packageName) {
		String path = "";
		path = packageName.replace('.', '/');
		path += "/";
		return path;
	}

	public static int transMetadateType(String type) {
		if(type.equalsIgnoreCase("INT"))
			return 4;
		else if(type.equalsIgnoreCase("NUMBER"))
			return 5;
		else if(type.equalsIgnoreCase("DATE"))
			return 3;
		else if(type.equalsIgnoreCase("LONG"))
			return 7;
		else if(type.equalsIgnoreCase("CHAR"))
			return 2;
		else
			return 1;
	}

}

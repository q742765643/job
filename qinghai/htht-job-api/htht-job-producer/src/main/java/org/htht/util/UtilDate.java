package org.htht.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * ���ڴ���)չ����.
 *
 *
 * @author <a href="mailto:ouzf@vip.sina.com">���ȷ�</a>
 * @since BASE 0.1
 */
public class UtilDate {
	/**
	 * ����תΪ����.
	 * ������ת���ɺ��� dateToChinese("2002/01/01","/") out ���������һ��һ��
	 * ��dateToChinese("2002-01-01","-") out ���������һ��һ��
	 *
	 * @param sDate �����ַ�
	 * @param DelimeterChar �ָ��
	 * @return ���������ַ�
	 * @since BASE 0.1
	 */

	public static String dateToChinese(String sDate, String DelimeterChar) {
		String tmpArr[] = sDate.split(DelimeterChar);
		String dArr[] = { "��", "һ", "��", "��", "��", "��", "��", "��", "��",
				"��" };
		for (int i = 0; i < 10; i++) {
			Integer x = new Integer(i);
			String temp = x.toString();
			tmpArr[0] = tmpArr[0].replaceAll(temp, dArr[i]);
		}
		tmpArr[0] = tmpArr[0] + "��";
		if (tmpArr[1].length() == 1) {
			tmpArr[1] = dArr[Integer.parseInt(tmpArr[1])] + "��";
		} else {
			if (tmpArr[1].substring(0, 1).equals("0")) {
				tmpArr[1] = dArr[Integer.parseInt(tmpArr[1].substring(tmpArr[1]
						.length() - 1, tmpArr[1].length()))]
						+ "��";
			} else {
				tmpArr[1] = "ʮ"
						+ dArr[Integer.parseInt(tmpArr[1].substring(tmpArr[1]
								.length() - 1, tmpArr[1].length()))] + "��";
				tmpArr[1] = tmpArr[1].replaceAll("��", "");
			}

		}
		if (tmpArr[2].length() == 1) {
			tmpArr[2] = dArr[Integer.parseInt(tmpArr[2])] + "��";
		} else {
			if (tmpArr[2].substring(0, 1).equals("0")) {
				tmpArr[2] = dArr[Integer.parseInt(tmpArr[2].substring(tmpArr[2]
						.length() - 1, tmpArr[2].length()))]
						+ "��";
			} else {
				tmpArr[2] = dArr[Integer.parseInt(tmpArr[2].substring(0, 1))]
						+ "ʮ"
						+ dArr[Integer.parseInt(tmpArr[2].substring(tmpArr[2]
								.length() - 1, tmpArr[2].length()))] + "��";
				tmpArr[2] = tmpArr[2].replaceAll("��", "");
			}
		}
		return tmpArr[0] + tmpArr[1] + tmpArr[2];
	}

	/**
	 * ����ת���ɸ�ʽ���ַ�.
	 *
	 *
	 * @param date �����ַ�
	 * @param format ��Ч�����ڸ�ʽ,�磺yyyy-MM-dd
	 * @return String ��ʽ���ַ�
	 * @since BASE 0.1
	 */
	public static String dateToString(java.util.Date date, String format) {
		java.text.SimpleDateFormat df = new SimpleDateFormat(format);

		return df.format(date);
	}

	/**
	 * ��ʽ����������,ת����"-"�ָ�����ڸ�ʽ.
	 * <b>ע�⣺</b>�ֲ�֧�ִ�ʱ�����������ת��.
	 *
	 * @param strDate �������ڣ��磺2000��10��01��
	 * @return String "-"�ָ�����ڸ�ʽ,�磺2000-10-01
	 * @since BASE 0.1
	 */
	public static String formatChineseDate(String strDate) {
		strDate = strDate.replaceAll("[��]", "");

		if (strDate.endsWith("��"))
			strDate = strDate.substring(strDate.length() - 1, strDate.length());
		if (strDate.endsWith("��"))
			strDate = strDate.substring(strDate.length() - 1, strDate.length());

		return strDate.replaceAll("[��,��]", "-");
	}

	/**
	 * ����������ת�������������գ�ע��Ŀǰֻ֧��"-"
	 * @param strDate
	 * @return
	 */
	public static String formatNumberDate(String strDate) {
		strDate = strDate.replaceFirst("-", "��");
		strDate = strDate.replaceFirst("-", "��");
		return strDate + "��";
	}

	/**
	 * ����ַ�������ڸ�ʽ.
	 * ֧�ָ�ʽ��
	 * <li>2000��10��12�� 12:11:30</li> ��֧��!
	 * <li>2000/10/12 12:11:30</li>
	 * <li>2000-10-12 12:11:30</li>
	 * <li>2000.10.12 12:11:30</li>
	 *
	 * @param strDate ������Ч�����ڴ�
	 * @return String ���ڸ�ʽ����
	 * @since BASE 0.1
	 */
	public static String getDateFormat(String strDate) {
		String dateToken = "-";
		String timeToken = ":";

		try {
			if (strDate.indexOf("-") >= 0) {
				dateToken = "-";
			} else if (strDate.indexOf("/") >= 0) {
				dateToken = "/";
			} else if (strDate.indexOf(".") >= 0) {
				dateToken = ".";
			} else {
				return null;
			}

			String date = strDate.toLowerCase().trim();
			int pos = date.indexOf(" ");
			String day = "";
			String time = "";

			if (pos > 0) {
				day = date.substring(0, pos).trim();
				time = date.substring(pos + 1).trim();
			} else {
				day = date.trim();
			}

			StringBuffer buf = new StringBuffer();
			//�ֽ�����
			String[] parseDay = UtilString.stringToArray(day, dateToken);

			for (int i = 0; i < parseDay.length; i++) {

				if (i == 0) {
					buf
							.append("yyyy".substring(0, parseDay[i].trim()
									.length()));
				}
				if (i == 1) {
					buf.append("MM".substring(0, parseDay[i].trim().length()));
				}
				if (i == 2) {
					buf.append("dd".substring(0, parseDay[i].trim().length()));
				}
				if (i < parseDay.length - 1)
					buf.append(dateToken);
			}

			//�ֽ�ʱ��
			if (!(time.equals("") || time == null)) {
				buf.append(" ");
				String[] parseTime = UtilString.stringToArray(time, timeToken);
				for (int i = 0; i < parseTime.length; i++) {
					if (i == 0) {
						buf.append("HH".substring(0, parseTime[i].trim()
								.length()));
					}
					if (i == 1) {
						buf.append("mm".substring(0, parseTime[i].trim()
								.length()));
					}
					if (i == 2) {
						buf.append("ss".substring(0, parseTime[i].trim()
								.length()));
					}
					if (i < parseTime.length - 1)
						buf.append(timeToken);
				}
			}

			return buf.toString().trim();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ���ĳһ���ڶ�Ӧ������������һ��.
	 *
	 * @param date ����
	 * @return Date �����������
	 * @since BASE 0.1
	 */
	public static java.util.Date getEndWeekDay(java.util.Date date) {
		java.util.Date dtRet = new java.util.Date();

		java.util.GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(GregorianCalendar.DATE, 6 - gc
				.get(GregorianCalendar.DAY_OF_WEEK));
		dtRet = gc.getTime();

		return dtRet;
	}

	/**
	 * ���ĳһ���ڶ�Ӧ������һ����һ��.
	 *
	 * @param date ����
	 * @return Date ����һ������
	 * @since BASE 0.1
	 */
	public static java.util.Date getFirstWeekDay(java.util.Date date) {
		java.util.Date dtRet = new java.util.Date();

		java.util.GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(GregorianCalendar.DATE, (-1)
				* (gc.get(GregorianCalendar.DAY_OF_WEEK) - 2));
		dtRet = gc.getTime();

		return dtRet;
	}

	/**
	 * �ж��Ƿ�Ϊ��Ч�����ڸ�ʽ.
	 *
	 * @param strDate ���ڴ�
	 * @return boolean true=��Ч�����ڸ�ʽ
	 * @since BASE 0.1
	 */
	public static boolean isDate(String strDate) {
		java.util.Date format = toDate(strDate);
		if (format == null)
			return false;
		else
			return true;
	}

	/**
	 * �����ڸ�ʽ���ַ�,ת����Ŀ�ĸ�ʽ���ַ�.
	 *
	 * @param oldStr
	 * @param oldFormat
	 * @param newFormat
	 * @return
	 */
	public static String formatDateString(String oldStr, String oldFormat,
			String newFormat) {
		Date date = toDate(oldStr, oldFormat);
		if (date == null)
			return null;
		return dateToString(date, newFormat);
	}

	/**
	 * ����ĳһ����֮ǰ��֮�������.
	 *
	 *
	 * @param date �ο�����
	 * @param days ֮ǰ��֮�������,����Ϊ��ֵ
	 * @return ����������
	 * @since BASE 0.1
	 */
	public static Date relativeDate(Date date, int days) {

		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, days);
		return c.getTime();
	}

	/**
	 * ��ݵ�ǰ������������õ��������.
	 *
	 *
	 * @param days �������
	 * @return Date ����������
	 * @since BASE 0.1
	 */
	public static Date relativeDate(int days) {
		return relativeDate(new java.util.Date(), days);
	}

	/**
	 *  ��ݵ�ǰ�������·ݼ��õ��������.
	 *
	 *
	 * @param months �������
	 * @return Date ����������
	 * @since BASE 0.1
	 */
	public static Date relativeMonth(int months) {
		return relativeDate(new java.util.Date(), months);
	}

	/**
	 * ��ݵ�ǰ�������·ݼ��õ��������.
	 *
	 *
	 * @param date ����
	 * @param months �������
	 * @return ����������
	 * @since BASE 0.1
	 */
	public static Date relativeMonth(java.util.Date date, int months) {
		java.util.GregorianCalendar gc = new java.util.GregorianCalendar();
		gc.setTime(date);
		gc.add(Calendar.DAY_OF_MONTH, months);
		return gc.getTime();
	}

	/**
	 * ���ַ�ת������������.
	 * ����ַ�Ϊ�Ƿ��ַ��򷵻�null
	 * ��ʶ��ĸ�ʽΪ��
	 * ʹ�����ı�׼��ʽ������������Сʱ����ĸ�ʽ�磺yyyy-mm-dd hh:mm:ss,yyyy/mm/dd
	 *
	 * @param strDate ��Ч�������ַ�
	 * @return Date ת�������������
	 * @since BASE 0.1
	 */

	public static java.util.Date toDate(String strDate) {
		String format = getDateFormat(strDate);
		if (format == null)
			return null;
		else
			return toDate(strDate, format);
	}

	/**
	 * �ַ�ת��������.
	 *
	 * @param strDate ����
	 * @param strFormat ��ʽ
	 * @return Date ת���������
	 * @since BASE 0.1
	 */
	public static java.util.Date toDate(String strDate, String strFormat) {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
			sdf.setLenient(false);
			return sdf.parse(strDate);
		} catch (ParseException e) {
			//System.out.println("toDate error");
			return null;
		}
	}

	/**
	 * �ַ�ת��������.
	 *
	 * @param strDate
	 * @param strFormat
	 * @param Lenient �Ƿ����?��ʵ�ʵ����ڴ���
	 * @return
	 */
	public static java.util.Date toDate(String strDate, String strFormat,
			boolean lenient) {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
			sdf.setLenient(lenient);
			return sdf.parse(strDate);
		} catch (ParseException e) {
			//System.out.println("toDate error");
			return null;
		}
	}

	/**
	 * �ַ�ת����sql����.
	 *
	 * @param strDate String ����
	 * @return Date ת�������������
	 * @since BASE 0.1
	 */
	public static java.sql.Date toSqlDate(String strDate) {
		java.util.Date utilDate = toDate(strDate);
		if (utilDate == null)
			return null;
		return new java.sql.Date(utilDate.getTime());

	}

	/**
	 * �ַ�ת����sql����.
	 *
	 * @param strDate String ����
	 * @param strFormat String ��ʽ
	 * @return Date ת�������������
	 * @since BASE 0.1
	 */
	public static java.sql.Date toSqlDate(String strDate, String strFormat) {
		java.util.Date utilDate = toDate(strDate, strFormat);
		if (utilDate == null)
			return null;
		return new java.sql.Date(utilDate.getTime());
	}

	/**
	 * ���ص�ǰʱ����ַ�.
	 * @param format���ڸ�ʽ����yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String toTodayDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new java.util.Date());
	}

	/**
	 * �ַ�ת����Timestamp����.
	 *
	 * @param strDate String ����
	 * @return Timestamp ת�������������
	 * @since BASE 0.1
	 */
	public static java.sql.Timestamp toSqlTimestamp(String strDate) {
		java.util.Date utilDate = toDate(strDate);
		if (utilDate == null)
			return null;
		return new java.sql.Timestamp(utilDate.getTime());
	}

	/**
	 * �ַ�ת����Timestamp����.
	 *
	 * @param strDate String ����
	 * @param strFormat String ��ʽ
	 * @return Timestamp ת�������������
	 * @since BASE 0.1
	 */
	public static java.sql.Timestamp toSqlTimestamp(String strDate,
			String strFormat) {
		java.util.Date utilDate = toDate(strDate, strFormat);
		if (utilDate == null)
			return null;
		return new java.sql.Timestamp(utilDate.getTime());
	}

	public static Date add(int arg, Date date, int num) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(arg, num);
		return c.getTime();
	}

	private UtilDate() {
	}

	//获得某月的第一天
	public static Date getFirstMonthDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date theDate = calendar.getTime();
		return theDate;
	}

	//获得某季度的第一天
	public static Date getFirstSeansonDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int month = date.getMonth() + 1;
		if (month >= 1 && month <= 3) {
			calendar.set(Calendar.MONTH, 0);
		} else if (month <= 6) {
			calendar.set(Calendar.MONTH, 3);
		} else if (month <= 9) {
			calendar.set(Calendar.MONTH, 6);
		} else {
			calendar.set(Calendar.MONTH, 9);
		}
		return calendar.getTime();
	}

	//获得某年的第一天
	public static Date getFirstYearDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public static void main(String[] args) {

		/*System.out.println(dateToString(relativeDate(2), "yyyy-MM-dd"));
		System.out.println(dateToString(toDate("8/9/2005 0:0:0","MM/dd/yyyy h:m:s"),"yyyy-MM-dd"));
		java.sql.Date date=toSqlDate("2005-8-9","yyyy-M-d");*/
		//System.out.println(toTodayDate());
		//System.out.println(toSqlDate("1930-01","yyyy-MM"));
	}

}

package com.sanbo.common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * 
* HISTORY
*
* @author kangning.hu
* @createtime 2009-6-24 下午02:16:12
*
 */
public class FileUploadUtil {

	public static final String CHAR_SOURCE = "qazwsxedcrfvtgbyhnujmikolp";
	public static final int CHAR_SOURCE_LENGTH = CHAR_SOURCE.length();

	/**
	 * 随几字符串
	 * @return
	 */
	public static String getRandomStr() {
		Random random = new Random(System.currentTimeMillis());
		StringBuffer sb = new StringBuffer(3);
		int index = 0;
		for (int i = 0; i < 5; i++) {
			index = random.nextInt(CHAR_SOURCE_LENGTH);
			if (index >= CHAR_SOURCE_LENGTH)
				index = CHAR_SOURCE_LENGTH / 2;
			sb.append(CHAR_SOURCE.charAt(index));
		}
		return sb.toString();
	}
	/**
	 * 随机串+时间
	 * @return
	 */
	public static String getDateRandomStr()
	{
		return getRandomStr()+getDateStr(new Date());
	}
	/**
	 * 
	 * @param date
	 * @return 
	 */
	public static String getDateStr(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		StringBuffer sb = new StringBuffer();
		sb = sb.append(calendar.get(Calendar.YEAR)).append(
				1+calendar.get(Calendar.MONTH)).append(
				calendar.get(Calendar.DAY_OF_MONTH)).append(
				calendar.get(Calendar.HOUR)).append(
				calendar.get(Calendar.MINUTE)).append(
				calendar.get(Calendar.SECOND));
		return sb.toString();
	}
	public static String getDateStr()
	{
		return getDateStr(DateUtil.now());
	}
	public static String getSufix(String filename) {
		int ind = filename.lastIndexOf(".");
		return ind != -1 ? filename.substring(ind, filename.length()) : "";
	}
	
}

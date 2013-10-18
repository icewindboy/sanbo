package com.sanbo.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public final class StringUtil {
	private static final Logger LOGGER = Logger.getLogger(StringUtil.class);
	private static final Set<String> noSet = new HashSet<String>();

	private StringUtil() {

	}

	/**
	 * 判断字符串是否为null或空恪
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrBlank(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否为正数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isPlusNumeric(String str) {

		Pattern pattern = Pattern.compile("^(\\+)?\\d+\\.?\\d*$");
		return pattern.matcher(str).matches();
	}

	public static boolean isPhone(String str) {

		Pattern pattern = Pattern
				.compile("^((\\(\\d{2,3}\\))|(\\d{3}\\-))?(\\(0\\d{2,3}\\)|0\\d{2,3}-)?[1-9]\\d{5,7}(\\-\\d{1,4})?$");
		return pattern.matcher(str).matches();
	}

	public static boolean isMobile(String str) {

		Pattern pattern = Pattern.compile("^((\\(\\d{2,3}\\))|(\\d{3}\\-))?1\\d{10}$");
		return pattern.matcher(str).matches();
	}

	public static boolean isDate(String str) {
		Pattern pattern = Pattern.compile("^((\\d{4})|(\\d{2}))([-./])(\\d{1,2})\\4(\\d{1,2})$");
		if (pattern.matcher(str).matches()) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.FORMATE_STYLE_DATA_SHORT);
				simpleDateFormat.setLenient(false);
				simpleDateFormat.parse(str);
				return true;
			} catch (ParseException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str == null || str.trim().equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(-|\\+)?\\d+\\.?\\d*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 0-7位整数，0-2位小数的浮点数
	 * 
	 * @param str
	 * @return
	 */
	// public static boolean isDouble2(String str) {
	// Pattern pattern = Pattern.compile("^[-\\+]?(\\d{0,7})(\\.\\d{0,2})?$");
	// return pattern.matcher(str).matches();
	// }

	/**
	 * 判断是否正确email格式
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isCorrectEmail(String email) {
		String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		// ^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * 判断是否正确url格式
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isCorrectUrl(String url) {
		// String regex = "^http://([w-]+.)+[w-]+(/[w-./?%\\&=]*)?$";
		String regex = "^http:\\/\\/[A-Za-z0-9]+\\.[A-Za-z0-9]+[\\/=\\?%\\-&_~`@[\\\\]\\':+!]*([^<>\\\"\\\"])*$";
		// ^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		return matcher.matches();
	}

	/**
	 * 判断是否正确的正整数
	 * 
	 * @param val
	 * @return
	 */
	public static boolean isCorrectSignlessIntegral(String val) {
		String regex = "^[+]?(\\d{0,9})$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(val);
		return matcher.matches();
	}

	/**
	 * 判断字符串是否符合"yyyyMM"的格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isFormatDate(String str) {
		Pattern pattern = Pattern.compile("\\d{6}");
		return pattern.matcher(str).matches();
	}

	public static String trimBlackStr(String str) {
		if (str == null) {
			return null;
		}
		return str.trim();
	}

	/**
	 * 只去掉首尾的空格
	 * 
	 * @param str
	 * @return
	 */
	public static String leftOrRightTrim(String str) {
		if (str == null) {
			return null;
		}
		return str.replaceAll("(^[ |　]*|[ |　]*$)", "");
	}

	/**
	 * 分割以 splitStr 组成的int字符串
	 * 
	 * @param condition
	 * @return
	 */
	public static List<Integer> getIds(String condition, String splitStr) {
		List<Integer> list = new ArrayList<Integer>();
		if (condition == null) {
			return list;
		}
		String[] tmps = condition.split(splitStr);
		for (String tmp : tmps) {
			try {
				Integer id = Integer.parseInt(tmp);
				if (id != null) {
					list.add(id);
				}
			} catch (NumberFormatException e) {
				continue;
			}
		}
		return list;
	}

	// public static List<String> getStrs(String condition) {
	// List<String> list = new ArrayList<String>();
	// if (condition == null) {
	// return list;
	// }
	// String[] tmps = condition.split("[;,]");
	// for (String tmp : tmps) {
	// if (tmp == null) {
	// continue;
	// }
	// list.add(tmp);
	// }
	// return list;
	// }

	public static String mergeInteger(char split, Collection<Integer> ids) {
		if (ids == null) {
			return "";
		}
		boolean first = true;
		StringBuffer buffer = new StringBuffer();
		for (Integer id : ids) {
			if (id == null) {
				continue;
			}
			if (!first) {
				buffer.append(String.valueOf(split));
			}
			buffer.append(id.toString());
			first = false;
		}
		return buffer.toString();
	}


	/**
	 * 将int组成以split分割的字符串
	 * 
	 * @param split
	 * @param id
	 * @param ids
	 * @return
	 */
	public static String mergeInteger(char split, int[] ids) {
		if (ids == null) {
			return "";
		}
		List<Integer> list = new ArrayList<Integer>();
		for (int id : ids) {
			list.add(id);
		}
		return mergeInteger(split, list);
	}

	/**
	 * 将 BASE64 编码的字符串 s 进行解码
	 * 
	 * @param s
	 * @return
	 */
	// public static String getFromBASE64(String s) {
	// if (s == null) {
	// return null;
	// }
	// BASE64Decoder decoder = new BASE64Decoder();
	// byte[] b;
	// try {
	// b = decoder.decodeBuffer(s);
	// } catch (IOException e) {
	// return s;
	// }
	// return new String(b);
	// }

	// add by alex.su
	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
					LOGGER.error(ex);
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) {
						k += 256;
					}
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	/** 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符） */
	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	/** 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1 */
	public static int length(String s) {
		if (s == null) {
			return 0;
		}
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}

	/** 截取一段字符的长度,不区分中英文,如果数字不被2整除，则少取一个字符位 */
	public static String substring(String origin, int len) {
		if (origin == null || origin.length() < 1 || len < 1) {
			return "";
		}
		if (len > length(origin)) {
			return origin;
		}
		String result = "";
		int length = 0;
		for (char c : origin.toCharArray()) {
			length++;
			if (!isLetter(c)) {
				length++;
			}
			if (length <= len) {
				result = result + String.valueOf(c);
			} else {
				return result;
			}
		}
		return result;
	}

	/**
	 * 用来生成webtouch的操作标识
	 * 
	 * @param c
	 *            cookie
	 * @param p
	 *            phone
	 * @return
	 */
	public static String createWebtouchKeyPhone(String c, String p) {
		if (c == null) {
			c = "";
		}
		if (p == null) {
			p = "";
		}
		return c.trim() + ":" + p.trim();
	}

	/**
	 * Converts a String to a Locale.<br>
	 * This method takes the string format of a locale and creates the locale
	 * object from it.<br>
	 * 
	 * <pre>
	 *   StringUtil.toLocale(&quot;en&quot;)         = new Locale(&quot;en&quot;, &quot;&quot;)
	 *   StringUtil.toLocale(&quot;en_GB&quot;)      = new Locale(&quot;en&quot;, &quot;GB&quot;)
	 *   StringUtil.toLocale(&quot;en_gb&quot;)      = new Locale(&quot;en&quot;, &quot;GB&quot;)
	 *   StringUtil.toLocale(&quot;en_GB_xxx&quot;)  = new Locale(&quot;en&quot;, &quot;GB&quot;, &quot;xxx&quot;)
	 * </pre>
	 * 
	 * @author denny.lv
	 * @createtime Aug 5, 2008
	 * @param str
	 * @return
	 */
	public static Locale toLocale(String str) {
		if (str == null) {
			return Locale.CHINA;
		}
		int len = str.length();
		if (len != 2 && len != 5 && len < 7) {
			throw new IllegalArgumentException("Invalid locale format: " + str);
		}
		char ch0 = str.charAt(0);
		char ch1 = str.charAt(1);
		if (ch0 < 'a' || ch0 > 'z' || ch1 < 'a' || ch1 > 'z') {
			throw new IllegalArgumentException("Invalid locale format: " + str);
		}
		if (len == 2) {
			return new Locale(str, "");
		} else {
			if (str.charAt(2) != '_') {
				throw new IllegalArgumentException("Invalid locale format: " + str);
			}
			char ch3 = Character.toUpperCase(str.charAt(3));
			char ch4 = Character.toUpperCase(str.charAt(4));
			if (ch3 < 'A' || ch3 > 'Z' || ch4 < 'A' || ch4 > 'Z') {
				throw new IllegalArgumentException("Invalid locale format: " + str);
			}
			if (len == 5) {
				return new Locale(str.substring(0, 2), str.substring(3, 5).toUpperCase());
			} else {
				if (str.charAt(5) != '_') {
					throw new IllegalArgumentException("Invalid locale format: " + str);
				}
				return new Locale(str.substring(0, 2), str.substring(3, 5).toUpperCase(), str.substring(6));
			}
		}
	}

	public static <T> String mergeArrayToStr(char split, T[] ids) {
		if (ids == null) {
			return "";
		}
		boolean first = true;
		StringBuffer buffer = new StringBuffer();
		for (T id : ids) {
			if (first) {
				first = false;
			} else {
				buffer.append(String.valueOf(split));
			}
			buffer.append(id.toString());
		}
		return buffer.toString();
	}

	public static String getstandardFilePath(String path) {
		if (path == null || "".equals(path))
			return "";
		return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
	}

	public static void main(String[] args) {
		String s = "zh_cn";
		System.out.println(toLocale(s));
	}

	/**
	 * 随机编号
	 * 
	 * @param length
	 *            生成的编号的位数
	 * @return
	 */
	public static synchronized String generateNO(int length) {
		String no = generate(length);
		if (noSet.add(no))
			return no;
		else
			return generateNO(length);
	}

	private static synchronized String generate(int length) {
		if (noSet.size() > 100000)
			noSet.clear();
		int no_lenght = length > 0 ? length : 13;
		StringBuffer sb = new StringBuffer(no_lenght);
		int index = 0;
		for (int i = 0; i < no_lenght; i++) {
			Random rd = new Random();
			index = rd.nextInt(Constants.CARD_NO_CHAR_SOURCE.length())
					% (Constants.CARD_NO_CHAR_SOURCE.length() + 1);
			if (index >= Constants.CARD_NO_CHAR_SOURCE.length())
				index = Constants.CARD_NO_CHAR_SOURCE.length() / 2;
			sb.append(Constants.CARD_NO_CHAR_SOURCE.charAt(index));
		}
		return sb.toString();
	}

	public static String filterScript(String inputString) {
		if(StringUtil.isNullOrBlank(inputString))
			return inputString;
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;	
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_iframe  = "<[\\s]*?iframe [^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?iframe [\\s]*?>";//iframe 
			String regEx_form   = "<[\\s]*?form  [^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?form  [\\s]*?>";//form 

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_iframe, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤iframe标签

			p_html = Pattern.compile(regEx_form, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤form标签			
			
			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;// 返回文本字符串
	}

	public static String html2Text(String inputString) {
		if(StringUtil.isNullOrBlank(inputString))
			return inputString;		
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			// }
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			// }
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return textStr;// 返回文本字符串
	}
}

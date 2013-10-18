package com.sanbo.common;


/**
 * 
 * @author kangning.hu
 * @createtime 2009-7-7 上午10:29:48
 * 
 */
public final class ParamsUtilities {
	private ParamsUtilities() {
	}

	/**
	 * Replace reserved char of sql.
	 * 
	 * @param str
	 * @return
	 */
	public static String dealWithReservedSqlChar(String str) {
		String result = str;
		if (!StringUtil.isNullOrBlank(str)) {
			result = result.replace("\\", "\\\\");
			result = result.replace("%", "\\%");
			result = result.replace("_", "\\_");
			result = result.replace("'", "''");
		}
		return result;
	}
}

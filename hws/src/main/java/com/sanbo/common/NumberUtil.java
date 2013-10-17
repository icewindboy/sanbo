package com.sanbo.common;

import java.text.DecimalFormat;

public class NumberUtil {
	static public DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.############");

	static public DecimalFormat INT_FORMAT = new DecimalFormat("#");

	static public String format(Object number) {
		if (number != null) {
			return NUMBER_FORMAT.format(number);
		}
		return "";
	}

}

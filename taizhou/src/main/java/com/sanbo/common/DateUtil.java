package com.sanbo.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author kangning.hu
 * @createtime 2009-6-26 上午10:58:31
 *
 */
public final class DateUtil {

    private static DateUtil instance = null;

    public static DateUtil getInstance() {
        if (instance == null) {
            instance = new DateUtil();
        }
        return instance;
    }

    private DateUtil() {
    }

    /**
     * 当前时间
     * @return
     */
    public static Date now() {
        return new Date();
    }

    /**
     * convert string to date, with default pattern,throws ParseException
     *
     * @param strDate
     *            the str date
     * @return date
     */
    public static Date strToDate(String strDate) {
        return strToDate(strDate, Constants.FORMATE_STYLE_DATA_SHORT);
    }
    public static Date toSimpleDateFormat(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat(Constants.FORMATE_STYLE_DATA_LONG);
        return strToDate(sdf.format(date), Constants.FORMATE_STYLE_DATA_LONG);
    }
    /**
     * convert string to date ,with custom pattern,throws
     * IllegalArgumentException
     *
     * @param strDate
     * @param format
     *            format style such as yyyy-MM-dd
     * @return the date.
     */
    public static Date strToDate(String strDate, String format) {
        if (strDate == null || "".equals(strDate)) {
            return null;
        }
        Date result = null;
        try {
            SimpleDateFormat formater = new SimpleDateFormat(format);
            result = formater.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException("String To Date Convert Error", e);
        }
        return result;
    }

    /**
     * Date to String format as CEMConstants.FORMATE_STYLE_DATA("yyyy-MM-dd").
     *
     * @param date
     * @return the string value format as
     *         CEMConstants.FORMATE_STYLE_DATA("yyyy-MM-dd").
     */
    public static String dateToShortStr(Date date) {
        return dateToStr(date, Constants.FORMATE_STYLE_DATA_SHORT);
    }

    public static String dateToLongStr(Date date) {
        return dateToStr(date, Constants.FORMATE_STYLE_DATA_LONG);
    }

    public static String dateToLongStr(Date date, Integer hour, Integer minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour == null ? 0 : hour);
        cal.set(Calendar.MINUTE, minute == null ? 0 : minute);
        cal.set(Calendar.SECOND, 0);

        return dateToLongStr(cal.getTime());
    }

    /**
     * Date to String accroding to the format.
     *
     * @param date
     *            date
     * @param format
     *            format
     * @return the string format of the data value.
     */
    public static String dateToStr(Date date, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        return formater.format(date);
    }

    /**
     * 获得给定时间的00:00:00
     *
     * @param date
     * @return
     */
    public static Date getStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dealwithDateNull(date));

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startTime = calendar.getTime();
        return startTime;
    }

    /**
     * 获得当天的开始时间00:00:00
     *
     * @return
     */
    public static Date getTodayStart() {
        return getStartTime(now());
    }

    /**
     * 获得当天的结束时间23:59:59
     *
     * @return
     */
    public static Date getTodayEnd() {
        return getEndTime(now());
    }

    /**
     * 获得给定时间的23:59:59
     *
     * @param date
     * @return
     */
    public static Date getEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dealwithDateNull(date));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date startTime = calendar.getTime();
        return startTime;
    }

    /**
     * Deal with the date -- if the date is null then return the current time
     * otherwise return itself.
     *
     * @param date
     *            date
     * @return date
     */
    public static Date dealwithDateNull(Date date) {
        if (date == null) {
            return new Date();
        }
        return date;
    }
//    // add by alex.su@2008-6-4
//    /**
//     * GMT+8 timzone offset value.
//     */
//    public static final String CST_TIMEZONE_OFFSET = "-480";
//
//    /**
//     * Convert the date to GMT.
//     *
//     * @param date
//     *            date.
//     * @return GMT Date.
//     */
//    public static Date toGMTDate(Date date, String timeZoneDigital) {
//        if (date == null) {
//            return null;
//        }
//
//        Calendar cal = Calendar.getInstance(getTimeZone(timeZoneDigital));
//        cal.setTime(date);
//
//        cal.add(Calendar.HOUR_OF_DAY, getTimeOffSet(timeZoneDigital));
//        // add at 20071015 by Jeecy for the timezone maybe infact minute.
//        cal.add(Calendar.MINUTE, getMinuteOffSet(timeZoneDigital));
//        return cal.getTime();
//    }
//
//    /**
//     * Convert the date to GMT.
//     *
//     * @param strDate
//     *            strDate.
//     * @return GMT Date.
//     */
//    public static Date toGMTDate(String strDate, String timeZoneDigital) {
//        Date date = strToDate(strDate);
//        Calendar cal = Calendar.getInstance(getTimeZone(timeZoneDigital));
//        cal.setTime(date);
//
//        cal.add(Calendar.HOUR_OF_DAY, getTimeOffSet(timeZoneDigital));
//        // add at 20071015 by Jeecy for the timezone maybe infact minute.
//        cal.add(Calendar.MINUTE, getMinuteOffSet(timeZoneDigital));
//        return cal.getTime();
//    }
//
//    /**
//     * Convert date to the special timezone date.<br>
//     * If timeZoneDigital==null, default convert to GMT+8(CST).
//     *
//     * @param date
//     *            date
//     * @param timeZoneDigital
//     *            timeZoneDigital
//     * @return the special timezone date.
//     */
//    public static Date toLocalDate(Date date, String timeZoneDigital) {
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//        cal.setTime(date);
//        cal.add(Calendar.HOUR_OF_DAY, -getTimeOffSet(timeZoneDigital));
//        // add at 20071015 by Jeecy for the timezone maybe infact minute.
//        cal.add(Calendar.MINUTE, -getMinuteOffSet(timeZoneDigital));
//        return cal.getTime();
//    }
//
//    /**
//     * Get the timezone according to the timeZone_digital. <br>
//     * If the timeZoneDigital==null, default return GMT+8(CST).
//     *
//     * @param timeZoneDigital
//     * @return
//     */
//    public static TimeZone getTimeZone(String timeZoneDigital) {
//        String gmt = "";
//        if (timeZoneDigital != null) {
//            int length = timeZoneDigital.length();
//            String f = "";
//            if (length == 1) {
//                gmt = "GMT";
//            } else if (timeZoneDigital.startsWith("-")) {
//                f = timeZoneDigital.substring(1, length);
//                gmt = "GMT+" + Integer.parseInt(f) / 60;
//            } else {
//                gmt = "GMT-" + Integer.parseInt(timeZoneDigital) / 60;
//            }
//
//        } else {
//            return TimeZone.getTimeZone("GMT+8");
//        }
//        return TimeZone.getTimeZone(gmt);
//    }
//
//    private static int getTimeOffSet(String timeZoneDigital) {
//        String localTimeZone = CST_TIMEZONE_OFFSET;
//        if (timeZoneDigital != null && timeZoneDigital.trim().length() > 0) {
//            localTimeZone = timeZoneDigital;
//        }
//        return Integer.parseInt(localTimeZone) / 60;
//    }
//
//    private static int getMinuteOffSet(String timeZoneDigital) {
//        String localTimeZone = CST_TIMEZONE_OFFSET;
//        if (timeZoneDigital != null && timeZoneDigital.trim().length() > 0) {
//            localTimeZone = timeZoneDigital;
//        }
//        return (Integer.parseInt(localTimeZone) % 60);
//    }
//
//    public static int getYear(Date date) {
//        SimpleDateFormat yearFm = new SimpleDateFormat("yyyy");
//        return Integer.parseInt(yearFm.format(date));
//    }
//
//    public static String getMonth(Date date) {
//        SimpleDateFormat monthFm = new SimpleDateFormat("MM");
//        return monthFm.format(date);
//    }
//
//    public static int getDay(Date date) {
//        SimpleDateFormat dayFm = new SimpleDateFormat("DD");
//        return Integer.parseInt(dayFm.format(date));
//    }
}

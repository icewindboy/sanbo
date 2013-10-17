package engine.util;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.sql.Date;
import java.math.BigDecimal;
/**
 * Title:        自定义的Beans
 * Description:  1.客户端TableDatSet的Provide和Resolve
 * 2.服务端更新提交的数据到数据库
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */

public class Format implements java.io.Serializable
{
  /**
   * 转换Date格式到Date的字符串格式（忽略1970-01-01）
   * @param date Date类型的的日期时间
   * @param pattern 转换为字符串的时间格式，如"yyyy-MM-dd"
   * @param stripTime 需要剥离的时间, 该时间需要显示为空
   * @return  转换后的字符串的日期时间格式
   */
  public static String DateToStr(java.util.Date date, String pattern)
  {
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);//"yyyy-MM-dd"
    String sDate = dateFormat.format(date);
    /*
    if(sDate.equals(stripTime))
      sDate = "";*/
    return sDate;
  }

  /**
   * 转换Timestamp格式到Date的字符串格式（忽略1970-01-01）
   * @param dateTime Timestamp类型的的日期时间
   * @param pattern 转换为字符串的时间格式，如"yyyy-MM-dd"
   * @param stripTime 需要剥离的时间, 该时间需要显示为空
   * @return  转换后的字符串的日期时间格式
   */
  public static String TimestampToStr(java.sql.Timestamp dateTime, String pattern, String stripTime)
  {
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);//"yyyy-MM-dd"
    String date = dateFormat.format(dateTime);
    if(stripTime!=null && date.equals(stripTime))
      date = "";
    return date;
  }

  /**
   * 转换Timestamp格式到Date的字符串格式（忽略1970-01-01）
   * @param dateTime Timestamp类型的的日期时间
   * @param pattern 转换为字符串的时间格式，如"yyyy-MM-dd"
   * @return  转换后的字符串的日期时间格式
   */
  public static String TimestampToStr(java.sql.Timestamp dateTime, String pattern)
  {
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);//"yyyy-MM-dd"
    String date = dateFormat.format(dateTime);
    /*
    if(date.equals("1970-01-01"))
      date = "";
    */
    return date;
  }

  /**
   * Date的字符串格式转换为DateTime格式
   * @param date Date的字符串格式
   * @return  转换后的Timestamp类型的的日期时间
   */
  public static java.sql.Timestamp StrToTimestamp(String date, boolean isDateTime) throws ParseException
  {
    SimpleDateFormat datetimeformat = new SimpleDateFormat(isDateTime ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd");
    java.util.Date ddate = datetimeformat.parse(date);
    return new java.sql.Timestamp(ddate.getTime());
  }

  /**
   * 将long格式成指定的格式的字符串
   * @param l 要格式的long
   * @param pattern 格式化的字符串，如#,##0.00
   * @return 返回格式化过的字符串
   */
  public static String formatNumber(long l, String pattern)// throws Exception
  {
	  pattern="#.############";
    if(pattern == null)
      return new StringBuffer().append( l ).toString();

    String sValue = "";
    DecimalFormat decimalFormat;
    try{
      decimalFormat = new DecimalFormat(pattern);
      sValue = decimalFormat.format( l );
    }
    catch(IllegalArgumentException  iae){
      sValue = new StringBuffer().append( l ).toString();
    }
    return sValue;
  }

  /**
   * 将double格式成指定的格式的字符串
   * @param d 要格式的double
   * @param pattern 格式化的字符串，如#,##0.00
   * @return 返回格式化过的字符串
   */
  public static String formatNumber(double d, String pattern)// throws Exception
  {
	  pattern="#.############";
    if(pattern == null)
      return new StringBuffer().append( d ).toString();

    String sValue = "";
    DecimalFormat decimalFormat;
    try{
      decimalFormat = new DecimalFormat(pattern);
      sValue = decimalFormat.format( d );
    }
    catch(IllegalArgumentException  iae){
      sValue = new StringBuffer().append( d ).toString();
    }
    return sValue;
  }

  /**
   * 将Object对象格式成指定的格式的字符串
   * @param Object 要格式的对象
   * @param pattern 格式化的字符串，如#,##0.00
   * @return 返回格式化过的字符串
   */
  public static String formatNumber(Object o, String pattern)
  {
	  pattern="#.############";
    if(o == null)
      return null;

    if(pattern == null)
      return o.toString();

    String sValue = null;
    DecimalFormat decimalFormat;
    try{
      BigDecimal tot = null;
      if(o instanceof String)
        tot = new BigDecimal((String)o);
      else if(o instanceof BigDecimal)
        tot = (BigDecimal)o;

      decimalFormat = new DecimalFormat(pattern);
      if(tot == null)
        sValue = decimalFormat.format( o );
      else
        sValue = decimalFormat.format( tot.doubleValue() );
    }
    catch(Exception  iae){
      sValue = o.toString();
    }
    return sValue;
  }

  /**
   * 将数字转化为中文大写的数字表达式
   * @param value 要格式的数字
   * @return 返回大写的数字表达式
   */
  public static String toChineseCurrency(float value)
  {
    return toChineseCurrency(String.valueOf(value));
  }

  //中文数字
  public final static String[] CHINESE_NUMBER
      = new String[]{"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
  //整数部分单位
  public final static String[] INTEGER_UNIT
      = new String[]{"圆","拾","佰","仟","万","拾","佰","仟","亿","拾","佰","仟"};
  //小数部分单位
  public final static String[] DECIMAL_UNIT
      = new String[]{"角","分"};

  /**
   * 将数字转化为中文大写的数字表达式
   * @param value 要格式的数字
   * @return 返回大写的数字表达式
   */
  public static String toChineseCurrency(String value)
  {
    if(value == null)
      return "零圆整";
    value = value.trim();
    if(value.length() == 0 || value.equals("-"))
      return "零圆整";
    try{
      Double.parseDouble(value);
    }
    catch(NumberFormatException ex){
      return value;
    }
    //
    String integerPart = null, decimalPart = null;
    int indexPos = value.indexOf(".");
    if(indexPos > -1){
      integerPart = value.substring(0, indexPos);
      decimalPart = value.substring(indexPos+1);
    }
    else
      integerPart = value;

    StringBuffer money = new StringBuffer();
    //处理负数
    if(integerPart.startsWith("-"))
    {
      money.append("负");
      integerPart = integerPart.substring(1);
    }
    //剔除非法的0
    while(integerPart.startsWith("0"))
      integerPart = integerPart.substring(1);
    if(integerPart.length() == 0)
      return "零圆整";
    //不可太大
    if(integerPart.length() > INTEGER_UNIT.length)
      return "金额太大";
    //"分","角"
    // 0,  1,   2,  3,   4,   5,   6,  7,   8,  9,   10,  11
    //"圆","拾","佰","仟","万","拾","佰","仟","亿","拾","佰","仟" --INTEGER_UNIT
    //"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"          --CHINESE_NUMBER
    boolean isPrivZero = false;
    int j = integerPart.length()-1;
    for(int i=0; i < integerPart.length(); i++){
      int num = Integer.parseInt(String.valueOf(integerPart.charAt(i)));
      boolean isZero = num == 0;
      if(isZero)//isZeros[i]
      {
        //圆,亿,万
        if(j==0 || j==8 || j==4)
          money.append(INTEGER_UNIT[j]);
        /*
        else if(j==8 || j==4)
          money.append(INTEGER_UNIT[j]).append(CHINESE_NUMBER[0]);*/
        else if(!isPrivZero)
          money.append(CHINESE_NUMBER[0]);
      }
      else
        money.append(CHINESE_NUMBER[num]).append(INTEGER_UNIT[j]);

      isPrivZero = j==0 || j==8 || j==4 ? false : isZero;
      j--;
    }
    //处理小数部分
    isPrivZero = true;
    if(decimalPart != null && decimalPart.length() >= 1){
      int num = Integer.parseInt(String.valueOf(decimalPart.charAt(0)));
      isPrivZero = num == 0;
      if(!isPrivZero)
        money.append(CHINESE_NUMBER[num]).append(DECIMAL_UNIT[0]);
    }
    if(decimalPart != null && decimalPart.length() >= 2){
      int next = decimalPart.length() == 2 ? 0 :
                 Integer.parseInt(String.valueOf(decimalPart.charAt(2))) > 4 ? 1 : 0;
      int num  = Integer.parseInt(String.valueOf(decimalPart.charAt(1))) + next;
      boolean isZero = num == 0;
      if(!isZero && !isPrivZero)
        money.append(CHINESE_NUMBER[num]).append(DECIMAL_UNIT[1]);
      else if(!isZero && isPrivZero)
        money.append(CHINESE_NUMBER[0]).append(CHINESE_NUMBER[num]).append(DECIMAL_UNIT[1]);
    }
    money.append("整");
    return money.toString();
  }
  /*
  public static void main(String[] avgs){
    String value = toChineseCurrency("980650301.01");
    System.out.println(value);
    value = toChineseCurrency("87600320.10");
    System.out.println(value);
    value = toChineseCurrency("1087600320.11");
    System.out.println(value);
  }*/

  /**
   * 将数字转化为中文大写的数字表达式
   * @param value 要格式的数字
   * @return 返回大写的数字表达式
   */
  public static String toChineseCurrency(double value)
  {
    return toChineseCurrency(String.valueOf(value));
    /*String[] Unit = new String[]{"分","角","圆","拾","佰","仟","万","拾","佰","仟","亿","拾","佰","仟"};
    String[] ChineseNum = new String[]{"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
    //处理小数部分，四舍五入
    int n = Math.round((float)value * 100);
    //判断是否为负数
    boolean myisnegative = n<0;
    n = Math.abs(n);
    //处理整数部分，最大不超过9999亿
    if(n/100 >= Math.pow(10,12))
      return "金额太大！";
    if(n==0)
      return("零圆整");

    //生成字符串
    String mystr = "";
    boolean iszero = true;
    int unitindex = 0;
    while(n > 0)
    {
     if(unitindex==2 && mystr.length() == 0)//整的处理
       mystr = mystr + "整";

     if(n%10 > 0)//非零的处理
     {
       mystr = ChineseNum[n%10] + Unit[unitindex] + mystr;
       iszero = false;
     }
     else //零的处理
     {
       if( unitindex==2 )//元的处理
       {
         if(n>0)//段中有数字
         {
           mystr = Unit[unitindex] + mystr;
           iszero=true;
         }
       }
       else if( unitindex==6 || unitindex==10)//万、亿
       {
         if(n%1000>0)//段中有数字
           mystr = Unit[unitindex] + mystr;
       }

       if(!iszero)//前一位非零
         mystr = ChineseNum[0] + mystr;

       iszero = true;
     }

     n = new BigDecimal(Math.floor((double)n/(double)10)).intValue();
     unitindex++;
    }
    if(myisnegative)
     mystr = "负" + mystr;//负数处理
    return mystr;
    */
  }

}
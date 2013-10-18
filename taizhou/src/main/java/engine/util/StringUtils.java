package engine.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.math.BigDecimal;
import engine.util.calc.*;

/**
 * Title:        自定义的Beans
 * Description:  1.客户端TableDatSet的Provide和Resolve
 * 2.服务端更新提交的数据到数据库
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */

public final class StringUtils
{

  public static final int UPPERCASE = 1;
  public static final int LOWERCASE = -1;
  public static final int NOCASE = 0;

  /**
   * 将字符串中含有所有旧字符串替换未新的字符串
   * @param line 需要做被替换操作的字符串
   * @param oldString 需要被替换成新字符串的旧字符串
   * @param newString 用于替换旧字符串的新字符串
   * @return 返回经过替换的新字符串
   */
  public static final String replace( String line, String oldString, String newString)
  {
      if (line == null) {
          return null;
      }
      int i=0;
      if ( ( i=line.indexOf( oldString, i ) ) >= 0 ) {
          char [] line2 = line.toCharArray();
          char [] newString2 = newString.toCharArray();
          int oLength = oldString.length();
          StringBuffer buf = new StringBuffer(line2.length);
          buf.append(line2, 0, i).append(newString2);
          i += oLength;
          int j = i;
          while( ( i=line.indexOf( oldString, i ) ) > 0 ) {
              buf.append(line2, j, i-j).append(newString2);
              i += oLength;
              j = i;
          }
          buf.append(line2, j, line2.length - j);
          return buf.toString();
      }
      return line;
  }

  /**
   * 成批替换多个，字符串line中含有所有旧字符串替换未新的字符串.
   * @param line 需要做被替换操作的字符串
   * @param replaces key:旧字符串, value:新字符串
   * @return 返回经过替换的新字符串
   */
  public static final String replaceStrings(String line, Map replaces)
  {
    if (line == null)
      return null;
    else if(replaces == null || replaces.size() == 0)
      return line;

    synchronized(replaces)
    {
      //得到字段和值
      Object[] entrys = replaces.entrySet().toArray();
      for(int i=0; i < entrys.length; i++)
      {
        Map.Entry entry = (Map.Entry)entrys[i];
        String oldString = (String)entry.getKey();
        String newString = (String)entry.getValue();
        line = replace(line, oldString, newString);
      }
    }
    return line;
  }

  /**
   * 将字符串中含有所有旧字符串替换未新的字符串
   * @param line 需要做被替换操作的字符串
   * @param oldString 需要被替换成新字符串的旧字符串
   * @param newString 用于替换旧字符串的新字符串
   * @param count 参数count[0]表示被替换的旧字符串的次数
   * @return 返回经过替换的新字符串
   */
  public static final String replace(String line, String oldString,
                                     String newString, int[] count)
   {
    if (line == null) {
      return null;
    }
    int i=0;
    if ((i=line.indexOf(oldString, i)) >= 0)
    {
      int counter = 0;
      counter++;
      char [] line2 = line.toCharArray();
      char [] newString2 = newString.toCharArray();
      int oLength = oldString.length();
      StringBuffer buf = new StringBuffer(line2.length);
      buf.append(line2, 0, i).append(newString2);
      i += oLength;
      int j = i;
      while ((i=line.indexOf(oldString, i)) > 0) {
        counter++;
        buf.append(line2, j, i-j).append(newString2);
        i += oLength;
        j = i;
      }
      buf.append(line2, j, line2.length-j);
      count[0] = counter;
      return buf.toString();
    }
    return line;
  }

  /**
   * 将字符串中含有所有旧字符串替换成新的字符串，并忽略大小学
   * @param line 需要做被替换操作的字符串
   * @param oldString 需要被替换成新字符串的旧字符串
   * @param newString 用于替换旧字符串的新字符串
   * @return 返回经过替换的新字符串
   */
  public static final String replaceIgnoreCase(String line, String oldString,
          String newString)
  {
      if (line == null) {
          return null;
      }
      String lcLine = line.toLowerCase();
      String lcOldString = oldString.toLowerCase();
      int i=0;
      if ((i=lcLine.indexOf(lcOldString, i)) >= 0) {
          char [] line2 = line.toCharArray();
          char [] newString2 = newString.toCharArray();
          int oLength = oldString.length();
          StringBuffer buf = new StringBuffer(line2.length);
          buf.append(line2, 0, i).append(newString2);
          i += oLength;
          int j = i;
          while ((i=lcLine.indexOf(lcOldString, i)) > 0) {
              buf.append(line2, j, i-j).append(newString2);
              i += oLength;
              j = i;
          }
          buf.append(line2, j, line2.length - j);
          return buf.toString();
      }
      return line;
  }

  /**
   * 将字符串中含有所有旧字符串替换成新的字符串，并忽略大小学。参数count[0]表示被替换的旧字符串的次数
   * @param line 需要做被替换操作的字符串
   * @param oldString 需要被替换成新字符串的旧字符串
   * @param newString 用于替换旧字符串的新字符串
   * @param count 参数count[0]表示被替换的旧字符串的次数
   * @return 返回经过替换的新字符串
   */
  public static final String replaceIgnoreCase(String line, String oldString,
          String newString, int[] count)
  {
      if (line == null) {
          return null;
      }
      String lcLine = line.toLowerCase();
      String lcOldString = oldString.toLowerCase();
      int i=0;
      if ((i=lcLine.indexOf(lcOldString, i)) >= 0) {
          int counter = 0;
          char [] line2 = line.toCharArray();
          char [] newString2 = newString.toCharArray();
          int oLength = oldString.length();
          StringBuffer buf = new StringBuffer(line2.length);
          buf.append(line2, 0, i).append(newString2);
          i += oLength;
          int j = i;
          while ((i=lcLine.indexOf(lcOldString, i)) > 0) {
              counter++;
              buf.append(line2, j, i-j).append(newString2);
              i += oLength;
              j = i;
          }
          buf.append(line2, j, line2.length - j);
          count[0] = counter;
          return buf.toString();
      }
      return line;
  }

  /**
   * 替换单引号和双引号为全角的
   * @param input 转换前的中文字符串
   * @return 返回替换后的字符串
   */
  public static final String replaceQuotatemark(String input)
  {
    if(input == null || input.length() == 0)
      return input;

    char c;
    String quotate;
    int one=0;
    int two=0;
    StringBuffer buf = new StringBuffer(input.length()+6);
    for(int i=0; i<input.length();i++)
    {
      c = input.charAt(i);
      switch(c)
      {
        case '\'':
          quotate = ++one%2==0 ? "’" : "‘";
          buf.append(quotate);
          break;
        case '\"':
          quotate = ++two%2==0 ? "”" : "“";
          buf.append(quotate);
          break;
        default:
          buf.append(input.charAt(i));
      }
    }
    return buf.toString();
  }

  /**
   * 替换非法的字符（<,>,\n,空格,/,",'）
   * @param input 转换前的中文字符串
   * @return 返回替换后的字符串
   */
  public static String toHTML(String input)
  {
    if(input == null || input.length() == 0)
    {
      return input;
    }
    StringBuffer buf = new StringBuffer(input.length()+6);
    int length = input.length();
    for(int i=0; i<length; i++)
    {
      char c = input.charAt(i);
      if(c=='<')
        buf.append("&lt;");
      else if(c == '>')
        buf.append("&gt;");
      else if(c == '\n')
        buf.append("<br>");
      else if(c == '\r' && i+1<length && input.charAt(i+1) == '\n')
      {
        buf.append("<br>");
        i++;
      }
      else if(c == ' ')
        buf.append("&nbsp;");
      else if(c == '\'')
        buf.append("&acute;");
      else if(c == '\"')
        buf.append("&quot;");
      else
        buf.append(c);
    }
    return buf.toString();
  }

  /**
   * 替换非法的字符（<,>,\n,空格,/）
   * @param input 转换前的中文字符串
   * @return 返回替换后的字符串
   */
  public static String replaceInvalid(String input)
  {
    if(input == null || input.length() == 0)
    {
      return input;
    }
    StringBuffer buf = new StringBuffer(input.length()+6);
    int length = input.length();
    for(int i=0; i<length;i++)
    {
      char c = input.charAt(i);
      if(c=='<')
        buf.append("&lt;");
      else if(c=='>')
        buf.append("&gt;");
      else if(c == '\r' && i+1<length && input.charAt(i+1) == '\n')
      {
        buf.append("<br>");
        i++;
      }
      else if(c == '\n')
        buf.append("<br>");
      else if(c == ' ')
        buf.append("&nbsp;");
      else if(c == '\'')
        buf.append("&acute;");
      else
        buf.append(c);
    }
    return buf.toString();
  }

  /**
   * 剥离并剔除与回车符号有关的非法字符
   * @param input 需要剥离非法的字符
   * @return 返回处理完字符
   */
  public static String stripEnterSymbol(String input)
  {
    char c;
    if (input == null)
      return null;

    int length = input.length();
    StringBuffer buf = new StringBuffer(length);
    for (int i = 0; i < length; i++)
    {
      c = input.charAt(i);
      if (c == '\r' || c == '\n')
        buf.append(' ');
      else if(c == '\\')
      {
        if (length <= i+1 )
          continue;

        char nextchar = input.charAt(i+1);
        if (nextchar == 'r' || nextchar == 'n') {
          buf.append(' ');
          i++;
        }
      }
      else
        buf.append(c);
    }
    return buf.toString();
  }

  /**
   * 转换为UTF-8中文
   * @param strvalue 转换前的中文字符串
   * @return UTF-8格式的中文
   */
  public final static String toChinese(String strvalue)
  {
    try{
      if(strvalue==null){
        return null;
      }
      else{
        strvalue = new String(strvalue.getBytes("ISO8859_1"),"UTF-8");
        return strvalue;
      }
    }
    catch(Exception e){
      return null;
    }
  }

  /**
   * 用特定的分割符分割字符窜, 返回字符串数组
   * @param s 要分割的字符串
   * @param sep 分割符
   * @return 分割后的字符串数组
   */
  public final static String[] parseString(String s, String sep)
  {
    return parseString(s, sep, "");
  }

  /**
   * 用特定的分割符分割字符窜, 返回字符串数组
   * @param s 要分割的字符串
   * @param sep 分割符
   * @return 分割后的字符串数组
   */
  public final static String[] parseString(String s, String sep, String nullValue)
  {
    StringTokenizer st = new StringTokenizer(s, sep);
    String result[] = new String[st.countTokens()];
    for (int i=0; i<result.length; i++) {
      result[i] = st.nextToken();
      if(result[i] == null && nullValue != null)
        result[i] = nullValue;
    }
    return result;
  }

  /**
   * 合并字符串
   * @param src 未组转之前的SQL语句
   * @param sep 特定字符，用于被替换成特定的值
   * @param values 用于替换字符的值
   * @return 组装后SQL语句
   */
  public static String combine(String src, String sep, String[] values)
  {
    StringBuffer buf = new StringBuffer();
    String[] result = parseString(src, sep);
    for(int i=0; i<result.length; i++)
    {
      buf.append(result[i]);
      //最后一次不用添加值，因为值都是添在中间的
      if(i<result.length-1 && values != null && i < values.length && values[i] != null)
        buf.append(values[i]);
    }
    return buf.toString();
  }

  /**
   * 按特定的格式拆分字符串为2维数组.1=hello&2=jac,先按&拆分在按=拆分
   * @param enumvalues 需要拆分的字符串
   * @return 返回拆分后的字符串
   */
  public static final String[][] getArrays(String enumvalues)
  {
    return getArrays(enumvalues, "&$", "=");
  }
  /**
   * 按特定的格式拆分字符串为2维数组.1=hello&2=jac,先按&拆分在按=拆分
   * @param enumvalues 需要拆分的字符串
   * @param firstsep 第一次分割符
   * @param secondsep 第二次分割符
   * @return 返回拆分后的字符串
   */
  public static final String[][] getArrays(String enumvalues, String firstsep, String secondsep)
  {
    String[] senum = parseString(enumvalues, firstsep);
    String[][] values = new String[2][senum.length];
    String[] temp = null;
    for(int i=0; i<senum.length; i++)
    {
      temp = parseString(senum[i], secondsep);
      if(temp.length == 1)
      {
        boolean isStart = !senum[i].endsWith(secondsep);
        values[0][i] = isStart ? "" : temp[0];
        values[1][i] = isStart ? temp[0] : "";
      }
      else if(temp.length == 2 )
      {
        values[0][i] = temp[0];
        values[1][i] = temp[1];
      }
    }
    return values;
  }

  /**
   * 按特定的格式拆分字符串为List数组.1=hello&2=jac,先按&拆分在按=拆分.list[0]表示key,list[1]表示value
   * @param enumvalues 需要拆分的字符串
   * @return 返回字段的各个枚举值
   */
  public final static List[] getEnumValues(String enumvalues)
  {
    List[] values = new List[]{new ArrayList(), new ArrayList()};
    String[] senum = parseString(enumvalues, "&$");
    String[] temp = null;
    for(int i=0; i<senum.length; i++)
    {
      temp = parseString(senum[i], "=");
      if(temp.length == 1)
      {
        boolean isStart = !senum[i].endsWith("=");
        values[0].add(isStart ? "" : temp[0]);
        values[1].add(isStart ? temp[0] : "");
      }
      else if(temp.length == 2 )
      {
        values[0].add(temp[0]);
        values[1].add(temp[1]);
      }
    }
    return values;
  }

  /**
   * 按特定的格式拆分字符串为Map, 1=hello&2=jac,先按&拆分在按=拆分.1表示key, hello表示value
   * @param enumvalues 需要拆分的字符串
   * @return 返回的Map对象
   */
  public final static Map StringToMap(String enumvalues)
  {
    return StringToMap(enumvalues, NOCASE);
  }

  /**
   * 按特定的格式拆分字符串为Map, 1=hello&2=jac,先按&拆分在按=拆分.1表示key, hello表示value
   * @param enumvalues 需要拆分的字符串
   * @param keyCase Map对象key的大小学转化, UPPERCASE:转换为大学, LOWERCASE:转换为小学, NOCASE:不变换
   * @return 返回的Map对象
   */
  public final static Map StringToMap(String enumvalues, int keyCase)
  {
    return StringToMap(enumvalues, "&$", "=", keyCase);
  }

  /**
   * 按特定的格式拆分字符串为Map, 1=hello&2=jac,先按&拆分在按=拆分.1表示key, hello表示value
   * @param enumvalues 需要拆分的字符串
   * @param firstsep 第一次分割符
   * @param secondsep 第二次分割符
   * @param keyCase Map对象key的大小学转化, UPPERCASE:转换为大学, LOWERCASE:转换为小学, NOCASE:不变换
   * @return 返回的Map对象
   */
  public final static Map StringToMap(String enumvalues, String firstsep, String secondsep, int keyCase)
  {
    Hashtable values = new Hashtable();
    String[] senum = parseString(enumvalues, firstsep);
    String[] temp = null;
    for(int i=0; i<senum.length; i++)
    {
      temp = parseString(senum[i], secondsep);
      if(temp.length == 1)
      {
        boolean isStart = !senum[i].endsWith(secondsep);
        if(isStart)
          values.put("", temp[0]);
        else if(keyCase > 0)
          values.put(temp[0].toUpperCase(), "");
        else if(keyCase < 0)
          values.put(temp[0].toLowerCase(), "");
        else
          values.put(temp[0], "");
      }
      else if(temp.length == 2 )
      {
        if(keyCase > 0)
          values.put(temp[0].toUpperCase(), temp[1]);
        else if(keyCase < 0)
          values.put(temp[0].toLowerCase(), temp[1]);
        else
          values.put(temp[0], temp[1]);
      }
    }
    return values;
  }

  /**
   * 得到数组对象的值，以特定的值为分割符号.如果arrayObjs是null,就返回空串
   * @param arrayObjs 需要得到值的数组对象
   * @return 返回数组对象的值
   */
  public static String getArrayValue(String[] arrayObjs)
  {
    return getArrayValue(arrayObjs, ",");
  }

  /**
   * 得到数组对象的值，以特定的值为分割符号.如果arrayObjs是null,就返回空串
   * @param arrayObjs 需要得到值的数组对象
   * @param sep 分割符号
   * @return 返回数组对象的值
   */
  public static String getArrayValue(String[] arrayObjs, String sep)
  {
    if(arrayObjs== null)
      return "";
    synchronized(arrayObjs)
    {
      StringBuffer buf = null;
      for(int i=0; i< arrayObjs.length; i++)
      {
        if(buf == null)
          buf = new StringBuffer(arrayObjs[i]);
        else
          buf.append(sep).append(arrayObjs[i]);
      }
      return buf == null ? "" : buf.toString();
    }
  }

  /**
   * 将map转化为字符串,默认的各项分割符号为" ",默认的key和value之间的分割符为等号, value前后默认用的“字符包含<br>
   * 如:key0="value0" key1="value1"
   * @param map map对象
   * @return 返回转化后的字符串
   */
  public final static String mapToString(Map map)
  {
    return mapToString(map, "=", " ", "\"", "\"");
  }

  /**
   * 将map转化为字符串
   * @param map map对象
   * @param entrySep 各项分割符号
   * @param keySep key和value之间的分割符
   * @param valueBerforeSep value前分割符
   * @param valueAfterSep value后分割符
   * @return 返回转化后的字符串
   */
  public final static String mapToString(Map map, String keySep, String entrySep, String valueBerforeSep, String valueAfterSep)
  {
    String[] entryStrs = mapToStrings(map, keySep, valueBerforeSep, valueAfterSep);
    StringBuffer buf = new StringBuffer();
    for(int i=0; i<entryStrs.length; i++)
    {
      buf.append(entryStrs[i]);
      if(i < entryStrs.length-1)
        buf.append(entrySep);
    }
    return buf.toString();
  }

  /**
   * 将map转化为字符串数组, 默认的key和value之间的分割符为"=", value前后没有字符
   * @param map map对象
   * @return 返回转化后的字符串数组
   */
  public final static String[] mapToStrings(Map map)
  {
    return mapToStrings(map, "=", null, null);
  }

  /**
   * 将map转化为字符串数组
   * @param map map对象
   * @param keySep key和value之间的分割符为等号
   * @param valueBerforeSep value前分割符
   * @param valueAfterSep value后分割符
   * @return 返回转化后的字符串数组
   */
  public final static String[] mapToStrings(Map map, String keySep, String valueBerforeSep, String valueAfterSep)
  {
    Map.Entry[] entrys = (Map.Entry[])map.entrySet().toArray(new Map.Entry[map.size()]);
    String[] entryStrs = new String[entrys.length];
    for(int i=0; i<entrys.length; i++)
    {
      StringBuffer entryStr = new StringBuffer().append(entrys[i].getKey()).append(keySep);
      if(valueBerforeSep != null && valueBerforeSep.length() > 0)
        entryStr.append(valueBerforeSep);
      //
      entryStr.append(entrys[i].getValue());
      //
      if(valueAfterSep != null && valueAfterSep.length() > 0)
        entryStr.append(valueAfterSep);
      entryStrs[i] = entryStr.toString();
    }
    return entryStrs;
  }

  /**
   * 将可变数组转化位字符串数组
   * @param list 可变数组
   * @return 返回字符串数组
   */
  public final static String[] listToStrings(List list)
  {
    return listToStrings(list, false);
  }

  /**
   * 将可变数组转化位字符串数组
   * @param list 可变数组
   * @param zeroIsNull 数组长度是0时候,是：返回null,否:返回长度是0的数组
   * @return 返回字符串数组
   */
  public final static String[] listToStrings(List list, boolean zeroIsNull)
  {
    if(list == null)
      return null;
    if(zeroIsNull && list.size() == 0)
      return null;
    return (String[])list.toArray(new String[list.size()]);
  }
  /**
   * 判断字符串对象是true字符串, 若是null或其他, 则返回false
   * @param value 字符串对象
   * @return 是否是true字符串
   */
  public static final boolean isTrue(String value){
    if(value == null)
      return false;
    return value.toLowerCase().equals("true");
  }

  /**
   * 得到字符串在字符串数组中的下标(忽略大小学).若不存在该对象,则返回-1
   * @param objs 字符串数组
   * @param obj 字符串
   * @return 返回数组中的下标
   */
  public static final int indexOfIgnoreCase(String[] objs, String obj)
  {
    if(obj == null)
      return -1;
    for(int i=0; i < objs.length; i++)
    {
      if(objs[i].equalsIgnoreCase(obj))
        return i;
    }
    return -1;
  }

  /**
   * 得到字符串在字符串数组中的下标.若不存在该对象,则返回-1
   * @param objs 字符串数组
   * @param obj 字符串
   * @return 返回数组中的下标
   */
  public static final int indexOf(String[] objs, String obj)
  {
    if(obj == null)
      return -1;
    for(int i=0; i < objs.length; i++)
    {
      if(objs[i].equals(obj))
        return i;
    }
    return -1;
  }

  /**
   * 得到字符串在字符串数组中的下标.若不存在该对象,则返回-1
   * @param objs 字符串数组
   * @param obj 字符串
   * @param fromIndex 从数值的第几个下标开始
   * @return 返回数组中的下标
   */
  public static final int indexOf(String[] objs, String obj, int fromIndex)
  {
    if(obj == null)
      return -1;
    for(int i=fromIndex; i < objs.length; i++)
    {
      if(objs[i].equals(obj))
        return i;
    }
    return -1;
  }

  /**
   * 截取字符串，忽略双字节的字符擦
   * @param content 需要被截取的字符串
   * @param length 截取长度
   * @return 返回截取后字符串
   */
  public static final String getUnicodeSubString(String content, int length)
  {
    if(content == null)
      return null;
    int count = content.getBytes().length;
    if(count > length)
    {
      char[] contentChars = content.toCharArray();
      int i=0;
      for(count=0; i<contentChars.length && count <= length; i++)
      {
        count += contentChars[i] > 255 ? 2 : 1;
        if(count > length)
          break;
      }
      return content.substring(0, i);
    }
    else
      return content;
  }
}

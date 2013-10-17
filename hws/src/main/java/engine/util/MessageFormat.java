package engine.util;

import java.text.Format;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
/**
 * <p>Title: 消息格式化类</p>
 * <p>Description:
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class MessageFormat extends Format
{

  private List messages = new ArrayList();

  private int  size     = 0;

  /**
   * 保存消息信息的类
   */
  private final class MessageUnit
  {
    public String message;
    public boolean isArgument;

    /**
     * 构造函数
     * @param message 消息
     * @param isArgument 是否是参数（需要提换的）
     */
    public MessageUnit(String message, boolean isArgument)
    {
      this.message = message;
      this.isArgument = isArgument;
    }
  }

  /**
   * 默认构造函数
   */
  public MessageFormat()  {  }

  /**
   * 构造函数
   * @param pattern 需要解析的字符串
   */
  public MessageFormat(String pattern)
  {
    applyPattern(pattern);
  }

  /**
   * 解析消息字符串
   * @param newPattern 消息字符串
   */
  public void applyPattern(String newPattern)
  {
    //清空变量
    this.size = 0;
    if(messages.size() > 0)
      messages.clear();
    //
    if(newPattern == null)
      return;
    //
    char[] patternChars = newPattern.toCharArray();
    StringBuffer buf = new StringBuffer();

    StringBuffer temp = new StringBuffer();
    char ch;
    boolean isLeft = false;
    for(int i=0; i<patternChars.length; i++)
    {
      ch = patternChars[i];
      if(ch == '{')
      {
        if(isLeft)
        {
          buf.append(ch).append(temp);
          temp.setLength(0);
        }
        else
          isLeft = true;
      }
      else if(ch == '}')
      {
        if(isLeft)
        {
          messages.add(new MessageUnit(buf.toString(), false));
          messages.add(new MessageUnit(temp.toString(), true));
          size ++;
          buf.setLength(0);
          temp.setLength(0);
          //
          isLeft = false;
        }
        else
          buf.append(ch);
      }
      else
      {
        if(isLeft)
          temp.append(ch);
        else
          buf.append(ch);
      }
    }
    if(temp.length() > 0)
      buf.append('{').append(temp);
    if(buf.length() > 0)
      messages.add(new MessageUnit(buf.toString(), false));
  }

  /**
   * 得到参数的名称数值
   * @return 返回参数的名称数值
   */
  public String[] getArgumentNames()
  {
    if(size == 0)
      return null;

    List names = new ArrayList(this.size+1);
    for(int i=0; i<messages.size(); i++)
    {
      MessageUnit unit = (MessageUnit)messages.get(i);
      if(!unit.isArgument)
        continue;
      String name = unit.message;
      if(names.contains(name))
        continue;
      names.add(name);
    }

    String[] argumentNames = (String[])names.toArray(new String[names.size()]);
    return argumentNames;
  }

  /**
   * 格式化消息表达式(静态的方法)
   * @param pattern 需要被解析的字符串
   * @param arguments 参数值数值
   * @return 返回格式化后的消息表达式
   */
  public static String format(String pattern, Map arguments) {
    MessageFormat temp = new MessageFormat(pattern);
    return temp.format(arguments, new StringBuffer(), new FieldPosition(0)).toString();
  }

  /**
   * 格式化消息表达式
   * @param source 参数值对象数组
   * @param result 需要添加的StringBuffer
   * @param pos
   * @return 返回格式化后的消息表达式
   */
  public final StringBuffer format(Map source, StringBuffer result, FieldPosition ignore)
  {
    if(result == null)
      result = new StringBuffer();

    //组装字符串
    for(int i=0; i<messages.size(); i++)
    {
      MessageUnit unit = (MessageUnit)messages.get(i);
      if(!unit.isArgument)
        result.append(unit.message);
      else
      {
        Object o = source == null ? null : source.get(unit.message);
        if(o == null)
          result.append('{').append(unit.message).append('}');
        else
          result.append(String.valueOf(o));
      }
    }
    return result;
  }


  /**
   * 格式化消息表达式
   * @param obj 参数值对象
   * @param toAppendTo 需要添加的StringBuffer
   * @param pos
   * @return 返回格式化后的消息表达式
   */
  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
  {
    return format((Map)obj, toAppendTo, pos);
  }


  public Object parseObject(String source, ParsePosition status)
  {
    throw new java.lang.UnsupportedOperationException("Method parseObject() not yet implemented.");
  }

}
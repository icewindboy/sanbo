package engine.dataset.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;

import engine.util.StringUtils;
import engine.util.ParseUnit;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.Variant;

/**
 * <p>Title: Query语句的Where条件的子句抽象类</p>
 * <p>Description: Query语句的Where条件的子句抽象类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public abstract class QueryWhereField implements Serializable, Expression, Cloneable
{
  public static final String TEXT   = "text";
  public static final String LOOKUP = "lookup";
  public static final String SELECT = "select";
  public static final String COMBOX = "combox";
  public static final String RADIO  = "radio";
  public static final String MULTI  = "multi";

  protected Column column;
  protected String linkTable;
  protected String linkColumn;
  protected String queryColumn;

  protected String extendName;
  protected String opersign;
  protected String type;
  protected String value;
  protected String lookup;
  protected boolean span = false;
  protected String initValue;
  protected boolean isNeed = false;
  protected boolean isshow = true;
  protected Map otherAttribute = null;
  protected String linkSign = Expression.AND;

  public int getIntDataType()
  {
    return this.column.getDataType();
  }

  public String getDataType(){
    int dataType = this.column.getDataType();
    switch(dataType)
    {
      case Variant.STRING:
        return "varchar";
      case Variant.FLOAT:
      case Variant.DOUBLE:
      case Variant.INT:
      case Variant.SHORT:
      case Variant.LONG:
      case Variant.BIGDECIMAL:
        return "number";
      case Variant.DATE:
        return "date";
      case Variant.TIME:
        return "time";
      case Variant.TIMESTAMP:
        return "datetime";
      default:
        return "varchar";
    }
  }

  /**
   * 将字符串表示的字符类型转换为整型
   * @param value 字符串表示的字符类型
   * @return 转换为整型的结果
   */
  protected int convertDataType(String value)
  {
    if(value == null)
      return Variant.STRING;

    value = value.toLowerCase();
    if(value.equals("varchar") || value.equals("char"))
      return Variant.STRING;
    else if(value.equals("number"))
      return Variant.BIGDECIMAL;
    else if(value.equals("date"))
      return Variant.DATE;
    else if(value.equals("time"))
      return Variant.TIME;
    else if(value.equals("datetime"))
      return Variant.TIMESTAMP;
    else
      return Variant.STRING;
  }

  /**
   * 得到column对象
   * @return 返回column对象
   */
  public Column getColumn()
  {
    return column;
  }

  /**
   * 得到字段的标题
   * @return 返回字段标题
   */
  public String getCaption()
  {
    return getColumn().getCaption();
  }

  /**
   * 得到字段名称
   * @return 返回字段名称
   */
  public String getColumnName()
  {
    return getColumn().getColumnName();
  }

  /**
   * 设置字段名称
   * @param name 字段名称
   */
  public void setColumnName(String name)
  {
    getColumn().setColumnName(name);
  }

  /**
   * 得到此字段相关联的表名
   * @return 返回此字段相关联的表名
   */
  public String getLinkTable()
  {
    return linkTable;
  }

  /**
   * 得到与此字段相关联的字段名
   * @return 返回此字段相关联的字段名
   */
  public String getLinkColumn()
  {
    return linkColumn;
  }


  /**
   * 得到用于查询的相关联的表的其他字段
   * @return 返回用于查询的相关联的表的其他字段
   */
  public String getQueryColumn()
  {
    return queryColumn;
  }

  /**
   * 得到附加的字段名，用于取显示字段的caption, 若为null, 将不起作用
   * @return 返回附加的字段名
   */
  public String getExtendName()
  {
    return extendName;
  }

  /**
   * 设置附加的字段名，用于取显示字段的caption, 若为null, 将不起作用
   * @param extendName 字段名
   */
  public void setExtendName(String extendName)
  {
    this.extendName = extendName;
  }

  /**
   * 得到操作符号, >, <, =, >=, <=, <>, 默认为LIKE
   * @return 返回操作符号
   */
  public String getOperSign()
  {
    return opersign;
  }

  /**
   * 得到输入框类型
   * @return 输入框类型
   */
  public String getType()
  {
    int dataType = column.getDataType();
    if(dataType == Variant.DATE || dataType == Variant.TIME || dataType == Variant.TIMESTAMP)
      type = "text";
    return type;
  }

  /**
   * 得到枚举值，与相关类型有关
   * @return 枚举值
   */
  public String getValue()
  {
    return value;
  }

  /**
   * 是否独占一行
   * @return 是否独占一行
   */
  public boolean isSpan()
  {
    return span;
  }

  /**
   * 是否必须输入查询值
   * @return 是否必须输入查询值
   */
  public boolean isNeed()
  {
    return isNeed;
  }

  /**
   * 设置是否需要必须输入查询值
   * @param isneed 是否必须
   */
  public void setIsNeed(boolean isneed)
  {
    this.isNeed = isneed;
  }

  /**
   * 得到lookupBean的key名称, 可用类型：select, combox
   * @return lookupBean的key名称, 可用类型：select, combox
   */
  public String getLookup() {
    return lookup;
  }

  /**
   * 得到lookup类型的输入框要调用的javasript函数
   * @return lookup类型的输入框要调用的javasript函数
   */
  public String getScript() {
    return otherAttribute == null ? null :StringUtils.mapToString(otherAttribute);
  }

  /**
   * 得到字段的初始化值
   * @return 字段的初始化值
   */
  public String getInitValue(){
    return initValue;
  }

  /**
   * 设置字段的初始化值
   * @param value 字段的初始化值
   */
  public void setInitValue(String value){
    this.initValue = value;
  }

  /**
   * 是否显示该查询字段
   * @return 是否显示该查询字段
   */
  public boolean isShow()
  {
    return isshow;
  }
  /**
   * 设置查询条件的类型
   * @param type 类型 text, lookup, select, combox, radio
   */
  public void setType(String type)
  {
    if(type == null)
    {
      this.type = TEXT;
      return;
    }
    type = type.toLowerCase();
    if(type.equals(TEXT) || type.equals(LOOKUP) || type.equals(SELECT)
       || type.equals(COMBOX) || type.equals(RADIO) || type.equals(MULTI))
    {
      this.type = type;
      if(type.equals(MULTI))
        this.opersign = IN;
    }
    else
      this.type = TEXT;
  }

  /**
   * 设置操作符号, >, < , =, >=, <=, <>, like, left_like(左LIKE), right_like(右LIKE), in
   * @param operSign 过滤&gt;为>, 过滤&lt;为<,
   */
  public void setOperSign(String sign)
  {
    if(sign == null)
      this.opersign = LIKE;
    else if(this.type != null && type.equals(MULTI))
      this.opersign = IN;
    else
    {
      opersign = sign.toLowerCase();
      this.opersign = sign.equals(GREATER_HTML) ? GREATER :
                      sign.equals(LESS_HTML) ? LESS :
                      sign.equals(GREATER_EQUALS_HTML) ? GREATER_EQUALS :
                      sign.equals(LESS_EQUALS_HTML) ? LESS_EQUALS :
                      sign.equals(NOT_EQUALS_HTML) ? NOT_EQUALS :
                      (  sign.equals(GREATER) || sign.equals(LESS) || sign.equals(EQUALS)
                      || sign.equals(GREATER_EQUALS) || sign.equals(LESS_EQUALS) || sign.equals(NOT_EQUALS)
                      || sign.equals(LIKE) || sign.equals(LEFT_LIKE) || sign.equals(RIGHT_LIKE)
                      || sign.equals(IN) ) || sign.equals(NOT_LIKE) ? sign : LIKE;
    }
  }

  /**
   * 设置javascript函数
   * @param script javascript函数
   */
  public void setScript(String script)
  {
    if(script == null)
      return;
    if(script.length() == 0)
      return;
    ParseUnit unit = new ParseUnit("<field "+ script +">", 0);
    this.otherAttribute = unit.getAttributes();
    //this.script = script;
  }

  /**
   * 设置其他属性
   * @param key 属性的键值名称
   * @param value 属性值
   */
  public void pubAttribute(String key, String value)
  {
    if(otherAttribute == null)
      otherAttribute = new Hashtable();
    otherAttribute.put(key.toLowerCase(), value);
  }

  /**
   * 得到其他属性
   * @param key 属性的键值名称
   * @return 返回属性值
   */
  public String getAttribute(String key)
  {
    return otherAttribute == null ? null : (String)otherAttribute.get(key.toLowerCase());
  }

  /**
   * 得到与下一个字段的连接符号(AND,OR)
   * @return 返回与下一个字段的连接符号
   */
  public String getLinkSign()
  {
    return this.linkSign;
  }

  /**
   * 设置与下一个字段的连接符号(AND,OR)
   * @param linkSign 与下一个字段的连接符号
   */
  public void setLinkSign(String linkSign)
  {
    if(linkSign == null)
      this.linkSign = Expression.AND;
    else
    {
      linkSign = linkSign.toUpperCase();
      this.linkSign = linkSign.equals(Expression.AND) || linkSign.equals(Expression.OR)
                    ? linkSign : Expression.AND;
    }
  }

  /**
   * 克隆自己
   * @return 返回克隆后的对象
   */
  public Object clone()
  {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }
}
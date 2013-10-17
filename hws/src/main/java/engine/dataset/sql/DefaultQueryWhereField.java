package engine.dataset.sql;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;

import engine.web.html.HtmlTag;
import engine.util.StringUtils;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.Variant;
/**
 * <p>Title: Query语句的Where条件的子句的查询字段</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public final class DefaultQueryWhereField extends QueryWhereField
{
  /**
   * 构造函数
   * @param columnName 查询字段名称
   * @param caption 查询字段标题
   * @param dataType 数据类型
   * @param linkTable 此字段相关联的表名
   * @param linkColumn 与此字段相关联的其他表的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   */
  public DefaultQueryWhereField(String columnName, String caption, String dataType, String linkTable,
                                String linkColumn, String queryColumn)
  {
    this(columnName, caption, dataType, linkTable, linkColumn, queryColumn, true);
  }

  /**
   * 构造函数
   * @param columnName 查询字段名称
   * @param caption 查询字段标题
   * @param dataType 数据类型
   * @param linkTable 此字段相关联的表名
   * @param linkColumn 与此字段相关联的其他表的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   * @param isshow 是否显示查询条件
   */
  public DefaultQueryWhereField(String columnName, String caption, String dataType, String linkTable,
                                String linkColumn, String queryColumn, boolean isshow)
  {
    this(columnName, caption, dataType, linkTable, linkColumn, queryColumn, null, null,
         null, false, null, null, null, null, isshow);
  }


  /**
   * 构造函数
   * @param columnName 查询字段名称
   * @param caption 查询字段标题
   * @param dataType 数据类型
   * @param linkTable 此列相关联的表名
   * @param linkColumn 与此列相关联的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   * @param extendName 附加的字段名，用于取显示字段的caption
   * @param opersign 操作符号, >, <, =, >=, <=, <>,IN, 默认为LIKE
   */
  public DefaultQueryWhereField(String columnName, String caption,     String dataType,   String linkTable,
                                String linkColumn, String queryColumn, String extendName, String opersign)
  {
    this(columnName, caption, dataType, linkTable, linkColumn, queryColumn, extendName, opersign,
         null, false, null, null, null, null, true);
  }

  /**
   * 构造函数
   * @param columnName 查询字段名称
   * @param caption 查询字段标题
   * @param dataType 数据类型
   * @param linkTable 此列相关联的表名
   * @param linkColumn 与此列相关联的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   * @param extendName 附加的字段名，用于取显示字段的caption
   * @param opersign 操作符号, >, <, =, >=, <=, <>,IN, 默认为LIKE
   * @param isshow 是否显示查询条件
   */
  public DefaultQueryWhereField(String columnName, String caption,     String dataType,   String linkTable,
                                String linkColumn, String queryColumn, String extendName, String opersign,
                                boolean isshow)
  {
    this(columnName, caption, dataType, linkTable, linkColumn, queryColumn, extendName, opersign,
         null, false, null, null, null, null, isshow);
  }

  /**
   * 构造函数
   * @param columnName 查询字段名称
   * @param caption 查询字段标题
   * @param dataType 数据类型
   * @param linkTable 此列相关联的表名
   * @param linkColumn 与此列相关联的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   * @param extendName 附加的字段名，用于取显示字段的caption
   * @param opersign 操作符号, >, <, =, >=, <=, <>,IN, 默认为LIKE
   * @param type 输入框类型
   * @param span 是否另起一行
   * @param value 枚举值,可用类型：select, combox, radio
   * @param lookup lookupBean的key名称, 可用类型：select, combox
   * @param script lookup类型的输入框要调用的javasript函数
   */
  public DefaultQueryWhereField(String columnName, String caption,     String dataType,   String linkTable,
                                String linkColumn, String queryColumn, String extendName, String opersign,
                                String type,       boolean span,       String value,      String lookup,
                                String script)
  {
    this(columnName, caption, dataType, linkTable, linkColumn, queryColumn, extendName, opersign,
         type, span, value, lookup, script, null, true);
  }

  /**
   * 构造函数
   * @param columnName 查询字段名称
   * @param caption 查询字段标题
   * @param dataType 数据类型
   * @param linkTable 此列相关联的表名
   * @param linkColumn 与此列相关联的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   * @param extendName 附加的字段名，用于取显示字段的caption
   * @param opersign 操作符号, >, <, =, >=, <=, <>,IN, 默认为LIKE
   * @param type 输入框类型
   * @param span 是否另起一行
   * @param value 枚举值,可用类型：select, combox, radio
   * @param lookup lookupBean的key名称, 可用类型：select, combox
   * @param script lookup类型的输入框要调用的javasript函数
   * @param initValue 初始化值
   * @param isshow 是否显示
   */
  public DefaultQueryWhereField(String columnName, String caption,     String dataType,   String linkTable,
                                String linkColumn, String queryColumn, String extendName, String opersign,
                                String type,       boolean span,       String value,      String lookup,
                                String script,     String initValue,   boolean isshow)
  {
    this(columnName, caption, dataType, linkTable, linkColumn, queryColumn, extendName, opersign,
         type, span, value, lookup, script, initValue, isshow, false);
  }
  /**
   * 构造函数
   * @param columnName 查询字段名称
   * @param caption 查询字段标题
   * @param dataType 数据类型
   * @param linkTable 此列相关联的表名
   * @param linkColumn 与此列相关联的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   * @param extendName 附加的字段名，用于取显示字段的caption
   * @param opersign 操作符号, >, <, =, >=, <=, <>,IN, 默认为LIKE
   * @param type 输入框类型
   * @param span 是否另起一行
   * @param value 枚举值,可用类型：select, combox, radio
   * @param lookup lookupBean的key名称, 可用类型：select, combox
   * @param script lookup类型的输入框要调用的javasript函数
   * @param initValue 初始化值
   * @param isshow 是否显示
   * @param isneed 是否需要输入查询值
   */
  public DefaultQueryWhereField(String columnName, String caption,     String dataType,   String linkTable,
                                String linkColumn, String queryColumn, String extendName, String opersign,
                                String type,       boolean span,       String value,      String lookup,
                                String script,     String initValue,   boolean isshow,    boolean isneed)
  {
    init(columnName, caption, dataType,  linkTable,
         linkColumn, queryColumn, extendName, opersign,
         type,       span,        value,      lookup,
         script,     initValue,   isshow,     isneed);
  }

  /**
   * 构造函数
   * @param fieldInfo 字段的属性
   */
  public DefaultQueryWhereField(Map fieldInfo)
  {
    if(otherAttribute == null)
      otherAttribute = new Hashtable();
    else
      otherAttribute.clear();

    otherAttribute.putAll(fieldInfo);

    String field = (String)otherAttribute.remove(HtmlTag.FIELD);
    String caption = (String)otherAttribute.remove(HtmlTag.CAPTION);
    String datatype = (String)otherAttribute.remove(HtmlTag.DATA_TYPE);
    String linktable = (String)otherAttribute.remove(HtmlTag.LINK_TABLE);
    String linkcolumn = (String)otherAttribute.remove(HtmlTag.LINK_COLUMN);
    String querycolumn = (String)otherAttribute.remove(HtmlTag.QUERY_COLUMN);
    String extendname = (String)otherAttribute.remove(HtmlTag.EXTEND_NAME);
    String opersign = (String)otherAttribute.remove(HtmlTag.OPER_SIGN);
    String type = (String)otherAttribute.remove(HtmlTag.TYPE);
    String value = (String)otherAttribute.remove(HtmlTag.VALUE);
    String lookup = (String)otherAttribute.remove(HtmlTag.LOOKUP);
    String script = (String)otherAttribute.remove(HtmlTag.SCRIPT);
    String span = (String)otherAttribute.remove(HtmlTag.SPAN);
    //
    String initValue = (String)otherAttribute.remove(HtmlTag.INIT_VALUE);
    String need = (String)otherAttribute.remove(HtmlTag.NEED);
    String show = (String)otherAttribute.remove(HtmlTag.SHOW);

    init(field, caption, datatype, linktable,
        linkcolumn, querycolumn, extendname, opersign,
        type, StringUtils.isTrue(span), value, lookup,
        script, initValue, show==null||StringUtils.isTrue(show), StringUtils.isTrue(need));
  }


  public void init(String columnName, String caption,     String dataType,   String linkTable,
                                String linkColumn, String queryColumn, String extendName, String opersign,
                                String type,       boolean span,       String value,      String lookup,
                                String script,     String initValue,   boolean isshow,    boolean isneed)
  {
    int iDataType = convertDataType(dataType);
    super.column = new Column(columnName, caption, iDataType);
    if(linkTable==null || (linkTable!= null && linkTable.length()==0) ||
       linkColumn ==null || (linkColumn != null && linkColumn.length()==0) ||
       queryColumn ==null || (queryColumn != null && queryColumn.length()==0))
    {
      super.linkTable = null;
      super.linkColumn = null;
      super.queryColumn = null;
    }
    else
    {
      super.linkTable = linkTable;
      super.linkColumn = linkColumn;
      super.queryColumn = queryColumn;
    }
    super.extendName = extendName != null && extendName.length() ==0 ? null : extendName;
    super.value = value !=null && value.length() == 0 ? null : value;
    super.lookup = lookup !=null && lookup.length() == 0 ? null : lookup;
    super.initValue = initValue !=null && initValue.length() == 0 ? null : initValue;
    super.span = span;
    super.isshow = isshow;
    super.isNeed = isneed;
    setOperSign(opersign);
    setType(type);
    setScript(!LOOKUP.equals(super.type) ? null : script);
  }
}
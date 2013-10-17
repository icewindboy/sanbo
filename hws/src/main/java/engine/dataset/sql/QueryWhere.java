package engine.dataset.sql;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import engine.util.StringUtils;
import engine.util.MessageFormat;
import engine.dataset.RowMap;

/**
 * <p>Title: 查询WHER子句的抽象类</p>
 * <p>Description: 查询WHER子句的抽象类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public abstract class QueryWhere implements Expression, java.io.Serializable
{
  private static final int FILED_ALL  = 0;
  private static final int FILED_SHOW = 1;
  private static final int FILED_NEED = 2;

  protected List columnNames = new ArrayList();
  protected List columnsArray = new ArrayList();

  protected RowMap whereValueRow = new RowMap();

  protected Map replaceInitValues = null;

  /**
   * 设置初始化参数
   * @param init 初始化参数
   */
  public void setInitValues(Map init)
  {
    replaceInitValues = init;
  }

  /**
   * 清楚所有的要显示的列
   */
  public void clearColumns()
  {
    if(columnNames.size() > 0)
      columnNames.clear();
    if(columnsArray.size() > 0)
      columnsArray.clear();
  }

  /**
   * 清除输入的值
   */
  public void clearWhereValues()
  {
    emptyWhereValues();
    for(int i=0; i < columnsArray.size(); i++)
    {
      QueryWhereField field = (QueryWhereField)columnsArray.get(i);
      String initValue = field.getInitValue();
      if(initValue == null)
        continue;
      initValue = MessageFormat.format(initValue, replaceInitValues);
      String name = (String)columnNames.get(i);
      String extendName = field.getExtendName();
      if(extendName !=null && extendName.length() > 0)
        name += "$"+extendName;

      if(QueryWhereField.MULTI.equals(field.getType()))
        whereValueRow.put(name, StringUtils.parseString(initValue, "$&"));
      else
        whereValueRow.put(name, initValue);
    }
  }

  /**
   * 清除所有的变量
   */
  public void emptyWhereValues()
  {
    whereValueRow.clear();
  }

  /**
   * 添加显示的列名
   * @param tableName 表名
   * @param column 需要显示列名
   */
  public abstract void addWhereField(String tableName, QueryWhereField column);

  /**
   * 添加多个显示的列名
   * @param tableName 表名
   * @param columns 需要显示列名列表
   */
  public void addWhereField(String tableName, QueryWhereField[] columns)
  {
    for(int i=0; i<columns.length; i++)
      addWhereField(tableName, columns[i]);
  }

  /**
   * 得到字段的全名. 若添加时设置了表名和扩展名, 则全名([]表示可省略)：[table$]field[$extendname]
   * @return 返回字段的全名
   */
  public String[] getFieldFullNames()
  {
    return getFieldFullNames(FILED_ALL);
  }

  /**
   * 得到字段的全名. 若添加时设置了表名和扩展名
   * @param condition 得到字段的条件
   * @return 返回字段的全名
   */
  private String[] getFieldFullNames(int condition)
  {
    List list = new ArrayList(columnsArray.size());
    for(int i=0; i<columnsArray.size(); i++)
    {
      QueryWhereField field = (QueryWhereField)columnsArray.get(i);
      if(condition == FILED_SHOW && !field.isShow())
        continue;
      else if(condition == FILED_NEED && !field.isNeed())
        continue;

      String name = (String)columnNames.get(i);
      String extendname = field.getExtendName();
      if(extendname != null && extendname.length() > 0)
        name = new StringBuffer(name).append("$").append(extendname).toString();
      list.add(name);
    }
    return (String[])list.toArray(new String[list.size()]);
  }

  /**
   * 得到可显示查询字段的全名
   * @return 返回可显示的查询条件的字段属性
   */
  public String[] getShowFieldFullNames()
  {
    return getFieldFullNames(FILED_SHOW);
  }

  /**
   * 得到需强制输入字段的全名
   * @return 返回可显示的查询条件的字段属性
   */
  public String[] getNeedFieldFullNames()
  {
    return getFieldFullNames(FILED_NEED);
  }

  /**
   * 得到查询条件的各个字段属性
   * @return 返回查询条件的各个字段属性
   */
  public QueryWhereField getWhereField(String name)
  {
    int index = columnNames.indexOf(name.toUpperCase());
    return index<0 ? null : (QueryWhereField)columnsArray.get(index);
  }

  /**
   * 得到查询条件的各个字段属性
   * @return 返回查询条件的各个字段属性
   */
  public QueryWhereField[] getWhereFields()
  {
    return getWhereFields(FILED_ALL);
  }

  /**
   * 得到查询条件的各个字段属性
   * @param condition 得到字段的条件
   * @return 返回查询条件的各个字段属性
   */
  private QueryWhereField[] getWhereFields(int condition)
  {
    List list = new ArrayList(columnsArray.size());
    for(int i=0; i<columnsArray.size(); i++)
    {
      QueryWhereField field = (QueryWhereField)columnsArray.get(i);
      if(condition == FILED_SHOW && !field.isShow())
        continue;
      else if(condition == FILED_NEED && !field.isNeed())
        continue;
      list.add(field);
    }
    return (QueryWhereField[])list.toArray(new QueryWhereField[list.size()]);
  }


  /**
   * 得到所有查询字段的数量
   * @return 返回所有查询字段的数量
   */
  public int getFieldCount()
  {
    return columnsArray.size();
  }

  /**
   * 得到可显示查询字段的数量
   * @return 返回所有查询字段的数量
   */
  public int getShowFieldCount()
  {
    return getFieldCount(FILED_SHOW);
  }

  /**
   * 得到得到需强制输入字段的数量
   * @return 返回所有查询字段的数量
   */
  public int getNeedFieldCount()
  {
    return getFieldCount(FILED_NEED);
  }

  /**
   * 得到查询条件的各个字段数量
   * @param condition 得到字段的条件
   * @return 返回查询条件的各个字段数量
   */
  private int getFieldCount(int condition)
  {
    int count = 0;
    for(int i=0; i<columnsArray.size(); i++)
    {
      QueryWhereField field = (QueryWhereField)columnsArray.get(i);
      if(condition == FILED_SHOW && !field.isShow())
        continue;
      else if(condition == FILED_NEED && !field.isNeed())
        continue;
      count++;
    }
    return count;
  }
  /**
   * 得到可显示的查询条件的字段属性
   * @return 返回可显示的查询条件的字段属性
   */
  public QueryWhereField[] getShowWhereFields()
  {
    return getWhereFields(FILED_SHOW);
  }

  /**
   * 得到需要强制输入的查询条件的字段属性
   * @return 返回可显示的查询条件的字段属性
   */
  public QueryWhereField[] getNeedWhereFields()
  {
    return getWhereFields(FILED_NEED);
  }

  /**
   * 得到查询过所保存的值
   * @return 查询过所保存的值
   */
  public RowMap getWhereValueRow()
  {
    return whereValueRow;
  }

  /**
   * 得到查询的值
   * @param colName 字段名称
   * @return 返回字段值
   */
  public String getWhereValue(String colName)
  {
    return whereValueRow.get(colName);
  }

  /**
   * 得到查询值的数组
   * @param colName 字段名称
   * @return 返回字段值数组
   */
  public String[] getWhereValues(String colName){
    return whereValueRow.getValues(colName);
  }

  /**
   * 设置查询的值
   * @param colName 字段名称
   * @param colValue 字段值
   */
  public void putWhereValue(String colName, String colValue)
  {
    whereValueRow.put(colName, colValue);
  }

  /**
   * 设置查询的值数组
   * @param colName 字段名称
   * @param colValues 字段值
   */
  public void putWhereValues(String colName, String[] colValues)
  {
    whereValueRow.put(colName, colValues);
  }

  /**
   * 设置查询的div点击确定时, 所有输入框的值
   * @param request web的请求
   */
  public void setWhereValues(HttpServletRequest request)
  {
    whereValueRow.put(request);
  }

  /**
   * 得到查询转化为SQL语句的WHERE子句的条件
   * @return WHERE子句的条件
   */
  public abstract String getWhereQuery();

}

package engine.project;

import java.util.*;
import javax.servlet.http.*;

import engine.dataset.*;
import engine.dataset.sql.QueryWhereField;
import engine.util.*;
import com.borland.jb.util.FastStringBuffer;
import com.borland.dx.dataset.Variant;

/**
 * <p>Title: 查询基础类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */
public abstract class QueryBasic implements java.io.Serializable
{
  //protected String endcoding  = PropertyManager.getPublicPropertyManager().getProp("encoding");

  protected ArrayList columnNames = new ArrayList();
  protected ArrayList columnCaptions = new ArrayList();
  protected ArrayList columnsArray = new ArrayList();

  protected RowMap searchRow = new RowMap();

  /**
   * 清楚所有的要显示的列
   */
  public void clearColumns()
  {
    if(columnNames.size() > 0)
      columnNames.clear();
    if(columnCaptions.size() > 0)
      columnCaptions.clear();
    if(columnsArray.size() > 0)
      columnsArray.clear();
  }

  /**
   * 添加显示地列名
   * @param tableName 表名
   * @param columnNames 需要显示列名列表
   */
  public abstract void addShowColumn(String tableName, QueryWhereField[] columns);

  /**
   * 得到字段的长名称. 若添加时设置了表名, 则长名称：table$field
   * @return 返回字段的长名称
   */
  public String[] getColumnLongNames()
  {
    String[] temps = new String[columnNames.size()];
    columnNames.toArray(temps);
    return temps;
  }

  /**
   * 得到查询条件的各个字段属性
   * @return 返回查询条件的各个字段属性
   */
  public QueryWhereField[] getWhereFields()
  {
    QueryWhereField[] temps = new QueryWhereField[columnsArray.size()];
    columnsArray.toArray(temps);
    return temps;
  }

  /**
   * 得到查询过所保存的值
   * @return 查询过所保存的值
   */
  public RowMap getSearchRow()
  {
    return searchRow;
  }

  /**
   * 设置查询的值
   * @param colName 字段名称
   * @param colValue 字段值
   */
  public void put(String colName, String colValue)
  {
    searchRow.put(colName, colValue);
  }

  /**
   * 设置查询的div点击确定时, 所有输入框的值
   * @param request web的请求
   */
  public abstract void setSearchValue(HttpServletRequest request);

  /**
   * 得到查询转化为SQL语句的WHERE子句的条件
   * @return WHERE子句的条件
   */
  public abstract String getWhereQuery();

}
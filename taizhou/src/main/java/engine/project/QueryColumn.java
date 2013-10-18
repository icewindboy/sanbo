package engine.project;

import com.borland.dx.dataset.Column;
import java.util.ArrayList;

import engine.dataset.sql.QueryWhereField;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */
public final class QueryColumn extends QueryWhereField
{
  /**
   * 构造函数
   * @param column column对象
   * @param linkTable 此字段相关联的表名
   * @param linkColumn 与此字段相关联的其他表的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   */
  public QueryColumn(Column column, String linkTable, String linkColumn, String queryColumn)
  {
    this(column, linkTable, linkColumn, queryColumn, null, null);
  }

  /**
   * 构造函数
   * @param column column对象
   * @param linkTable 此列相关联的表名
   * @param linkColumn 与此列相关联的字段名
   * @param queryColumn 用于查询的相关联的表的其他字段
   * @param extendName 附加的字段名，用于取显示字段的caption, 若为null, 将不起作用<br>
   * 1.类QueryForm的处理是将该字段要显示的名称, extendName=null时，用queryColumn, extendName!=null时，用extendName<br>
   * 2.类QueryFixedItem的处理，仅用于区别两个相同字段的不同处理
   * @param opersign 操作符号, >, <, =, >=, <=, <>,IN, 默认为LIKE
   */
  public QueryColumn(Column column, String linkTable, String linkColumn, String queryColumn,
                     String extendName, String opersign)
  {
    super.column = column;
    if(linkTable==null || (linkTable!= null && linkTable.equals("")) ||
       linkColumn ==null || (linkColumn != null && linkColumn.equals("")) ||
       queryColumn ==null || (queryColumn != null && queryColumn.equals("")))
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

    setOperSign(opersign);
  }

  /**
   * 得到字段名称
   * @return 返回字段名称
   */
  public String getColumnName()
  {
    return getColumn().getColumnName();
  }
}
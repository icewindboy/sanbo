package engine.project;

import java.util.*;
import javax.servlet.http.*;

import engine.dataset.*;
import engine.dataset.sql.Expression;
import engine.dataset.sql.QueryWhereField;
import engine.util.*;
import com.borland.jb.util.FastStringBuffer;
import com.borland.dx.dataset.Variant;
import com.borland.dx.dataset.DataSet;
/**
 * <p>Title: 固定查询项目</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */

public final class QueryFixedItem extends QueryBasic implements Expression
{
  /**
   * 添加显示地列名
   * @param tableName 表名.若为“”表示默认的表名
   * @param columns 需要显示列名列表
   */
  public void addShowColumn(String tableName, QueryWhereField[] columns)
  {
    for(int i=0; i<columns.length; i++)
      addShowColumn(tableName, columns[i]);
  }

  /**
   * 添加显示地列名
   * @param tableName 表名.若为“”表示默认的表名
   * @param column 需要显示列名
   */
  public void addShowColumn(String tableName, QueryWhereField column)
  {
    String columnName = column.getColumnName().toUpperCase();
    this.columnNames.add(tableName != null && tableName.length()>0 ? tableName.toUpperCase()+"$"+columnName : columnName);
    this.columnsArray.add(column);
  }

  /**
   * 设置查询的div点击确定时, 所有输入框的值
   * @param request web的请求
   */
  public void setSearchValue(HttpServletRequest request)
  {
    searchRow.put(request);
/*    Enumeration keys = request.getParameterNames();
    String key = null, value = null;
    while(keys.hasMoreElements())
    {
      key = (String)keys.nextElement();
      String uppperKey = key.toUpperCase();
      if(columnNames.indexOf(uppperKey) > -1)
      {
        value = request.getParameter(key);
        if(value == null)
          value = "";
        else
          value = value.trim();

        searchRow.put(uppperKey, value);
      }
    }
    */
  }

  /**
   * 得到查询转化为SQL语句的WHERE子句的条件
   * @return WHERE子句的条件
   */
  public String getWhereQuery()
  {
    FastStringBuffer buf = new FastStringBuffer();
    String value= null, item=null, opsign=null, extendname=null;
    for(int i=0; i<columnNames.size(); i++)
    {
      item = (String)columnNames.get(i);
      if(item == null || item.equals(""))
        continue;

      QueryWhereField col = (QueryWhereField)columnsArray.get(i);
      extendname = col.getExtendName();
      value = searchRow.get(extendname == null ? item : item+"$"+extendname);
      if(value.equals(""))
        continue;

      opsign = col.getOperSign();
      boolean isLike = opsign.equals(LIKE);
      boolean isLeftLike = opsign.equals(LEFT_LIKE);
      boolean isRightLike = opsign.equals(RIGHT_LIKE);
      if(isLeftLike || isRightLike)
        opsign = LIKE;
      isLeftLike = isLike || isLeftLike;
      isRightLike = isLike || isRightLike;
      //
      boolean isIn = opsign.equals(IN);
      //boolean isLike = opsign.equals(LIKE);//LIKE
      //boolean isIn = opsign.equals(IN);//IN
      int dataType= col.getColumn().getDataType();
      boolean isDate = dataType == Variant.DATE || dataType == Variant.TIMESTAMP;
      buf.append(" ");
      String field = item.replace('$','.');
      buf.append(field);
      if(col.getLinkTable() == null)
      {
        //判断日期格式的正确性, 不正确的丢弃
        if(isDate){
          try{
            java.sql.Date.valueOf(value);;
          }
          catch(Exception ex){
            continue;
          }
        }
        buf.append(" ");
        if(!isDate)
          buf.append(opsign);
        else if(isLike) //是date 并 是like。就直接用＝号
          buf.append("=to_date(");
        else
        {
          buf.append(opsign);
          buf.append("to_date(");
        }
        buf.append(isDate ? "'" : (isRightLike ? "'%" : isIn ? "(" : "'"));
        //buf.append(isDate ? "'" : (isLike ? "'%" : isIn ? "(" : "'"));
        if(!col.isShow())
          value = StringUtils.replaceQuotatemark(value);
        value = StringUtils.stripEnterSymbol(value);
        buf.append(value);
        buf.append(isDate ? "','YYYY-MM-DD') " : (isLike ? "%' " : isIn ? " )" : "' "));
      }
      else
      {
        buf.append(" IN (SELECT ");
        buf.append(col.getLinkColumn());
        buf.append(" FROM ");
        buf.append(col.getLinkTable());
        buf.append(" WHERE ");
        buf.append(col.getQueryColumn());
        buf.append(" ");
        if(!isDate)
          buf.append(opsign);
        else if(isLike) //是date 并 是like。就直接用＝号
          buf.append("=to_date(");
        else
        {
          buf.append(opsign);
          buf.append("to_date(");
        }

        buf.append(isDate ? "'" : (isRightLike ? "'%" : "'"));
        if(!col.isShow())
          value = StringUtils.replaceQuotatemark(value);
        value = StringUtils.stripEnterSymbol(value);
        buf.append(value);
        buf.append(isDate ? "','YYYY-MM-DD')) " : (isLeftLike ? "%') " : "') "));
      }
      buf.append("AND");
    }
    String temp = buf.toString();
    buf = null;
    //if(temp.endsWith("AND"))
    if(temp.length() > 3)
      temp = temp.substring(0, temp.length()-3);
    return temp;
  }


}
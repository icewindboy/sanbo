package engine.dataset.sql;

import java.util.ArrayList;
import engine.dataset.RowMap;
import engine.util.StringUtils;
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

public final class DefaultQueryWhere extends QueryWhere
{

  /**
   * 添加显示地列名
   * @param tableName 表名.若为“”表示默认的表名
   * @param column 需要显示列名
   */
  public void addWhereField(String tableName, QueryWhereField column)
  {
    String columnName = column.getColumnName().toUpperCase();
    this.columnNames.add(tableName != null && tableName.length()>0 ? tableName.toUpperCase()+"$"+columnName : columnName);
    this.columnsArray.add(column);
  }

  /**
   * 得到查询转化为SQL语句的WHERE子句的条件
   * @return WHERE子句的条件
   */
  public String getWhereQuery()
  {
    if(whereValueRow == null)
      return "";
    ArrayList sqlList = new ArrayList(columnNames.size());
    StringBuffer buf = null;
    String value= null, item=null, opsign=null, extendname=null;
    String[] values = null;
    for(int i=0; i<columnNames.size(); i++)
    {
      item = (String)columnNames.get(i);
      if(item == null || item.length() == 0)
        continue;

      QueryWhereField col = (QueryWhereField)columnsArray.get(i);
      extendname = col.getExtendName();
      boolean isMulti = col.getType().equals(col.MULTI);
      String colKey = extendname == null ? item : item+"$"+extendname;
      if(isMulti)
        values = whereValueRow.getValues(colKey);
      else
        value = whereValueRow.get(colKey);

      if((isMulti && values == null) || (!isMulti && value.length() == 0))
        continue;

      opsign = col.getOperSign();
      boolean isLike = opsign.equals(LIKE);
      boolean isNotLike = opsign.equals(NOT_LIKE);
      boolean isLeftLike = opsign.equals(LEFT_LIKE);
      boolean isRightLike = opsign.equals(RIGHT_LIKE);
      if(!isNotLike && (isLeftLike || isRightLike))
        opsign = LIKE;
      isLeftLike = isLike || isNotLike || isLeftLike;
      isRightLike = isLike || isNotLike|| isRightLike;

      buf = new StringBuffer(" ");
      String field = item.replace('$','.');
      buf.append(field);
      int dataType= col.getIntDataType();
      boolean isDate = dataType == Variant.DATE || dataType == Variant.TIMESTAMP;

      if(col.getLinkTable() == null)
      {
        if(!isMulti)
        {
          boolean isIn = opsign.equals(IN);
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
          else if(isLike)
            buf.append("=to_date(");
          else if(isNotLike)
            buf.append("<>to_date(");
          else
          {
            buf.append(opsign);
            buf.append("to_date(");
          }

          buf.append(isDate ? "'" : (isRightLike ? "'%" : isIn ? "(" : "'"));
          if(!col.isShow()){
            value = StringUtils.replaceQuotatemark(value);
          }
          value = StringUtils.stripEnterSymbol(value);
          buf.append(value);
          buf.append(isDate ? "','YYYY-MM-DD') " : (isLeftLike ? "%' " : isIn ? " ) " : "' "));
        }
        else
          buf.append(" IN (").append(StringUtils.getArrayValue(values, ",")).append(") ");
      }
      else
      {
        buf.append(" IN (SELECT ");
        buf.append(col.getLinkColumn());
        buf.append(" FROM ");
        buf.append(col.getLinkTable());
        buf.append(" WHERE ");
        buf.append(col.getQueryColumn());
        if(!isMulti)
        {
          buf.append(" ");
          if(!isDate)
            buf.append(opsign);
          else if(isLike)
            buf.append("=to_date(");
          else if(isNotLike)
            buf.append("<>to_date(");
          else
          {
            buf.append(opsign);
            buf.append("to_date(");
          }

          buf.append(isDate ? "'" : (isRightLike ? "'%" : "'"));
          if(!col.isShow()){
            value = StringUtils.replaceQuotatemark(value);
          }
          value = StringUtils.stripEnterSymbol(value);
          buf.append(value);
          buf.append(isDate ? "','YYYY-MM-DD')) " : (isLeftLike ? "%') " : "') "));
        }
        else
          buf.append(" IN (").append(StringUtils.getArrayValue(values, ",")).append(") ");
      }
      sqlList.add(buf);
      sqlList.add(col.getLinkSign());
    }
    if(sqlList.size() < 1)
      return "";
    else
    {
      StringBuffer sqlBuf = new StringBuffer();
      for(int i=0; i<sqlList.size()-1; i++)
        sqlBuf.append(sqlList.get(i));
      return sqlBuf.toString();
    }
  }
}

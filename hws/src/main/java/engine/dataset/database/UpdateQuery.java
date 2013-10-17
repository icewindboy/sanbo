package engine.dataset.database;

import java.sql.SQLException;
import com.borland.dx.dataset.*;
import com.borland.dx.sql.dataset.*;

/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

class UpdateQuery extends BaseQuery
{
  private static final char[] UPDATE = {'U','P','D','A','T','E',' '};
  private static final char[] SET    = {' ','S','E','T',' '};
  private static final char[] NULL   = {' ','=',' ','N','U','L','L'};

  /**
   * 构造函数
   * @param dbProvider 数据库处理对象
   * @param queryTimeout 运行SQL语句的没有响应的失效时间
   */
  public UpdateQuery(Database dbProvider, int queryTimeout)
  {
    super(dbProvider, queryTimeout);
  }
  /**
   * 设置UPDATE SQL语句的参数
   * @param tableName 表名
   * @param changeColumn 更改过的列
   * @param changeValues 更改过的列的新值
   * @param whereColumn  where条件的列
   * @param whereValues  where条件的列的值
   * @throws SQLException SQL异常
   * @throws DataSetException DataSet异常
   */
  final void setParameters(String tableName, Column[] changeColumns, Variant[] changeValues,
                           Column[] whereColumns, Variant[] whereValues)
      throws SQLException, DataSetException
  {
    if (bufQuery == null)
      bufQuery = new StringBuffer(128);
    else
      bufQuery.setLength(0);

    bufQuery.append(UPDATE);
    bufQuery.append(tableName);
    bufQuery.append(SET);

    boolean firstTime = true;

    for (int index = 0; index < changeColumns.length; index++)
    {
      if (!firstTime)
        bufQuery.append(COMMA);

      firstTime = false;

      columnString(changeColumns[index], bufQuery);

      if (changeValues[index].isNull())
        bufQuery.append(NULL);
      else
        bufQuery.append(PARAM);
    }

    if(whereColumns.length > 0)
      whereClause(whereColumns, whereValues);

    prepare();

    int paramIndex = setFieldParameters(changeColumns, changeValues);

    setWhereParameters(paramIndex, whereColumns, whereValues);
  }
}

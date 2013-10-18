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

class DeleteQuery extends BaseQuery
{
  private static final char[] DELETE = {'D','E','L','E','T','E',' ','F','R','O','M',' '};//"DELETE FROM "
  private static final char   BLANK  = ' ';//" "

  /**
   * 构造函数
   * @param dbProvider 数据库处理对象
   * @param queryTimeout 运行SQL语句的没有响应的失效时间
   */
  public DeleteQuery(Database dbProvider, int queryTimeout)
  {
    super(dbProvider, queryTimeout);
  }

  /**
   * 设置DELETE SQL语句的参数
   * @param tableName 表名
   * @param whereColumns WHERE子句的各个列
   * @param whereValues  WHERE子句的各个列相对应的值
   * @throws SQLException SQL异常
   * @throws DataSetException DataSet异常
   */
  final void setParameters(String tableName, Column[] whereColumns, Variant[] whereValues)
      throws SQLException, DataSetException
  {
    if (bufQuery == null) {
      bufQuery = new StringBuffer(128);
    }
    else
      bufQuery.setLength(0);

    bufQuery.append(DELETE);
    bufQuery.append(tableName);
    bufQuery.append(BLANK);
    if(whereColumns.length > 0)
      whereClause(whereColumns, whereValues);
    prepare();

    setWhereParameters(0, whereColumns, whereValues);
  }
}



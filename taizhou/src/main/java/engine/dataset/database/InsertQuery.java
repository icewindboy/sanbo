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

class InsertQuery extends BaseQuery
{
  private static final char[] INSERT = {'I','N','S','E','R','T',' ','I','N','T','O',' '};
  private static final char[] VALUES = {')',' ','V','A','L','U','E','S',' ','('};
  private static final char[] NULL   = {'N','U','L','L'};
  private static final char   PARAMATER      = '?';
  private static final char   LEFT_BRACKET   = '(';
  private static final char   RIGHT_BRACKET  = ')';
  private static final char   BLANK  = ' ';

  private StringBuffer insertBuf;
  private StringBuffer paramBuf;
  /**
   * 构造函数
   * @param dbProvider 数据库处理对象
   * @param queryTimeout 运行SQL语句的没有响应的失效时间
   */
  public InsertQuery(Database dbProvider, int queryTimeout)
  {
    super(dbProvider, queryTimeout);
  }

  /**
   * 设置INSERT INTO SQL语句的参数
   * @param tableName 被操作的表名
   * @param columns 被操作的字段数组
   * @param values 被操作的字段相应的值
   * @throws SQLException SQL异常
   * @throws DataSetException DataSet异常
   */
  final void setParameters(String tableName, Column[] columns, Variant[] values)
      throws SQLException, DataSetException
  {
    if (insertBuf == null)
      insertBuf = new StringBuffer(128);
    else
      insertBuf.setLength(0);

    if (paramBuf == null)
      paramBuf = new StringBuffer(128);
    else
      paramBuf.setLength(0);

    insertBuf.append(INSERT);
    paramBuf.append(VALUES);

    insertBuf.append(tableName);
    insertBuf.append(BLANK);
    insertBuf.append(LEFT_BRACKET);

    boolean first = true;

    for (int index = 0; index < columns.length; index++)
    {
      if (!first)
      {
        insertBuf.append(COMMA);
        paramBuf.append(COMMA);
      }
      first = false;
      columnString(columns[index], insertBuf);

      if (values[index].isNull())
        paramBuf.append(NULL);
      else
        paramBuf.append(PARAMATER);
    }

    if(bufQuery == null)
      bufQuery = new StringBuffer(insertBuf.length() + paramBuf.length());
    else
      bufQuery.setLength(0);

    bufQuery.append(insertBuf);
    bufQuery.append(paramBuf);
    bufQuery.append(RIGHT_BRACKET);

    prepare();

    setFieldParameters(columns, values);
  }
}
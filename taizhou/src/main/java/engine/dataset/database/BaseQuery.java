package engine.dataset.database;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.util.ArrayList;

import engine.util.log.Log;
import engine.util.StringUtils;
import engine.dataset.database.DatabaseProvider;
import engine.dataset.database.RuntimeMetaData;

import com.borland.jb.io.InputStreamToByteArray;
import com.borland.jb.util.FastStringBuffer;
import com.borland.dx.dataset.Variant;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.Column;
//import com.borland.dx.sql.dataset.*;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */
abstract class BaseQuery
{
  /*
  // Status indicators for query optimization (see below)
  final static int            nullRowId     = 0x01;
  final static int            rowId         = 0x02;
  final static int            wasNull     = 0x04;
  final static int            assignedValue = 0x08;
  final static int            changedValue  = 0x10;
  */
  //用与SQL有关的常量
  protected static final char[] WHERE  = {' ','W','H','E','R','E',' '};//" WHERE "
  protected static final char[] AND    = {' ','A','N','D',' '};        //" AND "
  protected static final char   COMMA  = ',';                          //逗号
  protected static final char[] ISNULL = {' ','I','S',' ','N','U','L','L'};//" = NULL"
  protected static final char[] PARAM  = {' ','=',' ','?'};            //" = ?"
  //protected static Log log = new Log("DAO");

  //用于生成SQL语句 Generated query string
  private   boolean           useObjectForStrings;
  private   boolean           useSpacePadding;
  protected char              quoteCharacter;
  boolean                     reuseSaveStatements;
  boolean                     useClearParameters;
  protected Database          dbProvider = null;
  //
  protected StringBuffer      bufQuery;  //SQL语句
  private   int               queryTimeout;//执行SQL失去响应的时间
  protected PreparedStatement preparedStatement;

  /**
   * 构造函数
   * @param dbProvider 数据库处理对象
   * @param queryTimeout 执行sql的过期试讲
   */
  public BaseQuery(Database dbProvider, int queryTimeout) {
    this.dbProvider     = dbProvider;
    quoteCharacter      = dbProvider.getRuntimeMetaData().getIdentifierQuoteChar();
    useObjectForStrings = dbProvider.getRuntimeMetaData().isUseSetObjectForStrings();
    useSpacePadding     = dbProvider.getRuntimeMetaData().isUseSpacePadding();
    reuseSaveStatements = dbProvider.getRuntimeMetaData().isReuseSaveStatements();
//    useClearParameters  = database.isUseClearParameters();
    queryTimeout        = queryTimeout;
  }

  /**
   * 关闭preparedStatement
   * @throws SQLException SQL异常
   */
  final void close() throws SQLException {
    if (preparedStatement != null) {
      preparedStatement.close();
      preparedStatement = null;
    }
  }

  /**
   * 得到SQL语句, 用于UPDATE的SQL语句
   * @return SQL语句
   */
  final String getQueryString(Variant[] changevalues, Variant[] wherevalues) {
    int length = (changevalues == null ? 0 : changevalues.length) +
                 (wherevalues == null ? 0 : wherevalues.length);
    ArrayList list = new ArrayList(length);
    for(int i=0; changevalues!=null && i<changevalues.length; i++)
      list.add(changevalues[i]);
    for(int i=0; wherevalues!=null && i<wherevalues.length; i++)
      list.add(wherevalues[i]);

    Variant[] values = (Variant[])list.toArray(new Variant[list.size()]);
    return getQueryString(values);
  }

  /**
   * 得到SQL语句
   * @return SQL语句
   */
  final String getQueryString(Variant[] values)
  {
    String sql = bufQuery.toString();
    if(values == null || values.length == 0)
      return sql;
    String[] params = new String[values.length];
    for(int i=0; i<values.length; i++)
    {
      switch (values[i].getType())
      {
        case Variant.STRING:
        case Variant.BIGDECIMAL:
        case Variant.BYTE:
        case Variant.SHORT:
        case Variant.INT:
        case Variant.BOOLEAN:
        case Variant.LONG:
        case Variant.FLOAT:
        case Variant.DOUBLE:
        case Variant.TIMESTAMP:
        case Variant.DATE:
        case Variant.TIME:
          params[i] = values[i].toString();
          break;
        case Variant.INPUTSTREAM:
          params[i] = "INPUTSTREAM";
          break;
        case Variant.OBJECT:
          params[i] = "OBJECT";
          break;
        case Variant.UNASSIGNED_NULL:
        case Variant.ASSIGNED_NULL:
          params[i] = "NULL";
          break;
        default:
          params[i] = "UNRECOGNIZED";
          break;
      }
    }
    sql = StringUtils.combine(sql, "?", params);
    return sql;
  }

  /**
   * 执行SQL语句
   * @return 返回执行后的结果
   * @throws SQLException SQL异常
   */
  final int execute() throws SQLException
  {
    return preparedStatement.executeUpdate();
  }

  /**
   * 准备执行SQL语句，创建preparedStatement
   * @throws SQLException SQL异常
   * @throws DataSetException DataSet异常
   */
  protected final void prepare() throws SQLException,DataSetException
  {
    close();

    preparedStatement = dbProvider.getConnection().prepareStatement(bufQuery.toString());
    if (queryTimeout != 0)
      preparedStatement.setQueryTimeout(queryTimeout);
  }

  /**
   * 设置SQL中的参数值（即?）
   * @param index 参数的位置
   * @param column 列名
   * @param value 列值
   * @throws SQLException SQL异常
   * @throws DataSetException DataSet异常
   */
  protected final void setParameter(int index, Column column, Variant value)
    throws SQLException, DataSetException
  {
    if (preparedStatement == null)
      prepare();

    index++;

    switch (value.getType())
    {
      case Variant.STRING:
        int sqlType1        = column.getSqlType();
        String stringValue  = value.getString();
        if (!useObjectForStrings)
          preparedStatement.setString(index, stringValue);
        else if (sqlType1 == java.sql.Types.NULL)
          preparedStatement.setObject(index, stringValue, java.sql.Types.VARCHAR, 0);
        else if (sqlType1 == java.sql.Types.LONGVARCHAR
              || sqlType1 == java.sql.Types.VARCHAR
              || sqlType1 == java.sql.Types.CLOB)
          preparedStatement.setObject(index, stringValue, sqlType1, 0);
        else {
          if (useSpacePadding) {
            int     precision = column.getPrecision();
            int     length    = stringValue.length();
            if ((precision != -1) && (length < precision)) {
              FastStringBuffer  buf = new FastStringBuffer(precision);

              buf.append(stringValue);
              while (length < precision) {
                buf.append(' ');
                length++;
              }

              preparedStatement.setObject(index, buf.toString(), java.sql.Types.CHAR, 0);
            }
            else
              preparedStatement.setString(index, stringValue);
          }
          else {
            preparedStatement.setString(index, stringValue);
          }
        }
        break;

      case Variant.BIGDECIMAL:
        BigDecimal bigDecimal = value.getBigDecimal();
        if (bigDecimal == null)
          preparedStatement.setNull(index, column.getSqlType());
        else {
          preparedStatement.setBigDecimal(index, bigDecimal);
        }
        break;

      case Variant.BYTE:
        preparedStatement.setByte(index, value.getByte());
        break;

      case Variant.SHORT:
        preparedStatement.setShort(index, value.getShort());
        break;

      case Variant.INT:
        preparedStatement.setInt(index, value.getInt());
        break;

      case Variant.BOOLEAN:
        preparedStatement.setBoolean(index, value.getBoolean());
        break;

      case Variant.LONG:
        preparedStatement.setLong(index, value.getLong());
        break;

      case Variant.FLOAT:
        preparedStatement.setFloat(index, (float)value.getAsDouble());
        break;

      case Variant.DOUBLE:
        preparedStatement.setDouble(index, value.getDouble());
        break;

      case Variant.INPUTSTREAM:
        InputStream stream  = value.getInputStream();
        if (stream == null)
          preparedStatement.setNull(index, column.getSqlType());
        else {
          int length  = 0;
          try {
            length = stream.available();
          }
          catch (IOException ex) {
            DataSetException.IOException(ex);
          }

          int sqlType = column.getSqlType();
          if (!dbProvider.getRuntimeMetaData().isUseSetObjectForStreams())
            sqlType = 0; // Force use of setInputStream.
          switch (sqlType) {
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.BLOB:
              byte[] bytes = null;
              try {
                bytes = InputStreamToByteArray.getBytes(stream);
              }
              catch (IOException ex) {
                DataSetException.IOException(ex);
              }
              preparedStatement.setObject(index, bytes, sqlType, 0);
              break;
            default:
              preparedStatement.setBinaryStream(index, stream, length);
          }
        }
        break;

      case Variant.TIMESTAMP:
        preparedStatement.setTimestamp(index, value.getTimestamp());
        break;

      case Variant.DATE:
        preparedStatement.setDate(index, (java.sql.Date)value.getDate());
        break;

      case Variant.TIME:
        preparedStatement.setTime(index, value.getTime());
        break;

      case Variant.OBJECT:
        preparedStatement.setObject(index, value.getObject());
        break;

      case Variant.UNASSIGNED_NULL:
      case Variant.ASSIGNED_NULL:
        preparedStatement.setNull(index, column.getSqlType());
      break;
      default:
        DataSetException.unrecognizedDataType();
        break;
    }
  }

  /**
   * 设置SQL语句中的字段名称
   * @param column 列
   * @param buf SQL语句
   */
  protected final void columnString(Column column, StringBuffer buf) {
    String result = column.getServerColumnName();
    if (quoteCharacter != '\0') {
      // The following is a workaround for DataGateway.
      // Since the local SQL parser doesn't accept identifier quotes,
      // but Paradox tables allows spaces in their column names, this is
      // a way to fool the parser into accepting these columns.
      {
        if (dbProvider.getRuntimeMetaData().isUseTableName()) {
          String tableName = column.getTableName();
          if (tableName == null)
            tableName = column.getDataSet().getTableName();
          buf.append(tableName);
          buf.append('.');
        }
        buf.append(quoteCharacter);
        buf.append(result);
        buf.append(quoteCharacter);
      }
    }
    else
      buf.append(result);
  }

  /**
   * 设置WHERE条件子句
   * @param whereColumns where条件的列
   * @param whereValues where条件的列的值
   */
  protected final void whereClause(Column[] whereColumns, Variant[] whereValues)
  {
    boolean firstTime = true;
    bufQuery.append(WHERE);//" WHERE "
    for (int index = 0; index < whereColumns.length; index++)
    {
      if (!firstTime)
        bufQuery.append(AND);//" AND "

      firstTime = false;

      columnString(whereColumns[index], bufQuery);

      if (whereValues[index].isNull())
        bufQuery.append(ISNULL);  //" IS NULL"
      else
        bufQuery.append(PARAM);//" = ?"
    }
  }

  /**
   * 设置字段的参数
   * @param whereColumns where条件的列
   * @param whereValues  where条件的列的值
   * @return 返回最后一个参数的计数器
   * @throws SQLException SQLyichang
   */
  protected final int setFieldParameters(Column[] changeColumns, Variant[] changeValues)
    throws SQLException
  {
    int parameterNumber =0;
    for (int index = 0; index <changeColumns.length; index++)
    {
      Column  column = changeColumns[index];
      Variant value = changeValues[index];
      if(!value.isNull())
      {
        setParameter(parameterNumber, column, value);
        parameterNumber++;
      }
    }
    return parameterNumber;
  }
  /**
   * 设置WHERE条件的参数
   * @param whereColumns where条件的列
   * @param whereValues  where条件的列的值
   * @throws SQLException SQLyichang
   */
  protected final void setWhereParameters(int parameterBegin, Column[] whereColumns, Variant[] whereValues)
    throws SQLException
  {
    for (int index = 0; index <whereColumns.length; index++)
    {
      Column  column = whereColumns[index];
      Variant value = whereValues[index];
      if(!value.isNull())
      {
        setParameter(parameterBegin, column, value);
        parameterBegin++;
      }
    }
  }
}
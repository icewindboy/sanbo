package engine.dataset.database;

/**
 * <p>Title: ResultSet转化类</p>
 * <p>Description: DataSet类型的转化，包括将ResaultSet转化为StorgeDataSet或DataSetData
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

import engine.util.log.Log;
import engine.dataset.*;
import com.borland.dx.dataset.*;
import com.borland.dx.text.Alignment;
import com.borland.jb.io.InputStreamToByteArray;

import java.sql.*;
import java.math.BigDecimal;
import java.io.*;
import java.util.*;

public class ResultSetTransform implements LoadCancel{

  private static    final long serialVersionUID = 1L;

  private Log log = new Log(getClass());
  private transient byte[]   binaryBytes;
  private transient boolean  cancel;
  private transient boolean  settingMetadata;
  private transient boolean  loadRowByRow;
  private transient boolean  isAsyncLoad;

  private transient Coercer  coercer;
  private           int      loadStatus  = RowStatus.LOADED;

  /**
   * 取消装载
   */
  public final void cancelLoad() {
    cancel = true;
  }
  /**
   * 将ResaultSet转化为StorgeDataSet
   * @param runtimeMetaData 数据库运行信息
   * @param dataSet 需要填充数据的数据集
   * @param result 需要转化数据的ResultSet
   */
  public void resultSetToDataSet(RuntimeMetaData runtimeMetaData, StorageDataSet dataSet, ResultSet result)
  {
    resultSetToDataSet(runtimeMetaData, dataSet, result, null);
  }
  /**
   * 将ResaultSet转化为StorgeDataSet
   * @param runtimeMetaData 数据库运行信息
   * @param dataSet 需要填充数据的数据集
   * @param result 需要转化数据的ResultSet
   * @param rowmin 要装载的最小的记录数, 小于0, 该参数失效 从0开始
   * @param rowmax 要装载的最大的记录数, 小于0, 该参数失效 到count结束
   */
  public void resultSetToDataSet(RuntimeMetaData runtimeMetaData, StorageDataSet dataSet,
    ResultSet result, ProvideInfo provideInfo)
  {
    if(provideInfo == null)
      provideInfo = new ProvideInfo();
    _resultSetToDataSet(runtimeMetaData, dataSet, result, provideInfo);
  }

  /**
   * 将ResaultSet转化为DataSetData
   * @param runtimeMetaData 数据库运行信息
   * @param result 需要转化数据的ResultSet
   * @return 返回转化后的DataSetData
   */
  public DataSetData resultSetToDataSetData(RuntimeMetaData runtimeMetaData, ResultSet result)
  {
    return resultSetToDataSetData(runtimeMetaData, result, null);
  }

  /**
   * 将ResaultSet转化为DataSetData
   * @param runtimeMetaData 数据库运行信息
   * @param result 需要转化数据的ResultSet
   * @param ProvideInfo 提供数据信息的对象
   * @return 返回转化后的DataSetData
   */
  public DataSetData resultSetToDataSetData(RuntimeMetaData runtimeMetaData, ResultSet result,
      ProvideInfo provideInfo)
  {
    if(provideInfo == null)
      provideInfo = new ProvideInfo();
    TableDataSet dataSet = new TableDataSet();
    resultSetToDataSet(runtimeMetaData, dataSet, result, provideInfo);
    DataSetData dsd = DataSetData.extractDataSet(dataSet);
    provideInfo.setProvideData(dsd);
    dataSet.close();
    dataSet = null;
    return dsd;
  }

  /**
   * 得到ResaultSet的总记录数
   * @param result 需要得到总记录数量的ResultSet
   * @return 返回ResultSet的总记录数
   */
  public static synchronized int getResultSetCount(ResultSet result) throws SQLException
  {
    int count =0;
    boolean cusorForwardOnly = result.getType() == ResultSet.TYPE_FORWARD_ONLY;
    if(cusorForwardOnly)
    {
      while (result.next()){
        count++;
      }
    }
    else
    {
      result.last();
      count = result.getRow();
    }
    return count;
  }

  /**
   * 私有的方法。将ResaultSet转化为StorgeDataSet
   * @param runtimeMetaData 数据库运行信息
   * @param dataSet 需要填充数据的数据集
   * @param result 需要转化数据的ResultSet
   * @param ProvideInfo 提供数据信息的对象
   */
  private void _resultSetToDataSet(RuntimeMetaData runtimeMetaData, StorageDataSet dataSet,
                                   ResultSet result, ProvideInfo provideInfo)
  {
    synchronized(dataSet)
    {
      if (result != null && dataSet != null)
      {
        dataSet.empty();
        try {
          Column[] columns = runtimeMetaData.processMetaData(dataSet.getMetaDataUpdate(),
              result, provideInfo.columnIsUseString());

          boolean isCompatible = compatibleColumns(dataSet, columns);
          int[] columnMap = ProviderHelp.initData(dataSet, columns, !isCompatible, false);//, false);

          copyResult(dataSet, result, columnMap, provideInfo);
        }
        catch (SQLException ex) {
          DataSetException.SQLException(ex);
        }
        catch (IOException ex) {
          DataSetException.throwExceptionChain(ex);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  /**
   * 检查字段是否兼容
   * @param sds 数据集
   * @param columns 新的字段数组
   * @return 是否兼容
   */
  private final boolean compatibleColumns(StorageDataSet sds, Column[] columns)
    /*-throws DataSetException-*/
  {
    if (sds.getColumnCount() < columns.length)
      return false;
    Column dsColumn;
    Column column;
    for (int ordinal = 0; ordinal < columns.length; ++ordinal) {
      column    = columns[ordinal];
      dsColumn  = sds.hasColumn(column.getColumnName());
      if (dsColumn == null)
        return false;
      if (dsColumn.getDataType() != column.getDataType())
        return false;
      if (dsColumn.isRowId() != column.isRowId())
        return false;
      if (dsColumn.isResolvable() != column.isResolvable())
        return false;
      if (dsColumn.getPrecision() != column.getPrecision())
        return false;
      if (dsColumn.getScale() != column.getScale())
        return false;
    }
    return true;
  }

  /**
   * 拷贝ResultSet的数据到StorageDataSet中
   * @param dataSet StorageDataSet的数据集
   * @param result ResultSet
   * @param columnMap 各个列的次序的数组
   * @param rowmin 要装载的最小的记录数, 小于0, 该参数失效
   * @param rowmax 要装载的最大的记录数, 小于0, 该参数失效
   * @throws SQLException SQLException 抛出SQL异常
   * @throws IOException 抛出IO异常
   * @throws DataSetException 抛出DataSetException异常
   */
  private void copyResult(StorageDataSet dataSet, ResultSet result,
                          int columnMap[], ProvideInfo provideInfo)
    throws SQLException, IOException, DataSetException
  {
    int rowmin = provideInfo.getResultSetMin();
    int rowmax = provideInfo.getResultSetMax();
    if (rowmax ==0 || (rowmin>rowmax && rowmax>0))
    {
      release();
      dataSet.startLoading(this, loadStatus, isAsyncLoad, loadRowByRow);
      dataSet.endLoading();
      return;
    }

    boolean cusorForwardOnly = result.getType() == ResultSet.TYPE_FORWARD_ONLY;

    if (!result.next())
    {
      release();
      dataSet.startLoading(this, loadStatus, isAsyncLoad, loadRowByRow);
      dataSet.endLoading();
      return;
    }

    Variant[] variants = null;
    Variant[] loadVariants  = dataSet.startLoading(this, loadStatus, isAsyncLoad, loadRowByRow);
    if (coercer != null)
      variants  = coercer.init(loadVariants);
    else
      variants  = loadVariants;

    Column[] loadColumns    = dataSet.getColumns();

    int lastMap         = columnMap.length + 1;
    boolean earlyBreak  = false;
    boolean copyStreams = ProviderHelp.isCopyProviderStreams(dataSet);

    try
    {
      boolean rightTrimStrings  = true;
      cancel  = false;
      Variant value;
      int ordinal;
      int tempInt;
      java.io.InputStream tempStream;
      String tempString;
      BigDecimal tempBigDecimal;
      boolean tempBoolean;
      byte tempByte;
      short tempShort;
      long tempLong;
      float tempFloat;
      double tempDouble;
      java.sql.Date tempDate;
      java.sql.Time tempTime;
      java.sql.Timestamp tempTimestamp;
      Object tempObject;

      int currentRows = 1;
      if(rowmin >1 && cusorForwardOnly)
      {
        while(true)  {
          if(currentRows >= rowmin)
            break;
          if(!result.next())
          {
            earlyBreak = true;
            return;
          }
          currentRows++;
        }
      }
      else if(rowmin >1)
      {
        try{
          //long start = System.currentTimeMillis();
          result.absolute(rowmin);
          //System.out.println("absolute:"+(System.currentTimeMillis()-start));
          currentRows = rowmin;
        }
        catch(SQLException sql){};
      }

      do {
        for (int index=0; ++index < lastMap;)
        {
          ordinal = columnMap[index-1];
          value   = variants[ordinal];

          switch(value.getSetType()) {
            case Variant.STRING:
              switch(loadColumns[ordinal].getSqlType())
              {
                case java.sql.Types.CLOB:
                  Clob clob = result.getClob(index);
                  int length = new Long(clob.length()).intValue();
                  tempString = clob.getSubString(1, length);
                  break;

                case java.sql.Types.CHAR:
                  tempString = result.getString(index);
                  if(tempString != null && rightTrimStrings)
                    tempString = trimRight(tempString);
                  break;

                case java.sql.Types.TIMESTAMP:
                  Timestamp timestamp = result.getTimestamp(index);
                  if(timestamp == null)
                  {
                    tempString = "";
                    break;
                  }
                  long remain = timestamp.getTime();
                  remain = (remain+28800000)%8640000;
                  if(remain == 0)
                    tempString = engine.util.Format.TimestampToStr(timestamp, EngineDataSet.defaultDateFormat);
                  else
                    tempString = engine.util.Format.TimestampToStr(timestamp, EngineDataSet.defaultDateTimeFormat);
                  break;

                default:
                  tempString = result.getString(index);
              }

              if (tempString != null)
                value.setString(tempString);
              else
                value.setAssignedNull();
              break;

            case Variant.INPUTSTREAM:
              tempStream = result.getBinaryStream(index);
              if (tempStream != null)
              {
                if (copyStreams || tempStream instanceof sun.jdbc.odbc.JdbcOdbcInputStream)
                  tempStream = copyByteStream(tempStream);
                if (tempStream != null)
                  value.setInputStream(tempStream);
                else
                  value.setAssignedNull();
              }
              else
                value.setAssignedNull();
              break;

            case Variant.BIGDECIMAL:
              tempBigDecimal = result.getBigDecimal(index);
              if (tempBigDecimal != null)
                value.setBigDecimal(tempBigDecimal);
              else
                value.setAssignedNull();
              break;

            case Variant.INT:
              tempInt = result.getInt(index);
              if (tempInt == 0 && result.wasNull())
                value.setAssignedNull();
              else
                value.setInt(tempInt);
              break;


            case Variant.BOOLEAN:
              tempBoolean = result.getBoolean(index);
              if (!result.wasNull())
                value.setBoolean(tempBoolean);
              else
                value.setAssignedNull();
              break;

            case Variant.BYTE:
              tempByte = result.getByte(index);
              if (!result.wasNull())
                value.setByte(tempByte);
              else
                value.setAssignedNull();
              break;

            case Variant.SHORT:
              tempShort = result.getShort(index);
              if (!result.wasNull())
                value.setShort(tempShort);
              else
                value.setAssignedNull();
              break;

            case Variant.LONG:
              tempLong = result.getLong(index);
              if (!result.wasNull())
                value.setLong(tempLong);
              else
                value.setAssignedNull();
              break;

            case Variant.FLOAT:
              tempFloat = result.getFloat(index);
              if (!result.wasNull())
                value.setFloat(tempFloat);
              else
                value.setAssignedNull();
              break;


            case Variant.DOUBLE:
              tempDouble = result.getDouble(index);
              if (!result.wasNull())
                value.setDouble(tempDouble);
              else
                value.setAssignedNull();
              break;


            case Variant.DATE:
              tempDate = result.getDate(index);
              if (tempDate != null)
                value.setDate(tempDate);
              else
                value.setAssignedNull();
              break;

            case Variant.TIME:
              tempTime = result.getTime(index);
              if (tempTime != null)
                value.setTime(tempTime);
              else
                value.setAssignedNull();
              break;

            case Variant.TIMESTAMP:
              tempTimestamp= result.getTimestamp(index);
              if (tempTimestamp != null) {

                value.setTimestamp(tempTimestamp);
              }
              else
                value.setAssignedNull();
              break;

            case Variant.OBJECT:
              tempObject = result.getObject(index);
              if (tempObject != null)
                value.setObject(tempObject);
              else
                value.setAssignedNull();
              break;

            default:
              //Diagnostic.fail();
              break;

          }
        }

        if (coercer != null)
          coercer.coerceToColumn(loadColumns, loadVariants);

        dataSet.loadRow(loadStatus);

        if (cancel || (rowmax>0 && currentRows >= rowmax))
        {
          earlyBreak = true;
          break;
        }

        currentRows++;
      }
      while (result.next());
    }
    finally
    {
      release();
      if (variants != null)
        dataSet.endLoading();
    }
  }

  //-------------------------------
  public static String trimRight(String source) {
    if (source == null) return null;

    int len = source.length();

    while ((len > 0) && (source.charAt(len-1) == ' '))
      len--;

    return (len < source.length()) ? source.substring(0, len) : source;
  }
  //-------------------------------
  private final synchronized InputStream copyByteStream(InputStream inStream)
    throws IOException
  {
    if (binaryBytes == null) {
      binaryBytes = new byte[100*1024];
    }

    int     count       = 0;
    byte[]  streamBytes = null;
    byte[]  newBytes;

    try {
      while ((count = inStream.read(binaryBytes)) > 0) {

        if (streamBytes == null) {
          streamBytes = new byte[count];
          System.arraycopy(binaryBytes, 0, streamBytes, 0, count);
        }
        else {
          newBytes    = new byte[count+streamBytes.length];
          System.arraycopy(streamBytes, 0, newBytes, 0, streamBytes.length);
          System.arraycopy(binaryBytes, 0, newBytes, streamBytes.length, count);
          streamBytes = newBytes;
        }
      }
    }
    catch (Exception ex) {
      log.warn("ResultSetTransform: Problems with reading blob", ex);
      streamBytes = null;
      return null;
    }
    if (streamBytes == null)
      streamBytes = new byte[0];
    return new InputStreamToByteArray(streamBytes);
  }

  /**
   * 释放资源
   */
  private void release(){
    binaryBytes = null;
  }

  public static Vector getTableRowIds(Connection jdbcConnection, String catalog, String schema, String tableName)
      throws SQLException
  {
    if(tableName == null)
      return null;
    DatabaseMetaData dbMetaData = null;
    Vector vKeys = null;
    ResultSet keyResult = null;
    try{
      dbMetaData = jdbcConnection.getMetaData();
      keyResult = dbMetaData.getPrimaryKeys(catalog, schema, tableName);
      vKeys = new Vector(3);
      while(keyResult.next())
        vKeys.add(keyResult.getObject(4));
    }
    finally
    {
      if(keyResult != null)
        keyResult.close();
    }
    return vKeys;
  }
}

package engine.dataset;

import com.borland.dx.dataset.*;

import engine.util.log.Log;
import engine.util.StringUtils;
import engine.util.Format;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * EngineDataSet继承StorageDataSet，可以通过EngineDataSetProvider得到SessionBean的数据
 */
public final class EngineDataSet extends StorageDataSet
{
  //与设置数据时出错有关
  public static final int DATA_TYPE_ERROR        = -1;//设置数据时，转换数据出错
  public static final int DATA_PRECISION_ERROR   = -2;//设置数据时，数据长度出错
  public static final int DATA_IS_NULL           = -3;//设置数据时，入参是NULL，不对数据集做任何操作

  public  final static    String     defaultDateFormat = "yyyy-MM-dd";
  public  final static    String     defaultDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
  private static    Log log = new Log("EngineDataSet");

  private ResourceBundle res = Res.getBundle("engine.dataset.Res");
  private ReadWriteRow               paramRow;
  private EngineDataSetProvider      currentProvider;
  private EngineDataSetResolver      currentResolver;
  private           String           provideMethodName;
  private           String           resolveMethodName;
  private           String           dateFormat = defaultDateFormat;
  private           String           dateTimeFormat = defaultDateTimeFormat;

  private           Hashtable        htColumnFormat = null;
  private boolean   isOpened         = false;
  private boolean   isReplaceQuotate = true;

  ProvideInfo      provideInfo  = new ProvideInfo();
  ResolveInfo      resolveInfo  = new ResolveInfo();

  static{
    com.borland.jb.util.Diagnostic.enableChecking(false);
  }

  public EngineDataSet(){
    super();
  }
  /**
   * 覆盖父类的方法，设置数据的提供者，只接受EngineDataSetProvider
   * @param provider 数据的提供者
   */
  public void setProvider(Provider provider) /*-throws DataSetException-*/
  {
    if (provider != null && !(provider instanceof EngineDataSetProvider))
      throw new DataSetException(res.getString("RS_ProviderErr"));

    super.setProvider(provider);
    if(provider == null)
      currentProvider = null;
    else
      currentProvider = (EngineDataSetProvider)provider;
  }

  /**
   * 得到数据的提供者
   * @return 数据的提供者
   */
  public EngineDataSetProvider getDataSetProvider()
  {
    return this.currentProvider;
  }

  /**
   * 覆盖父类的方法，设置数据的更新者，只接受EngineDataSetResolver
   * @param resolver 数据的提交者
   */
  public void setResolver(Resolver resolver) /*-throws DataSetException-*/
  {
    if (resolver != null && !(resolver instanceof EngineDataSetResolver))
      throw new DataSetException(res.getString("RS_ResolverErr"));

    super.setResolver(resolver);
    if(resolver == null)
      currentResolver = null;
    else
      currentResolver = (EngineDataSetResolver)resolver;
  }

  /**
   * 得到数据的提交者
   * @return 数据的提交者
   */
  public EngineDataSetResolver getDataSetResolver()
  {
    return this.currentResolver;
  }

  /**
   * 根据条件打开特定（EngineDataSet）各个数据集
   * @param clientdataSets 想应的数据集
   * @throws DataSetException 抛出异常
   */
  public void openDataSets(EngineDataSet[] clientdataSets) throws Exception
  {
    if (currentProvider == null)
      DataSetException.badQueryProperties();
    currentProvider.openDataSets(clientdataSets);
  }

  /**
   * 根据条件打开特定（EngineDataSet）各个数据集
   * @param clientdataSets 想应的数据集
   * @param remoteMethod 远程方法的名称，如果不用ejb，可以为null
   * @throws DataSetException 抛出异常
   */
  public void openDataSets(EngineDataSet[] clientdataSets, String remoteMethod) throws Exception
  {
    if (currentProvider == null)
      DataSetException.badQueryProperties();
    currentProvider.openDataSets(clientdataSets, remoteMethod);
  }

  /**
   * 设置要提交数据集的一些必须的元素（包括 PrimaryKeys, SchemaName, TableName）
   * @throws DataSetException SQL异常
   */
  public void processElement() //throws SQLException
  {
    if (currentProvider == null)
      DataSetException.badQueryProperties();
    currentProvider.processElement(new EngineDataSet[]{this});
  }

  /**
   * 执行更新数据库的SQL (delete, update, insert)
   * @param updateSQL 需要执行的SQL语句的数组
   * @throws DataSetException 抛出异常
   */
  public final void updateQuery(String[] updateSQL) throws DataSetException
  {
    if (currentResolver == null)
      DataSetException.badQueryProperties();
    currentResolver.updateQuery(updateSQL);
  }

  /**
   * 提交主表，同时提交其下的各个从表, 并同时执行没有返回值的SQL语句
   * @param masterEngineDataSet 主表数据集
   * @throws DataSetException 抛出异常
   */
  public void saveDataSets(EngineDataSet masterEngineDataSet)
      throws DataSetException
  {
    if (currentResolver == null)
      DataSetException.badQueryProperties();
    currentResolver.saveDataSets(masterEngineDataSet);
  }

  /**
   * 同时更新多个数据集，从表必须跟在主表后面，并同时执行没有返回值的SQL语句.<br>
   * 如果用EJB表示用当前数据集的提交数据的远程方法名称(resolveMethodName)
   * @param clientDataSets 需要更新的数据集
   * @param updateSQL 同时执行的执行没有返回值的SQL语句
   * @throws DataSetException 抛出异常
   */
  public void saveDataSets(EngineDataSet[] clientDataSets)
      throws DataSetException
  {
    saveDataSets(clientDataSets, this.resolveMethodName);
  }
  /**
   * 同时更新多个数据集，从表必须跟在主表后面，并同时执行没有返回值的SQL语句
   * @param clientDataSets 需要更新的数据集
   * @param remoteMethod 远程方法的名称，如果不用ejb，可以为null
   * @throws DataSetException 抛出异常
   */
  public void saveDataSets(EngineDataSet[] clientDataSets, String remoteMethod)
      throws DataSetException
  {
    if (currentResolver == null)
      DataSetException.badQueryProperties();

    currentResolver.saveDataSets(clientDataSets, remoteMethod);
  }

  /**
   * 打开数据集
   * @return 是否成功
   */
  public final boolean openDataSet()
  {
    if(isOpen())
      return false;
    if(this.isOpened)
    {
      if(currentProvider != null)
      {
        refresh();
        return true;
      }
      else
        return super.open();
    }
    else
    {
      boolean isSucess = super.open();
      this.isOpened = true;
      return isSucess;
    }
  }
  /**
   * 关闭数据集
   * @return 是否成功
   */
  public final boolean closeDataSet()
  {
    if(!isOpen())
      return false;
    empty();
    return super.close();
  }


  /**
   * 保存指定的数据集到数据库，并更新数据集后执行没有返回值的SQL语句, 这两个操作处在同一个事务中
   * @param afterResolveSQL 同时执行的执行没有返回值的SQL语句
   */
  public void saveChanges(String[] afterResolveSQL)
  {
    saveChanges(null, afterResolveSQL);
  }

  /**
   * 保存指定的数据集到数据库，并更新数据集前后执行没有返回值的SQL语句, 这两个操作处在同一个事务中
   * @param beforeResolveSQL 更新数据集前需要执行的SQL语句
   * @param afterResolveSQL 更新数据集后需要执行的SQL语句
   */
  public void saveChanges(String[] beforeResolveSQL, String[] afterResolveSQL)
  {
    this.resolveInfo.setBeforeResolveSQL(beforeResolveSQL);
    this.resolveInfo.setAfterResolveSQL(afterResolveSQL);
    super.saveChanges();
  }


 /**
  * 所有字段是否全部使用String类型,不管数据库字段类型,除了inputstream, object
  * @return 是否全部使用String类型
   */
  public boolean columnIsUseString()
  {
    return this.provideInfo.columnIsUseString();
  }

  /**
   * 设置所有字段是否全部使用String类型,不管数据库字段类型,除了inputstream, object
   * @param value 是否全部使用String类型
   */
  public void setColumnIsUseString(boolean value)
  {
    this.provideInfo.setColumnIsUseString(value);
  }

  /**
   * 装载数据是否的自身的对象, 否则用DataSetData装载
   * @return 装载数据是否的自身的对象
   */
  public boolean isLoadDataUseSelf()
  {
    return this.provideInfo.isLoadDataUseSelf();
  }

  /**
   * 设置装载数据是否的自身的对象
   * @param value 装载数据是否的自身的对象
   */
  public void setLoadDataUseSelf(boolean value)
  {
    this.provideInfo.setLoadDataUseSelf(value);
  }

  /**
   * 装载数据是否的自身的对象, 否则用DataSetData装载
   * @return 保存数据是否的自身的对象
   */
  public boolean isSaveDataUseSelf()
  {
    return this.resolveInfo.isSaveDataUseSelf();
  }

  /**
   * 设置保存数据是否的自身的对象
   * @param value保存数据是否的自身的对象
   */
  public void setSaveDataUseSelf(boolean value)
  {
    this.resolveInfo.setSaveDataUseSelf(value);
  }

  /**
   * 设置表名
   * @param tableName 表名
   */
  public void setTableName(String tableName)
  {
    super.setTableName(tableName);
    this.resolveInfo.setTableName(tableName);
  }

  /**
   * 设置数据库方案表名
   * @param tableName 数据库方案表名
   */
  public void setSchemaName(String schemaName)
  {
    super.setSchemaName(schemaName);
    this.resolveInfo.setSchemaName(schemaName);
  }

  /**
   * 设置分段取得数据的最小列
   * @param rowMin 分段取得数据的最小列
   */
  public void setRowMin(int rowMin)
  {
    this.provideInfo.setRowMin(rowMin);
  }
  /**
   * 得到分段取得数据的最小列
   * @return 返回分段取得数据的最小列
   */
  public int getRowMin()
  {
    return this.provideInfo.getRowMin();
  }
  /**
   * 设置分段取得数据的最大列
   * @param rowMax 分段取得数据的最大列
   */
  public void setRowMax(int rowMax)
  {
    this.provideInfo.setRowMax(rowMax);
  }

  /**
   * 设置分段取得数据的最大列, 兼容于以前代码
   * @param row 应该null
   */
  public void setRowMax(String row)
  {
    this.provideInfo.setRowMax(-1);
  }
  /**
   * 得到分段取得数据的最大列
   * @return 分段取得数据的最大列
   */
  public int getRowMax()
  {
    return this.provideInfo.getRowMax();
  }

  /**
   * 准备刷新数据集
   */
  public void readyRefresh()
  {
    this.provideInfo.setRowMin(-1);
    this.provideInfo.setRowMax(-1);
  }
  /**
   * 得到包含自动更新序列信息的SequenceDescriptor
   * @return 返回包含自动更新序列信息的SequenceDescriptor
   */
  public SequenceDescriptor getSequence()
  {
    return this.resolveInfo.getSequence();
  }

  /**
   * 设置包含自动更新序列信息
   * @param sequence 包含自动更新序列信息
   */
  public void setSequence(SequenceDescriptor sequence)
  {
    this.resolveInfo.setSequence(sequence);
  }

  /**
   * 设置用提交数据之前需要执行的SQL语句，提交数据后将会清空
   * @param SQL 提交数据之前需要执行的SQL语句
   */
  public void setBeforeResolvedSQL(String[] SQL)
  {
    this.resolveInfo.setBeforeResolveSQL(SQL);
  }

  /**
   * 得到用提交数据之前需要执行的SQL语句，提交数据后将会清空
   * @return 提交数据之前需要执行的SQL语句
   */
  public String[] getBeforeResolvedSQL()
  {
    return this.resolveInfo.getBeforeResolveSQL();
  }

  /**
   * 是否有主键字段
   * @return 返回是否有
   */
  public boolean hasRowId() { return this.resolveInfo.hasRowIds(); }

  /**
   * 添加主键字段
   * @param name 字段名称
   * @return 返回是否成功
   */
  public boolean setRowIds(String name)
  {
    if(name == null)
      return false;

    List ids = this.resolveInfo.getRowIds();
    if(ids == null || ids.indexOf(name.toUpperCase()) < 0)
    {
      Column col = null;
      try{
        col = this.getColumn(name);
      }
      catch(Exception ex){
        return false;
      }

      if(ids == null)
      {
        ids = new ArrayList();
        this.resolveInfo.setRowIds(ids);
      }
      ids.add(name.toUpperCase());
    }
    return true;
  }

  /**
   * 删除主键字段
   * @param name 字段名称
   * @return 返回是否成功
   */
  public boolean removeRowIds(String name)
  {
    if(name == null)
      return false;

    List ids = this.resolveInfo.getRowIds();
    if(ids == null)
      return false;

    int index = ids.indexOf(name.toUpperCase());
    if(index < 0)
      return false;

    ids.remove(index);
    return true;
  }

  /**
   * 得到所有的主键字段
   * @return 返回所有的主键字段
   */
  public String[] getRowIds()
  {
    List ids = this.resolveInfo.getRowIds();
    if(ids == null)
      return null;
    else
    {
      String[] rowids = new String[ids.size()];
      ids.toArray(rowids);
      return rowids;
    }
  }
  /**
   * 设置用提交数据之后需要执行的SQL语句，提交数据后将会清空
   * @param SQL 提交数据之后需要执行的SQL语句
   */
  public void setAfterResolvedSQL(String[] SQL)
  {
    this.resolveInfo.setAfterResolveSQL(SQL);
  }

  /**
   * 得到用提交数据之后需要执行的SQL语句，提交数据后将会清空
   * @return 提交数据之后需要执行的SQL语句
   */
  public String[] getAfterResolvedSQL()
  {
    return this.resolveInfo.getAfterResolveSQL();
  }

  /**
   * 得到用getValue方法的时间字段的格式化
   * @return 返回时间字段的格式化
   */
  public String getDateFormat()
  {
    return dateFormat == null ? defaultDateFormat : dateFormat;
  }

  /**
   * 设置时间字段的格式化
   * @param dateFormat 时间字段的格式化
   */
  public void setDateFormat(String dateFormat)
  {
    if(dateFormat == null)
      return;
    this.dateFormat = dateFormat;
  }

  /**
   * 得到用getValue方法的日期时间字段的格式化
   * @return 返回日期时间字段的格式化
   */
  public String getDateTimeFormat()
  {
    return dateTimeFormat == null ? defaultDateFormat : dateTimeFormat;
  }

  /**
   * 设置日期时间字段的格式化
   * @param dateFormat 日期时间字段的格式化
   */
  public void setDateTimeFormat(String dateTimeFormat)
  {
    if(dateTimeFormat == null)
      return;
    this.dateTimeFormat = dateTimeFormat;
  }

  /**
   * 得到字段值
   * @param columnName 字段名称
   * @return 返回字段值
   */
  public String getValue(String columnName)
  {
    return getValue(this, getColumn(columnName), null, null);
  }

  /**
   * 得到字段值
   * @param ordinal 字段序号
   * @return 返回字段值
   */
  public String getValue(int ordinal)
  {
    return getValue(this, ordinal);
  }

  /**
   * 静态的方法，得到字段值
   * @param data (ReadWriteRow) 需要得到字段值的数据集(DataSet)或数据行(DataRow)
   * @param ordinal 字段序号
   * @return 返回字段值
   */
  public static String getValue(ReadWriteRow data, int ordinal)
  {
    return getValue(data, ordinal, null, null);
  }
  /**
   * 静态的方法，得到字段值
   * @param data (ReadWriteRow) 需要得到字段值的数据集(DataSet)或数据行(DataRow)
   * @param ordinal 字段序号
   * @param dateFormat 如果是时间类型的字段需要传递日期格式化参数 yyyy-MM-dd
   * @param dateFormat 如果是时间类型的字段需要传递日期时间格式化参数 yyyy-MM-dd HH:mm:ss
   * @return 返回字段值
   */
  public static String getValue(ReadWriteRow data, int ordinal, String dateFormat, String dateTimeFormat)
  {
    return getValue(data, data.getColumn(ordinal), dateFormat, dateTimeFormat);
  }
  /**
   * 静态的方法，得到字段值
   * @param data (ReadWriteRow) 需要得到字段值的数据集(DataSet)或数据行(DataRow)
   * @param column 字段对象
   * @param dateFormat 如果时时间类型的字段需要传递日期格式化参数
   * @return 返回字段值
   */
  public static synchronized String getValue(ReadWriteRow data, Column column, String dateFormat, String dateTimeFormat)
  {
    int ordinal = column.getOrdinal();
    if(data.isNull(ordinal))
      return "";

    int dataType= column.getDataType();
    String value= null;
    switch(dataType)
    {
      case Variant.BIGDECIMAL:
        value = data.getBigDecimal(ordinal).toString();
        break;
      case Variant.STRING:
        value = data.getString(ordinal);
        break;
      case Variant.TIMESTAMP:
        Timestamp timestamp = data.getTimestamp(ordinal);
        long remain = (timestamp.getTime()+28800000)%8640000;
        if(remain == 0)
          value = Format.TimestampToStr(timestamp, dateFormat== null ? defaultDateFormat : dateFormat);
        else
          value = Format.TimestampToStr(timestamp, dateTimeFormat==null ? defaultDateTimeFormat : dateTimeFormat);
        break;
      case Variant.DATE:
        value = Format.DateToStr(data.getDate(ordinal), dateFormat== null ? defaultDateFormat : dateFormat);
        break;
      default:
        value = data.format(ordinal);
    }

    if(value != null && value.length() > 0)
    {
      String formatStr = null;
      if(data instanceof EngineDataSet)
        formatStr = ((EngineDataSet)data).getColumnFormat(column.getColumnName());
      if(formatStr != null)
        return Format.formatNumber(value, formatStr);
    }
    return value;
  }

  /**
   * 设置字段值
   * @param columnName 字段名称
   * @param value 要设置的字段值
   * @return 返回值 <0 表示错误, 根据返回值判断错误类型
   */
  public int setValue(String columnName, String value)
  {
    Column column = getColumn(columnName);
    return setValue(this, column, column.getOrdinal(), value);
  }

  /**
   * 设置字段值, 支持int, long, float, double, BigDecimal, short, Timestamp, Date, String, boolean
   * @param ordinal 字段的位置
   * @param value 要设置的字段值
   * @return 返回值 <0 表示错误, 根据返回值判断错误类型
   */
  public int setValue(int ordinal, String value)
  {
    return setValue(this, ordinal, value);
  }

  /**
   * 设置字段值, 支持int, long, float, double, BigDecimal, short, Timestamp, Date, String, boolean
   * @param data (ReadWriteRow) 需要设置字段值的数据集(DataSet)或数据行(DataRow)
   * @param ordinal 字段的位置
   * @param value 要设置的字段值
   * @return 返回值 <0 表示错误, 根据返回值判断错误类型
   */
  public static int setValue(ReadWriteRow data, int ordinal, String value)
  {
    return setValue(data, data.getColumn(ordinal), ordinal, value);
  }

  /**
   * 设置字段值, 支持int, long, float, double, BigDecimal, short, Timestamp, Date, String, boolean
   * @param data (ReadWriteRow) 需要设置字段值的数据集(DataSet)或数据行(DataRow)
   * @param column 字段对象
   * @param value 要设置的字段值
   * @return 返回值 <0 表示错误, 根据返回值判断错误类型
   */
  private static synchronized int setValue(ReadWriteRow data, Column column, int ordinal, String value)
  {
    if(value == null)
      return DATA_IS_NULL;

    if(value.equals(""))
    {
      data.setAssignedNull(ordinal);
      return 1;
    }

    boolean isReplaceQuotate = false;
    String formatStr = null;
    if(data instanceof EngineDataSet)
    {
      isReplaceQuotate = ((EngineDataSet)data).isReplaceQuotate();
      formatStr = ((EngineDataSet)data).getColumnFormat(column.getColumnName());
    }
    if(formatStr != null)
      value = Format.formatNumber(value, formatStr);

    int dataType= column.getDataType();
    switch(dataType)
    {
      //
      case Variant.STRING:
        if(isReplaceQuotate)
          value = StringUtils.replaceQuotatemark(value);
        data.setString(ordinal, value);
        break;
      //
      case Variant.BIGDECIMAL:
        try{
          BigDecimal bg = new BigDecimal(value);
//          if(value.length()>0&&value.indexOf(".")<0)
//        	  data.setDouble(ordinal, Double.valueOf(value));
//          else
        	  data.setBigDecimal(ordinal, bg);
        }
        catch(NumberFormatException nfEx){
          return DATA_TYPE_ERROR;
        }
        break;
      //
      case Variant.BYTE:
        try{
          byte b = Byte.parseByte(value);
          data.setByte(ordinal, b);
        }
        catch(NumberFormatException nfEx){
          return DATA_TYPE_ERROR;
        }
        break;
      //
      case Variant.SHORT:
        try{
          short s = Short.parseShort(value);
          data.setShort(ordinal, s);
        }
        catch(NumberFormatException nfEx){
          return DATA_TYPE_ERROR;
        }
        break;
      //
      case Variant.INT:
        try{
          int i = Integer.parseInt(value);
          data.setInt(ordinal, i);
        }
        catch(NumberFormatException nfEx){
          return DATA_TYPE_ERROR;
        }
        break;
      //
      case Variant.BOOLEAN:
        data.setBoolean(ordinal, value.equalsIgnoreCase("true") || value.equals("1"));
        break;
      //
      case Variant.LONG:
        try{
          long l = Long.parseLong(value);
          data.setLong(ordinal, l);
        }
        catch(NumberFormatException nfEx){
          return DATA_TYPE_ERROR;
        }
        break;
      //
      case Variant.FLOAT:
        try{
          float f = Float.parseFloat(value);
          data.setFloat(ordinal, f);
        }
        catch(NumberFormatException nfEx){
          return DATA_TYPE_ERROR;
        }
        break;
      //
      case Variant.DOUBLE:
        try{
          double d = Double.parseDouble(value);
          data.setDouble(ordinal, d);
        }
        catch(NumberFormatException nfEx){
          return DATA_TYPE_ERROR;
        }
        break;
      //
      case Variant.INPUTSTREAM:
        data.setInputStream(ordinal, new ByteArrayInputStream(value.getBytes()));
        break;
      //
      case Variant.TIMESTAMP:
        boolean isDateTime = value.length() == defaultDateTimeFormat.length();
        java.sql.Timestamp timestamp = null;
        try{
          timestamp = isDateTime ? java.sql.Timestamp.valueOf(value)
                      : new java.sql.Timestamp(java.sql.Date.valueOf(value).getTime());
        }
        catch(Exception e){
          return DATA_TYPE_ERROR;
        }
        data.setTimestamp(ordinal, timestamp);
        break;
      //
      case Variant.DATE:
        java.sql.Date date = null;
        try{
          date = java.sql.Date.valueOf(value);
        }
        catch(Exception e){
          return DATA_TYPE_ERROR;
        }
        data.setDate(ordinal, date);
        break;
      //
      case Variant.TIME:
        java.sql.Time time = null;
        try{
          time = java.sql.Time.valueOf(value);
        }
        catch(Exception e){
          return DATA_TYPE_ERROR;
        }
        data.setTime(ordinal, time);
        break;
      //
      case Variant.OBJECT:
        data.setObject(ordinal, value);
        break;
      //
      case Variant.UNASSIGNED_NULL:
      case Variant.ASSIGNED_NULL:
        data.setAssignedNull(ordinal);
        break;
      //
      default:
        DataSetException.unrecognizedDataType();
        break;
    }
    return 1;
  }

  /**
   * 得到实际的记录总数
   * @return 返回实际的记录总数
   */
  public int getTrueRowCount()
  {
    return currentProvider.getTrueCount(getQueryString());
  }

  /**
   * 得到SQL语句
   * @return 返回单前数据集的SQL
   */
  public String getQueryString()
  {
    return provideInfo.getQueryString();
  }

  /**
   * 设置数据集需要提取数据SQL语句
   * @param queryString 设置数据集需要提取数据SQL语句
   */
  public void setQueryString(String queryString)
  {
    provideInfo.setQueryString(queryString);
  }

  public ReadWriteRow getParamRow()
  {
    return paramRow;
  }

  public void setParamRow(ReadWriteRow paramRow)
  {
    this.paramRow = paramRow;
  }

  /**
   * 设置提供数据的远程方法名称
   * @param provideMethodName 提供数据的远程方法名称
   */
  public void setProvideMethodName(String provideMethodName)
  {
    this.provideMethodName = provideMethodName;
  }
/**
 * 克隆数据行
 *
 * */
  public final EngineDataView cloneEngineDataView()
  {
    if(!isOpen())
      DataSetException.dataSetNotOpen();
    EngineDataView enginedataview = new EngineDataView();
    synchronized(this)
    {
      enginedataview.setStorageDataSet(this);
      if(getSort() != null)
        enginedataview.setSort(getSort());
      if(getRowFilterListener() != null)
        try
      {
        enginedataview.addRowFilterListener(getRowFilterListener());
      }
      catch(TooManyListenersException toomanylistenersexception)
      {
        log.warn("TooManyListenersException when cloneEngineDataView");
      }
      enginedataview.open();
      enginedataview.goToInternalRow(getInternalRow());
    }
    return enginedataview;
  }
  /**
   * 得到提供数据的远程方法名称
   * @return 提供数据的远程方法名称
   */
  public String getProvideMethodName()
  {
    return provideMethodName;
  }

  /**
   * 设置提交数据的远程方法名称
   * @param resolveMethodName 提交数据的远程方法名称
   */
  public void setResolveMethodName(String resolveMethodName)
  {
    this.resolveMethodName = resolveMethodName;
  }

  /**
   * 得到提交数据的远程方法名称
   * @return 提交数据的远程方法名称
   */
  public String getResolveMethodName()
  {
    return resolveMethodName;
  }

  /**
   * 更新数据完毕后清空临时的SQL
   */
  void clearResolvedUpdateSQL()
  {
    this.resolveInfo.setAfterResolveSQL(null);
  }

  /**
   * 得到数据集提交后要执行的SQL语句
   * @return 返回数据集提交后要执行的SQL语句
   */
  String[] getResolvedUpdateSQL()
  {
    return this.resolveInfo.getAfterResolveSQL();
  }

  /**
   * 设置提交数据的模式
   * @param updateMode 提交数据的模式
   */
  public void setUpdateMode(int updateMode)
  {
    this.resolveInfo.setUpdateMode(updateMode);
  }

  /**
   * 得到提交数据的模式
   * @return 返回提交数据的模式
   */
  public int getUpdateMode()
  {
    return this.resolveInfo.getUpdateMode();
  }

  /**
   * 设置格式化数字字段的格式
   * @param columnName 数字字段的字段名称
   * @param formatString 格式化串。若为null或“”,则删除该字段的格式化信息
   */
  public synchronized final void setColumnFormat(String columnName, String formatString)
  {
    if(columnName == null)
      return;

    columnName = columnName.toUpperCase();
    if(htColumnFormat == null)
      htColumnFormat = new Hashtable();

    if(formatString == null || formatString.equals(""))
      htColumnFormat.remove(columnName);
    else
      htColumnFormat.put(columnName, formatString);
  }

  /**
   * 得到数字字段的格式化串
   * @param columnName 数字字段的字段名称
   * @return 返回字段的格式化串
   */
  private final String getColumnFormat(String columnName)
  {
    columnName = columnName.toUpperCase();
    return htColumnFormat == null ? null : (String)htColumnFormat.get(columnName);
  }

  /**
   * 得到是否将半角的引号转为全角的引号
   * @return 是否将半角的引号转为全角的引号
   */
  public boolean isReplaceQuotate()
  {
    return isReplaceQuotate;
  }

  /**
   * 设置是否将半角的引号转为全角的引号
   * @param isReplaceQuotate 是否将半角的引号转为全角的引号
   */
  public void setReplaceQuotate(boolean isReplaceQuotate)
  {
    this.isReplaceQuotate = isReplaceQuotate;
  }
}
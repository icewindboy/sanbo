package engine.dataset.database;

import java.math.BigDecimal;
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.sql.DataSource;

import com.borland.dx.dataset.*;
import engine.dataset.sql.UniqueQueryAnalyzer;
import engine.dataset.sql.SQLElement;
import engine.util.log.Log;
import engine.dataset.*;



/**
 * Title:        DatabaseProvider
 * Description:  服务端提供数据和更新提交数据的类
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */

public final class DatabaseProvider implements Database
{
  private static Log log = new Log("engine.dataset.DAO");//得到写日志的实例

  private           ResultSetTransform resultTrans = null;
  private transient Connection conn = null;
  private           RuntimeMetaData runtimeMetaData = null;

  /**
   * 构造函数
   */
  public DatabaseProvider()
  {
  }

  public DatabaseProvider(Connection conn){
    if(conn != null)
      setConnection(conn);
  }

  /**
   * 执行没有返回值的SQL语句:Insert,delete,update.and all so
   * @param SQL 没有返回值的SQL语句
   */
  public void execSQL(String[] SQL)// throws DataSetException
  {
    if(SQL == null)
      return;

    Statement stmt = null;
    try{
      for(int i = 0; i < SQL.length; i++)
      {
        if(SQL[i] == null)
          continue;
        if(stmt == null)
          stmt = getConnection().createStatement();
        stmt.executeUpdate(SQL[i]);
      }
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
    finally{
      try{
        if(stmt == null)
          stmt.close();
      }
      catch(SQLException ex){}
    }
  }
  /**
   * 有返回数据集的SQL语句
   * @param SQL 要执行的SQL语句的数组。存储过程:"{CALL 包名.过程名(?, " + param + ")}",
   *        其中：？为需要返回的Cursor 并且在存储过程定义时Cursor必须为第一个参数。
   * @return DataSetData[] 返回的数据,无返回值的时是null
   */
  public ProvideInfo[] provideData(String[] SQL) throws Exception {
    if(SQL == null)
      return null;
    ProvideInfo[] infos = new ProvideInfo[SQL.length];
    for(int i=0; i<SQL.length; i++)
    {
      infos[i].setQueryString(SQL[i]);
      infos[i].setLoadDataUseSelf(false);
    }

    return this.provideData(infos);
  }
  /**
   * 有返回数据集的SQL语句
   * @param SQL 要执行的SQL语句的数组。存储过程:"{CALL 包名.过程名(?, " + param + ")}",
   *        其中：？为需要返回的Cursor 并且在存储过程定义时Cursor必须为第一个参数。
   * @param hmRelation 其他参数：在HashMap中的key值有rowmin，rowmax分别表示数据的区域,<b>注：该参数对存储过程无效</b>
   * @return DataSetData[] 返回的数据,无返回值的时是null
   */
  public ProvideInfo[] provideData(ProvideInfo[] infos) throws Exception {
    if(infos == null)
      return null;

    Statement stmt = null;
    CallableStatement callstmt = null;
    //DataSetData data = null;
    if(resultTrans == null)
      resultTrans = new ResultSetTransform();
    try {
      for(int i = 0; i < infos.length; i++)
      {
        if(infos[i] == null)
          continue;
        String SQL = infos[i].getQueryString();
        if(SQL == null)
          continue;

        if(SQL.toLowerCase().indexOf("call")>-1)
        {
          if(SQL.indexOf("?") < 0)
            continue;
          callstmt = getConnection().prepareCall(SQL);
          callstmt.registerOutParameter(1, -10);
          callstmt.execute();
          ResultSet result = (ResultSet)callstmt.getObject(1);
          //
          if(infos[i].isLoadDataUseSelf())
            resultTrans.resultSetToDataSet(getRuntimeMetaData(), infos[i].getProvideDataSet(), result, infos[i]);
          else
            resultTrans.resultSetToDataSetData(getRuntimeMetaData(), result, infos[i]);
          //
          result.close();
          result = null;
          callstmt.close();
          callstmt = null;
        }
        else
        {
          int rowmin = infos[i].getResultSetMin();
          int rowmax = infos[i].getResultSetMax();
          if(rowmin > 0 || rowmax > 0)
            stmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
          else
            stmt = getConnection().createStatement();

          ResultSet result = stmt.executeQuery(SQL);
          //
          if(infos[i].isLoadDataUseSelf())
            resultTrans.resultSetToDataSet(getRuntimeMetaData(), infos[i].getProvideDataSet(), result, infos[i]);
          else
            resultTrans.resultSetToDataSetData(getRuntimeMetaData(), result, infos[i]);
          //
          result.close();
          result = null;
          stmt.close();
          stmt = null;
        }
      }
      return infos;
    }
    catch(DataSetException dex){
      throw dex;
    }
    catch(Exception ex){
      throw ex;
    }
    finally {
      try{
        if(stmt != null)
          stmt.close();
      }
      catch(SQLException sex){};

      try{
        if(callstmt != null)
          callstmt.close();
      }
      catch(SQLException sex){};
    }
  }

  /**
   * 提交数据并序列值，并返回序列值数据集
   * @param ds 数据集数组
   * @param infos 提交数据所必须的信息。
   * @throws Exception 抛出异常
   */
  private void resolveData_Sequence(StorageDataSet[] ds, ResolveInfo[] infos)
    throws Exception
  {
    //提交数据
    setAutoCommit(false);
    DatabaseResolver dbResolver = new DatabaseResolver();
    dbResolver.saveChanges(this, ds, infos);
  }

  /**
   * 得到执行SQL语句返回的数据集的总记录数
   * @param queryString 与SELECT相关的SQL语句
   * @return 返回总记录数
   */
  public int getRowCount(String queryString)
  {
    ResultSet rs = null;
    CallableStatement callstmt = null;
    Statement statement = null;
    try{
      if(queryString.toUpperCase().indexOf("CALL") > -1 && queryString.indexOf("?") > -1)
      {
        callstmt = getConnection().prepareCall(queryString);
        callstmt.registerOutParameter(1, -10);
        callstmt.execute();
        rs = (ResultSet)callstmt.getObject(1);
        return ResultSetTransform.getResultSetCount(rs);
      }
      else
      {
        statement = getConnection().createStatement();
        StringBuffer buf = new StringBuffer("SELECT COUNT(1) FROM (").append(queryString).append(")");
        rs = statement.executeQuery(buf.toString());
        rs.next();
        return rs.getBigDecimal(1).intValue();
      }
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
    finally{
      try{
        if(rs != null)
          rs.close();
      }catch(SQLException ex){}

      try{
        if(statement != null)
          statement.close();
      }catch(SQLException ex){}

      try{
        if(callstmt != null)
          callstmt.close();
      }catch(SQLException ex){}
    }
    return -1;
  }
  /**
   * 得到序列, 没有记录返回null值
   * @param sequenceName 序列名称
   * @param statement Statement
   * @return 返回得到序列值
   * @throws Exception 抛出异常
   */
  final String getSequence(String sequenceName, Statement statement)
  {
    String Key = null;
    ResultSet rs = null;
    CallableStatement callstmt = null;
    try{
      if(sequenceName.toUpperCase().indexOf("CALL") > -1 && sequenceName.indexOf("?") > -1)
      {
        callstmt = getConnection().prepareCall(sequenceName);
        callstmt.registerOutParameter(1, -10);
        callstmt.execute();
        rs = (ResultSet)callstmt.getObject(1);
      }
      else
      {
        if(sequenceName.toUpperCase().indexOf("SELECT") < 0)
        {
          int dbName = DatabaseProperty.getDatabaseProductName(getConnection(),"connect_1");
          if (dbName == DatabaseProperty.DB_ORACLE)
            sequenceName = "SELECT "+sequenceName+".NEXTVAL FROM dual";
          else{
            int count = statement.executeUpdate("UPDATE "+ sequenceName +" SET id=id+1");
            if(count == 0)
            {
              statement.executeUpdate("INSERT INTO "+ sequenceName + "(id) VALUES(1)");
              return "1";
            }
            sequenceName = "SELECT id FROM "+sequenceName;
          }
        }
        rs = statement.executeQuery(sequenceName);
      }
      if(!rs.next())
        return null;

      int sqlType = rs.getMetaData().getColumnType(1);
      switch (sqlType)
      {
        case java.sql.Types.LONGVARCHAR:
        case java.sql.Types.CHAR:
        case java.sql.Types.VARCHAR:
          Key = rs.getString(1);
          break;
        case java.sql.Types.NUMERIC:
        case java.sql.Types.DECIMAL:
          BigDecimal bd = rs.getBigDecimal(1);
          Key = bd == null ? null : bd.toString();
          break;
      }
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
    finally{
      try{
        if(rs != null)
          rs.close();
        if(callstmt != null)
          callstmt.close();
      }
      catch(SQLException ex){}
    }
    return Key;
  }

  /**
   * 得到序列.
   * @param sequenceName 序列名称
   * @return 返回得到序列值
   * @throws Exception 抛出异常
   */
  public String getSequence(String sequenceName)
  {
    Statement statement = null;
    try{
      statement = this.getConnection().createStatement();
      return getSequence(sequenceName, statement);
    }
    catch(SQLException ex){
      throw new DataSetException("DatabaseProvider[getSequence]:"+ex.getMessage());
    }
    finally{
      try{
        if(statement != null)
          statement.close();
      }
      catch(SQLException ex){
        log.warn("getSequence Exception", ex);
      }
    }
  }

  /**
   * 提交多个数据集
   * @param Changes 更改过的数据
   * @param SQL SQL语句
   * @param hmRelation 相关参数
   * @return 返回提交过的信息
   * @throws DataSetException 抛出异常
   */
  public ResolveInfo[] resolveData(ResolveInfo[] infos) throws DataSetException
  {
    if(infos == null)
      return null;
    synchronized(infos)
    {
      //check
      for(int i=0; i<infos.length; i++)
      {
        if(infos[i] == null || (infos[i].getQueryString()==null && infos[i].getTableName()==null))
          throw new DataSetException(DataSetException.SQL_ERROR, "query is null");
        if(infos[i].isSaveDataUseSelf()){
          if(infos[i].getChangedDataSet() == null)
            throw new DataSetException(DataSetException.NEED_STORAGEDATASET, "changed storagedataset is null");
        }
        else if(infos[i].getChangedData() == null)
          throw new DataSetException(DataSetException.NEED_STORAGEDATASET, "changed datasetdata is null");
      }
      StorageDataSet[] dsChanges = new StorageDataSet[infos.length];
      try {
        for(int i=0; i<dsChanges.length; i++)
        {
          if(infos[i].isSaveDataUseSelf())
            dsChanges[i] = infos[i].getChangedDataSet();
          else{
            dsChanges[i] = new StorageDataSet();
            infos[i].getChangedData().loadDataSet(dsChanges[i]);
          }
        }

        processDataSetElements(dsChanges, infos);

        resolveData_Sequence(dsChanges, infos);
        return infos;
      }
      catch(Exception ex){
        System.out.println("ResolveMultiTable Error:");
        ex.printStackTrace();
        throw new DataSetException(ex.getMessage());
      }
      finally {
        for(int i=0; dsChanges != null && i<dsChanges.length; i++)
        {
          if(!infos[i].isSaveDataUseSelf() && dsChanges[i] != null && dsChanges[i].isOpen()){
            dsChanges[i].empty();
            dsChanges[i].close();
          }
          infos[i].setChangedData(null);
        }
      }
    }
  }

  /**
   * 设置要提交数据集的一些必须的元素（包括 PrimaryKeys, SchemaName, TableName）
   * @param infos 返回的信息（PrimaryKeys, SchemaName, TableName）
   * @throws DataSetException SQL异常
   */
  public void processDataSetElements(ResolveInfo[] infos) //throws SQLException
  {
    try{
      processDataSetElements(null, infos);
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
  }

  /**
   * 设置要提交数据集的一些必须的元素（包括 PrimaryKeys, SchemaName, TableName）
   * @param datset 需要设置属性的数据集, 为null时,将不设置
   * @param infos 返回的信息（PrimaryKeys, SchemaName, TableName）
   * @throws SQLException SQL异常
   */
  private void processDataSetElements(StorageDataSet[] datSets, ResolveInfo[] infos) throws SQLException
  {
    StorageDataSet datset = null;
    String schemaName = null;
    String tableName  = null;
    List   rowids     = null;
    //Vector[] dataSetRowids = new Vector[datSets.length];
    for(int i=0; i<infos.length; i++)
    {
      tableName = infos[i].getTableName();
      schemaName = infos[i].getSchemaName();
      datset = datSets == null ? null : datSets[i];
      if(tableName == null || !infos[i].hasRowIds())
      {
        String SQL = tableName == null ? infos[i].getQueryString() : "SELECT * FROM "+tableName;

        if(tableName == null)
        {
          int orderPos = SQL.toUpperCase().indexOf("ORDER BY");
          if(orderPos > -1)
            SQL = SQL.substring(0, orderPos);
        }
        UniqueQueryAnalyzer queryAnalyzer = new UniqueQueryAnalyzer(this, SQL);

        if(!infos[i].hasRowIds())
        {
          queryAnalyzer.analyze();
          Vector vRowids = queryAnalyzer.getBestRowId().size()>0 ? queryAnalyzer.getBestRowId() : queryAnalyzer.getCurrentRowId();

          rowids = new ArrayList(vRowids.size());
          for(int j=0; j < vRowids.size(); j++)
            rowids.add(((SQLElement)vRowids.get(j)).getName().toUpperCase());

          infos[i].setRowIds(rowids);
        }
        else
          queryAnalyzer.analyzeTableName();

        tableName = queryAnalyzer.getTableName();
        schemaName = queryAnalyzer.getSchemaName();
        infos[i].setTableName(tableName);
        infos[i].setSchemaName(schemaName);
      }

      rowids = infos[i].getRowIds();

      for(int j=0; datset != null && j < rowids.size(); j++)
      {
        String name = (String)rowids.get(j);
        try{
          datset.setRowId(name, true);
        }
        catch(Exception ex){
          log.warn("processDataSetElements.setRowId DatSet["+i+"]", ex);
          rowids.remove(j);
          j--;
        }
      }
      if(datset != null)
      {
        datset.setTableName(tableName);
        datset.setSchemaName(schemaName);
      }
    }
  }

  /**
   * 关闭数据库的连接
   */
  public void close()
  {
    //try{
    if(runtimeMetaData != null)
      runtimeMetaData.close();

    conn = null;
  }

  /**
   * 设置类里的数据库组件是否自动提交
   * @param isAutoCommit  is true执行完SQL就提交；is false需要Commit才能提交，rollback就可以回滚
   */
  public void setAutoCommit(boolean isAutoCommit)
  {
    try{
      getConnection().setAutoCommit(isAutoCommit);
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
  }

  /**
   * 得到类里的数据库组件是否自动提交
   * @return 返回类里的数据库是否自动提交
   */
  public boolean getAutoCommit()
  {
    try{
      return getConnection().getAutoCommit();
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
    return false;
  }

  /**
   * 设置类里的数据库组件提交到数据库
   */
  public void commit()
  {
    try{
      getConnection().commit();
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
  }

  /**
   * 回滚数据
   */
  public void rollback()
  {
    try{
      getConnection().rollback();
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
  }

  /**
   * 得到数据库的连接实例
   * @return 返回数据库的连接实例
   */
  public Connection getConnection()
  {
    if(this.conn == null)
      throw new DataSetException(DataSetException.CONNECTION_DESCRIPTOR_NOT_SET, "connection is not set");
    return this.conn;
  }

  /**
   * 设置数据库的连接实例
   * @param connDescriptor 数据库的连接描述
   */
  public void setConnection(Connection conn)
  {
    this.conn = conn;
    setAutoCommit(false);
  }

  /**
   * 得到运行的数据库实例信息
   * @return 返回运行的数据库实例信息
   */
  public synchronized RuntimeMetaData getRuntimeMetaData()
  {
    if(this.runtimeMetaData == null)
      runtimeMetaData = new RuntimeMetaData(this);
    else if(runtimeMetaData.getDatabase() == null)
      runtimeMetaData.setDatabase(this);
    return this.runtimeMetaData;
  }

  /**
   * 设置运行的数据库实例信息
   * @param runtimeMetaData 运行的数据库实例信息
   */
  public void setRuntimeMetaData(RuntimeMetaData runtimeMetaData)
  {
    this.runtimeMetaData = runtimeMetaData;
  }

  /**
   * 得到数据库信息
   * @return 返回数据库信息
   */
  public DatabaseMetaData getMetaData()
  {
    return getRuntimeMetaData().getMetaData();
  }
}
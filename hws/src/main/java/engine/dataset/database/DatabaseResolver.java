package engine.dataset.database;

import java.util.*;
import java.sql.*;
import java.math.BigDecimal;

import engine.util.log.Log;
import engine.dataset.SequenceDescriptor;
import engine.dataset.ResolveInfo;

import engine.dataset.*;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

public final class DatabaseResolver
{
  private static Log log = new Log("engine.dataset.DAO");

  private static final int INSERT_MODE = 1;
  private static final int UPDATE_MODE = 2;
  private static final int DELETE_MODE = 3;

  private transient DatabaseProvider dbProvider = null;
  private transient DataSet[] dataSets  = null;
  private transient String[] tableNames = null;
  private transient String   tableName;
  private transient ResolveInfo[] infos = null;
  private transient int queryTimeout;

  private transient Variant[] oldValues  = null;
  private transient Variant[] values     = null;

  private transient Vector vChangeColumns = null;
  private transient Vector vValues     = null;

  private transient Vector vWhereColumns  = null;
  private transient Vector vWhereValues   = null;

  private transient Column[] cachedColumns = null;

  private transient DeleteQuery deleteQuery = null;
  private transient UpdateQuery updateQuery = null;
  private transient InsertQuery insertQuery = null;

  private transient Statement stmt = null;
  /**
   * 释放资源
   */
  private void release()
  {
    dbProvider = null;
    dataSets   = null;
    tableNames = null;
    tableName  = null;
    infos = null;

    oldValues  = null;
    values     = null;

    vChangeColumns = null;
    vValues     = null;

    vWhereColumns  = null;
    vWhereValues   = null;

    cachedColumns = null;

    deleteQuery = null;
    updateQuery = null;
    insertQuery = null;

    if(stmt != null)
    {
      try{
        stmt.close();
      }
      catch(SQLException ex){
        log.warn("DatabaseResolver release resource.", ex);
      }
    }
  }

  /**
   * 保存变更的数据到数据库中。
   * @param dataBase DataBase组件
   * @param dataSets 需要更新数据到数据库的数据集组件
   * @param updateMode 提交数据是生成SQL的模式
   */
  public void saveChanges(DatabaseProvider provider, DataSet[] dataSets, ResolveInfo[] infos)
  {
    saveChanges(provider, dataSets, false, false, infos, 0);
  }

  /**
   * 保存变更的数据到数据库中.
   * @param dataBase DataBase组件
   * @param dataSets 需要更新数据到数据库的数据集组件
   * @param postEdits 更新数据是是否允许与给数据相关联其他的数据组件（如DataSetView）做POST操作
   * @param resetPendingStatus 更新完毕后是否合并各个数据集的日志
   * @param infos 提交数据时生成SQL的模式
   * @param queryTimeout 执行SQL语句失去响应的最长时间, 0表示不设置时间
   */
  public void saveChanges(DatabaseProvider provider, DataSet[] dataSets,
                          boolean postEdits, boolean resetPendingStatus,
                          ResolveInfo[] infos, int queryTimeout){
    try{
      _saveChanges(provider, dataSets, postEdits, resetPendingStatus, infos, queryTimeout);
    }
    finally{
      release();
    }
  }

  /**
   * 保存变更的数据到数据库中.
   * @param dataBase DataBase组件
   * @param dataSets 需要更新数据到数据库的数据集组件
   * @param postEdits 更新数据是是否允许与给数据相关联其他的数据组件（如DataSetView）做POST操作
   * @param resetPendingStatus 更新完毕后是否合并各个数据集的日志
   * @param infos 提交数据时生成SQL的模式
   * @param queryTimeout 执行SQL语句失去响应的最长时间, 0表示不设置时间
   */
  private void _saveChanges(DatabaseProvider provider, DataSet[] dataSets,
                            boolean postEdits, boolean resetPendingStatus,
                            ResolveInfo[] infos, int queryTimeout)
  {
    //synchronized(infos)
    this.queryTimeout = queryTimeout;
    this.dbProvider   = provider;
    this.infos        = infos;

    tableNames = getTableNames(dataSets);

    Exception ex   = null;
    boolean autoCommit = provider.getAutoCommit();

    try {
      provider.setAutoCommit(false);
      for(int i = 0; i < infos.length; i++)
        execSQL(infos[i].getBeforeResolveSQL());

      processDataSetDeletes(dataSets);
      processDataSetInserts(dataSets);
      processDataSetUpdates(dataSets);

      for(int i = 0; i < infos.length; i++)
        execSQL(infos[i].getAfterResolveSQL());

      if(autoCommit)
      {
        provider.commit();
        provider.setAutoCommit(autoCommit);
      }
      //
      mappingToLookup(dataSets);
    }
    catch (Exception ex0) {
      ex = ex0;
    }

    try {
      if (resetPendingStatus)
        resetPendingStatus(dataSets, ex == null);
    }
    catch(Exception ex1) {
      if (ex == null)
        ex = ex1;
    }

    if (ex != null) {
      try {
        provider.rollback();
      }
      catch(Exception ex2) {
      }
    }

    if (ex != null)
      DataSetException.throwException(DataSetException.EXCEPTION_CHAIN, ex);
  }

  /**
   * 执行没有返回值的SQL语句:Insert,delete,update.and all so
   * @param SQL 没有返回值的SQL语句
   */
  private void execSQL(String[] SQL) throws DataSetException
  {
    if(SQL == null)
      return;
    try{
      for(int i = 0; i < SQL.length; i++)
      {
        if(SQL[i] == null)
          continue;
        if(this.stmt == null)
          stmt = this.dbProvider.getConnection().createStatement();
        stmt.executeUpdate(SQL[i]);
      }
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
  }
  /**
   * 处理数据的修改，先处理主表，再处理从表
   * @param resOrder 数据集数组
   * @throws SQLException SQL异常
   */
  private void processDataSetUpdates(DataSet[] resOrder) throws SQLException
  {
    for (int index=0; index < resOrder.length; index++)
    {
      DataSet     dataSet = resOrder[index];
      ResolveInfo resolveInfo = infos[index];
      StorageDataSet sds = dataSet.getStorageDataSet();
      if (sds.getUpdatedRowCount() > 0)
      {
        if(!sds.hasRowIds())
        {
          resolveInfo.setUpdateMode(UpdateMode.ALL_COLUMNS);
          log.warn("Process resOrder["+index+"] updates: has not primary key. so change the update mode to ALL_COLUMNS." );
        }
        DataSetView updateDataSet = new DataSetView();
        sds.getUpdatedRows(updateDataSet);

        if(updateQuery == null)
          updateQuery = new UpdateQuery(dbProvider, queryTimeout);

        tableName = tableNames[index];
        try
        {
          processUpdates(updateDataSet, dataSet, resolveInfo.getUpdateMode());
        }
        finally
        {
          updateQuery.close();
          updateDataSet.close();
        }
      }
    }
  }

  /**
   * 处理删除的数据，先处理从表，再处理主表
   * @param resOrder 数据集数组
   * @throws SQLException SQL异常
   */
  private void processDataSetDeletes(DataSet[] resOrder) throws SQLException
  {
    for (int index = resOrder.length-1; index >= 0; index--)
    {
      DataSet     dataSet = resOrder[index];
      ResolveInfo resolveInfo = infos[index];
      StorageDataSet sds = dataSet.getStorageDataSet();
      if (sds.getDeletedRowCount() > 0)
      {
        if(!sds.hasRowIds())
        {
          resolveInfo.setUpdateMode(UpdateMode.ALL_COLUMNS);
          log.warn("Process resOrder["+index+"] deletes: has not primary key. so change the update mode to ALL_COLUMNS." );
        }

        DataSetView deleteDataSet = new DataSetView();
        sds.getDeletedRows(deleteDataSet);

        if(deleteQuery == null)
          deleteQuery = new DeleteQuery(dbProvider, queryTimeout);

        tableName = tableNames[index];
        try {
          processDeletes(deleteDataSet, dataSet, resolveInfo.getUpdateMode());
        }
        finally
        {
          deleteQuery.close();
          deleteDataSet.close();
        }
      }
    }
  }

  /**
   * 处理插入的数据，先处理主表，再处理从表
   * @param resOrder 数据集数组
   * @throws SQLException SQL异常
   */
  private void processDataSetInserts(DataSet[] resOrder) throws SQLException
  {
    for (int index=0; index < resOrder.length; index++)
    {
      DataSet     dataSet = resOrder[index];
      StorageDataSet sds = dataSet.getStorageDataSet();
      if (sds.getInsertedRowCount() > 0)
      {
        DataSetView insertDataSet = new DataSetView();
        sds.getInsertedRows(insertDataSet);

        if(insertQuery == null)
          insertQuery = new InsertQuery(dbProvider, queryTimeout);

        tableName = tableNames[index];
        try
        {
          processInserts(insertDataSet, dataSet, infos[index]);
        }
        finally
        {
          insertQuery.close();
          insertDataSet.close();
        }
      }
    }
  }

  /**
   * 处理一个表的需要插入的数据
   * @param insertDataSet 保存该表的需要插入的数据的数据集
   * @param dataSet 未处理的数据集
   * @param seq 该数据集的序列类
   * @throws SQLException SQL异常
   */
  private void processInserts(DataSet insertDataSet, DataSet dataSet, ResolveInfo resolveInfo) throws SQLException
  {
    Statement stmt = null;
    try{
      if (!insertDataSet.isEmpty())
      {
        DatabaseResolveHelper resolverHelp = resolveInfo.isSaveDataUseSelf() ? null :
            resolveInfo.getSequence() == null ? null : new DatabaseResolveHelper(resolveInfo.getSequence());
        //
        insertDataSet.first();
        int status;
        do {
          status  = insertDataSet.getStatus();
          if ((status & RowStatus.DELETED) == 0)
          {
            if(resolveInfo.getSequence() != null)
            {
              if(stmt == null)
                stmt = dbProvider.getConnection().createStatement();
              processSequence(insertDataSet, dataSet, resolveInfo, resolverHelp, stmt);
            }
            processInsertRow(insertDataSet);
          }
        } while(insertDataSet.next());

        if(resolverHelp != null)
        {
          resolveInfo.setResolveData(resolverHelp.getKeys());
          resolverHelp.release();
        }
      }
    }
    finally{
      if(stmt != null)
        stmt.close();
    }
  }

  /**
   * 处理一行数据的各个序列
   * @param insertDataSet 保存该表的需要插入的数据的数据集
   * @param srcDataSet 未处理的数据集
   * @param resolveInfo 该数据集的序列类
   * @param resolverHelp 提交数据的助手
   * @param stmt Statement
   */
  private void processSequence(DataSet insertDataSet,  DataSet srcDataSet, ResolveInfo resolveInfo,
                               DatabaseResolveHelper resolverHelp, Statement stmt)
  {
    srcDataSet.goToInternalRow(insertDataSet.getInternalRow());
    long internal = insertDataSet.getLong(DatabaseResolveHelper.INTERNALROW);
    String[] uniqueCols   = resolveInfo.getSequence().getUniqueColumns();
    String[] sequenceCols = resolveInfo.getSequence().getSequenceNames();
    for(int i=0; i<uniqueCols.length; i++)
    {
      String value = null;    //新的字段值
      String columnName = uniqueCols[i];
      int dataType = srcDataSet.getColumn(columnName).getDataType();
      if(dataType == Variant.BIGDECIMAL)
      {
        if(srcDataSet.getBigDecimal(columnName).intValue() > 0)
          continue;
        else
        {
          value = dbProvider.getSequence(sequenceCols[i], stmt);
          srcDataSet.setBigDecimal(columnName, new BigDecimal(value));
          srcDataSet.post();
          if(resolverHelp != null)
            resolverHelp.handleKey(internal, columnName, value);
        }
      }
      else if (dataType == Variant.STRING)
      {
        String columnValue = srcDataSet.getString(columnName);
        if(columnValue != null && !columnValue.equals(""))
          continue;
        else
        {
          value = dbProvider.getSequence(sequenceCols[i], stmt);
          srcDataSet.setString(columnName,value);
          srcDataSet.post();
          if(resolverHelp != null)
            resolverHelp.handleKey(internal, columnName, value);
        }
      }
    }
  }
  /**
   * 处理一个表的需要删除的数据
   * @param deleteDataSet 保存该表需要删除的数据的数据集
   * @param dataSet 未处理的数据集
   * @throws SQLException SQL异常
   */
  private void processDeletes(DataSet deleteDataSet, DataSet dataSet, int updateMode) throws SQLException
  {
    if (!deleteDataSet.isEmpty()) {
      deleteDataSet.first();
      int status;
      do {
        status  = deleteDataSet.getStatus();
        if ((status & RowStatus.INSERTED) == 0) {
          processDeleteRow(deleteDataSet, updateMode);
        }

      } while (deleteDataSet.next());
    }
  }

  /**
   * 处理一个表的需要修改的数据
   * @param updateDataSet 保存该表需要修改的数据的数据集
   * @param dataSet 未处理的数据集
   * @throws SQLException SQL异常
   */
  private void processUpdates(DataSet updateDataSet, DataSet dataSet, int updateMode) throws SQLException
  {
    if (!updateDataSet.isEmpty()) {
      DataRow     oldDataRow        = new DataRow(dataSet);
      StorageDataSet  dataSetStore  = dataSet.getStorageDataSet();

      updateDataSet.first();
      int status;
      do {
        status  = updateDataSet.getStatus();
        if (   (status&RowStatus.DELETED) == 0) {

          dataSetStore.getOriginalRow(updateDataSet, oldDataRow);
          processUpdateRow(updateDataSet, oldDataRow, updateMode);
        }

      } while (updateDataSet.next());
    }
  }

  /**
   * 处理一个表的需要插入的一行数据，并更新到数据库中
   * @param insertDataSet 保存该表需要插入的数据的数据集
   * @throws SQLException SQL异常
   */
  private void processInsertRow(DataSet insertDataSet) throws SQLException
  {
    try{
      initCache(insertDataSet, INSERT_MODE);

      cachedColumns = insertDataSet.getColumns();

      for (int index = 0; index < cachedColumns.length; index++)
      {
        insertDataSet.getVariant(index, values[index]);

        if (!values[index].isNull() && cachedColumns[index].isResolvable())
        {
          vChangeColumns.add(cachedColumns[index]);
          vValues.add(values[index]);
        }
      }

      if (vValues.size() < 1)
        DataSetException.noUpdatableColumns();

      Column[] insertColumns = new Column[vChangeColumns.size()];
      Variant[] insertValues = new Variant[vValues.size()];

      vChangeColumns.copyInto(insertColumns);
      vValues.copyInto(insertValues);

      insertQuery.setParameters(tableName, insertColumns, insertValues);

      int rowCount = insertQuery.execute();

      if (rowCount == 0)
        DataSetException.noRowsAffected(insertQuery.getQueryString(insertValues));
      else if (rowCount > 1)
        DataSetException.multipleRowsAffected(insertQuery.getQueryString(insertValues));
    }
    catch(SQLException ex) {
      throw ex;
    }
  }

  /**
   * 处理一个表的需要删除的一行数据，并更新到数据库中
   * @param deleteDataSet 保存该表需要删除的数据的数据集
   * @throws SQLException SQL异常
   */
  private void processDeleteRow(DataSet deleteDataSet, int updateMode) throws SQLException
  {
    try{
      initCache(deleteDataSet, DELETE_MODE);
      cachedColumns = deleteDataSet.getColumns();
      for (int index = 0; index < cachedColumns.length; index++)
      {
        deleteDataSet.getVariant(index, values[index]);

        if(cachedColumns[index].isRowId() ||
            (updateMode == UpdateMode.ALL_COLUMNS
             && cachedColumns[index].isSearchable()
             && cachedColumns[index].isResolvable()
            )
          )
        {
          vWhereColumns.add(cachedColumns[index]);
          vWhereValues.add(values[index]);
        }
      }

      if (vWhereValues.size() < 1)
        DataSetException.noUpdatableColumns();

      Column[] deleteColumns = new Column[vWhereColumns.size()];
      Variant[] deleteValues = new Variant[vWhereValues.size()];

      vWhereColumns.copyInto(deleteColumns);
      vWhereValues.copyInto(deleteValues);

      deleteQuery.setParameters(tableName, deleteColumns, deleteValues);

      int rowCount = deleteQuery.execute();

      if (rowCount == 0)
        noRowsAffected(deleteDataSet, deleteQuery.getQueryString(deleteValues));
      else if (rowCount > 1)
        DataSetException.multipleRowsAffected(deleteQuery.getQueryString(deleteValues));
    }
    catch(SQLException ex)
    {
      throw ex;
    }
  }

  /**
   * 处理一个表的需要修改的一行数据，并更新到数据库中
   * @param updateDataSet 保存该表需要修改的数据的数据集
   * @param oldRow 保存旧值的行
   * @throws SQLException SQL异常
   */
  private void processUpdateRow(DataSet updateDataSet, ReadWriteRow  oldRow, int updateMode) throws SQLException
  {
    try {
      initCache(updateDataSet, UPDATE_MODE);
      cachedColumns = updateDataSet.getColumns();

      boolean   isChanges   = false;
      boolean resolvable;
      for (int index = 0; index < cachedColumns.length; index++)
      {
        updateDataSet.getVariant(index, values[index]);
        oldRow.getVariant(index, oldValues[index]);

        resolvable  = cachedColumns[index].isResolvable();
        switch(updateMode)
        {
          case UpdateMode.KEY_COLUMNS:
            if(cachedColumns[index].isRowId())
              isChanges = addRowidToCache(cachedColumns[index], values[index], oldValues[index]) || isChanges;

            else if(resolvable)
              if(!values[index].equals(oldValues[index]))
              {
                isChanges = true;
                vChangeColumns.add(cachedColumns[index]);
                vValues.add(values[index]);
              }
            break;
          case UpdateMode.CHANGED_COLUMNS:
            if(cachedColumns[index].isRowId())
              isChanges = isChanges || addRowidToCache(cachedColumns[index], values[index], oldValues[index]);
            else if(resolvable)
              if(!values[index].equals(oldValues[index]))
              {
                if(cachedColumns[index].isSearchable())
                {
                  vWhereColumns.add(cachedColumns[index]);
                  vWhereValues.add(oldValues[index]);
                }
                isChanges = true;
                vChangeColumns.add(cachedColumns[index]);
                vValues.add(values[index]);
              }
            break;
          case UpdateMode.ALL_COLUMNS:
            if(cachedColumns[index].isRowId())
              isChanges = isChanges || addRowidToCache(cachedColumns[index], values[index], oldValues[index]);
            else if(resolvable)
            {
              if(cachedColumns[index].isSearchable())
              {
                vWhereColumns.add(cachedColumns[index]);
                vWhereValues.add(oldValues[index]);
              }
              if(!values[index].equals(oldValues[index]))
              {
                isChanges = true;
                vChangeColumns.add(cachedColumns[index]);
                vValues.add(values[index]);
              }
            }
            break;
        }
      }
      if (!isChanges)
        return;

      if (vChangeColumns.size() < 1)
        DataSetException.noUpdatableColumns();

      Column[] changeColumns = new Column[vChangeColumns.size()];
      Variant[] changevalues = new Variant[vValues.size()];
      Column[] whereColumns = new Column[vWhereColumns.size()];
      Variant[] whereValues = new Variant[vWhereValues.size()];

      vChangeColumns.copyInto(changeColumns);
      vValues.copyInto(changevalues);
      vWhereColumns.copyInto(whereColumns);
      vWhereValues.copyInto(whereValues);

      updateQuery.setParameters(tableName, changeColumns, changevalues, whereColumns, whereValues);
      int rowCount = updateQuery.execute();

      if (rowCount == 0) {
        noRowsAffected(oldRow, updateQuery.getQueryString(changevalues, whereValues));
      }
      else if (rowCount > 1)
        DataSetException.multipleRowsAffected(updateQuery.getQueryString(changevalues, whereValues));
    }
    catch(SQLException ex) {
      throw ex;
    }
  }

  /**
   * 生成更新数据的SQL语句时，将RowID加入临时变量中
   * @param idColumn ID的列
   * @param value    新值
   * @param oldValue 旧值
   * @return 返回新值与旧值是否不同
   */
  private boolean addRowidToCache(Column idColumn, Variant value, Variant oldValue)
  {
    vWhereColumns.add(idColumn);
    vWhereValues.add(oldValue);

    if(idColumn.isResolvable() && !value.equals(oldValue))
    {
      vChangeColumns.add(idColumn);
      vValues.add(value);
      return true;
    }
    return false;
  }

  /**
   * 初始化中间变量
   * @param dataSet 需要初始化中间变量的数据集
   * @param ResolverMode 更新数据的操作方式
   */
  private void initCache(DataSet dataSet, int ResolverMode)
  {
    values        = dataSet.allocateValues();
    if(ResolverMode == UPDATE_MODE)
      oldValues   = dataSet.allocateValues();

    if(ResolverMode == UPDATE_MODE || ResolverMode == INSERT_MODE)
    {
      if (vChangeColumns == null)
        vChangeColumns = new Vector();
      else if(vChangeColumns.size() > 0)
        vChangeColumns.clear();

      if (vValues == null)
        vValues = new Vector();
      else if(vValues.size() > 0)
        vValues.clear();
    }

    if(ResolverMode == UPDATE_MODE || ResolverMode == DELETE_MODE)
    {
      if (vWhereColumns == null)
        vWhereColumns = new Vector();
      else if(vWhereColumns.size() > 0)
        vWhereColumns.clear();

      if (vWhereValues == null)
        vWhereValues = new Vector();
      else if(vWhereValues.size() > 0)
        vWhereValues.clear();
    }
  }

  /**
   * 根据不同的数据库提取表名
   * @param dataSets 数据集
   * @return 返回表名
   */
  private String[] getTableNames(DataSet dataSets[])
  {
    String[] tables = new String[dataSets.length];
    for(int i=0; i<dataSets.length; i++)
    {
      char quoteCharacter = dbProvider.getRuntimeMetaData().getIdentifierQuoteChar();
      String tableName = dataSets[i].getTableName();
      String schemaName = dataSets[i].getSchemaName();
      if (tableName != null && tableName.length() > 0)
      {
        if (quoteCharacter != '\0' && !dbProvider.getRuntimeMetaData().isUseTableName())
          tableName = quoteCharacter + tableName + quoteCharacter;
        if (schemaName != null && schemaName.length() > 0)
        {
          if (quoteCharacter != '\0')
            tableName = quoteCharacter + schemaName + quoteCharacter + "." + tableName;
          else
            tableName = schemaName + "." + tableName;
        }
      }
      tables[i] = tableName;
    }
    return tables;
  }

  /**
   * 将表数据的变更映射到lookup去
   * @param dataSets 数据集数组
   */
  private synchronized static void mappingToLookup(DataSet dataSets[]){
    for(int i=0; i<dataSets.length; i++)
    {
      String tableName = dataSets[i].getTableName();
      if (tableName == null || tableName.length() == 0)
        continue;
      try{
        engine.web.lookup.TableMappingPool.mappingLookup(tableName);
      }
      catch(Exception ex){
        log.warn("mappingToLookup error!", ex);
      }
    }
  }
  /**
   * 执行UPDATE语句时没有行得到更新
   * @param searchRow   行数据
   * @param queryString SQL语句
   */
  private final void noRowsAffected(ReadRow searchRow, String queryString)
    /*-throws DataSetException-*/
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(queryString);
    buffer.append("\n");
    try {
      int count = searchRow.getColumnCount();
      int ordinal = 0;
      while(true) {
        buffer.append(searchRow.getColumn(ordinal).getColumnName());
        buffer.append('=');
        buffer.append(searchRow.format(ordinal));
        if (++ordinal >= count)
          break;
        buffer.append(':');
      }
    }
    catch(DataSetException ex) {
      ex.printStackTrace();
    }
    DataSetException.noRowsAffected(buffer.toString());
  }

  /**
   * 合并数据集的更新日志
   * @param dataSets 数据集数组
   * @param markResolved 是否合并日志
   */
  private void resetPendingStatus(DataSet[] dataSets, boolean markResolved)
  {
    for (int index = 0; index < dataSets.length; ++index)
      dataSets[index].resetPendingStatus(markResolved);
  }
}
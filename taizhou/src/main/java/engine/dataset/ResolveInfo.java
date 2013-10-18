package engine.dataset;

import com.borland.dx.dataset.DataSetData;
import com.borland.dx.dataset.*;
import java.util.List;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author 江海岛
 * @version 1.0
 */

public final class ResolveInfo implements DebugSql, java.io.Serializable
{
  private transient String queryString = null;

  private transient String masterColumnName = null;

  private           SequenceDescriptor sequence = null;

  private           int         updateMode = UpdateMode.KEY_COLUMNS;

  private transient DataSetData changedData = null;

  private transient StorageDataSet changedDataSet = null;
  //
  private transient DataSetData resolveData = null;

  private transient String[]    beforeResolveSQL = null;

  private transient String[]    afterResolveSQL  = null;

  private           List        rowIds = null;

  private           String      tableName = null;

  private           String      schemaName = null;

  private           boolean     isSaveDataUseSelf = false;

  public ResolveInfo(){}

  public ResolveInfo(String sql){
    this.queryString = sql;
  }

  public void clearTemp()
  {
    this.changedData = null;
    this.resolveData = null;
    this.changedDataSet = null;
    this.beforeResolveSQL = null;
    this.afterResolveSQL = null;
    this.queryString = null;
    this.masterColumnName = null;
  }
  /**
   * 得到SQL语句
   * @return 返回单前数据集的SQL
   */
  public String getQueryString()
  {
    return queryString;
  }

  public void setQueryString(String queryString)
  {
    this.queryString = queryString;
  }

  public String getMasterColumnName()
  {
    return masterColumnName;
  }

  public void setMasterColumnName(String masterColumnName)
  {
    this.masterColumnName = masterColumnName;
  }

  public SequenceDescriptor getSequence()
  {
    return sequence;
  }

  public void setSequence(SequenceDescriptor sequence)
  {
    this.sequence = sequence;
  }

  public DataSetData getResolveData()
  {
    return resolveData;
  }

  public void setResolveData(DataSetData resolveData)
  {
    this.resolveData = resolveData;
  }

  public DataSetData getChangedData()
  {
    return changedData;
  }


  public void setChangedData(DataSetData changedData)
  {
    this.changedData = changedData;
  }

  /**
   * 得到从数据库提取到的数据
   * @return 返回从数据库提取到的数据
   */
  public StorageDataSet getChangedDataSet()
  {
    return changedDataSet;
  }

  /**
   * 得到从数据库提取到的数据
   * @return 返回从数据库提取到的数据
   */
  public void setChangedDataSet(StorageDataSet ds)
  {
    if(!isSaveDataUseSelf)
      throw new engine.util.EngineRuntimeException("Load data state is use DataSetData object");
    this.changedDataSet = ds;
  }

  public int getUpdateMode()
  {
    return updateMode;
  }

  /**
   * 提交数据时生成SQL的模式
   * @param updateMode 生成SQL的模式
   */
  public void setUpdateMode(int updateMode)
  {
    this.updateMode = updateMode;
    if(this.updateMode < UpdateMode.ALL_COLUMNS || this.updateMode > UpdateMode.CHANGED_COLUMNS)
      this.updateMode = UpdateMode.KEY_COLUMNS;
  }

  public String[] getAfterResolveSQL()
  {
    return afterResolveSQL;
  }

  public void setAfterResolveSQL(String[] afterResolveSQL)
  {
    this.afterResolveSQL = afterResolveSQL;
  }

  public String[] getBeforeResolveSQL()
  {
    return beforeResolveSQL;
  }

  public void setBeforeResolveSQL(String[] beforeResolveSQL)
  {
    this.beforeResolveSQL = beforeResolveSQL;
  }

  public List getRowIds()
  {
    return rowIds;
  }

  public void setRowIds(List rowIds)
  {
    this.rowIds = rowIds;
  }

  public boolean hasRowIds()
  {
    return this.rowIds == null ? false : rowIds.size() > 0;
  }

  public String getSchemaName()
  {
    return schemaName;
  }

  public void setSchemaName(String schemaName)
  {
    this.schemaName = schemaName;
  }

  public String getTableName()
  {
    return tableName;
  }

  public void setTableName(String tableName)
  {
    this.tableName = tableName;
  }

  /**
   * 装载数据是否的自身的对象, 否则用DataSetData装载
   * @return 装载数据是否的自身的对象
   */
  public boolean isSaveDataUseSelf()
  {
    return isSaveDataUseSelf;
  }

  /**
   * 设置装载数据是否的自身的对象
   * @param value 装载数据是否的自身的对象
   */
  public void setSaveDataUseSelf(boolean value)
  {
    this.isSaveDataUseSelf = value;
  }
}
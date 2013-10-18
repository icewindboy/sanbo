package engine.dataset;

import engine.util.StringUtils;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: 1.客户端TableDatSet的Provide和Resolve
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author 江海岛
 * @version 1.0
 */

public final class ProvideInfo implements DebugSql, java.io.Serializable
{
  private int rowmin = -1, rowmax = -1;

  private transient DataSetData    provideData = null;

  private transient StorageDataSet provideDataSet = null;

  private           String         queryString = null;

  private           boolean        columnIsUseString= false;

  private           boolean        isLoadDataUseSelf = true;

  public ProvideInfo() {  }

  public ProvideInfo(String sql){
    this.queryString = sql;
  }


 public void clearTemp()
 {
   this.provideData = null;
   this.provideDataSet = null;
 }

  /**
   * 得到 提取数据段的最大值
   * @return 返回 提取数据段的最大值
   */
  public int getRowMax()
  {
    return rowmax;
  }

  /**
   * 提取数据段的最大值
   * @return 返回 提取数据段的最大值
   */
  public int getResultSetMax()
  {
    return rowmax < 0 ? -1 : rowmax+1;
  }

  /**
   * 设置 提取数据段的最大值
   * @param rowmax 提取数据段的最小值
   */
  public void setRowMax(int rowmax)
  {
    this.rowmax = rowmax;
  }

  /**
   * 得到 提取数据段的最小值
   * @return 返回 提取数据段的最小值
   */
  public int getRowMin()
  {
    return rowmin;
  }

  /**
   * 提取数据段的最小值
   * @return 返回 提取数据段的最小值
   */
  public int getResultSetMin()
  {
    return rowmin < 0 ? -1 : rowmin+1;
  }

  /**
   * 设置 提取数据段的最小值
   * @param rowmin 提取数据段的最小值
   */
  public void setRowMin(int rowmin)
  {
    this.rowmin = rowmin;
  }

  /**
   * 得到从数据库提取到的数据
   * @return 返回从数据库提取到的数据
   */
  public DataSetData getProvideData()
  {
    return provideData;
  }

  /**
   * 得到从数据库提取到的数据
   * @return 返回从数据库提取到的数据
   */
  public void setProvideData(DataSetData data)
  {
    if(isLoadDataUseSelf)
      throw new engine.util.EngineRuntimeException("Load data state is use self object");
    this.provideData = data;
  }

  /**
   * 得到从数据库提取到的数据
   * @return 返回从数据库提取到的数据
   */
  public StorageDataSet getProvideDataSet()
  {
    return provideDataSet;
  }

  /**
   * 得到从数据库提取到的数据
   * @return 返回从数据库提取到的数据
   */
  public void setProvideDataSet(StorageDataSet ds)
  {
    if(!isLoadDataUseSelf)
      throw new engine.util.EngineRuntimeException("Load data state is use DataSetData object");
    this.provideDataSet = ds;
  }

  /**
   * 得到SQL语句
   * @return 返回单前数据集的SQL
   */
  public String getQueryString()
  {
    return queryString;
  }

  /**
   * 设置数据集需要提取数据SQL语句
   * @param queryString 设置数据集需要提取数据SQL语句
   */
  public void setQueryString(String queryString)
  {
    this.queryString = StringUtils.stripEnterSymbol(queryString);
  }

  /**
   * 字段是否String类型
   * @return 字段是否String类型
   */
  public boolean columnIsUseString()
  {
    return columnIsUseString;
  }

  /**
   * 设置字段是否String类型
   * @param value 字段是否String类型
   */
  public void setColumnIsUseString(boolean value)
  {
    this.columnIsUseString = value;
  }

  /**
   * 装载数据是否的自身的对象
   * @return 装载数据是否的自身的对象
   */
  public boolean isLoadDataUseSelf()
  {
    return isLoadDataUseSelf;
  }

  /**
   * 设置装载数据是否的自身的对象
   * @param value 装载数据是否的自身的对象
   */
  public void setLoadDataUseSelf(boolean value)
  {
    this.isLoadDataUseSelf = value;
  }
}
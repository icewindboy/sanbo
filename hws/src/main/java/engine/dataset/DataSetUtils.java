package engine.dataset;

import java.util.ArrayList;
import com.borland.dx.dataset.*;

/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public final class DataSetUtils
{

  private DataSetUtils(){}

  /**
   * 将数据集的值转化为数组
   * @param ds 传入的数据集
   * @param columnName 字段名称
   * @return 转化后的String数组
   */
  public final static String[] rowsToArray(DataSet ds, String columnName)
  {
    synchronized(ds)
    {
      String[] rowValues = new String[ds.getRowCount()];
      if(ds.getRowCount() == 0)
        return rowValues;
      long internalRow = ds.getInternalRow();
      int ordinal = ds.getColumn(columnName).getOrdinal();
      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
      {
        rowValues[i] = EngineDataSet.getValue(ds, ordinal);
        ds.next();
      }
      ds.goToInternalRow(internalRow);
      return rowValues;
    }
  }

  /**
   * 将数据集的值转化为数组
   * @param ds 传入的数据集
   * @param columnName 字段名称
   * @param isNeedNull 是否需求空值
   * @param isRepeat 是否需求重复的值
   * @return 转化后的String数组
   */
  public final static String[] rowsToArray(DataSet ds, String columnName,
      boolean isNeedNull, boolean isRepeat)
  {
    if(isNeedNull && isRepeat)
      return rowsToArray(ds, columnName);
    else
    {
      synchronized(ds)
      {
        if(ds.getRowCount() == 0)
          return new String[0];

        ArrayList idList = new ArrayList(ds.getRowCount());
        long internalRow = ds.getInternalRow();
        int ordinal = ds.getColumn(columnName).getOrdinal();
        ds.first();
        for(int i = 0; i < ds.getRowCount(); i++)
        {
          String id = EngineDataSet.getValue(ds, ordinal);
          if(!isNeedNull && !isRepeat && id.length() > 0 && !idList.contains(id))
            idList.add(id);
          else if(!isNeedNull && id.length() > 0)
            idList.add(id);
          else if(!isRepeat && !idList.contains(id))
            idList.add(id);
          else if(isNeedNull && isRepeat)
            idList.add(id);
          ds.next();
        }
        ds.goToInternalRow(internalRow);

        String[] ids = new String[idList.size()];
        idList.toArray(ids);
        return ids;
      }
    }
  }
}
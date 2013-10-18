package engine.report.util;

import java.util.Map;
import java.util.Hashtable;
import java.math.BigDecimal;

import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.Variant;
import com.borland.dx.dataset.Column;
import engine.dataset.RowMap;
import engine.dataset.EngineDataSet;
import engine.util.Format;
/**
 * <p>Title: report</p>
 * <p>Description: 打印报表报表的数集</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public final class ReportData implements java.io.Serializable
{
  private Map data = null;

  /**
   * 添加报表的数据源
   * @param datasetName 数据源名称
   * @param ds 数据源对象
   */
  public void addReportData(String datasetName, DataSet ds)
  {
    addReportObject(datasetName, ds);
  }

  /**
   * 添加报表的数据源
   * @param datasetName 数据源名称
   * @param multi 数据源对象
   */
  public void addReportData(String datasetName, RowMap[] multi)
  {
    addReportObject(datasetName, multi);
  }

  /**
   * 添加报表的数据源
   * @param datasetName 数据源名称
   * @param single 单条数据源对象
   */
  public void addReportData(String datasetName, RowMap single)
  {
    addReportObject(datasetName, new RowMap[]{single});
  }

  /**
   * 添加报表的数据集
   * @param datasetName 数据源名称
   * @param ds 数据源对象
   */
  public void addReportData(String datasetName, Map[] multi)
  {
    addReportObject(datasetName, multi);
  }

  /**
   * 添加报表的数据集
   * @param datasetName 数据源名称
   * @param ds 数据源对象
   */
  public void addReportData(String datasetName, Map single)
  {
    addReportObject(datasetName, new Map[]{single});
  }

  /**
   * 添加报表的数据源
   * @param datasetName 数据源名称
   * @param ds 数据源对象
   */
  private void addReportObject(String datasetName, Object ds)
  {
    if(datasetName == null)
      return;
    if(ds == null)
      return;
    if(data == null)
      data = new Hashtable();

    datasetName = datasetName.toLowerCase();
    data.put(datasetName, ds);
  }

  /**
   * 得到报表数据源Map
   * @return 报表数据源Map
   */
  public Map getReportMap()
  {
    return this.data;
  }

  /**
   * 根据数据源名称得到报表数据源
   * @param datasetName dataset名称
   * @return 返回报表数据源
   */
  public Object getReportData(String datasetName)
  {
    if(this.data == null)
      return null;

    return this.data.get(datasetName);
  }

  /**
   * 清除数据
   */
  public void clear()
  {
    if(data == null)
      return;
    if(data.size() == 0)
      return;
    synchronized(data)
    {
      Object[] entrys = data.entrySet().toArray();
      String[] values = new String[entrys.length];
      for(int i=0; i < entrys.length; i++)
      {
        Map.Entry entry = (Map.Entry)entrys[i];
        Object obj = entry.getValue();
        if(obj instanceof EngineDataSet)
        {
          EngineDataSet eds = (EngineDataSet)obj;
          if(eds.isOpen())
            eds.closeDataSet();
        }
        else if(obj instanceof DataSet)
        {
          DataSet ds = (DataSet)obj;
          if(ds.isOpen())
            ds.close();
        }
      }
      data.clear();
    }
  }

  /**
   * 得到数据集的记录数
   * @param reportData 保存报表数据的对象
   * @param dateSetName 数据集的名称
   * @return 返回数据集的记录数
   */
  public int getReportDataRowCount(String dateSetName)
  {
    return getReportDataRowCount(this, dateSetName);
  }

  /**
   * 得到数据集的记录数
   * @param reportData 保存报表数据类
   * @param dateSetName 数据集的名称
   * @param reportData 提供给报表打印的数据
   * @return 返回数据集的记录数
   */
  private static int getReportDataRowCount(ReportData reportData, String dateSetName)
  {
    Object obj = reportData.getReportData(dateSetName);
    if(obj == null)
      return 0;

    int num =0;
    if(obj instanceof Map[])
      num = ((Map[])obj).length;
    else if(obj instanceof RowMap[])
      num = ((RowMap[])obj).length;
    else if(obj instanceof DataSet)
    {
      DataSet ds = ((DataSet)obj);
      num = ds.isOpen() ? ds.getRowCount() : 0;
    }
    return num;
  }

  /**
   * 从提供的数据集中提取需要的数据
   * @param dateSetName 数据集名称
   * @param rownum 数据集第几行
   * @param fieldName 字段名称
   * @param format 字段格式化信息
   * @param zeroformat 数字型字段为0时的格式化信息
   * @return 返回提取到的数据
   */
  public String getReportDataValue(String dateSetName, int row,
                                    String fieldName, String format, String zeroformat)
  {
    return getReportDataValue(this, dateSetName, row, fieldName, format, zeroformat);
  }


  private static String getReportDataValue(ReportData reportData, String dateSetName, int row,
                                    String fieldName, String format, String zeroformat)
  {
    Object obj = reportData.getReportData(dateSetName);
    if(obj == null)
      return "";
    String fieldValue = null;
    if(obj instanceof RowMap[])
    {
      RowMap curData = ((RowMap[])obj)[row];
      if(curData == null)
        fieldValue = zeroformat == null ? "" : zeroformat;
      else
      {
        fieldValue = String.valueOf(curData.get(fieldName));
        if(fieldValue.length() == 0 && zeroformat != null)
          fieldValue = zeroformat;
        else if(format != null && isDouble(fieldValue))
          fieldValue = Format.formatNumber(fieldValue, format);
      }
    }
    else if(obj instanceof Map[])
    {
      Map curData = ((Map[])obj)[row];
      if(curData == null)
        fieldValue = zeroformat == null ? "" : zeroformat;
      else
      {
        Object value = curData.get(fieldName);
        fieldValue = value == null ? "" : String.valueOf(curData.get(fieldName));
        if(fieldValue.length() == 0 && zeroformat != null)
          fieldValue = zeroformat;
        else if(format != null && isDouble(fieldValue))
          fieldValue = Format.formatNumber(fieldValue, format);
      }
    }
    else if(obj instanceof DataSet)
    {
      DataSet ds = (DataSet)obj;
      Column column = ds.hasColumn(fieldName);
      if(column == null)
        return "";
      int iDataType = column.getDataType();
      if(!ds.goToRow(row))
        return "";

      switch(iDataType)
      {
        case Variant.BIGDECIMAL:
          BigDecimal bg = ds.getBigDecimal(fieldName);
          if(format!= null && zeroformat != null && bg.doubleValue() == 0)
            fieldValue = zeroformat;
          else{
            fieldValue = format!= null ? Format.formatNumber(bg,format) :
                         ds.isAssignedNull(fieldName) ? "" : bg.toString();
          }
          break;
        case Variant.BOOLEAN:
          fieldValue = ds.getBoolean(fieldName) ? "是" : "否";
          break;
        default :
          fieldValue = EngineDataSet.getValue(ds, ds.getColumn(fieldName).getOrdinal());
      }
    }
    return fieldValue==null ? "" : fieldValue;
  }

  /**
   * 检测字符串是否是数字型的
   * @param value 需要检测的字符串
   * @return 返回是否是Double型的
   */
  private static boolean isDouble(String value)
  {
    try{
      Double.parseDouble(value);
    }catch(Exception ex){
      return false;
    }
    return true;
  }
}
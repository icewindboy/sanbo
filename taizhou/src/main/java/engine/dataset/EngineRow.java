package engine.dataset;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author 江海岛
 * @version 1.0
 */

public class EngineRow extends DataRow
{

  /**
   * 构建包含指定DataSet组件的特定几个列的EngineRow（但是没有数据）
   * 的Constructs a "scoped" DataRow containing the data structure (but no data)
   * from specified columns of the DataSet.
   * @param dataSet 需要Clone结构的DataSet组件
   * @param columnNames 需要构建的列名数组
   */
  public EngineRow(DataSet dataSet, String[] columnNames)
  {
    super(dataSet, columnNames);
  }

  /**
   * 构建指定DataSet组件的1列的EngineRow（但是没有数据）
   * @param dataSet    需要Clone结构的DataSet组件
   *                   只有指定的columnName参数的值包含在EngineRow中
   * @param columnName 需要构建EngineRow的列名.
   */
  public EngineRow(DataSet dataSet, String columnName)
  {
    super(dataSet, columnName);
  }

  /**
   * 构建指定DataSet组件的所有列的EngineRow（但是没有数据）
   * @param dataSet   需要Clone结构的DataSet组件，DataSet组件的所有列将被包含在EngineRow中
   */
  public EngineRow(DataSet dataSet)
  {
    super(dataSet);
  }

  /**
   * 得到字段值
   * @param columnName 字段名称
   * @return 返回字段值
   */
  public String getValue(String columnName)
  {
    return getValue(getColumn(columnName).getOrdinal());
  }

  /**
   * 得到字段值
   * @param ordinal 字段序号
   * @return 返回字段值
   */
  public String getValue(int ordinal)
  {
    return EngineDataSet.getValue(this, ordinal);
  }
  /**
   * 设置字段值, 支持int, long, float, double, BigDecimal, short, Timestamp, Date, String, boolean
   * 注意: EngineRow的Column的数量是构造时的列名数组的长度映射的，从0开始的。
   * 如:EngineRow EngineRow = new EngineRow(DataSet,"XXX"); 则设置XXX字段时需要: EngineRow.setValue(0,"123");
   * @param ordinal 字段的位置
   * @param value 要设置的字段值
   * @return 返回值 <0 表示错误, 根据返回值判断错误类型
   */
  public int setValue(int ordinal, String value)
  {
    return EngineDataSet.setValue(this, ordinal, value);
  }
}
package engine.dataset.database;

import com.borland.dx.dataset.*;
import engine.dataset.*;
/**
 * <p>Title: 处理主键或唯一的列的类</p>
 * <p>Description: 处理主键或唯一的列的类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */
final class DatabaseResolveHelper
{
  private String[] changecolumns;

  SequenceDescriptor sequence = null;
  /**
   * 构造函数
   * @param uniquecolumns 所有唯一列名
   * @param detailColumn 连接主表的列名
   */
  public DatabaseResolveHelper(SequenceDescriptor sequence)
  {
    this.sequence = sequence;
    changecolumns = sequence == null ? null : sequence.getUniqueColumns();
  }

  /**
   * 得到序列的键值
   * @return 返回包含所有更新过的序列
   */
  DataSetData getKeys() throws DataSetException {
    return this.keyDataSet == null ? null : DataSetData.extractDataSet(keyDataSet);
  }
  /**
   *
   */
  void handleKey(long internalRowNum, String columnName, String newValue) throws DataSetException
  {
    if (keyDataSet == null)
      createKeyDataSet();
    DataRow rowLocate = new DataRow(keyDataSet, INTERNALROW);
    rowLocate.setLong(INTERNALROW, internalRowNum);
    if(!keyDataSet.locate(rowLocate, Locate.FIRST))
    {
      keyDataSet.insertRow(false);
      keyDataSet.setLong(INTERNALROW, internalRowNum);
    }
    keyDataSet.setString(columnName, newValue);
    keyDataSet.post();
  }
  /**
   * 创建一个包含所有序列列的数据集
   */
  private void createKeyDataSet() throws DataSetException
  {
    keyDataSet = new TableDataSet();
    Column[] keyColumn = new Column[changecolumns.length+1];
    for(int i =0; i<changecolumns.length; i++)
      keyColumn[i] = new Column(changecolumns[i],changecolumns[i],Variant.STRING);

    keyColumn[changecolumns.length] = new Column(INTERNALROW,INTERNALROW,Variant.LONG);
    keyDataSet.setColumns(keyColumn);
    keyDataSet.open();
  }

  void release()
  {
    if(keyDataSet != null)
    {
      keyDataSet.empty();
      keyDataSet.close();
      keyDataSet = null;
    }
  }

  private transient TableDataSet keyDataSet;
  public static final String INTERNALROW = "INTERNALROW";
}
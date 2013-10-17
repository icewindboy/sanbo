package engine.dataset;

import java.io.Serializable;

/**
 * @author 江海岛
 * @version 1.0
 */

public class SequenceDescriptor implements Serializable
{
  /**
   * 构造函数
   * @param uniqueColumns 主键或需要用序列生成的字段  如：ID,         ID2
   * @param sequenceOrProcedure 序列名或存储过程     如：S_sequence, CALL pkg.procedure(?)
   */
  public SequenceDescriptor(String[] uniqueColumns, String[] sequenceOrProcedure) throws Exception
  {
    if(uniqueColumns.length != sequenceOrProcedure.length)
      throw new Exception("SequenceDescriptor: the Number of ProcedureName is not equal the Number of sequenceOrProcedure");
    for (int i=0; sequenceOrProcedure!=null && i<sequenceOrProcedure.length; i++)
    {
      String temp = sequenceOrProcedure[i].toLowerCase();
      if(temp.indexOf("call") > -1 && temp.indexOf("?") < -1)
        throw new Exception("SequenceDescriptor: the ProcedureName must return dataset");
    }
    this.uniqueColumns = uniqueColumns;
    this.sequenceNames = sequenceOrProcedure;
  }
  private String[] uniqueColumns;
  private String[] sequenceNames;
  /**
   * 得到类包含的成员的长度。如果uniqueColumns与sequenceNames的长度不同，去最小值
   * @return 返回类包含的成员的长度
   */
  public int length(){
    if(uniqueColumns==null|| sequenceNames==null)
      return 0;
    return uniqueColumns.length>sequenceNames.length ? sequenceNames.length : uniqueColumns.length;
  }
  /**
   * 得到某一位置的UniqueColumn，如果越界则返回null
   * @param position UniqueColumns的位置
   * @return 返回指定位置的UniqueColumn
   */
  public String getUniqueColumn(int position){
    if(position > this.length())
      return null;
    return uniqueColumns[position];
  }
  /**
   * 得到某一位置的SequenceName，如果越界则返回null
   * @param position sequenceNames的位置
   * @return 返回指定位置的SequenceName
   */
  public String getSequenceName(int position){
    if(position > this.length())
      return null;
    return sequenceNames[position];
  }
  /**
   * 得到所有的的UniqueColumn
   * @return 返回所有的UniqueColumn
   */
  public String[] getUniqueColumns(){ return this.uniqueColumns;  }
  /**
   * 得到所有的SequenceName
   * @return 返回所有的SequenceName
   */
  public String[] getSequenceNames(){ return this.sequenceNames;  }

}
package engine.project;

import engine.dataset.RowMap;
import java.util.Map;
import com.borland.dx.dataset.DataSet;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */

public interface LookUp extends java.io.Serializable
{
  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ds 传入的数据集（需要有）
   * @param idColumnName 与默认主键值相对应的数据集ds的字段名称, 主要用于提取数据集中的各行的值
   * //@throws Exception 异常信息
   */
  public void regData(DataSet ds, String idColumnName) ;//throws Exception;

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ds 传入的数据集（需要有）
   * @param idColumnNames 与默认主键值相对应的数据集ds的字段名称数组, 主要用于提取数据集中的各行的值
   * //@throws Exception 异常信息
   */
  public void regData(DataSet ds, String[] idColumnNames);

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ds 传入的数据集（需要有）
   * @param regColumnName 需要用于注册的字段名称, 数据集ds中的名称可能于之不同, 等于null将用默认的主键字段
   * @param regValuesColumn 于regColumnName相对应的数据集ds的字段名称, 主要用于提取数据集中的各行的值
   * //@throws Exception 异常信息
   */
  //public void regData(DataSet ds, String regColumnName, String regValuesColumn) throws Exception;

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ids 主键数组
   * //@throws Exception 异常信息
   */
  public void regData(String[] idValues) ;//throws Exception;

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param regInfos 注册数据的字段名称以及相应的数据值数组, 用该字段提取数据(非主键字段)
   */
  public void regData(LookupParam[] regInfos);

  /**
   * 注册其他条件数据, 在调用方法getList(String selectid, String paramid, String paramvalue)之前
   * @param ds 数据集
   * @param regColumnName 注册的字段值
   * @throws Exception 异常
   */
  public void regConditionData(DataSet ds, String regColumnName) ;//throws Exception;

  /**
   * 注册其他条件数据, 在调用方法getList(String selectid, String paramid, String paramvalue)之前
   * @param regColumnName 注册的字段名称
   * @param regColumnValues 注册的字段值
   * @throws Exception 异常
   */
  public void regConditionData(String regColumnName, String[] regColumnValues) ;//throws Exception;

  /**
   * 根据主键id得到需要查找的名称字段值
   * @param id 主键
   * @return 返回查找到的名称字段值
   */
  public String getLookupName(String idValue);

  /**
   * 根据多个主键得到需要查找的名称字段值
   * @param ids 主键数组
   * @return 返回查找到的名称字段值
   */
  public String getLookupName(String[] idValues);

  /**
   * 根据主键id得到需要查找记录的所有信息
   * @param id 主键
   * @return 返回查找到的记录的所有信息
   */
  public RowMap getLookupRow(String idValue);


  /**
   * 根据多个主键得到需要查找记录的所有信息
   * @param ids 主键数组
   * @return 返回查找到的记录的所有信息
   */
  public RowMap getLookupRow(String[] idValues);

  /**
   * 得到下拉框信息列表(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @param selectid 需要选中的id
   * @return 返回下拉框信息
   * @throws Exception 异常
   */
  public String getList(String selectid);// throws Exception;

  /**
   * 得到下拉框信息列表(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @return 返回下拉框信息
   * @throws Exception 异常
   */
  public String getList();// throws Exception;

  /**
   * 得到下拉框信息列表(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @param selectid 需要选中的id
   * @param paramid 制定打印含有显示特定字段和值的下拉框列表
   * @param paramvalue 制定打印含有显示特定字段和值的下拉框列表
   * @return 返回下拉框信息
   * @throws Exception 异常
   */
  public String getList(String selectid, String paramid, String paramvalue);// throws Exception;

  /**
   * 得到下拉框信息列表(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @param selectid 需要选中的id
   * @param paramids 各个参数id值
   * @return 返回下拉框信息
   * @throws Exception 异常
   */
  //public String getList(String selectid, Map paramids) throws Exception;

  /**
   * 得到下拉框信息列表(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @param selectid 需要选中的id
   * @param paramlist 各个参数
   * @return 返回下拉框信息
  public String getList(String selectid, Map paramlist) throws Exception;*/
}
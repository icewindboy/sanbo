package engine.web.lookup;

import java.util.*;
import engine.web.taglib.Select;
import engine.dataset.*;
import engine.util.log.Log;
import engine.util.StringUtils;
import engine.util.EngineRuntimeException;
import engine.util.MessageFormat;
import engine.dataset.RowMap;

import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.Locate;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class LookupImpl implements Lookup
{
  private ArrayList valueList = null;

  private EngineDataSet dsData = null;

  private LookupHelper help = null;

  private LookupParam[] filterValues = null;

  private String SQL = null;

  private Log log = new Log("Lookup");

  /**
   * 构造函数
   */
  LookupImpl() {  }

  public void init(LookupHelper help, EngineDataSet dsData){
    this.help = help;
    this.dsData = dsData;
    this.SQL = help.getSelect();
  }

  /**
   * 设置过滤条件的值
   * @param values 过滤条件的值
   */
  private void setFilterParams(LookupParam[] values)
  {
    this.filterValues = values;
  }

  /**
   * 设置分公司ID等特定的查询条件
   * @param whereParams 分公司ID等特定的查询条件
   */
  private void setWhereParams(LookupParam[] whereParams)
  {
    this.SQL = help.getSelect();
    Hashtable table = new Hashtable();
    for(int i=0; i < whereParams.length; i++)
    {
      String key = whereParams[i].getKey();
      if(key == null || key.length() == 0)
        continue;
      String[] values = whereParams[i].getValues();
      if(values == null || values.length == 0)
      {
        table.put(key, "1=1");
        continue;
      }

      boolean needOR = false;
      StringBuffer buf = new StringBuffer();
      for(int j=0; j<values.length; j++)
      {
        if(values[j] == null)
          continue;

        if(needOR)
          buf.append("OR ");
        else
          needOR = true;

        String upperValue = values[j].toUpperCase();
        if(upperValue.indexOf("IN") > -1 || upperValue.indexOf("<>") > -1)
          buf.append(key).append(" ").append(values[j]).append(" ");
        else
          buf.append(key).append("='").append(values[j]).append("'");
      }
      table.put(key, buf.toString());
    }
    this.SQL = MessageFormat.format(SQL, table);
  }

  /**
   * 锁定并打开只打开一次的数据集
   */
  private void openOneDataSet()
  {
    synchronized(dsData)
    {
      if(!dsData.isOpen())
      {
        String sortStr = help.getSortStr();
        dsData.setQueryString(sortStr != null ? (SQL + " " + sortStr) : SQL);
        dsData.openDataSet();
      }
    }
  }

  /**
   * 定位数据集
   * @param columnValue 定位数据集的字段名称值
   * @return 返回是否有此记录
   */
  private boolean locateDataSet(String columnValue)// throws Exception
  {
    checkDataSetReg();
    String[] keyColumns = help.getKeyColumns();
    if(keyColumns.length > 1)
      throw new LookupInvalidArgumentException("lookup length of key not equals length of value");

    return locateDataSet(dsData, keyColumns, new String[]{columnValue});
  }

  /**
   * 定位数据集
   * @param columnValues 定位数据集的字段名称值
   * @return 返回是否有此记录
   */
  private boolean locateDataSet(String[] columnValues)// throws Exception
  {
    checkDataSetReg();
    String[] keyColumns = help.getKeyColumns();
    if(keyColumns.length != columnValues.length)
      throw new LookupInvalidArgumentException("lookup length of key not equals length of value");

    return locateDataSet(dsData, keyColumns, columnValues);
  }

  /**
   * 定位数据集
   * @param ds 需要定位的数据集体（可以事view也可以是）
   * @param columnName 定位数据集的字段名称
   * @param columnValue 定位数据集的字段名称值
   * @return 返回是否有此记录
   */
  private boolean locateDataSet(DataSet ds, String[] columnNames, String[] columnValues)// throws Exception
  {
    EngineRow locateRow = new EngineRow(ds, columnNames);//
    for(int i=0; i<columnValues.length; i++)
      locateRow.setValue(i, columnValues[i]);

    if(!ds.locate(locateRow, Locate.FIRST))
    {
      EngineDataSetProvider provider =  (EngineDataSetProvider)dsData.getProvider();
      StringBuffer buf = new StringBuffer(SQL);
      buf.append(SQL.toUpperCase().indexOf("WHERE") > -1 ? " AND " : " WHERE ");
      for(int i=0; i<columnNames.length; i++)
      {
        if(columnValues[i].length() == 0)
          buf.append(columnNames[i]).append(" IS NULL ");
        else
          buf.append(columnNames[i]).append("='").append(columnValues[i]).append("'");
        if(i < columnNames.length -1)
          buf.append(" AND ");
      }
      //
      ProvideInfo provideInfo = new ProvideInfo(buf.toString());
      provideInfo.setLoadDataUseSelf(false);
      ProvideInfo[] provide = new ProvideInfo[]{provideInfo};
      try{
        provide = provider.getDataSetData_info(provide);
      }
      catch(Exception ex){
        throw new EngineRuntimeException("locate at db", ex);
      }
      provide[0].getProvideData().loadDataSet(ds);
      provide = null;
      return ds.locate(locateRow, Locate.FIRST);
    }
    return true;
  }

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ds 传入的数据集（需要有）
   * @param idName 与默认主键值相对应的数据集ds的字段名称, 主要用于提取数据集中的各行的值
   * @throws Exception 异常信息
   */
  public synchronized void regData(DataSet ds, String idColumnName) //throws Exception
  {
    if(help.isList())
    {
      openOneDataSet();
      return;
    }
    String[] regKeyColumns = help.getRegKeyColumns();
    if(regKeyColumns.length > 1)
      throw new LookupInvalidArgumentException("lookup length of key not equals length of value");

    String[] ids = columnToArray(ds, idColumnName);
    //
    regData(new LookupParam[]{new LookupParam(regKeyColumns[0], ids)});
  }

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ds 传入的数据集（需要有）
   * @param idColumnNames 与默认主键值相对应的数据集ds的字段名称数组, 主要用于提取数据集中的各行的值
   * @throws Exception 异常信息
   */
  public synchronized void regData(DataSet ds, String[] idColumnNames)
  {
    if(help.isList())
    {
      openOneDataSet();
      return;
    }
    if(idColumnNames == null)
      return;

    String[] regKeys = help.getRegKeyColumns();
    if(regKeys.length != idColumnNames.length)
      throw new LookupInvalidArgumentException("lookup length of key not equals length of value");

    LookupParam[] params = new LookupParam[regKeys.length];
    for(int i=0; i<regKeys.length; i++)
    {
      String[] ids = columnToArray(ds, idColumnNames[i]);
      params[i] = new LookupParam(regKeys[i], ids);
    }
    //
    regData(params);
  }


  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param idValues 主键值数组
   * @throws Exception 异常信息
   */
  public synchronized void regData(String[] idValues) //throws Exception
  {
    String[] regKeyColumns = help.getRegKeyColumns();
    if(regKeyColumns.length > 1)
      throw new LookupInvalidArgumentException("lookup length of key not equals length of value");

    regData(new LookupParam[]{new LookupParam(regKeyColumns[0], idValues)});
  }

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param regInfos 注册数据的字段名称以及相应的数据值数组, 用该字段提取数据(非主键字段)
   */
  public synchronized void regData(LookupParam[] regInfos)// throws Exception
  {
    if(help.isList())
    {
      openOneDataSet();
      return;
    }

    if(regInfos == null)
      return;

    String tempSQL = arrryToSQL(SQL, help.getSelect(), regInfos);
    synchronized(dsData)
    {
      dsData.setQueryString(tempSQL);
      if(dsData.isOpen())
        dsData.refresh();
      else
        dsData.openDataSet();
    }
  }

  /**
   * 将数据集的值转化为数组
   * @param ds 传入的数据集
   * @param columnName 字段名称
   * @return 转化后的String数组
   */
  private static String[] columnToArray(DataSet ds, String columnName)
  {
    synchronized(ds)
    {
      ArrayList idList = new ArrayList(ds.getRowCount());
      int rownum = ds.getRow();
      ds.first();
      int ordinal = ds.getColumn(columnName).getOrdinal();
      for(int i = 0; i < ds.getRowCount(); i++)
      {
        String id = EngineDataSet.getValue(ds, ordinal);
        if(id.length() > 0 && idList.indexOf(id) > -1)
          idList.add(id);
        ds.next();
      }
      if(ds.getRowCount() > 0)
        ds.goToRow(rownum);

      String[] ids = new String[idList.size()];
      idList.toArray(ids);
      return ids;
    }
  }

  /**
   * 组装主键的WHERE IN 子句
   * @param select select..from 部分SQL语句
   * @param initSelect 初始化的select.部分SQL语句，不带多余的where条件的
   * @param wheresValues WHERE条件字段名称和值的数组
   * @return 返回装后的SQL语句
   */
  private static String arrryToSQL(String select, String initSelect, LookupParam[] wheresValues)
  {
    synchronized(wheresValues)
    {
      StringBuffer buf = new StringBuffer(select);
      buf.append(select.toUpperCase().indexOf("WHERE") > -1 ? " AND " : " WHERE ");
      int startLength = buf.length();
      for(int i=0; i<wheresValues.length; i++)
      {
        LookupParam whereValue = wheresValues[i];
        String[] columnValues = whereValue.getValues();
        if(columnValues==null)
          continue;
        if(columnValues.length > 0)
          buf.append(whereValue.getKey()).append(" IN ('" );
        for(int j=0; j<columnValues.length; j++)
        {
          String value = columnValues[j] == null || columnValues[j].length()==0 ? "-1" : columnValues[j];
          buf.append(value).append(j== columnValues.length-1 ? "')" : "','");
        }
        if(i<wheresValues.length-1   && columnValues.length > 0 &&
           wheresValues[i+1] != null && wheresValues[i+1].getValues() != null &&
           wheresValues[i+1].getValues().length > 0)
          buf.append(" AND ");
      }
      if(buf.length() == startLength)
      {
        buf.setLength(0);
        buf.append(initSelect);
        buf.append(initSelect.toUpperCase().indexOf("WHERE") > -1 ? " AND " : " WHERE ");
        buf.append("1<>1");
      }

      return buf.toString();
    }
  }

  /**
   * 根据主键id得到需要查找的名称字段值
   * @param idValue 主键
   * @return 返回查找到的名称字段值
   */
  public synchronized String getLookupName(String idValue)// throws Exception
  {
    return idValue == null || idValue.length() == 0
                    ? "" : getLookupName(new String[]{idValue});
  }

  /**
   * 根据多个主键得到需要查找的名称字段值
   * @param ids 主键数组
   * @return 返回查找到的名称字段值
   */
  public synchronized String getLookupName(String[] idValues)
  {
    if(idValues == null || idValues.length == 0 || !locateDataSet(idValues))
      return "";

    StringBuffer name = new StringBuffer();
    int i=0;
    String[] capsColumn = help.getCaptionColumns();
    for(; i<capsColumn.length; i++){
      name.append(dsData.getValue(capsColumn[i]));
      if(i < capsColumn.length-1)
        name.append(" ");
    }
    return name.toString();
  }

  /**
   * 根据主键id得到需要查找记录的所有信息
   * @param idValue 主键
   * @return 返回查找到的记录的所有信息
   * @throws Exception 异常信息
   */
  public synchronized RowMap getLookupRow(String idValue)
  {
    return idValue == null || idValue.length() == 0 || !locateDataSet(idValue)
                   ? new RowMap() : new RowMap(dsData);
  }

  /**
   * 根据多个主键得到需要查找记录的所有信息
   * @param ids 主键数组
   * @return 返回查找到的记录的所有信息
   */
  public synchronized RowMap getLookupRow(String[] idValues)
  {
    return idValues == null || idValues.length == 0 || !locateDataSet(idValues)
                    ? new RowMap() : new RowMap(dsData);
  }

  /**
   * 的lookup的对象可用列表信息
   * @param paramids 需要打印的列名和相应的值, 每个LookupParam保存key=field1, value=1,2。
   * 表示字段field1的值含有1或2的加入下拉框列表。
   * 如果lookup.xml配置文件定义了filterKey属性。paramIds的值将与filterKey合并
   * 如: filter:key=field1, value=1,2; paramIds:key=field1, value=3。将合并为key=field1, value=1,2,3
   * @return 可用列表的二维数组
   */
  public synchronized String[][] getList(LookupParam[] paramIds)
  {
    checkDataSetReg(); //检测数据集是否已经注册

    if(valueList == null)
      valueList = new ArrayList();
    else
      valueList.clear();

    if(help.isTree())
    {
      dsData.first();
      while(dsData.inBounds())
      {
        String fatherCode = dsData.getValue(help.getTreeCode());
        dataSetToTreeOption(this, fatherCode, 0, valueList);
      }
    }
    else if(paramIds == null)
      dataSetToOption(dsData, help.getKeyColumns(), help.getCaptionColumns(),
                      this.filterValues, valueList);
    else if(this.filterValues == null)
      dataSetToOption(dsData, help.getKeyColumns(), help.getCaptionColumns(),
                      paramIds, valueList);
    else
    {
      LookupParam[] allParams = LookupParam.union(this.filterValues, paramIds);
      dataSetToOption(dsData, help.getKeyColumns(), help.getCaptionColumns(),
                      allParams, valueList);
    }

    return (String[][])valueList.toArray(new String[valueList.size()][2]);
  }

  /**
   * 创建树装下拉框
   * @param lookupBean lookupBeand对象
   * @param fatherCode 父结点编号
   * @param level 层
   * @return 返回是否还有子结点
   */
  private synchronized static boolean dataSetToTreeOption(LookupImpl lookupBean, String fatherCode,
      int level, List valueList)
  {
    //EngineDataView ds = lookupBean.viewData;
    EngineDataSet ds = lookupBean.dsData;
    LookupHelper help = lookupBean.help;
    String codeFieldName = help.getTreeCode();

    String[] keyColumns = help.getKeyColumns();
    String[] curNodeIds  = new String[keyColumns.length];
    for(int i=0; i<curNodeIds.length; i++)
      curNodeIds[i] = ds.getValue(keyColumns[i]);

    String curNodeCode = ds.getValue(codeFieldName);

    String[] capsColumn = help.getCaptionColumns();
    StringBuffer curNodeName = new StringBuffer();
    for(int i=0; i<capsColumn.length; i++)
    {
      curNodeName.append(ds.getValue(capsColumn[i]));
      if(i < capsColumn.length-1)
        curNodeName.append(" ");
    }

    boolean hasChild = false;
    boolean isLeaf = false;

    if(ds.next())
    {
      String nextCode = ds.getValue(codeFieldName);
      isLeaf = !nextCode.startsWith(curNodeCode);
      hasChild = nextCode.startsWith(fatherCode);
    }
    else
    {
      isLeaf = true;
      hasChild = false;
    }

    StringBuffer buf = new StringBuffer(curNodeName.length()+10);
    for(int i=0; i<level; i++)
      buf.append("  ");
    buf.append(curNodeName);

    valueList.add(
        new String[]{curNodeIds.length==1 ? curNodeIds[0] : StringUtils.getArrayValue(curNodeIds),
          buf.toString()
    });
    //
    if(!isLeaf)
    {
      while(true)
      {
        boolean isHasChild = dataSetToTreeOption(lookupBean, curNodeCode, level+1, valueList);
        if(!isHasChild)
          break;
      }

      hasChild = ds.inBounds() && ds.getValue(codeFieldName).startsWith(fatherCode);
    }
    return hasChild;
  }

  /**
   * 将数据集转化为网页的下拉框的option的内容
   * @param ds 数据集
   * @param selectIds id列名和初始化option
   * @param capColumns 显示的名称
   * @param paramids 需要打印的列名和相应的值
   * @return 包含option的字符串
   */
  private synchronized static void dataSetToOption(DataSet ds, String[] selectIds,
      String capColumns[], LookupParam[] paramIds, List valueList)
  {
    if(ds.getRowCount() == 0)
      return;
    int[] idOrdianal = new int[selectIds.length];
    for(int i=0; i<idOrdianal.length; i++)
      idOrdianal[i] = ds.getColumn(selectIds[i]).getOrdinal();

    int[] capOrdinals = new int[capColumns.length];
    for(int i=0; i<capOrdinals.length; i++)
      capOrdinals[i] = ds.getColumn(capColumns[i]).getOrdinal();

    ds.first();
    for(int i=0; i<ds.getRowCount(); i++)
    {
      if(paramIds == null)
        addSelectItem(ds, idOrdianal, capOrdinals, valueList);
      else
      {
        boolean isAdd = true;
        for(int j=0; j<paramIds.length; j++)
        {
          String   key = paramIds[j].getKey();
          String[] values = paramIds[j].getValues();
          String   data = EngineDataSet.getValue(ds, ds.getColumn(key).getOrdinal());
          if(StringUtils.indexOf(values, data) < 0)
          {
            isAdd = false;
            break;
          }
        }
        if(isAdd)
          addSelectItem(ds, idOrdianal, capOrdinals, valueList);
      }
      ds.next();
    }
  }

  /**
   * 添加选择框的选择项
   * @param buf 选择项对象
   * @param ds 数据集
   * @param idOrdianal id字段的Ordianal
   * @param capOrdinals 显示的各个字段的Ordianal
   */
  private synchronized static void addSelectItem(DataSet ds, int[] idOrdianals, int[] capOrdinals,
      List valueList)
  {
    String[] idValues = new String[idOrdianals.length];
    for(int i=0; i<idValues.length; i++)
      idValues[i] = EngineDataSet.getValue(ds, idOrdianals[i]);

    StringBuffer buf = new StringBuffer();
    int j=0;
    for(; j<capOrdinals.length-1; j++)
    {
      buf.append(EngineDataSet.getValue(ds, capOrdinals[j]));
      buf.append(" ");
    }
    buf.append(EngineDataSet.getValue(ds, capOrdinals[j]));
    //
    valueList.add(
        new String[]{idValues.length==1 ? idValues[0] : StringUtils.getArrayValue(idValues, ","),
          buf.toString()
    });
  }


  /**
   * 检测数据集是否已经注册
   * @throws Exception 异常信息
   */
  private void checkDataSetReg() throws LookupNoRegException
  {
    if(help.isList())
      openOneDataSet();

    if(!dsData.isOpen())
    {
      log.warn(help.getSelect()+": you should call method of regData() at first!");
      throw new LookupNoRegException("you should call method of regData() at first!");
    }
  }

  /**
   * 释放资源
   */
  public void release()
  {
    if(help.isOneOpen())
      return;

    if(this.dsData != null)
    {
      this.dsData.closeDataSet();
      this.dsData = null;
    }

    this.help = null;
    this.SQL = null;
    this.log = null;
  }

  /**
   * 刷新数据
   */
  public void refresh(){
    if(help.isList() && dsData.isOpen())
      dsData.closeDataSet();
  }
}
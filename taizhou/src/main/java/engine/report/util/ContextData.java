package engine.report.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;

import engine.dataset.EngineDataSetProvider;
import engine.dataset.sql.QueryWhere;
import engine.util.context.DefaultContext;
import engine.util.StringUtils;
import java.io.*;
/**
 * <p>Title: 报表上下文数据</p>
 * <p>Description: 报表上下文数据：保存打印报表的信息，包括数据源，监听器，已经字段信息</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public final class ContextData extends DefaultContext implements Serializable
{
  private ReportListener listeners = null;

  private List     showColumns = null;
  private List     sumColumns = null;
  private List     groupColumns = null;
  private Map      columnCaptions = null;

  private List datas = null;

  private List selectedColumns = null;

  private String groupColumn = null;

  private List selectedSumColumns = null;

  private String[] sortColumns = null;

  private QueryWhere queryWhere = null;

  private QueryWhere advanceWhere = null;

  private EngineDataSetProvider dataSetProvider = null;

  /**
   * 添加份报表数据
   * @param data 报表数据源
   */
  public void addReportData(ReportData data)
  {
    if(data == null)
      return;
    if(datas == null)
      datas = new ArrayList();
    datas.add(data);
  }

  /**
   * 添加份报表数据
   * @param data 报表数据源
   */
  public void addReportDatas(ReportData[] datas)
  {
    if(datas == null)
      return;
    for(int i=0; i<datas.length; i++)
      addReportData(datas[i]);
  }

  /**
   * 得到报表数据源数组
   * @return 返回报表数据源数组
   */
  public ReportData[] getReportDatas()
  {
    if(datas == null)
      return null;

    ReportData[] temps = new ReportData[datas.size()];
    datas.toArray(temps);
    return temps;
  }

  /**
   * 得到分组的列名
   * @return 返回分组的列名
   */
  public String getGroupColumn() {
    return groupColumn;
  }

  /**
   * 设置分组的列名
   * @param groupColumn 分组的列名
   */
  public void setGroupColumn(String groupColumn)
  {
    this.groupColumn = groupColumn;
  }

  /**
   * 添加需要显示的字段名
   * @param columnName 需要显示的字段名
   */
  public void addShowColumn(String columnName)
  {
    if(columnName == null)
      return;
    if(selectedColumns == null)
      selectedColumns = new ArrayList();
    selectedColumns.add(columnName.toLowerCase());
  }

  /**
   * 添加需要显示的字段名数组
   * @param columnNames 显示的字段名数组
   */
  public void addShowColumns(String[] columnNames)
  {
    if(columnNames == null)
      return;
    for(int i=0; i<columnNames.length; i++)
      addShowColumn(columnNames[i]);
  }

  /**
   * 得到报表数据源数组
   * @return 返回报表数据源数组
   */
  public String[] getShowColumns()
  {
    return StringUtils.listToStrings(selectedColumns, true);
  }

  /**
   * 添加报表需要统计字段
   * @param columnName 报表需要统计字段
   */
  public void addShowSumColumn(String columnName)
  {
    if(columnName == null)
      return;
    if(selectedSumColumns == null)
      selectedSumColumns = new ArrayList();
    selectedSumColumns.add(columnName.toLowerCase());
  }

  /**
   * 得到报表需要统计字段数组
   * @return 返回报表需要统计字段数组
   */
  public String[] getShowSumColumns()
  {
    return StringUtils.listToStrings(selectedSumColumns, true);
  }

  /**
   * 得到报表监听器
   */
  public ReportListener getReportListeners()
  {
    if(listeners == null)
      listeners = new ReportListener();
    return listeners;
  }

  /**
   * 清除所有的字段有关的变量
   */
  public void clearTemps()
  {
    if(selectedColumns != null)
      selectedColumns.clear();

    if(selectedSumColumns != null)
      selectedSumColumns.clear();

    sortColumns = null;
    groupColumn = null;
  }

  /**
   * 清除报表数据
   */
  public void clear()
  {
    super.clear();

    if(showColumns != null)
      showColumns.clear();
    if(sumColumns != null)
      sumColumns.clear();
    if(groupColumns != null)
      groupColumns.clear();
    if(columnCaptions != null)
      columnCaptions.clear();

    if(listeners != null)
      listeners.clear();
    //
    clearTemps();
    //
    if(datas == null)
      return;
    synchronized(datas)
    {
      int count = datas.size();
      for(int i=0; i < count; i++)
      {
        ReportData rep = (ReportData)datas.get(i);
        rep.clear();
      }
      datas.clear();
    }

    queryWhere = null;
    advanceWhere = null;
    dataSetProvider = null;
  }

  /**
   * 释放资源
   */
  public void release()
  {
    clear();
    showColumns = null;
    sumColumns = null;
    groupColumns = null;
    columnCaptions = null;

    datas = null;
    selectedColumns = null;
    selectedSumColumns = null;
    groupColumn = null;
    listeners = null;
  }

  //---------------------------------------
  /**
   * 添加字段, 添加显示字段
   * @param name 字段名称,
   */
  public void addColumn(String name)
  {
    if(showColumns == null)
      showColumns = new ArrayList();
    showColumns.add(name);
    //同时默认选中
    addShowColumn(name);
  }

  /**
   * 重新设置所有字段的信息
   * @param names
   */
  public void setColumns(String[] names)
  {
    if(names == null || names.length == 0)
      return;

    if(showColumns == null)
      showColumns = new ArrayList();
    else
      showColumns.clear();

    for(int i=0; i<names.length; i++)
      showColumns.add(names[i]);
  }

  /**
   * 添加字段标题
   * @param name 字段名称
   * @param caption 字段标题
   */
  public void addColumnCaption(String name, String caption)
  {
    if(columnCaptions == null)
      columnCaptions = new Hashtable();
    columnCaptions.put(name, caption);
  }

  /**
   * 添加统计字段
   * @param name 字段名称
   */
  public void addSumColumn(String name)
  {
    if(sumColumns == null)
      sumColumns = new ArrayList();
    sumColumns.add(name);
  }

  /**
   * 添加分组字段
   * @param name 字段名称
   */
  public void addGroupColumn(String name)
  {
    if(groupColumns == null)
      groupColumns = new ArrayList();
    groupColumns.add(name);
  }

  /**
   * 得到所有显示的字符串信息
   * @return 返回字符串信息
   */
  public String[] getAllColumns()
  {
    return StringUtils.listToStrings(showColumns, true);
  }

  /**
   * 得到计算字段数组
   * @return 返回计算字段数组
   */
  public String[] getSumColumns()
  {
    return StringUtils.listToStrings(sumColumns, true);
  }

  /**
   * 得到分组字段数组
   * @return 返回分组字段数组
   */
  public String[] getGroupColumns()
  {
    return StringUtils.listToStrings(groupColumns, true);
  }

  /**
   * 得到字段标题
   * @param name 名称
   * @return 返回字段标题
   */
  public String getCaption(String name)
  {
    return columnCaptions == null ? null : (String)columnCaptions.get(name);
  }

  /**
   * 是否需要显示字段
   * @param name 字段名
   * @return 是否需要显示
   */
  public boolean isInColumn(String name)
  {
    return showColumns == null ? false : showColumns.contains(name);
  }

  /**
   * 是否需要计算显示字段
   * @param name 字段名称
   * @return 是否是需要计算的字段
   */
  public boolean isSumColumn(String name)
  {
    return sumColumns == null ? false : sumColumns.contains(name);
  }

  /**
   * 是否需要分组显示字段
   * @param name 字段名称
   * @return 是否是需要分组的字段
   */
  public boolean isGroupColumn(String name)
  {
    return groupColumns == null ? false : groupColumns.contains(name);
  }

  /**
   * 是否是选中字段的一个
   * @param name 字段名称
   * @return 是否是选择字段的一个
   */
  public boolean isShowColumn(String name)
  {
    return selectedColumns == null ? false : selectedColumns.contains(name);
  }

  /**
   * 得到排序字段
   * @return 返回排序字段
   */
  public String[] getSortColumns()
  {
    return sortColumns;
  }

  /**
   * 设置排序字段
   * @param 排序字段
   */
  public void setSortColumns(String[] sortColumns)
  {
    this.sortColumns = sortColumns;
  }

  /**
   * 得到页面的查询条件
   * @return 返回页面的查询条件
   */
  public QueryWhere getQueryWhere() {
    return queryWhere;
  }

  /**
   * 设置页面的查询条件
   * @param queryWhere 页面的查询条件
   */
  public void setQueryWhere(QueryWhere queryWhere) {
    this.queryWhere = queryWhere;
  }

  /**
   * 得到页面的高级查询条件
   * @return 返回页面的查询条件
   */
  public QueryWhere getAdvanceWhere() {
    return advanceWhere;
  }

  /**
   * 设置页面的高级查询条件
   * @param advanceWhere 页面的查询条件
   */
  public void setAdvanceWhere(QueryWhere advanceWhere) {
    this.advanceWhere = advanceWhere;
  }

  /**
   * 得到数据集的提供对象
   * @return 返回数据集的提供对象
   */
  public EngineDataSetProvider getDataSetProvider() {
    return dataSetProvider;
  }

  /**
   * 设置数据集的提供对象
   * @param provider 数据集的提供对象
   */
  public void setDataSetProvider(EngineDataSetProvider provider) {
    this.dataSetProvider = provider;
  }
}
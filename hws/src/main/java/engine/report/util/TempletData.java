package engine.report.util;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;

import engine.util.StringUtils;
import engine.report.pdf.*;
/**
 * <p>Title: report</p>
 * <p>Description: 报表工具</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public final class TempletData
    implements Serializable
{
  private Map fieldName = null;

  private Map pageInfo = null;

  private List table_other_Infos = null;

  private List datesetInfos = null;

  private List fieldInfos = null;

  private List whereInfos = null;

  private List paramInfos = null;

  private String title = "";
  /**
   * 添加table标签的信息
   * @param table table标签的信息
   */
  public void addTable(HtmlTable table)
  {
    if(table_other_Infos == null)
      table_other_Infos = new ArrayList();
    table_other_Infos.add(table);
  }

  /**
   * 添加其他信息
   * @param other 其他信息
   */
  public void addOther(String other)
  {
    if(table_other_Infos == null)
      table_other_Infos = new ArrayList();
    table_other_Infos.add(other);
  }

  /**
   * 得到table标签的信息其他信息（如JAVASRCIPT）的数组
   * @return 返回table标签的信息其他信息（如JAVASRCIPT）的数组
   */
  public Object[] getTablesAndOther()
  {
    if(table_other_Infos == null)
      return null;
    return table_other_Infos.toArray();
  }

  /**
   * 设置其他信息（如JAVASRCIPT）
   * @param index 在数组的位置
   * @param other 其他的信息
   * @return 若数组还未初始化或该位置的数组不是其他信息,则返回false
   */
  public boolean setOther(int index, String other)
  {
    if(table_other_Infos == null)
      return false;
    Object o = table_other_Infos.get(index);
    if(o instanceof String)
    {
      table_other_Infos.set(index, other);
      return true;
    }
    return false;
  }

  /**
   * 得到table标签的信息数组
   * @return 返回table标签的信息数组
   */
  public HtmlTable[] getTables()
  {
    return getTables(false);
  }


  private HtmlTable[] getTables(boolean isEnable)
  {
    if(table_other_Infos == null)
      return null;
    List list = new ArrayList(table_other_Infos.size());
    for(int i=0; i<table_other_Infos.size(); i++)
    {
      Object o = table_other_Infos.get(i);
      if(o instanceof HtmlTable)
      {
        HtmlTable table = (HtmlTable)o;
        if(isEnable)
        {
          if(!table.isDisable())
            list.add(table);
        }
        else
          list.add(table);
      }
    }
    //
    HtmlTable[] tables = new HtmlTable[list.size()];
    list.toArray(tables);
    return tables;
  }

  public void clearTablesAndOther()
  {
    if(table_other_Infos != null)
      table_other_Infos.clear();
  }

  /**
   * 得到可用的table标签的信息数组
   * @return 返回可用的table标签的信息数组
   */
  public HtmlTable[] getEnableTables()
  {
    return getTables(true);
  }

  /**
   * 添加可显示字段信息
   * @param field 可显示字段信息
   */
  public void addField(Map field)
  {
    addField(null, field);
  }

  /**
   * 添加可显示字段信息
   * @param datasetName dataset名称
   * @param field 可显示字段信息
   */
  public void addField(String datasetName, Map field)
  {
    if(fieldInfos == null)
      fieldInfos = new ArrayList();
    if(fieldName == null)
      fieldName = new Hashtable();

    if(datasetName != null)
      field.put(Tag.DATASET, datasetName);

    fieldInfos.add(field);
    fieldName.put(field.get(Tag.NAME), field);
  }

  /**
   * 添加可显示字段信息
   * @param datasetName dataset名称
   * @param fields 可显示字段的字符串信息
   */
  public void addFields(String datasetName, String fields)
  {
    ReportTempletParser.addFields(this, datasetName, fields);
  }

  /**
   * 得到可显示字段信息数组
   * @return 返回可显示字段信息数组
   */
  public Map[] getFields()
  {
    if(fieldInfos == null)
      return null;
    Map[] fields = new Map[fieldInfos.size()];
    fieldInfos.toArray(fields);
    return fields;
  }

  /**
   * 通过字段对象的名称得到字段对象
   * @param name 字段对象的名称
   * @return 字段对象
   */
  public Map getField(String name)
  {
    if(fieldName == null)
      return null;

    return (Map)fieldName.get(name);
  }

  /**
   * 设置报表打印纸张信息
   * @param pageInfo 报表打印纸张信息
   */
  public void setPage(Map pageInfo)
  {
    this.pageInfo = pageInfo;
  }

  /**
   * 得到报表打印纸张信息
   * @return 返回报表打印纸张信息
   */
  public Map getPage()
  {
    return this.pageInfo;
  }

  /**
   * 添加报表的数据集信息
   * @param dataset 报表的数据集信息
   */
  public void addDataSet(Map dataset)
  {
    if(datesetInfos == null)
      datesetInfos = new ArrayList();
    datesetInfos.add(dataset);
  }

  /**
   * 得到报表的数据集信息数组
   * @return 返回报表的数据集信息数组
   */
  public Map[] getDataSets()
  {
    if(datesetInfos == null)
      return null;
    Map[] datasets = new Map[datesetInfos.size()];
    datesetInfos.toArray(datasets);
    return datasets;
  }

  /**
   * 添加报表的参数信息
   * @param param 报表的参数信息
   */
  public void addParam(Map param)
  {
    if(paramInfos == null)
      paramInfos = new ArrayList();
    paramInfos.add(param);
  }

  /**
   * 得到报表的参数信息数组
   * @return 返回报表的参数信息数组
   */
  public Map[] getParams()
  {
    if(paramInfos == null)
      return null;
    Map[] params = new Map[paramInfos.size()];
    paramInfos.toArray(params);
    return params;
  }

  /**
   * 添加报表的查询信息
   * @param where 报表的查询信息
   */
  public void addWhere(Map where)
  {
    if(whereInfos == null)
      whereInfos = new ArrayList();
    whereInfos.add(where);
  }

  /**
   * 得到报表的查询信息数组
   * @return 返回报表的查询信息数组
   */
  public Map[] getWheres()
  {
    if(whereInfos == null)
      return null;
    Map[] wheres = new Map[whereInfos.size()];
    whereInfos.toArray(wheres);
    return wheres;
  }

  /**
   * 得到报表标题
   * @return 返回报表标题
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * 设置报表标题
   * @param title 报表标题
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * 根据模板文件对应的上下文对象的页面显示数据
   * @param pageData 页面显示数据
   * @return 返回页面显示数据
   */
  public void setContextPageData(ContextData context)
  {
    setContextPageData(this, context);
  }

  private static final void setContextPageData(TempletData templet, ContextData context)
  {
    if(context == null)
      return;
    synchronized(templet)
    {
      List fields = templet.fieldInfos;
      if(fields == null)
        return;

      for(int i=0; i<fields.size(); i++)
      {
        Map field = (Map)fields.get(i);
        String name = (String)field.get(Tag.NAME);
        String caption = (String)field.get(Tag.CAPTION);
        String sum = (String)field.get(Tag.SUM);
        String show = (String)field.get(Tag.SHOW);
        if(show == null || show.length() == 0 || StringUtils.isTrue(show))
          context.addColumn(name);
        context.addColumnCaption(name, caption);
        if(StringUtils.isTrue(sum))
          context.addSumColumn(name);
        else
          context.addGroupColumn(name);
      }
    }
  }

  public void clearFields()
  {
    if(this.fieldName != null)
      this.fieldName.clear();
    if(this.fieldInfos != null)
      this.fieldInfos.clear();
  }

  public void clear()
  {
    if(this.fieldName != null)
      this.fieldName.clear();
    if(this.pageInfo != null)
      this.pageInfo.clear();
    if(this.table_other_Infos != null)
      this.table_other_Infos.clear();
    if(datesetInfos != null)
      this.datesetInfos.clear();
    if(this.fieldInfos != null)
      this.fieldInfos.clear();
    if(this.whereInfos != null)
      this.whereInfos.clear();
    if(this.paramInfos != null)
      this.paramInfos.clear();

    this.title = "";
  }
}
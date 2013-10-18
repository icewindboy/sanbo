package engine.report.util;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;

import engine.util.StringUtils;
/**
 * Title:        print
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ENGINE
 * @author hukn
 * @version 1.0
 */

public final class HtmlTableCell implements Serializable
{
  public HtmlTableCell()
  {
  }

  public HtmlTableCell(Map cellInfo)
  {
    this.cellInfo = cellInfo;
  }

  private Map cellInfo = null;

  private List tables = null;

  private List allInfos = null;

  private List fieldValueInfos = null;

  /**
   * 得到Html列的信息即td的信息
   * @return 返回该Html列的信息
   */
  public Map getCellInfo()
  {
    return cellInfo;
  }

  /**
   * 设置Html列信息
   * @cellInfo Html行的信息
   */
  public void setCellInfo(Map cellInfo) {
    this.cellInfo = cellInfo;
  }
  /**
   * 添加一个嵌套表
   * @param table 该嵌套表的类
   */
  public void addTable(HtmlTable table)
  {
    if(tables == null)
      tables = new ArrayList();
    tables.add(table);
  }
  /**
   * 清空所有的嵌套表包括信息
   */
  public void clearAllTables()  {  tables.clear();  }

  /**
   * 得到改列的所有的嵌套表
   * @param index 要得到列的列号
   * @return 得到嵌套表数组
   */
  public HtmlTable[] getTables() throws ArrayStoreException
  {
    HtmlTable[] htmltables = new HtmlTable[tables.size()];
    tables.toArray(htmltables);
    return htmltables;
  }

  /**
   * 得到一个的嵌套表信息
   * @param index 要得到嵌套表的序号
   * @return 得到嵌套表的类
   */
  public HtmlTable getTable(int index)
  {
    try{
      return (HtmlTable)tables.get(index);
    }
    catch(ArrayIndexOutOfBoundsException  outof) {
      return null;
    }
  }

  /**
   * 得到该列所包含的嵌套表的数量
   */
  public int getTableCount()
  {
    return tables.size();
  }

  /**
   * 添加需要填充的字段值信息
   * @param value 需要填充的字段值信息
   */
  public void addValue(Map value)
  {
    if(value == null)
      return;
    if(allInfos == null)
      allInfos = new ArrayList();
    if(fieldValueInfos == null)
      fieldValueInfos = new ArrayList();

    allInfos.add(value);
    fieldValueInfos.add(value);
  }

  /**
   * 添加td显示的文本内容
   * @param value 需要填充的字段值信息
   */
  public void addContent(String content)
  {
    if(content == null)
      return;
    if(allInfos == null)
      allInfos = new ArrayList();
    allInfos.add(content);
  }

  /**
   * 得到需要填充的字段值信息数组
   * @return 返回需要填充的字段值信息数组
   */
  public Object[] getValueInfos()
  {
    if(allInfos == null)
      return null;
    return allInfos.toArray();
  }

  /**
   * HtmlTableCell中是否具有字段信息
   * @return 是否具有字段信息
   */
  public boolean hasValueInfo()
  {
    if(allInfos == null)
      return false;
    return allInfos.size() > 0;
  }

  /**
   * 得到需要用字段值代替内容的信息数组
   * @return 返回需要用字段值代替内容的信息数组
   */
  public Map[] getFieldValueInfos()
  {
    if(fieldValueInfos == null)
      return null;
    Map[] temps = new Map[fieldValueInfos.size()];
    fieldValueInfos.toArray(temps);
    return temps;
  }

  /**
   * 转化为String。打印表格信息和包含的所有行信息
   * @return 返回转化后的表格的String
   */
  public String toString()
  {
    return toStringBuffer(null).toString();
  }

  /**
   * 转化为StringBuffer。打印表格信息和包含的所有行信息
   * @param buf 若为null, 将自动创建一个StringBuffer实例
   * @return 返回转化后的表格的StringBuffer
   */
  public StringBuffer toStringBuffer(StringBuffer buf)
  {
    if(buf == null)
      buf = new StringBuffer();
    buf.append("\n<td");
    if(cellInfo != null)
      buf.append(" ").append(StringUtils.mapToString(cellInfo));
    buf.append(">");
    //
    for(int i=0; allInfos!=null && i<allInfos.size(); i++)
    {
      Object o = allInfos.get(i);
      if(o instanceof Map)
        buf.append("<value ").append(StringUtils.mapToString((Map)o)).append(">");
      else
        buf.append(o);
    }
    //
    for(int i=0; tables!=null && i<tables.size(); i++)
    {
      HtmlTable table = (HtmlTable)tables.get(i);
      table.toStringBuffer(buf);
    }

    buf.append("</td>");
    return buf;
  }
}
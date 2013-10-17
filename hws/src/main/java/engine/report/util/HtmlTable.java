package engine.report.util;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;

import engine.util.StringUtils;
import engine.report.util.*;
/**
 * Title:        print
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      ENGINE
 * @author hukn
 * @version 1.0
 */

public final class HtmlTable implements Serializable
{
  public HtmlTable()
  {
  }

  public HtmlTable(Map tableInfo)
  {
    this.tableInfo = tableInfo;
  }
  //
  private int iColumnNum = 0;
  //
  private Map tableInfo = null;
  //保存
  private List tableRows = new ArrayList();

  /**
   * 得到Html表信息
   * @return 返回该Html表的信息
   */
  public Map getTableInfo()
  {
    return tableInfo;
  }

  /**
   * 设置Html表信息
   * @tableInfo Html表的信息
   */
  public void setTableInfo(Map tableInfo)
  {
    this.tableInfo = tableInfo;
  }

  /**
   * 添加一行
   * @param row 该列的类
   */
  public void addRow(HtmlTableRow row)
  {
    tableRows.add(row);
  }

  /**
   * 删除一行
   * @param index 要移去的行号
   * @return 是否成功
   */
  public boolean removeRow(int index)
  {
    try{
      tableRows.remove(index);
    }
    catch(ArrayIndexOutOfBoundsException  outof) {
      return false;
    }
    return true;
  }

  /**
   * 得到一行的信息
   * @param index 要得到列的行号
   * @return 得到列的类
   */
  public HtmlTableRow getTableRow(int index)
  {
    try{
      return (HtmlTableRow)tableRows.get(index);
    }
    catch(ArrayIndexOutOfBoundsException outof) {
      return null;
    }
  }

  /**
   * 得到所有的列
   * @return 返回所有行的数组
   */
  public HtmlTableRow[] getTableRows()
  {
    HtmlTableRow[] rows = new HtmlTableRow[tableRows.size()];
    tableRows.toArray(rows);
    return rows;
  }

  /**
   * 计算总列数
   */
  private void calculateColumnCount()
  {
    iColumnNum = 0;
    if(tableRows.size() >0)
    {
      HtmlTableCell[] cells = getTableRow(0).getCells();
      Map cellInfo = null;
      for(int i=0; i<cells.length; i++)
      {
        cellInfo = cells[i].getCellInfo();
        String value = (String)cellInfo.get(Tag.COLSPAN);
        try {
          int num = Integer.parseInt(value);
          iColumnNum += num;
        }
        catch(NumberFormatException nfex) {
          iColumnNum ++;
        }
      }
    }
  }

  /**
   * 清空
   */
  public void clearAllRows()
  {
    iColumnNum = 0;
    tableRows.clear();
  }

  /**
   * 得到包含的列数
   * @return 返回包含的列数
   */
  public int getColumnCount()
  {
    calculateColumnCount();
    return iColumnNum;
  }

  /**
   * 得到包含的行数
   * @return 返回包含的行数
   */
  public int getRowCount()
  {
    return tableRows.size();
  }

  /**
   * 该HtmlTable对象是否不可用的
   * @return 是否不可用的
   */
  public boolean isDisable()
  {
    String s = (String)tableInfo.get(Tag.DISABLE);
    return StringUtils.isTrue(s);
  }

  /**
   * 设置HtmlTable对象不可用
   */
  public void setDisable()
  {
    tableInfo.put(Tag.DISABLE, Tag.TRUE);
  }

  /**
   * 设置HtmlTable对象可用
   */
  public void setEnable()
  {
    tableInfo.put(Tag.DISABLE, Tag.FLASE);
  }

  /**
   * 尝试用String类的大学或小学取Map的数据
   * @param map 要取数据的类
   * @param key 要取数据的的键值
   * @return 返回得到色的对象
   */
  public static final Object getIgnoreCaseKey(Map map, String key)
  {
    Object o = map.get(key.toLowerCase());
    if(o != null)
      return o;
    else
      o = map.get(key.toUpperCase());

    return o;
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
    buf.append("\n<table");
    if(tableInfo != null)
      buf.append(" ").append(StringUtils.mapToString(tableInfo));
    buf.append(">");
    //
    for(int i=0; i < tableRows.size(); i++)
    {
      HtmlTableRow tableRow = (HtmlTableRow)tableRows.get(i);
      tableRow.toStringBuffer(buf);
    }
    buf.append("</table>");
    return buf;
  }
}
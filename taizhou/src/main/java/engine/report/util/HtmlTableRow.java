package engine.report.util;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import engine.util.HtmlParser;

import engine.util.StringUtils;
/**
 * Title:        print
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      ENGINE
 * @author hukn
 * @version 1.0
 */

public final class HtmlTableRow implements Serializable
{
  private Map rowInfo = null;

  private List tableCells = new ArrayList();

  /**
   * 构造函数
   */
  public HtmlTableRow()
  {
  }

  /**
   * 构造函数
   * @param rowInfo 行信息
   */
  public HtmlTableRow(Map rowInfo)
  {
    this.rowInfo = rowInfo == null ? new Hashtable() : rowInfo;
  }

  /**
   * 得到Html行的信息即tr的信息
   * @return 返回该Html表的信息
   */
  public Map getRowInfo() { return rowInfo;  }

  /**
   * 设置Html行信息
   * @rowInfo Html行的信息
   */
  public void setRowInfo(Map rowInfo) {
    this.rowInfo = rowInfo;
  }

  /**
   * 添加一列
   * @param cell 该列的类
   */
  public void addCell(HtmlTableCell cell)
  {
    tableCells.add(cell);
  }

  /**
   * 添加多行
   * @param cells 该行信息的HTML语言
   */
  public void addCells(String cells){
    ReportTempletParser.addCells(this, cells);
  }

  /**
   * 清空所有的行包括信息
   */
  public void clearAllCells(){
    tableCells.clear();
  }

  /**
   * 得到改行的所有的列
   * @param index 要得到列的列号
   * @return 得到列的信息
   */
  public HtmlTableCell[] getCells()
  {
    HtmlTableCell[] cells = new HtmlTableCell[tableCells.size()];
    tableCells.toArray(cells);
    return cells;
  }

  /**
   * 得到一列的信息
   * @param index 要得到列的行号
   * @return 得到列的类
   */
  public HtmlTableCell getCell(int index)
  {
    try{
      return (HtmlTableCell)tableCells.get(index);
    }
    catch(ArrayIndexOutOfBoundsException  outof) {
      return null;
    }
  }

  /**
   * 得到该行所包含的列的数量
   */
  public int getCellCount()
  {
    return tableCells.size();
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
    buf.append("\n<tr");
    if(rowInfo != null)
      buf.append(" ").append(StringUtils.mapToString(rowInfo));
    buf.append(">");
    //
    for(int i=0; tableCells!=null && i<tableCells.size(); i++)
    {
      HtmlTableCell cell = (HtmlTableCell)tableCells.get(i);
      cell.toStringBuffer(buf);
    }

    buf.append("</tr>");
    return buf;
  }
}
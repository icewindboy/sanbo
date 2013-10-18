package engine.report.html;

import java.util.ArrayList;
import java.io.Serializable;
/**
 * <p>Title: HTML的TABLE对象</p>
 * <p>Description: HTML的TABLE对象</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */
public final class Table implements Serializable
{
  private static final String DEFAULT_ALGIN = "center";

  private String id = null;

  private String name = null;

  private int border = 0;

  private int cellspacing = 0;

  private int cellpadding = 0;

  private String width = null;

  private String height = null;

  private String align = null;

  private String valign = null;

  private String className = null;

  private String style = null;

  private ArrayList rows = null;

  public Table(){}

  public void addRow(Row row){
    if(rows == null)
      rows = new ArrayList();
    synchronized(rows)
    {
      rows.add(row);
    }
  }

  public void procOneCellTable(int colspan)
  {
    int max = 0;
    for(int i=0; i<getRowCount(); i++){
      int curr = ((Row)rows.get(i)).getCellCount();
      max = max > curr ? max : curr;
    }
    if(max > 1)
      return;
    //
    for(int i=0; i<getRowCount(); i++)
    {
      Row row = (Row)rows.get(i);
      for(int j=0; j<row.getCellCount(); j++)
      {
        Cell cell = row.getCell(j);
        if(cell == null)
          continue;
        cell.setColspan(colspan);
      }
    }
  }

  public int getRowCount()
  {
    return rows == null ? 0 : rows.size();
  }

  /**
   * 得到表格中不含标题的行数量
   * @return 返回表格中不含标题的行数
   */
  public int getRowCountWithoutTitle()
  {
    int count = getRowCount();
    if(count < 1)
      return 0;

    int noTitle = 0;
    for(int i=0; i<count; i++)
    {
      Row row = (Row)rows.get(i);
      if(!row.isTitle())
        noTitle++;
    }
    return noTitle;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setBorder(int border)
  {
    this.border = border;
  }

  public void setCellspacing(int cellspacing)
  {
    this.cellspacing = cellspacing;
  }

  public void setCellpadding(int cellpadding)
  {
    this.cellpadding = cellpadding;
  }

  public void setWidth(String width)
  {
    this.width = width;
  }

  public void setHeight(String height)
  {
    this.height = height;
  }

  public void setAlign(String align)
  {
    this.align = align;
  }

  public void setValign(String valign)
  {
    this.valign = valign;
  }

  public void setClass(String className)
  {
    this.className = className;
  }

  public void setStyle(String Style)
  {
    this.style = Style;
  }

  static StringBuffer printTable(Table obj, StringBuffer buf)
  {
    if(buf == null)
      buf = new StringBuffer();
    buf.append("<table");
    if(obj.id != null)
      buf.append(" id=\"").append(obj.id).append("\"");
    if(obj.name != null)
      buf.append(" name=\"").append(obj.name).append("\"");
    if(obj.border > -1)
      buf.append(" border=\"").append(obj.border).append("\"");
    if(obj.cellspacing > -1)
      buf.append(" cellspacing=\"").append(obj.cellspacing).append("\"");
    if(obj.cellpadding > -1)
      buf.append(" cellpadding=\"").append(obj.cellpadding).append("\"");
    if(obj.width != null)
      buf.append(" width=\"").append(obj.width).append("\"");
    if(obj.height != null)
      buf.append(" height=\"").append(obj.height).append("\"");

    buf.append(" align=\"").append(obj.align == null ? DEFAULT_ALGIN : obj.align).append("\"");
    if(obj.valign != null)
      buf.append(" valign=\"").append(obj.valign).append("\"");
    if(obj.className != null)
      buf.append(" class=\"").append(obj.className).append("\"");

    if(obj.style != null)
      buf.append(" style=\"").append(obj.style).append("\"");
    buf.append(">\n");
    int count = obj.getRowCount();
    for(int i=0; i<count; i++)
    {
      Row row = (Row)obj.rows.get(i);
      Row.printRow(row, buf);
    }
    buf.append("</table>\n");
    return buf;
  }

  /**
   * 释放资源
   */
  public void release() {
    this.id = null;
    this.name = null;
    this.border = 0;
    this.cellspacing = 0;
    this.cellpadding = 0;
    this.width = null;
    this.height = null;
    this.align = null;
    this.valign = null;
    this.className = null;
    this.style = null;
    if(rows != null)
      rows.clear();
  }

  public String toString()
  {
    return toStringBuffer(null).toString();
  }

  public StringBuffer toStringBuffer(StringBuffer buf)
  {
    return printTable(this, buf);
  }
}
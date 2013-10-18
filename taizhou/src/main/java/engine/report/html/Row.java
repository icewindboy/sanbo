package engine.report.html;

import java.util.ArrayList;
import java.io.Serializable;
/**
 * <p>Title: HTML的TR对象</p>
 * <p>Description: HTML的TR对象</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public final class Row implements Serializable
{
  public static final String TITLE_CLASS = "tableTitle";

  private String id = null;

  private String name = null;

  private String width = null;

  private String height = null;

  private String align = null;

  private String valign = null;

  private String className = null;

  private String style = null;

  private String onClick = null;

  private String onDblClick = null;

  private boolean isTitle = false;

  private ArrayList cells = null;

  public Row(){}

  public Row(boolean isTitle){
    this.isTitle = isTitle;
  }

  public void addCell(Cell cell){
    if(cells == null)
      cells = new ArrayList();
    synchronized(cells)
    {
      cells.add(cell);
    }
  }

  public Cell getCell(int index)
  {
    if(index > getCellCount() - 1)
      return null;
    return (Cell)cells.get(index);
  }

  public int getCellCount()
  {
    return cells == null ? 0 : cells.size();
  }

  public void setIsTitle(boolean isTitle)
  {
    this.isTitle = isTitle;
  }

  public boolean isTitle()
  {
    return this.isTitle;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public void setName(String name)
  {
    this.name = name;
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

  public void setOnClick(String onClick)
  {
    this.onClick = onClick;
  }

  public void setOnDblClick(String onDblClick)
  {
    this.onDblClick = onDblClick;
  }

  static StringBuffer printRow(Row obj, StringBuffer buf)
  {
    if(buf == null)
      buf = new StringBuffer();
    buf.append("<tr");;
    if(obj.id != null)
      buf.append(" id=\"").append(obj.id).append("\"");
    if(obj.name != null)
      buf.append(" name=\"").append(obj.name).append("\"");
    if(obj.width != null)
      buf.append(" width=\"").append(obj.width).append("\"");
    if(obj.height != null)
      buf.append(" height=\"").append(obj.height).append("\"");
    if(obj.align != null)
      buf.append(" align=\"").append(obj.align).append("\"");
    if(obj.valign != null)
      buf.append(" valign=\"").append(obj.valign).append("\"");

    if(obj.className == null && obj.isTitle)
      obj.className = TITLE_CLASS;
    if(obj.className != null)
      buf.append(" class=\"").append(obj.className).append("\"");

    if(obj.onClick != null)
      buf.append(" onClick=\"").append(obj.onClick).append("\"");
    if(obj.onDblClick != null)
      buf.append(" onDblClick=\"").append(obj.onDblClick).append("\"");

    if(obj.style != null)
      buf.append(" style=\"").append(obj.style).append("\"");
    buf.append(">\n");

    int count = obj.getCellCount();
    for(int i=0; i<count; i++)
    {
      Cell cell = (Cell)obj.cells.get(i);
      Cell.printCell(cell, buf);
    }
    buf.append("</tr>\n");
    return buf;
  }

  /**
   * 释放资源
   */
  public void release() {
    this.id = null;
    this.name = null;
    this.width = null;
    this.height = null;
    this.align = null;
    this.valign = null;
    this.className = null;
    this.style = null;
    this.isTitle = false;
    if(cells != null)
      cells.clear();
  }

  public String toString()
  {
    return printRow(this, null).toString();
  }
}
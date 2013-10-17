package engine.report.html;

import java.io.Serializable;
/**
 * <p>Title: HTML的TD对象</p>
 * <p>Description: HTML的TD对象</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public final class Cell implements Serializable
{
  private static final String DEFAULT_CLASS = "td";

  private String id = null;

  private String name = null;

  private int colspan = 0;

  private int rowspan = 0;

  private String width = null;

  private String height = null;

  private String align = null;

  private String valign = null;

  private String className = null;

  private String style = null;

  private boolean noWrap = false;

  private boolean isBold = false;

  private String context = null;

  public Cell(){}

  public Cell(String context){
    this.context = context;
  }

  public void setContext(String context)
  {
    this.context = context;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setColspan(int colspan)
  {
    this.colspan = colspan;
  }

  public void setRowspan(int rowspan)
  {
    this.rowspan = rowspan;
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

  public void setNoWrap(boolean nowrap)
  {
    this.noWrap = nowrap;
  }

  public void setIsBold(boolean isBold)
  {
    this.isBold = isBold;
  }

  static StringBuffer printCell(Cell obj, StringBuffer buf)
  {
    if(buf == null)
      buf = new StringBuffer();
    buf.append("<td");
    if(obj.id != null)
      buf.append(" id=\"").append(obj.id).append("\"");
    if(obj.name != null)
      buf.append(" name=\"").append(obj.name).append("\"");
    if(obj.colspan > 1)
      buf.append(" colspan=\"").append(obj.colspan).append("\"");
    if(obj.rowspan > 1)
      buf.append(" rowspan=\"").append(obj.rowspan).append("\"");
    if(obj.width != null)
      buf.append(" width=\"").append(obj.width).append("\"");
    if(obj.height != null)
      buf.append(" height=\"").append(obj.height).append("\"");
    if(obj.align != null)
      buf.append(" align=\"").append(obj.align).append("\"");
    if(obj.valign != null)
      buf.append(" valign=\"").append(obj.valign).append("\"");

      buf.append(" class=\"").append(obj.className == null ? DEFAULT_CLASS : obj.className).append("\"");
    if(obj.style != null)
      buf.append(" style=\"").append(obj.style).append("\"");
    if(obj.noWrap)
      buf.append(" nowrap");
    buf.append(">");

    if(obj.isBold)
      buf.append("<b>");
    if(obj.context == null || obj.context.length() == 0)
      obj.context = "&nbsp;";
    buf.append(obj.context);

    if(obj.isBold)
      buf.append("</b>");
    buf.append("</td>\n");
    return buf;
  }

  /**
   * 释放资源
   */
  public void release() {
    this.id = null;
    this.name = null;
    this.colspan = 0;
    this.rowspan = 0;
    this.width = null;
    this.height = null;
    this.align = null;
    this.valign = null;
    this.className = null;
    this.style = null;
    this.noWrap = false;
    this.isBold = false;
    this.context = null;
  }

  public String toString()
  {
    return printCell(this, null).toString();
  }
}
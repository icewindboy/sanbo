package engine.web.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Option extends TagSupport
{
  private String value = null;

  /**
   * 设置控件显示的值
   * @param valueName 控件显示标题的名称
   */
  public void setValue(String value)
  {
    this.value = value;
  }


  /**
   * 标签的开始函数
   */
  public int doStartTag() throws JspTagException
  {
    JspWriter out = pageContext.getOut();
    try
    {
      out.print(getStartTag(null));
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      System.out.println("Option: Error doStartTag:" + ioe);
    }
    return EVAL_BODY_INCLUDE;
  }

  /**
   * 得到开始标签打印的字符串
   * @return 打印的字符串
   */
  public StringBuffer getStartTag(StringBuffer buf) {
    if(buf == null)
      buf = new StringBuffer();

    if(value == null)
      value = "";
    buf.append("AddSelectItem('");
    buf.append(value);
    buf.append("','");
    return buf;
  }

  /**
   * 结束打印标签
   * @return EVAL_PAGE
   * @throws JspException 异常
   */
  public int doEndTag() throws JspException {
    try{
      pageContext.getOut().println("');");
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
      System.out.println("Option: Error doEndTag:" + ioe);
    }
    return EVAL_PAGE;
  }

  /**
   * 得到结束标签打印的字符串
   * @return 打印的字符串
   */
  public StringBuffer getEndTag(StringBuffer buf)
  {
    if(buf == null)
      buf = new StringBuffer();
    buf.append("');");
    return buf;
  }

  /**
   * 释放资源
   */
  public void release() {
    this.value = null;
  }
}
package engine.web.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
/**
 * <p>Title: 快捷键标签</p>
 * <p>Description: 快捷键标签</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class Shortcut extends TagSupport
{
  private String script = null;

  private String key = null;

  /**
   * 设置触发快捷键的key一般是ALT＋key
   * @param key 触发快捷键的key一般是ALT＋key
   */
  public void setKey(String key)
  {
    this.key = key.toLowerCase();
  }

  /**
   * 快捷键触发的javascript语句
   * @param script 快捷键触发的javascript语句
   */
  public void setScript(String script)
  {
    this.script = script;
  }

  /**
   * 标签的开始函数
   */
  public int doStartTag() throws JspTagException
  {
    try
    {
      if(this.key == null || this.key.length() == 0)
        return SKIP_BODY;

      int keyCode = this.key.charAt(0)-(97-65);
      JspWriter out = pageContext.getOut();
      StringBuffer buf = new StringBuffer("<script language='javascript'>GetShortcutControl(");
      buf.append(keyCode).append(",\"").append(script).append("\");</script>");
      out.println(buf.toString());
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      System.out.println("Shortcut: Error doStartTag:" + ioe);
    }

    return SKIP_BODY;
  }

  /**
   * 释放资源
   */
  public void release() {
    this.key = null;
    this.script = null;
  }
}

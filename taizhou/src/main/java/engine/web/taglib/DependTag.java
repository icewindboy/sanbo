package engine.web.taglib;

//import engine.web.struts.Globals;
//import engine.web.struts.JspUtils;
//import engine.pools.PoolFacade;
import engine.util.log.Log;
import java.io.Serializable;
import java.util.ArrayList;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public abstract class DependTag implements Serializable, Tag
{
  protected static String lineEnd = System.getProperty("line.separator");

  protected Log log = new Log(getClass());

  private   ArrayList depends = null;

  public DependTag()
  {
  }

  /**
   * 添加依赖的消息键值
   * @param deps 依赖的消息键值数组
   */
  protected final void addDepends(String[] deps)
  {
    if(deps == null)
      return;
    if(depends == null)
      depends = new ArrayList();
    for(int i=0; i<deps.length; i++)
    {
      if(deps[i] != null && deps[i].length() > 0)
        depends.add(deps[i]);
    }
  }

  private   Tag       parent;

  protected String	  id;
  protected PageContext pageContext;

  public int doStartTag() throws javax.servlet.jsp.JspException
  {
    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException
  {
    return EVAL_PAGE;
  }

  /**
   * Set the id attribute for this tag.
   * @param id The String for the id.
   */

  public void setId(String id) {
    this.id = id;
  }

  /**
   * The value of the id attribute of this tag; or null.
   * @returns the value of the id attribute, or null
   */
  public String getId() {
    return id;
  }

  public void setPageContext(PageContext pc)
  {
    this.pageContext = pc;
  }

  public void setParent(Tag t)
  {
    this.parent = t;
  }

  public Tag getParent()
  {
    return parent;
  }

  public void release()
  {
  }
}
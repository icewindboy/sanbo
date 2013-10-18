package engine.html;

import java.util.EventListener;
import javax.servlet.jsp.JspWriter;
import com.borland.dx.dataset.DataSet;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public interface HtmlPrintCellListener extends java.io.Serializable
{

  /**
   * 打印行需要实现的接口
   * @param out JSP页面的输出对象
   * @param reponse 打印的响应对象, 可以用该对象取消打印
   */
  public void printCell(JspWriter out, HtmlPrintCellResponse reponse, DataSet ds) throws Exception;
}
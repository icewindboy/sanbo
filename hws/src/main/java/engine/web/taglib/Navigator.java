package engine.web.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

import engine.dataset.EngineDataSet;
//import com.borland.dx.dataset.DataSet;

/**
 * Title:        显示网页页数
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */

public class Navigator extends TagSupport
{
  private int pageSize =25;
  private int recordCount = 0;
  private boolean isPrint= true;
  private String goProperty = "style='border-style:solid; border-width:1px; border-color:#3399ff; background-color:lightcyan; color:red; font-size:8pt; width: auto'";
  private String attributeKey = null;
  private String form = null;
  private String operate = null;
  private boolean disable = false;
  private String id = null;

  private PaginationInfo paginationInfo = null;

  public void setId( String newId ) {
    id = newId;
  }

  public String getId() {
    return id;
  }
  /**
   *设置输出每一页的大小
   *@param pageSize 输出每一页的大小
   */
  public void setPageSize(String pageSize)
  {
    try{
      this.pageSize = Integer.parseInt(pageSize);
    }
    catch(NumberFormatException e){
      this.pageSize = 25;
    }
  }
  /**
   * 设置数据集
   * @param dataSetKey 数据集保存在pageContext的中的key
   */
  public void setRecordCount(String count) throws Exception
  {
    try{
      this.recordCount = Integer.parseInt(count);
    }
    catch(NumberFormatException e){
      this.recordCount = 0;
    }
  }

  /**
   * 需要提交的表单名称
   * @param form 表单名称
   */
  public void setForm(String form)
  {
    this.form = form==null || form.length()==0 ? null : form;
  }

  /**
   * 设置提交表单的的javascript函数，若函数为空，则直接调用form.submit();
   * @param operate 提交表单的的javascript函数
   */
  public void setOperate(String operate)
  {
    this.operate = operate==null || operate.length()==0 ? null : operate;
  }

  /**
   * 设置是否打印分页信息的属性
   * @param isPrint 是否打印分页信息
   */
  public void setIsPrint(String isPrint)
  {
    this.isPrint = !(isPrint!= null && isPrint.toLowerCase().equals("false"));
  }

  /**
   * 设置超级链接的Class风格
   * @param linkClass 超级链接的Class风格
   */
  public void setGoProperty(String goProperty)
  {
    this.goProperty = goProperty;
  }

  /**
   * 设置当前网页URL
   * @param jspUrl 当前网页URL
   */
  public void setJspUrl(String jspUrl)
  {
    if(jspUrl == null)
      return;
    this.attributeKey = jspUrl;
  }

  public void setDisable(String disable){
    this.disable = disable !=null && (disable.equals("1") || disable.equalsIgnoreCase("true"));
  }

  /**
   * 标签的开始函数
   */
  public int doStartTag() throws JspTagException
  {
    pageContext.setAttribute(getId(), this);
    HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
    HttpSession session = pageContext.getSession();
    if(attributeKey == null)
      attributeKey = req.getRequestURL().toString();

    paginationInfo = PaginationInfo.getInstance(session, attributeKey+".navigator");
    PaginationInfo.pageControl(paginationInfo, req, pageSize, recordCount, disable);

    if(!isPrint)
      return SKIP_BODY;
    try
    {
      paginationInfo.setForm(form);
      paginationInfo.setOperate(operate);
      JspWriter out = pageContext.getOut();
      StringBuffer buf = PaginationInfo.getPrintInfo(paginationInfo, attributeKey, goProperty);
      out.println(buf);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      System.out.println("Navigator: Error printing info:" + ioe);
    }

    return SKIP_BODY;
  }

  /**
   * 释放资源
   */
  public void release() {
    super.release();
    this.pageSize = 25;
    this.recordCount = 0;
    this.isPrint = true;
    this.attributeKey = null;
    this.form = null;
    this.goProperty = "style='border-style:solid; border-width:1px; border-color:#3399ff; background-color:lightcyan; color:red; font-size:8pt; width: auto'";
    this.disable = false;
  }

  /**
   * 提供分页的数据集的最小列数.也可以自己写代码:<br>
   * @param request 请求网页的request对象
   * @return 返回该网页中分页的数据集的最小列数
   */
  public static int getRowMin(HttpServletRequest request)
  {
    return getRowMin(request, null);
  }
  /**
   * 提供分页的数据集的最小列数.也可以自己写代码:<br>
   * @param request 请求网页的request对象
   * @param url 请求网页的url
   * @return 返回该网页中分页的数据集的最小列数
   */
  public static int getRowMin(HttpServletRequest request, String url)
  {
    if(url == null)
      url = request.getRequestURL().toString();
    url = url+".navigator";
    HttpSession session = request.getSession();
    PaginationInfo pageInfo = PaginationInfo.getInstance(session, url);
    int min = pageInfo.getStartRecord();
    if(min <0)
      return 0;
    return min;
  }
  /**
   * 提供分页的数据集的最大列数
   * @param request 请求网页的request对象
   * @return 返回该网页中分页的数据集的最大列数
   */
  public static int getRowMax(HttpServletRequest request)
  {
    return getRowMax(request, null);
  }
  /**
   * 提供分页的数据集的最大列数
   * @param request 请求网页的request对象
   * @param url 请求网页的url
   * @return 返回该网页中分页的数据集的最大列数
   */
  public static int getRowMax(HttpServletRequest request, String url)
  {
    if(url == null)
      url = request.getRequestURL().toString();
    url = url+".navigator";
    HttpSession session = request.getSession();
    PaginationInfo pageInfo = PaginationInfo.getInstance(session, url);
    int max = pageInfo.getEndRecord();
    if(max <0)
      return 0;
    return max;
  }
}